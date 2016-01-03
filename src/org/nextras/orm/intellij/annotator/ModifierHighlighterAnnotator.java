package org.nextras.orm.intellij.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.nextras.orm.intellij.annotator.highlighter.ModifierHighlighter;
import org.nextras.orm.intellij.parser.PhpDocTagModifier;
import org.nextras.orm.intellij.parser.PhpDocTagModifierName;
import org.nextras.orm.intellij.parser.PhpDocTagModifierParameterName;
import org.nextras.orm.intellij.parser.PhpDocTagModifierParameterValue;


public class ModifierHighlighterAnnotator implements Annotator
{
	@Override
	public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder)
	{
		PsiElement parent = psiElement.getParent();
		IElementType type = psiElement instanceof LeafPsiElement ? psiElement.getNode().getElementType() : null;

		// LBRACE || RBRACE
		if (parent instanceof PhpDocTagModifier && (type == PhpDocTokenTypes.DOC_LBRACE || type == PhpDocTokenTypes.DOC_RBRACE)) {
			annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(ModifierHighlighter.BRACES);

		} else if (psiElement instanceof PhpDocTagModifierName) {
			annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(ModifierHighlighter.MODIFIER);

		} else if (psiElement instanceof PhpDocTagModifierParameterName) {
			annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(ModifierHighlighter.PARAMETER_NAME);

		} else if (psiElement instanceof PhpDocTagModifierParameterValue) {
			annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(ModifierHighlighter.PARAMETER_VALUE);
		}
	}
}
