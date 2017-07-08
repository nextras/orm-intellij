package org.nextras.orm.intellij.utils;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
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


	public static Collection<PhpClass> findRepositoryEntities(PhpClass repositoryClass)
	{
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
		final Collection<PhpClass> entities = new ArrayList<PhpClass>(1);
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
		return entities;
	}
}
