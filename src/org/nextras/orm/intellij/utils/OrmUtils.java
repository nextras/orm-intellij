package org.nextras.orm.intellij.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Pattern;


public class OrmUtils
{
	private static final Pattern entityPattern = Pattern.compile("(?:@entity\\s+([a-zA-Z0-9_\\\\]+))");

	public static boolean isEntity(PhpClass cls, PhpIndex phpIndex)
	{
		PhpClass entityInterface = PhpClassUtils.getInterface(phpIndex, "\\Nextras\\Orm\\Entity\\IEntity");
		return PhpClassUtils.isImplementationOfInterface(cls, entityInterface);
	}


	public static boolean isRepository(PhpClass cls, PhpIndex phpIndex)
	{
		PhpClass entityInterface = PhpClassUtils.getInterface(phpIndex, "\\Nextras\\Orm\\Repository\\IRepository");
		return PhpClassUtils.isImplementationOfInterface(cls, entityInterface);
	}


	public static boolean isMapper(PhpClass cls, PhpIndex phpIndex)
	{
		PhpClass entityInterface = PhpClassUtils.getInterface(phpIndex, "\\Nextras\\Orm\\Mapper\\IMapper");
		return PhpClassUtils.isImplementationOfInterface(cls, entityInterface);
	}


	public static Collection<PhpClass> findRepositoryEntities(Collection<PhpClass> repositories)
	{
		final Collection<PhpClass> entities = new ArrayList<PhpClass>(1);
		for (PhpClass repositoryClass : repositories) {

			Method entityNamesMethod = repositoryClass.findMethodByName("getEntityClassNames");
			if (entityNamesMethod == null) {
				return Collections.emptyList();
			}
			if (!(entityNamesMethod.getLastChild() instanceof GroupStatement)) {
				return Collections.emptyList();
			}
			if (!(((GroupStatement) entityNamesMethod.getLastChild()).getFirstPsiChild() instanceof PhpReturn)) {
				return Collections.emptyList();
			}
			if (!(((GroupStatement) entityNamesMethod.getLastChild()).getFirstPsiChild().getFirstPsiChild() instanceof ArrayCreationExpression)) {
				return Collections.emptyList();
			}
			ArrayCreationExpression arr = (ArrayCreationExpression) ((GroupStatement) entityNamesMethod.getLastChild()).getFirstPsiChild().getFirstPsiChild();
			final PhpIndex phpIndex = PhpIndex.getInstance(repositoryClass.getProject());
			for (PsiElement el : arr.getChildren()) {
				if (!(el.getFirstChild() instanceof ClassConstantReference)) {
					continue;
				}
				ClassConstantReference ref = (ClassConstantReference) el.getFirstChild();
				if (!ref.getName().equals("class")) {
					continue;
				}
				entities.addAll(PhpIndexUtils.getByType(ref.getClassReference().getType(), phpIndex));
			}
		}
		return entities;
	}


	public static Collection<PhpClass> findQueriedEntities(Collection<PhpClass> repositories, String[] path)
	{
		if (repositories.size() == 0) {
			return Collections.emptyList();
		}
		if (path.length <= 1) {
			return findRepositoryEntities(repositories);
		}
		Project project = null;
		for (PhpClass cls : repositories) {
			project = cls.getProject();
			break;
		}
		PhpIndex index = PhpIndex.getInstance(project);
		Collection<PhpClass> classes = path[0].equals("this") ? findRepositoryEntities(repositories) : PhpIndexUtils.getByType(new PhpType().add(path[0]), index);
		return findTargetEntities(classes, path, 1);
	}


	private static Collection<PhpClass> findTargetEntities(Collection<PhpClass> currentEntities, String[] path, int pos)
	{
		if (path.length == (pos + 1)) {
			return currentEntities;
		}
		Collection<PhpClass> entities = new HashSet<>();
		for (PhpClass cls : currentEntities) {
			Field field = cls.findFieldByName(path[pos], false);
			if (!(field instanceof PhpDocProperty)) {
				continue;
			}
			PhpIndex index = PhpIndex.getInstance(field.getProject());
			for (String type : field.getType().getTypes()) {
				if (type.contains("Nextras\\Orm\\Relationship")) {
					continue;
				}
				if (type.endsWith("[]")) {
					type = type.substring(0, type.length() - 2);
				}
				for (PhpClass entityCls : PhpIndexUtils.getByType(new PhpType().add(type), index)) {
					if (!OrmUtils.isEntity(entityCls, index)) {
						continue;
					}
					entities.add(entityCls);
				}
			}
		}
		return findTargetEntities(entities, path, pos + 1);
	}

}
