package org.nextras.orm.intellij.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class SetValueReferenceProvider extends PsiReferenceProvider
{

	@NotNull
	@Override
	public PsiReference[] getReferencesByElement(@NotNull PsiElement el, @NotNull ProcessingContext processingContext)
	{
		assert (el instanceof StringLiteralExpression);
		if (el.getParent() == null || el.getParent().getParent() == null || !(el.getParent().getParent() instanceof MethodReference)) {
			return new PsiReference[0];
		}
		MethodReference method = (MethodReference) el.getParent().getParent();
		if (method.getName() == null || !(method.getName().equals("setValue") || method.getName().equals("setReadOnlyValue"))) {
			return new PsiReference[0];
		}

		return new PsiReference[]{new EntityPropertyReference((StringLiteralExpression) el)};

	}

}
