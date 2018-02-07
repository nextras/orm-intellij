package org.nextras.orm.intellij.typeProvider

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3

class PersistenceMethodsTypeProvider : PhpTypeProvider3 {
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
        val className = element.classReference!!.name!!.toLowerCase()

        //cannot access index here, so just dummy check
        if (!className.endsWith("model") && !className.endsWith("repository") && !className.endsWith("repositorycontainer")) {
            return null
        }
        if (element.parameters.size == 0) {
            return null
        }
        return if (element.parameters[0] !is PhpTypedElement) {
            null
        } else (element.parameters[0] as PhpTypedElement).type
    }

    override fun getBySignature(expression: String, visited: Set<String>, depth: Int, project: Project): Collection<PhpNamedElement>? {
        return null
    }
}
