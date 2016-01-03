package org.nextras.orm.intellij.parser;

import com.intellij.lang.ASTNode;
import com.jetbrains.php.lang.documentation.phpdoc.psi.impl.tags.PhpDocParamTagImpl;


public class PhpDocTagModifierParameterValue extends PhpDocParamTagImpl
{
	public PhpDocTagModifierParameterValue(ASTNode node)
	{
		super(node);
	}
}