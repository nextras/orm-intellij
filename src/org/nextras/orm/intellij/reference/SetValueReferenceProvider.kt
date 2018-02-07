package org.nextras.orm.intellij.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class SetValueReferenceProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(el: PsiElement, processingContext: ProcessingContext): Array<PsiReference> {
		assert(el is StringLiteralExpression)
		if (el.parent == null || el.parent.parent == null || el.parent.parent !is MethodReference) {
			return emptyArray()
		}
		val method = el.parent.parent as MethodReference
		return if (method.name == null || !(method.name == "setValue" || method.name == "setReadOnlyValue")) {
			emptyArray()
		} else arrayOf(EntityPropertyReference(el as StringLiteralExpression))
	}
}
