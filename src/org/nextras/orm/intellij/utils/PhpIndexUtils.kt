package org.nextras.orm.intellij.utils

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import java.util.*

object PhpIndexUtils {

    fun getByType(type: PhpType, phpIndex: PhpIndex): Collection<PhpClass> {
        return getByType(type, phpIndex, HashSet(), null as Set<String>?, 0)
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

    private fun getBySignature(sig: String, phpIndex: PhpIndex, visited: Set<String>, phpIndexVisited: Set<String>?, phpIndexDepth: Int): Collection<PhpClass> {
        val classes = HashSet<PhpClass>()
        for (el in phpIndex.getBySignature(sig)) {
            classes.addAll(getByType(el.type, phpIndex, visited.toMutableSet(), phpIndexVisited, phpIndexDepth))
        }

        return classes
    }
}
