package org.nextras.orm.intellij.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.PhpIndexImpl;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nextras.orm.intellij.utils.OrmUtils;
import org.nextras.orm.intellij.utils.PhpClassUtils;
import org.nextras.orm.intellij.utils.PhpIndexUtils;

import java.util.ArrayList;

public class EntityPropertyReference extends PsiPolyVariantReferenceBase<StringLiteralExpression>
{

	public EntityPropertyReference(StringLiteralExpression psiElement)
	{
		super(psiElement);
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve(boolean b)
	{
		StringLiteralExpression expr = getElement();
		assert (expr.getParent().getParent() != null);

		MethodReference method = (MethodReference) expr.getParent().getParent();
		assert (method.getName() != null && (method.getName().equals("setValue") || method.getName().equals("setReadOnlyValue")));

		ArrayList<ResolveResult> result = new ArrayList<ResolveResult>();
		PhpIndex phpIndex = PhpIndex.getInstance(this.getElement().getProject());
		for (PhpClass cls : PhpIndexUtils.getByType(method.getClassReference().getType(), phpIndex)) {
			if (!OrmUtils.isEntity(cls, phpIndex)) {
				continue;
			}
			final Field field = cls.findFieldByName(expr.getContents(), false);
			if (field == null || !(field instanceof PhpDocProperty)) {
				continue;
			}
			result.add(new ResolveResult()
			{
				@Nullable
				@Override
				public PsiElement getElement()
				{
					return field;
				}

				@Override
				public boolean isValidResult()
				{
					return true;
				}
			});
		}
		return result.toArray(new ResolveResult[result.size()]);
	}

	@NotNull
	@Override
	public Object[] getVariants()
	{
		return new Object[0];
	}
}
