package org.nextras.orm.intellij.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.*
import org.nextras.orm.intellij.utils.OrmUtils
import java.util.regex.Pattern

class CollectionPropertyReferenceProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(psiElement: PsiElement, processingContext: ProcessingContext): Array<PsiReference> {
		val ref = getMethodReference(psiElement) ?: return emptyArray()

		val content = (psiElement as StringLiteralExpression).contents
		val isV3 = OrmUtils.isV3(psiElement.project)
		val matcher = when (isV3) {
			true -> fieldExpressionV3.matcher(content)
			false -> fieldExpressionV4.matcher(content)
		}
		if (!matcher.matches()) {
			return emptyArray()
		}

		val (sourceCls, path) = OrmUtils.parsePathExpression(matcher.group(1), isV3)
		if (sourceCls == null && path.isEmpty()) {
			return emptyArray()
		}

		val result = processExpression(
			el = psiElement,
			ref = ref,
			sourceCls = sourceCls,
			path = path,
			fieldName = processingContext.get("field") as String?
		)
		return result.toTypedArray()
	}

	private fun getMethodReference(el: PsiElement): MethodReference? {
		// incomplete array
		if (el.parent.node.elementType === PhpElementTypes.ARRAY_VALUE
			&& (el.parent as PhpPsiElement).prevPsiSibling == null
			&& el.parent.parent is ArrayCreationExpression
			&& el.parent.parent.parent is ParameterList
			&& el.parent.parent.parent.parent is MethodReference
		) {
			val ref = el.parent.parent.parent.parent as MethodReference
			return when (ref.name) {
				"findBy", "getBy", "getByChecked", "orderBy" -> ref
				else -> null
			}
		}

		if (el.parent.node.elementType === PhpElementTypes.ARRAY_KEY
			&& el.parent.parent is ArrayHashElement
			&& el.parent.parent.parent is ArrayCreationExpression
			&& el.parent.parent.parent.parent is ParameterList
			&& el.parent.parent.parent.parent.parent is MethodReference
		) {
			val ref = el.parent.parent.parent.parent.parent as MethodReference
			return when (ref.name) {
				"findBy", "getBy", "getByChecked", "orderBy" -> ref
				else -> null
			}
		}

		if (el.parent is ParameterList && el.parent.parent is MethodReference) {
			val ref = el.parent.parent as MethodReference
			if (ref.name == "orderBy") {
				return ref
			}
		}

		return null
	}

	private fun processExpression(
		el: StringLiteralExpression,
		ref: MethodReference,
		sourceCls: String?,
		path: Array<String>,
		fieldName: String?
	): Collection<PsiReference> {
		val result = mutableListOf<PsiReference>()

		if (sourceCls != null && fieldName == null) {
			result.add(CollectionClassReference(el, ref, sourceCls))
		}

		for (i in path.indices) {
			if ((path[i] != "" && fieldName == null) || (fieldName != null && fieldName == path[i])) {
				result.add(CollectionPropertyReference(el, ref, sourceCls, path, i))
			}
		}

		return result
	}

	companion object {
		private val fieldExpressionV3 = Pattern.compile("^([\\w\\\\]+(?:->\\w*)*)(!|!=|<=|>=|=|>|<)?$")
		private val fieldExpressionV4 = Pattern.compile("^((?:[\\w\\\\]+::)?(\\w*)?(?:->\\w*)*)(!|!=|<=|>=|=|>|<)?$")
	}
}
