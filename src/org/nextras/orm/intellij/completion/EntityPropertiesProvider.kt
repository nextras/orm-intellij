package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import org.nextras.orm.intellij.utils.OrmUtils

class EntityPropertiesProvider {
	fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
		val element = parameters.position.parent

		val parameterList = PsiTreeUtil.getParentOfType(element, ParameterList::class.java) ?: return
		if ((element.parent as? PhpPsiElement)?.prevPsiSibling?.node?.elementType === PhpElementTypes.ARRAY_KEY) {
			return
		}

		val context = parameterList.context as? MethodReference ?: return
		val method = context.resolve() as? Method ?: return

		if (!(method.name == "findBy" || method.name == "getBy" || method.name == "orderBy")) {
			return
		}

		parameters.editor.project ?: return

		val fieldExpression = parameters.originalPosition!!.text
		val path = fieldExpression.split("->").toTypedArray()

		val queriedEntities = OrmUtils.findQueriedEntities(context, path)
		queriedEntities
			.filter { it.docComment != null }
			.forEach { cls ->
				var strPathPrefix = path.dropLast(1).joinToString("->")
				if (strPathPrefix.isNotEmpty()) {
					strPathPrefix += "->"
				}

				for (phpDocPropertyTag in cls.docComment!!.propertyTags) {
					val types = phpDocPropertyTag.type.typesSorted
						.filter { !it.contains("Nextras\\Orm\\Relationships") && it != "?" }
						.map {
							when (it.startsWith("\\")) {
								true -> it.substring(1)
								false -> it
							}
						}

					val fieldName = phpDocPropertyTag.property!!.text.substring(1)
					val strPath = strPathPrefix + fieldName

					result.addElement(
						LookupElementBuilder.create(strPath)
							.withIcon(PhpIcons.FIELD_ICON)
							.withPresentableText(fieldName)
							.withTypeText(types.joinToString("|"))
					)
				}

				if (path.size == 1) {
					result.addElement(
						LookupElementBuilder.create("this")
							.withIcon(PhpIcons.CLASS_ICON)
							.withTypeText(cls.type.toString())
					)
					result.addElement(
						LookupElementBuilder.create(cls.fqn)
							.withIcon(PhpIcons.CLASS_ICON)
							.withTypeText(cls.type.toString())
					)
				}
			}
	}
}
