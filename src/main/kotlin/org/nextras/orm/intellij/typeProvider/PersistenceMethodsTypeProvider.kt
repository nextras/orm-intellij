package org.nextras.orm.intellij.typeProvider

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4

class PersistenceMethodsTypeProvider : PhpTypeProvider4 {
	override fun getKey(): Char {
		return '\u0240'
	}

	override fun getType(element: PsiElement): PhpType? {
		if (element !is MethodReference) {
			return null
		}
		if (element.name != "persist" && element.name != "persistAndFlush") {
			return null
		}

		//cannot access index here, so just dummy check
		val className = element.classReference!!.name!!.lowercase()
		if (!className.endsWith("model") && !className.endsWith("repository") && !className.endsWith("repositorycontainer")) {
			return null
		}

		if (element.parameters.isEmpty()) {
			return null
		}

		return when {
			element.parameters[0] !is PhpTypedElement -> null
			else -> (element.parameters[0] as PhpTypedElement).type
		}
	}

	override fun complete(expression: String, project: Project): PhpType? {
		return null
	}

	override fun getBySignature(expression: String, visited: Set<String>, depth: Int, project: Project): Collection<PhpNamedElement>? {
		return null
	}
}
