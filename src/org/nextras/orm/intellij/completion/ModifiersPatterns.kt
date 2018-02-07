package org.nextras.orm.intellij.completion

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes
import org.nextras.orm.intellij.parser.PhpDocTypes


object ModifiersPatterns {
	val modifierPattern: ElementPattern<PsiElement>
		get() = PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_IDENTIFIER)
			.afterLeaf(PlatformPatterns.psiElement(PhpDocTokenTypes.DOC_LBRACE))
			.inside(PlatformPatterns.psiElement(PhpDocTypes.phpDocTagModifier))
			.withLanguage(PhpLanguage.INSTANCE)
}
