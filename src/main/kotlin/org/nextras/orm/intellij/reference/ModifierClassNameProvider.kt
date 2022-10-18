package org.nextras.orm.intellij.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiWhiteSpace
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocToken

/**
 * Makes entity class name in relationship modifiers {m:m ClassName}|{1:m ClassName}|{m:1 ClassName}|{1:1 ClassName} a class reference.
 * This make it click-able to the class definition.
 */
class ModifierClassNameProvider : PsiReferenceProvider() {
	companion object {
		val PATTERN = PlatformPatterns
			.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER)
			.afterLeafSkipping(
				PlatformPatterns.psiElement(PsiWhiteSpace::class.java),
				PlatformPatterns.or(
					PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText("m").afterLeaf(
						PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_TEXT).withText(":").afterLeaf(
							PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText("m")
						)
					),
					PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText("1").afterLeaf(
						PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_TEXT).withText(":").afterLeaf(
							PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText("m")
						)
					),
					PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText("m").afterLeaf(
						PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_TEXT).withText(":").afterLeaf(
							PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText("1")
						)
					),
					PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText("1").afterLeaf(
						PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_TEXT).withText(":").afterLeaf(
							PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText("1")
						)
					),
				)
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

		return arrayOf(ModifierClassName(element))
	}
}
