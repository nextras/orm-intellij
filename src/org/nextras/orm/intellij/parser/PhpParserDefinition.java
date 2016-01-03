package org.nextras.orm.intellij.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.documentation.phpdoc.parser.PhpDocParser;
import com.jetbrains.php.lang.documentation.phpdoc.parser.tags.PhpDocTagParserRegistry;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocType;
import org.jetbrains.annotations.NotNull;


public class PhpParserDefinition extends com.jetbrains.php.lang.parser.PhpParserDefinition
{
	public PhpParserDefinition()
	{
		super();
		new PhpDocParser(); //initialize phpdoc parser registry
		PhpDocTagParserRegistry.register("@property", new PhpDocPropertyTagParser());
		PhpDocTagParserRegistry.register("@property-read", new PhpDocPropertyTagParser());
	}


	@NotNull
	@Override
	public PsiElement createElement(ASTNode node)
	{
		IElementType type = node.getElementType();
		if (type == PhpDocTypes.phpDocTagModifier) {
			return new PhpDocTagModifier(node);
		} else if (type == PhpDocTypes.phpDocTagModifierName) {
			return new PhpDocTagModifierName(node);
		} else if (type == PhpDocTypes.phpDocTagModifierParameterName) {
			return new PhpDocTagModifierParameterName(node);
		} else if (type == PhpDocTypes.phpDocTagModifierParameterValue) {
			return new PhpDocTagModifierParameterValue(node);
		}
		return super.createElement(node);
	}
}
