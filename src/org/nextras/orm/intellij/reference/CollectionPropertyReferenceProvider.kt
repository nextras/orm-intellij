package org.nextras.orm.intellij.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.*
import java.util.*
import java.util.regex.Pattern

class CollectionPropertyReferenceProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(psiElement: PsiElement, processingContext: ProcessingContext): Array<PsiReference> {
		val ref = getMethodReference(psiElement) ?: return emptyArray()

		val content = (psiElement as StringLiteralExpression).contents
		val matcher = fieldExpression.matcher(content)
		if (!matcher.matches()) {
			return emptyArray()
		}

		val parts = matcher.group(1).split("->").toTypedArray()
		if (parts.isEmpty()) {
			return emptyArray()
		}

		val result = processExpression(psiElement, ref, parts, processingContext.get("field") as String?)
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
				"findBy", "getBy", "getByChecked" -> ref
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
				"findBy", "getBy", "getByChecked" -> ref
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

	private fun processExpression(el: StringLiteralExpression, ref: MethodReference, parts: Array<String>, fieldName: String?): Collection<PsiReference> {
		val result = mutableListOf<PsiReference>()

		if (parts.size > 1 && fieldName == null) {
			result.add(CollectionClassReference(el, ref, parts[0]))
		}

		for (i in (if (parts.size == 1) 0 else 1) until parts.size) {
			if (parts[i] != "" && fieldName == null || fieldName != null && fieldName == parts[i]) {
				result.add(CollectionPropertyReference(el, ref, parts, i))
			}
		}

		return result
	}

	companion object {
		private val fieldExpression = Pattern.compile("^([\\w\\\\]+(?:->\\w*)*)(!|!=|<=|>=|=|>|<)?$")
	}
}
