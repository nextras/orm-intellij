package org.nextras.orm.intellij.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocToken

/**
 * Resolves (entity) class names in (relationship) modifiers `{m:m ClassName}`, `{1:m ClassName}`, `{m:1 ClassName}`,
 * `{1:1 ClassName}`, `{wrapper ClassName}` as a class reference.
 *
 * This makes it click-able to the class definition.
 */
class ModifierClassNameProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(
		element: PsiElement,
		context: ProcessingContext
	): Array<PsiReference> {
		if (element !is PhpDocToken) {
			return PsiReference.EMPTY_ARRAY
		}

		return arrayOf(ModifierClassName(element))
	}
}
