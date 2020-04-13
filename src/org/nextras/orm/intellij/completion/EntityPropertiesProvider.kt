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
	private val relationshipRegexp = ".*\\{(?:m:m|m:1|1:m|1:1).+".toRegex()

	fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
		val element = parameters.position.parent

		val parameterList = PsiTreeUtil.getParentOfType(element, ParameterList::class.java) ?: return
		if ((element.parent as? PhpPsiElement)?.prevPsiSibling?.node?.elementType === PhpElementTypes.ARRAY_KEY) {
			return
		}

		val context = parameterList.context as? MethodReference ?: return
		val method = context.resolve() as? Method ?: return

		if (!(
				method.name == "findBy"
					|| method.name == "getBy"
					|| method.name == "getByChecked"
					|| method.name == "orderBy"
				)
		) {
			return
		}

		val project = parameters.editor.project ?: return
		val fieldExpression = parameters.originalPosition!!.text
		val fieldExpressionLen = parameters.editor.caretModel.offset - parameters.originalPosition!!.textOffset
		val expression = fieldExpression.substring(0, fieldExpressionLen)

		val isV3 = OrmUtils.isV3(project)
		val (sourceCls, path) = OrmUtils.parsePathExpression(expression, isV3)
		val classSuffix = when (isV3) {
			true -> "->"
			false -> "::"
		}

		val queriedEntities = OrmUtils.findQueriedEntities(context, sourceCls, path)
		queriedEntities
			.filter { it.docComment != null }
			.forEach { cls ->
				var strPathPrefix = (sourceCls?.let { it + classSuffix } ?: "") +
					path.dropLast(1).joinToString("->")
				if (path.size > 1) {
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

					val isRelationship = phpDocPropertyTag.text.matches(relationshipRegexp)
					val sep = if (isRelationship) "->" else ""
					val fieldName = phpDocPropertyTag.property!!.text.substring(1)
					val strPath = strPathPrefix + fieldName

					result.addElement(
						LookupElementBuilder.create(strPath + sep)
							.withPresentableText(fieldName)
							.withIcon(PhpIcons.FIELD_ICON)
							.withTypeText(types.joinToString("|"))
					)
				}

				if (isV3 && path.size == 1 && sourceCls == null) {
					result.addElement(
						LookupElementBuilder.create("this->")
							.withPresentableText("this")
							.withIcon(PhpIcons.CLASS_ICON)
							.withTypeText(cls.type.toString())
					)
				}
				if (path.size == 1 && sourceCls == null)
					result.addElement(
						LookupElementBuilder.create(cls.fqn + classSuffix)
							.withPresentableText(cls.fqn)
							.withIcon(PhpIcons.CLASS_ICON)
							.withTypeText(cls.type.toString())
					)
			}
	}
}
