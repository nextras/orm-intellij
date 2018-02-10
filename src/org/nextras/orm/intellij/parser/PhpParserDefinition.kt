package org.nextras.orm.intellij.parser

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.documentation.phpdoc.parser.PhpDocParser
import com.jetbrains.php.lang.documentation.phpdoc.parser.tags.PhpDocTagParserRegistry

class PhpParserDefinition : com.jetbrains.php.lang.parser.PhpParserDefinition() {
	init {
		PhpDocParser() //initialize phpdoc parser registry
		PhpDocTagParserRegistry.register("@property", PhpDocPropertyTagParser())
		PhpDocTagParserRegistry.register("@property-read", PhpDocPropertyTagParser())
	}

	override fun createElement(node: ASTNode): PsiElement {
		return when (node.elementType) {
			PhpDocTypes.phpDocTagModifier -> PhpDocTagModifier(node)
			PhpDocTypes.phpDocTagModifierName -> PhpDocTagModifierName(node)
			PhpDocTypes.phpDocTagModifierIdentifier -> {
				if (node == getFirstArgument(node.treeParent)) {
					val tagName = node.treeParent.firstChildNode.treeNext.text.trim { it <= ' ' }
					if (tagName == "container") {
						return PhpDocTagModifierClassType(node)
					} else if (tagName.length in 3..4 && tagName.indexOf(':') == 1) { // m:n, 1:x, 1:1d …
						return PhpDocTagModifierClassType(node)
					}
				}

				return PhpDocTagModifierIdentifier(node)
			}
			else -> super.createElement(node)
		}
	}

	private fun getFirstArgument(tagNode: ASTNode): ASTNode? {
		var tagNameFound = false
		var token: ASTNode? = tagNode.firstChildNode
		token = token!!.treeNext // skip opening bracket
		while (token != null) {
			if (token.text.trim { it <= ' ' } != "") {
				if (tagNameFound) {
					return token
				}
				tagNameFound = true
			}

			token = token.treeNext
		}
		return null
	}
}
