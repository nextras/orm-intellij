package org.nextras.orm.intellij.typeProvider;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeSignatureKey;
import org.jetbrains.annotations.Nullable;
import org.nextras.orm.intellij.utils.OrmUtils;
import org.nextras.orm.intellij.utils.PhpIndexUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionTypeProvider implements PhpTypeProvider3
{

	private static Set<String> pluralMethods = new HashSet<>(Arrays.asList("findBy", "orderBy", "limitBy", "fetchAll", "findAll", "findById", "get"));
	private static Set<String> singularMethods = new HashSet<>(Arrays.asList("fetch", "getBy", "getById"));


	@Override
	public char getKey()
	{
		return '\u0241';
	}

	@Nullable
	@Override
	public PhpType getType(PsiElement element)
	{
		if (!(element instanceof MethodReference)) {
			return null;
		}
		MethodReference ref = (MethodReference) element;
		if (ref.getClassReference() == null) {
			return null;
		}
		if (!pluralMethods.contains(ref.getName()) && !singularMethods.contains(ref.getName())) {
			return null;
		}
		PhpType type = ref.getClassReference().getType();
		PhpType resultType = new PhpType();
		String arraySuffix = pluralMethods.contains(ref.getName()) ? "[]" : "";
		for (String typeStr : type.filterMixed().filterNull().filterPrimitives().getTypes()) {
			if (typeStr.startsWith("#V")) {
				continue;
			}
			resultType.add("#" + getKey() + typeStr + "." + ref.getName() + arraySuffix);
		}
		return resultType;
	}

	@Override
	public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project)
	{
		if (expression.endsWith("[]")) {
			return Collections.emptyList();
		}
		Collection<PhpNamedElement> result = new HashSet<>();
		int pos = expression.lastIndexOf(".");
		String refSig = expression.substring(0, pos);
		if (refSig.endsWith("[]")) {
			refSig = refSig.substring(0, refSig.length() - 2);
		}
		String methodName = expression.substring(pos + 1);
		if (methodName.endsWith("[]")) {
			methodName = methodName.substring(0, methodName.length() - 2);
		}
		PhpIndex index = PhpIndex.getInstance(project);
		Collection<PhpClass> types = PhpIndexUtils.getByType(new PhpType().add(refSig), index, visited, depth);

		if (types.stream().anyMatch(cls -> OrmUtils.OrmClass.HAS_MANY.is(cls, index)) && methodName.equals("get") && refSig.startsWith("#")) {
			result.addAll(PhpIndexUtils.getByType(new PhpType().add(PhpTypeSignatureKey.ARRAY_ELEMENT.sign(refSig)), index, visited, depth));
		} else {
			result.addAll(types.stream().filter(cls -> OrmUtils.OrmClass.ENTITY.is(cls, index)).collect(Collectors.toList()));
			Stream<PhpClass> repoClasses = types.stream().filter(cls -> OrmUtils.OrmClass.REPOSITORY.is(cls, index));
			List<PhpClass> repoClassesList = repoClasses.collect(Collectors.toList());
			if (repoClassesList.size() > 0) {
				result.addAll(OrmUtils.findRepositoryEntities(repoClassesList));
				result.addAll(types.stream().filter(cls -> OrmUtils.OrmClass.ENTITY.is(cls, index)).collect(Collectors.toList()));
			}
		}

		return result;
	}
}
