package org.nextras.orm.intellij.annotator.highlighter

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object ModifierHighlighter {
	val BRACES = TextAttributesKey.createTextAttributesKey("BRACES", DefaultLanguageHighlighterColors.BRACKETS)
	val MODIFIER = TextAttributesKey.createTextAttributesKey("MODIFIER", DefaultLanguageHighlighterColors.KEYWORD)
	val IDENTIFIER = TextAttributesKey.createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE)
}
