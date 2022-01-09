package org.nextras.orm.intellij.usageProvider

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Method
import org.nextras.orm.intellij.utils.OrmUtils

class EntityUsageProvider : ImplicitUsageProvider {
    companion object {
        private val CACHED_KEY: Key<CachedValue<Boolean>> = Key.create("nextras.orm.unused.reference.key")
    }

    override fun isImplicitUsage(element: PsiElement): Boolean =
        CachedValuesManager.getCachedValue(element, CACHED_KEY) {
            val used = isGetterSetter(element)
            CachedValueProvider.Result.create(used, PsiModificationTracker.MODIFICATION_COUNT)
        }

    override fun isImplicitRead(element: PsiElement): Boolean = false

    override fun isImplicitWrite(element: PsiElement): Boolean = false

    private fun isGetterSetter(element: PsiElement): Boolean {
        val method = element as? Method ?: return false
        if (!method.name.startsWith("getter") && !method.name.startsWith("setter")) return false

        val clazz = method.containingClass ?: return false
        val phpIndex = PhpIndex.getInstance(element.project)
        if (!OrmUtils.OrmClass.ENTITY.`is`(clazz, phpIndex)) return false

        val propertyName = method.name.substring(6)
        val tags = clazz.docComment?.propertyTags ?: return false

        return tags.any { it.property?.text?.substring(1).equals(propertyName, ignoreCase = true) }
    }
}
