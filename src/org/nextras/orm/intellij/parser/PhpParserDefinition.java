package org.nextras.orm.intellij.parser;

import com.jetbrains.php.lang.documentation.phpdoc.parser.PhpDocParser;
import com.jetbrains.php.lang.documentation.phpdoc.parser.tags.PhpDocTagParserRegistry;


public class PhpParserDefinition extends com.jetbrains.php.lang.parser.PhpParserDefinition
{
	public PhpParserDefinition()
	{
		super();
		new PhpDocParser(); //initialize phpdoc parser registry
		PhpDocTagParserRegistry.register("@property", new PhpDocPropertyTagParser());
		PhpDocTagParserRegistry.register("@property-read", new PhpDocPropertyTagParser());
	}
}
