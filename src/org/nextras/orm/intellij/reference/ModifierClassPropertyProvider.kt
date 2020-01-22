package org.nextras.orm.intellij.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocToken

/**
 * Makes property of entity class name in relationship modifiers {x:x ClassName::$property} a member reference.
 * This make it click-able to the member definition.
 */
class ModifierClassPropertyProvider : PsiReferenceProvider() {
	companion object {
		val PATTERN = PlatformPatterns
			.psiElement(PhpDocTokenTypes.DOC_VARIABLE)
			.afterLeaf(
				PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_STATIC)
					.withText("::")
					.afterLeaf(ModifierClassNameProvider.PATTERN)
			)
			.withLanguage(PhpLanguage.INSTANCE)
	}

	override fun getReferencesByElement(
		element: PsiElement,
		context: ProcessingContext
	): Array<PsiReference> {
		if (element !is PhpDocToken) {
			return PsiReference.EMPTY_ARRAY
		}

		return arrayOf(ModifierClassProperty(element))
	}
}
