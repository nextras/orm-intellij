package org.nextras.orm.intellij.parser;

import com.intellij.lang.PsiBuilder;
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


	public boolean parseContents(PhpPsiBuilder builder)
	{
		parseTypes(builder);
		parseProperty(builder);
		return true;
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
				while (!builder.compareAndEat(DOC_RBRACE) && !builder.compare(DOC_TAG_VALUE_END) && !builder.eof()) {
					builder.advanceLexer();
				}
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
}
