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


public class ModifierHighlighterAnnotator implements Annotator
{
	@Override
	public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder)
	{
		PsiElement parent = psiElement.getParent();
		if (!(parent instanceof PhpDocTagModifier)) {
			return;
		}

		if (!(psiElement instanceof LeafPsiElement)) {
			return;
		}

		IElementType type = psiElement.getNode().getElementType();
		if (type == PhpDocTokenTypes.DOC_LBRACE || type == PhpDocTokenTypes.DOC_RBRACE) {
			annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(ModifierHighlighter.BRACKETS);
		} else {
			annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(ModifierHighlighter.MODIFIER);
		}
	}
}
