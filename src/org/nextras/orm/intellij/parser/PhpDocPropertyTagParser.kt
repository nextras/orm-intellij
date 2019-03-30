package org.nextras.orm.intellij.parser

import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes
import com.jetbrains.php.lang.documentation.phpdoc.parser.PhpDocElementTypes
import com.jetbrains.php.lang.parser.PhpPsiBuilder

class PhpDocPropertyTagParser : com.jetbrains.php.lang.documentation.phpdoc.parser.tags.PhpDocPropertyTagParser() {
	override fun parse(builder: PhpPsiBuilder, inside: Boolean) {
		val tag = builder.mark()
		builder.match(PhpDocTokenTypes.DOC_TAG_NAME)
		this.parseContents(builder)
		if (!inside) {
			parseValue(builder)
		}

		tag.done(this.elementType)
	}

	companion object {
		private fun parseValue(builder: PhpPsiBuilder) {
			val value = builder.mark()

			while (true) {
				while (!builder.compare(PhpDocTokenTypes.DOC_LBRACE) && !builder.compare(PhpDocTokenTypes.DOC_TAG_VALUE_END) && !builder.eof()) {
					builder.advanceLexer()
				}

				if (builder.compare(PhpDocTokenTypes.DOC_LBRACE)) {
					val modifier = builder.mark()
					builder.advanceLexer()
					parseModifierContent(builder)
					if (builder.compare(PhpDocTokenTypes.DOC_RBRACE)) {
						builder.advanceLexer()
					}
					modifier.done(PhpDocTypes.phpDocTagModifier)
				} else {
					break
				}
			}

			// eat the rest
			while (!builder.compare(PhpDocTokenTypes.DOC_TAG_VALUE_END) && !builder.eof()) {
				builder.advanceLexer()
			}

			value.done(PhpDocElementTypes.phpDocTagValue)
		}

		private fun parseModifierContent(builder: PhpPsiBuilder) {
			val modifierName = builder.mark()
			while (!builder.compare(PhpDocTokenTypes.DOC_RBRACE) && !builder.compare(PhpDocTokenTypes.DOC_TAG_VALUE_END) && !builder.eof()) {
				if (builder.rawLookup(1) == PhpDocTokenTypes.DOC_WHITESPACE) {
					builder.advanceLexer()
					break
				}
				builder.advanceLexer()
			}
			modifierName.done(PhpDocTypes.phpDocTagModifierName)

			while (!builder.compare(PhpDocTokenTypes.DOC_RBRACE) && !builder.compare(PhpDocTokenTypes.DOC_TAG_VALUE_END) && !builder.eof()) {
				if (builder.compare(PhpDocTokenTypes.DOC_IDENTIFIER) || builder.compare(PhpDocTokenTypes.DOC_STRING) || builder.compare(PhpDocTokenTypes.DOC_VARIABLE)) {
					val modifierKey = builder.mark()
					builder.advanceLexer()
					modifierKey.done(PhpDocTypes.phpDocTagModifierIdentifier)
				} else {
					builder.advanceLexer()
				}
			}
		}
	}
}
