package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocPropertyTag
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.*
import org.nextras.orm.intellij.utils.OrmUtils

import java.util.Arrays
import java.util.stream.Collectors
import java.util.stream.Stream


class EntityPropertiesProvider {
	fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
		val element = parameters.position.parent

		val parameterList = PsiTreeUtil.getParentOfType(element, ParameterList::class.java) ?: return
		if (element.parent is PhpPsiElement
			&& (element.parent as PhpPsiElement).prevPsiSibling != null
			&& (element.parent as PhpPsiElement).prevPsiSibling!!.node.elementType === PhpElementTypes.ARRAY_KEY) {
			return
		}
		val context = parameterList.context as? MethodReference ?: return
		val methodEl = context.resolve()
		if (methodEl == null || methodEl !is Method) {
			return
		}
		val method = methodEl as Method?
		if (!(method!!.name == "findBy" || method.name == "getBy" || method.name == "orderBy")) {
			return
		}

		val project = parameters.editor.project ?: return


		val fieldExpression = parameters.originalPosition!!.text
		val path = fieldExpression.split("->".toRegex()).toTypedArray()

		val queriedEntities = OrmUtils.findQueriedEntities(context, path)
		for (cls in queriedEntities) {

			if (cls.docComment == null) {
				continue
			}
			for (phpDocPropertyTag in cls.docComment!!.propertyTags) {

				val types = phpDocPropertyTag.type.typesSorted
					.filter { s -> !s.contains("Nextras\\Orm\\Relationships") && s != "?" }
					.map { s -> if (s.startsWith("\\")) s.substring(1) else s }

				var strPath = Arrays.copyOfRange(path, 0, path.size - 1).joinToString("->")
				if (strPath.length > 0) {
					strPath += "->"
				}
				val fieldName = phpDocPropertyTag.property!!.text.substring(1)
				strPath += fieldName

				result.addElement(LookupElementBuilder.create(strPath)
					.withPresentableText(fieldName)
					.withTypeText(types.joinToString("|")))
			}
			if (path.size == 1) {
				result.addElement(LookupElementBuilder.create("this").withTypeText(cls.type.toString()))
				result.addElement(LookupElementBuilder.create(cls.fqn).withTypeText(cls.type.toString()))
			}
		}
	}
}
