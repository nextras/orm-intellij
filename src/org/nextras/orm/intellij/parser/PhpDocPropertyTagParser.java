package org.nextras.orm.intellij.parser;

import com.intellij.lang.PsiBuilder;
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes;
import com.jetbrains.php.lang.documentation.phpdoc.parser.PhpDocElementTypes;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocType;
import com.jetbrains.php.lang.parser.PhpPsiBuilder;


public class PhpDocPropertyTagParser extends com.jetbrains.php.lang.documentation.phpdoc.parser.tags.PhpDocPropertyTagParser
{
	public void parse(PhpPsiBuilder builder, boolean inside)
	{
		PsiBuilder.Marker tag = builder.mark();
		builder.match(DOC_TAG_NAME);
		this.parseContents(builder);
		if (!inside) {
			parseValue(builder);
		}

		tag.done(this.getElementType());
	}


	protected static void parseValue(PhpPsiBuilder builder)
	{
		PsiBuilder.Marker value = builder.mark();

		while (true) {
			while (!builder.compare(DOC_LBRACE) && !builder.compare(DOC_TAG_VALUE_END) && !builder.eof()) {
				builder.advanceLexer();
			}

			if (builder.compare(DOC_LBRACE)) {
				PsiBuilder.Marker modifier = builder.mark();
				builder.advanceLexer();
				parseModifierContent(builder);
				modifier.done(PhpDocTypes.phpDocTagModifier);
			} else {
				break;
			}
		}

		// eat the rest
		while (!builder.compare(DOC_TAG_VALUE_END) && !builder.eof()) {
			builder.advanceLexer();
		}

		value.done(phpDocTagValue);
	}


	private static void parseModifierContent(PhpPsiBuilder builder)
	{
		PsiBuilder.Marker modifierName = builder.mark();
		while (!builder.compare(DOC_RBRACE) && !builder.compare(DOC_TAG_VALUE_END) && !builder.eof() && !(builder.getTokenText() != null && builder.getTokenText().equals(" "))) {
			builder.advanceLexer();
		}
		modifierName.done(PhpDocTypes.phpDocTagModifierName);

		PsiBuilder.Marker modifierKey;
		while (true) {
			if (builder.eof() || builder.compare(DOC_TAG_VALUE_END) || builder.compareAndEat(DOC_RBRACE)) {
				break;
			} else if (builder.compare(DOC_IDENTIFIER) || builder.compare(DOC_STRING)) {
				modifierKey = builder.mark();
				builder.advanceLexer();
				modifierKey.done(PhpDocTypes.phpDocTagModifierIdentifier);
			} else {
				builder.advanceLexer();
			}
		}
	}
}
