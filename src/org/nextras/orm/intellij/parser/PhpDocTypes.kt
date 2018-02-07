package org.nextras.orm.intellij.parser

import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocElementType


interface PhpDocTypes {
	companion object {
		val phpDocTagModifier = PhpDocElementType("PhpDocTagModifier")
		val phpDocTagModifierName = PhpDocElementType("PhpDocTagModifierName")
		val phpDocTagModifierIdentifier = PhpDocElementType("PhpDocTagModifierIdentifier")
	}
}
