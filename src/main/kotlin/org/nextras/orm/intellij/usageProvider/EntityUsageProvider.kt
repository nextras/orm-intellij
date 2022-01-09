package org.nextras.orm.intellij.usageProvider

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Method
import org.nextras.orm.intellij.utils.OrmUtils

class EntityUsageProvider : ImplicitUsageProvider {
    override fun isImplicitUsage(element: PsiElement): Boolean {
        val method = element as? Method ?: return false
        if (!method.name.startsWith("getter") && !method.name.startsWith("setter")) return false

        val clazz = method.containingClass ?: return false
        val phpIndex = PhpIndex.getInstance(element.project)
        if (!OrmUtils.OrmClass.ENTITY.`is`(clazz, phpIndex)) return false

        val propertyName = method.name.substring(6)
        val tags = clazz.docComment?.propertyTags ?: return false
        return tags.any { it.property?.text?.substring(1).equals(propertyName, ignoreCase = true) }
    }

    override fun isImplicitRead(element: PsiElement): Boolean = false

    override fun isImplicitWrite(element: PsiElement): Boolean = false
}
