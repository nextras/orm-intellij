package org.nextras.orm.intellij.annotator.highlighter;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;


public class ModifierHighlighter
{
	public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("BRACES", DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey MODIFIER = TextAttributesKey.createTextAttributesKey("MODIFIER", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
}
