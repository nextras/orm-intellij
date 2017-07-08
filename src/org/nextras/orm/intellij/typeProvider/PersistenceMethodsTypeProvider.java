package org.nextras.orm.intellij.typeProvider;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class PersistenceMethodsTypeProvider implements PhpTypeProvider3
{

	@Override
	public char getKey()
	{
		return '\u0240';
	}

	@Nullable
	@Override
	public PhpType getType(PsiElement element)
	{
		if (!(element instanceof MethodReference)) {
			return null;
		}
		MethodReference methodReference = (MethodReference) element;
		if (!methodReference.getName().equals("persist") && !methodReference.getName().equals("persistAndFlush")) {
			return null;
		}
		String className = ((MethodReference) element).getClassReference().getName().toLowerCase();

		//cannot access index here, so just dummy check
		if (!className.endsWith("model") && !className.endsWith("repository") && !className.endsWith("repositorycontainer")) {
			return null;
		}
		if (methodReference.getParameters().length == 0) {
			return null;
		}
		if (!(methodReference.getParameters()[0] instanceof PhpTypedElement)) {
			return null;
		}
		return ((PhpTypedElement) methodReference.getParameters()[0]).getType();
	}

	@Override
	public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project)
	{
		return null;
	}
}
