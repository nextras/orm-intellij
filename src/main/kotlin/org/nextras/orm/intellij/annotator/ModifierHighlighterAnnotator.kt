package org.nextras.orm.intellij.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocPropertyTag
import com.jetbrains.php.lang.psi.elements.impl.PhpPsiElementImpl
import org.nextras.orm.intellij.annotator.highlighter.ModifierHighlighter

/**
 * This is a modifier highlighter.
 */
class ModifierHighlighterAnnotator : Annotator {
	override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {
		// currently PhpDoc property comment is always wrapped in this specific type
		if (psiElement::class != PhpPsiElementImpl::class) return
		if (psiElement.parent !is PhpDocPropertyTag) return

		var isModifier = false
		var isModifierName = false
		var element: PsiElement? = psiElement.node.firstChildNode as? PsiElement ?: return

		while (element != null) {
			if (element.node.elementType == PhpDocTokenTypes.DOC_LBRACE) {
				isModifier = true
				isModifierName = true
				annotationHolder
					.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.range(element)
					.textAttributes(ModifierHighlighter.BRACES)
					.create()

			} else if (element.node.elementType == PhpDocTokenTypes.DOC_RBRACE) {
				isModifier = false
				annotationHolder
					.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.range(element)
					.textAttributes(ModifierHighlighter.BRACES)
					.create()

			} else if (isModifier) {
				if (isModifierName && element !is PsiWhiteSpace) {
					annotationHolder
						.newSilentAnnotation(HighlightSeverity.INFORMATION)
						.range(element)
						.textAttributes(ModifierHighlighter.MODIFIER)
						.create()

				} else if (!isModifierName && (element.node.elementType == PhpDocTokenTypes.DOC_IDENTIFIER || element.node.elementType == PhpDocTokenTypes.DOC_VARIABLE)) {
					annotationHolder
						.newSilentAnnotation(HighlightSeverity.INFORMATION)
						.range(element)
						.textAttributes(ModifierHighlighter.IDENTIFIER)
						.create()

				} else {
					isModifierName = false
				}
			}
			element = element.nextSibling
		}
	}
}
