package org.nextras.orm.intellij.parser;

import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocElementType;


public interface PhpDocTypes
{
	PhpDocElementType phpDocTagModifier = new PhpDocElementType("PhpDocTagModifier");
	PhpDocElementType phpDocTagModifierName = new PhpDocElementType("PhpDocTagModifierName");
	PhpDocElementType phpDocTagModifierIdentifier = new PhpDocElementType("PhpDocTagModifierIdentifier");
}
