package org.nextras.orm.intellij.completion;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes;
import com.jetbrains.php.lang.documentation.phpdoc.parser.PhpDocElementTypes;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import org.nextras.orm.intellij.parser.PhpDocTypes;


public class ModifiersPatterns
{
	public static ElementPattern<PsiElement> getModifierPattern()
	{
		return PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER)
			.afterLeaf(PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_LBRACE))
			.inside(PlatformPatterns.psiElement(PhpDocTypes.phpDocTagModifier))
			.withLanguage(PhpLanguage.INSTANCE);
	}
}
