package org.nextras.orm.intellij.annotator.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.jetbrains.php.lang.highlighter.PhpFileSyntaxHighlighter
import javax.swing.Icon

class ModifierHighlighterSettings : ColorSettingsPage {
	override fun getIcon(): Icon? {
		return null
	}

	override fun getHighlighter(): SyntaxHighlighter {
		return PhpFileSyntaxHighlighter()
	}

	override fun getDemoText(): String {
		return "<?php\n// IntelliJ IDEs do not support previewing custom highlighter here."
	}

	override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? {
		return null
	}

	override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
		return DESCRIPTORS
	}

	override fun getColorDescriptors(): Array<ColorDescriptor> {
		return ColorDescriptor.EMPTY_ARRAY
	}

	override fun getDisplayName(): String {
		return "Nextras Orm"
	}

	companion object {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor("Modifier braces", ModifierHighlighter.BRACES),
			AttributesDescriptor("Modifier name", ModifierHighlighter.MODIFIER),
			AttributesDescriptor("Modifier identifier", ModifierHighlighter.IDENTIFIER)
		)
	}
}
