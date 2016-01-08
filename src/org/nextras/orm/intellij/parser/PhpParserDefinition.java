package org.nextras.orm.intellij.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.documentation.phpdoc.parser.PhpDocParser;
import com.jetbrains.php.lang.documentation.phpdoc.parser.tags.PhpDocTagParserRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PhpParserDefinition extends com.jetbrains.php.lang.parser.PhpParserDefinition
{
	public PhpParserDefinition()
	{
		super();
		new PhpDocParser(); //initialize phpdoc parser registry
		PhpDocTagParserRegistry.register("@property", new PhpDocPropertyTagParser());
		PhpDocTagParserRegistry.register("@property-read", new PhpDocPropertyTagParser());
	}


	@Nullable
	private ASTNode getFirstArgument(ASTNode tagNode)
	{
		boolean tagNameFound = false;
		ASTNode token = tagNode.getFirstChildNode();
		token = token.getTreeNext(); // skip opening bracket
		while (token != null) {
			if (!token.getText().trim().equals("")) {
				if (tagNameFound) {
					return token;
				}
				tagNameFound = true;
			}

			token = token.getTreeNext();
		}
		return null;
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
		} else if (type == PhpDocTypes.phpDocTagModifierIdentifier) {
			if (node.equals(getFirstArgument(node.getTreeParent()))) {
				String tagName = node.getTreeParent().getFirstChildNode().getTreeNext().getText().trim();
				if (tagName.equals("container")) {
					return new PhpDocTagModifierClassType(node);
				} else if (3 <= tagName.length() && tagName.length() <= 4 && tagName.indexOf(':') == 1) { // m:n, 1:x, 1:1d …
					return new PhpDocTagModifierClassType(node);
				}
			}
			return new PhpDocTagModifierIdentifier(node);
		}
		return super.createElement(node);
	}
}
