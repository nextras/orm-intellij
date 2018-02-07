package org.nextras.orm.intellij.marker

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import org.nextras.orm.intellij.utils.OrmUtils

import java.util.ArrayList
import java.util.HashSet

class RepositoryMapperMethodMarkerProvider : RelatedItemLineMarkerProvider() {


	override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
		if (element !is PhpDocMethod) {
			return
		}
		val containingClass = element.containingClass
		val index = PhpIndex.getInstance(element.project)
		if (!OrmUtils.OrmClass.REPOSITORY.`is`(containingClass!!, index)) {
			return
		}
		if (baseMethods.contains(element.name)) {
			return
		}
		val repositoryClass = containingClass.fqn
		val mapperClass = repositoryClass.substring(0, repositoryClass.length - 10) + "Mapper"


		val phpIndex = PhpIndex.getInstance(element.getProject())
		val methods = ArrayList<Method>()
		for (cls in phpIndex.getClassesByFQN(mapperClass)) {
			if (!OrmUtils.OrmClass.MAPPER.`is`(cls, phpIndex)) {
				continue
			}
			val mapperMethod = cls.findMethodByName(element.name) ?: continue
			methods.add(mapperMethod)
		}
		result.add(NavigationGutterIconBuilder.create(PhpIcons.METHOD)
			.setTargets(methods)
			.setTooltipText("Navigate to mapper method")
			.createLineMarkerInfo(element)
		)

	}

	companion object {

		private val baseMethods = HashSet<String>()

		init {
			baseMethods.add("findAll")
			baseMethods.add("findBy")
			baseMethods.add("findById")
			baseMethods.add("getBy")
			baseMethods.add("getById")
		}
	}


}
