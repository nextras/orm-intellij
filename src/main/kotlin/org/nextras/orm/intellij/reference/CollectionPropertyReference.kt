package org.nextras.orm.intellij.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import org.nextras.orm.intellij.utils.OrmUtils

/**
 * Makes property reference in methods like `$collection->findBy(['title' => 'value'])`. Supports traversing
 * on the path. This makes it clickable to the property definition.
 */
class CollectionPropertyReference constructor(
	element: StringLiteralExpression,
	private val methodReference: MethodReference,
	private val sourceCls: String?,
	path: Array<String>,
	private val pos: Int
) : PsiPolyVariantReferenceBase<StringLiteralExpression>(element) {
	private val path: Array<String> = path.copyOfRange(0, pos + 1)

	init {
		val end = (sourceCls?.let { it.length + 2 } ?: 0) +
			this.path.joinToString("->").length + 1
		rangeInElement = TextRange(end - path[pos].length, end)
	}

	override fun multiResolve(b: Boolean): Array<ResolveResult> {
		val classes = OrmUtils.findQueriedEntities(methodReference, sourceCls, path)
		return classes
			.mapNotNull {
				it.findFieldByName(path[pos], false)
			}
			.filterIsInstance<PhpDocProperty>()
			.toSet()
			.map {
				object : ResolveResult {
					override fun getElement(): PsiElement {
						return it
					}

					override fun isValidResult(): Boolean {
						return true
					}
				}
			}.toTypedArray()
	}

	override fun getVariants(): Array<Any> {
		return emptyArray()
	}
}
