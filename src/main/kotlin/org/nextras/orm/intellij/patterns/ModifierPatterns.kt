package org.nextras.orm.intellij.patterns

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiWhiteSpace
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes

object ModifierPatterns {
	val Modifier =
		PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER)
			.afterLeaf(PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_LBRACE))
			.withLanguage(PhpLanguage.INSTANCE)

	val RelationshipClassName =
		PlatformPatterns
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

	val RelationshipProperty =
		PlatformPatterns
			.psiElement(PhpDocTokenTypes.DOC_VARIABLE)
			.afterLeaf(
				PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_STATIC)
					.withText("::")
					.afterLeaf(RelationshipClassName)
			)
			.withLanguage(PhpLanguage.INSTANCE)

	val WrapperClassName =
		PlatformPatterns
			.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER)
			.afterLeafSkipping(
				PlatformPatterns.psiElement(PsiWhiteSpace::class.java),
				PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER).withText("wrapper"),
			)
			.withLanguage(PhpLanguage.INSTANCE)
}
