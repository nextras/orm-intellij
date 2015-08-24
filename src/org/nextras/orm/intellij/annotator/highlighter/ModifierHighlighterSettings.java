package org.nextras.orm.intellij.annotator.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.jetbrains.php.lang.highlighter.PhpFileSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nextras.orm.intellij.Icons;

import javax.swing.*;
import java.util.Map;


public class ModifierHighlighterSettings implements ColorSettingsPage
{
	private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
			new AttributesDescriptor("Modifier", ModifierHighlighter.MODIFIER),
			new AttributesDescriptor("Brackets", ModifierHighlighter.BRACKETS),
	};


	@Nullable
	@Override
	public Icon getIcon()
	{
		return Icons.FILE;
	}


	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter()
	{
		return new PhpFileSyntaxHighlighter();
	}


	@NotNull
	@Override
	public String getDemoText()
	{
		return "<?php\n// Sadly, there is no way how to preview your color settings in here.";
	}


	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
	{
		return null;
	}


	@NotNull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors()
	{
		return DESCRIPTORS;
	}


	@NotNull
	@Override
	public ColorDescriptor[] getColorDescriptors()
	{
		return ColorDescriptor.EMPTY_ARRAY;
	}


	@NotNull
	@Override
	public String getDisplayName()
	{
		return "Nextras Orm";
	}
}
