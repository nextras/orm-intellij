package org.nextras.orm.intellij.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

/**
 * Makes property reference in methods like `$this->getProperty('title')`.
 * This makes it clickable to the property definition.
 */
class EntityPropertyNameReferenceProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(el: PsiElement, processingContext: ProcessingContext): Array<PsiReference> {
		assert(el is StringLiteralExpression)
		val method = el.parent?.parent as? MethodReference ?: return emptyArray()

		return when (method.name) {
			"setValue", "setReadOnlyValue", "getValue", "hasValue", "getProperty", "getRawProperty" -> {
				arrayOf(EntityPropertyNameReference(el as StringLiteralExpression))
			}
			else -> {
				emptyArray()
			}
		}
	}
}
