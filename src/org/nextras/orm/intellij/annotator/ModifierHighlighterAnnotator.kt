package org.nextras.orm.intellij.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocType
import com.jetbrains.php.lang.documentation.phpdoc.psi.impl.PhpDocTypeImpl
import org.nextras.orm.intellij.annotator.highlighter.ModifierHighlighter
import org.nextras.orm.intellij.parser.PhpDocTagModifier
import org.nextras.orm.intellij.parser.PhpDocTagModifierClassType
import org.nextras.orm.intellij.parser.PhpDocTagModifierName
import org.nextras.orm.intellij.parser.PhpDocTagModifierIdentifier


class ModifierHighlighterAnnotator : Annotator {
	override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {
		val parent = psiElement.parent
		val type = (psiElement as? LeafPsiElement)?.node?.elementType

		// LBRACE || RBRACE
		if (parent is PhpDocTagModifier && (type === PhpDocTokenTypes.DOC_LBRACE || type === PhpDocTokenTypes.DOC_RBRACE)) {
			annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = ModifierHighlighter.BRACES

		} else if (psiElement is PhpDocTagModifierName) {
			annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = ModifierHighlighter.MODIFIER

		} else if (psiElement is PhpDocTagModifierIdentifier || psiElement is PhpDocTagModifierClassType) {
			annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = ModifierHighlighter.IDENTIFIER
		}
	}
}
