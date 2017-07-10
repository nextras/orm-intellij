package org.nextras.orm.intellij.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nextras.orm.intellij.utils.OrmUtils;

import java.util.Collection;

public class CollectionClassReference extends PsiPolyVariantReferenceBase<StringLiteralExpression>
{

	private final MethodReference methodReference;

	private String name;


	public CollectionClassReference(StringLiteralExpression psiElement, MethodReference methodReference, String name)
	{
		super(psiElement, new TextRange(1, name.length() + 1));
		this.methodReference = methodReference;
		this.name = name;
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve(boolean b)
	{
		Collection<PhpClass> classes = OrmUtils.findQueriedEntities(methodReference, new String[]{name});
		ResolveResult[] result = new ResolveResult[classes.size()];
		int i = 0;
		for (PhpClass cls : classes) {
			result[i++] = new ResolveResult()
			{
				@Nullable
				@Override
				public PsiElement getElement()
				{
					return cls;
				}

				@Override
				public boolean isValidResult()
				{
					return true;
				}
			};
		}

		return result;
	}

	@NotNull
	@Override
	public Object[] getVariants()
	{
		return new Object[0];
	}
}