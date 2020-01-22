package org.nextras.orm.intellij.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
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
				annotationHolder.createInfoAnnotation(element, null).textAttributes = ModifierHighlighter.BRACES
			} else if (element.node.elementType == PhpDocTokenTypes.DOC_RBRACE) {
				isModifier = false
				annotationHolder.createInfoAnnotation(element, null).textAttributes = ModifierHighlighter.BRACES
			} else if (isModifier) {
				if (isModifierName && element !is PsiWhiteSpace) {
					annotationHolder.createInfoAnnotation(element, null).textAttributes = ModifierHighlighter.MODIFIER
				} else if (!isModifierName && (element.node.elementType == PhpDocTokenTypes.DOC_IDENTIFIER || element.node.elementType == PhpDocTokenTypes.DOC_VARIABLE)) {
					annotationHolder.createInfoAnnotation(element, null).textAttributes = ModifierHighlighter.IDENTIFIER
				} else {
					isModifierName = false
				}
			}
			element = element.nextSibling
		}
	}
}
