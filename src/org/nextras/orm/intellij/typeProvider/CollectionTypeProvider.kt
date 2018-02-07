package org.nextras.orm.intellij.typeProvider

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeSignatureKey
import org.nextras.orm.intellij.utils.OrmUtils
import org.nextras.orm.intellij.utils.PhpIndexUtils

import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

class CollectionTypeProvider : PhpTypeProvider3 {
    override fun getKey(): Char {
        return '\u0241'
    }

    override fun getType(element: PsiElement): PhpType? {
        if (element !is MethodReference) {
            return null
        }
        if (element.classReference == null) {
            return null
        }
        if (!pluralMethods.contains(element.name) && !singularMethods.contains(element.name)) {
            return null
        }
        val type = element.classReference!!.type
        val resultType = PhpType()
        val arraySuffix = if (pluralMethods.contains(element.name)) "[]" else ""
        for (typeStr in type.filterMixed().filterNull().filterPrimitives().types) {
            if (typeStr.startsWith("#V")) {
                continue
            }
            resultType.add("#" + key + typeStr + "." + element.name + arraySuffix)
        }
        return resultType
    }

    override fun getBySignature(expression: String, visited: Set<String>, depth: Int, project: Project): Collection<PhpNamedElement> {
        if (expression.endsWith("[]")) {
            return emptyList()
        }
        val result = HashSet<PhpNamedElement>()
        val pos = expression.lastIndexOf(".")
        var refSig = expression.substring(0, pos)
        if (refSig.endsWith("[]")) {
            refSig = refSig.substring(0, refSig.length - 2)
        }
        var methodName = expression.substring(pos + 1)
        if (methodName.endsWith("[]")) {
            methodName = methodName.substring(0, methodName.length - 2)
        }
        val index = PhpIndex.getInstance(project)
        val types = PhpIndexUtils.getByType(PhpType().add(refSig), index, visited, depth)

        if (types.stream().anyMatch { cls -> OrmUtils.OrmClass.HAS_MANY.`is`(cls, index) } && methodName == "get" && refSig.startsWith("#")) {
            result.addAll(PhpIndexUtils.getByType(PhpType().add(PhpTypeSignatureKey.ARRAY_ELEMENT.sign(refSig)), index, visited, depth))
        } else {
            result.addAll(types.filter { cls -> OrmUtils.OrmClass.ENTITY.`is`(cls, index) })
            val repoClassesList = types.filter { cls -> OrmUtils.OrmClass.REPOSITORY.`is`(cls, index) }
            if (repoClassesList.size > 0) {
                result.addAll(OrmUtils.findRepositoryEntities(repoClassesList))
                result.addAll(types.filter { cls -> OrmUtils.OrmClass.ENTITY.`is`(cls, index) })
            }
        }

        return result
    }

    companion object {
        private val pluralMethods = setOf("findBy", "orderBy", "limitBy", "fetchAll", "findAll", "findById", "get")
        private val singularMethods = setOf("fetch", "getBy", "getById")
    }
}
