package org.nextras.orm.intellij.annotator.highlighter;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;


public class ModifierHighlighter
{
	public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("BRACES", DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey MODIFIER = TextAttributesKey.createTextAttributesKey("MODIFIER", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey PARAMETER_NAME = TextAttributesKey.createTextAttributesKey("PARAMETER", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
	public static final TextAttributesKey PARAMETER_VALUE = TextAttributesKey.createTextAttributesKey("PARAMETER_VALUE", DefaultLanguageHighlighterColors.STRING);
}
