package org.nextras.orm.intellij.utils

import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.codeInsight.PhpCodeInsightUtil
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import java.util.*

object PhpIndexUtils {
	fun getByType(type: PhpType, phpIndex: PhpIndex): Collection<PhpClass> {
		return getByType(type, phpIndex, HashSet(), null, 0)
	}

	fun getByType(type: PhpType, phpIndex: PhpIndex, phpIndexVisited: Set<String>?, phpIndexDepth: Int): Collection<PhpClass> {
		return getByType(type, phpIndex, HashSet(), phpIndexVisited, phpIndexDepth)
	}

	private fun getByType(type: PhpType, phpIndex: PhpIndex, visited: MutableSet<String>, phpIndexVisited: Set<String>?, phpIndexDepth: Int): Collection<PhpClass> {
		val types = type.types
		return getByType(types.toTypedArray(), phpIndex, visited, phpIndexVisited, phpIndexDepth)
	}

	private fun getByType(types: Array<String>, phpIndex: PhpIndex, visited: MutableSet<String>, phpIndexVisited: Set<String>?, phpIndexDepth: Int): Collection<PhpClass> {
		val classes = HashSet<PhpClass>()
		for (className in types) {
			if (className == "?" || visited.contains(className)) {
				//do nothing
			} else if (className.startsWith("#")) {
				visited.add(className)
				classes.addAll(getBySignature(className, phpIndex, visited, phpIndexVisited, phpIndexDepth))
			} else {
				classes.addAll(phpIndex.getAnyByFQN(className))
			}
		}
		return classes
	}

	private fun getBySignature(sig: String, phpIndex: PhpIndex, visited: MutableSet<String>, phpIndexVisited: Set<String>?, phpIndexDepth: Int): Collection<PhpClass> {
		val classes = HashSet<PhpClass>()
		for (el in phpIndex.getBySignature(sig)) {
			classes.addAll(getByType(el.type, phpIndex, visited, phpIndexVisited, phpIndexDepth))
		}
		return classes
	}

	fun getFqnForClassNameByContext(psiElement: PsiElement, className: String): String? {
		val scope = PhpCodeInsightUtil.findScopeForUseOperator(psiElement) ?: return null

		val useImports = mutableMapOf<String?, String?>()
		for (phpUseList in PhpCodeInsightUtil.collectImports(scope)) {
			for (phpUse in phpUseList.declarations) {
				val alias = phpUse.aliasName
				if (alias != null) {
					useImports[alias] = phpUse.fqn
				} else {
					useImports[phpUse.name] = phpUse.fqn
				}
			}
		}

		return if (useImports.containsKey(className)) {
			useImports[className]
		} else {
			val namespace = scope as? PhpNamespace ?: return null
			namespace.fqn + "\\" + className
		}
	}
}
