package org.nextras.orm.intellij.typeProvider

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeSignatureKey
import org.nextras.orm.intellij.utils.OrmUtils
import org.nextras.orm.intellij.utils.PhpIndexUtils

class CollectionTypeProvider : PhpTypeProvider4 {
	override fun getKey(): Char {
		return '\u0241'
	}

	override fun getType(element: PsiElement): PhpType? {
		if (element !is MethodReference || element.classReference == null) {
			return null
		}

		val methodName = element.name
		if (!allMethods.contains(methodName)) {
			return null
		}

		val isPluralMethod = pluralMethods.contains(methodName)
		val arraySuffix = if (isPluralMethod) "[]" else ""

		@Suppress("MoveVariableDeclarationIntoWhen")
		val parent = element.classReference!!
		when (parent) {
			is MethodReference -> {
				val type = PhpType()
				parent.type.types
					.filter { it.startsWith("#$key") } // we propagate further only our signature
					.forEach { subType ->
						if (!isPluralMethod) {
							type.add(subType.removeSuffix("[]"))
						} else {
							type.add(subType)
						}
					}
				if (type.isEmpty) {
					parent.type.types.forEach { subType ->
						type.add("#$key$subType")
					}
				}
				return type
			}
			is FieldReference -> {
				if (!parent.resolveLocalType().isEmpty) {
					return PhpType().add("#$key${parent.signature}$arraySuffix")
				}
				// allowed type -> general processing
			}
			is Variable -> {
				// allowed type -> general processing
			}
			else -> {
				return null
			}
		}

		val type = PhpType()
		parent.type.types
			.forEach { subType ->
				if (subType.startsWith("#$key")) {
					if (!isPluralMethod) {
						type.add(subType.removeSuffix("[]"))
					} else {
						type.add(subType)
					}
				} else {
					type.add("#$key${subType.removeSuffix("[]")}$arraySuffix")
				}
			}
		return type
	}

	override fun complete(expression: String, project: Project): PhpType? {
		return null
	}

	override fun getBySignature(expression: String, visited: Set<String>, depth: Int, project: Project): Collection<PhpNamedElement>? {
		if (expression.endsWith("[]")) {
			return null
		}

		val index = PhpIndex.getInstance(project)
		val classTypes = PhpIndexUtils.getByType(PhpType().add(expression), index)
		if (classTypes.isEmpty()) {
			return null
		}

		// $orm->books->getById()
		val repoClassesList = classTypes.filter { OrmUtils.OrmClass.REPOSITORY.`is`(it, index) }
		if (repoClassesList.isNotEmpty()) {
			return OrmUtils.findRepositoryEntities(repoClassesList);
		}

		// $books->tags->toCollection()->getById()
		val hasHasManyType = classTypes.any { OrmUtils.OrmClass.HAS_MANY.`is`(it, index) }
		if (hasHasManyType && expression.startsWith("#")) {
			val arrayElementType = PhpType().add(PhpTypeSignatureKey.ARRAY_ELEMENT.sign(expression))
			return PhpIndexUtils.getByType(arrayElementType, index, visited, depth)
		}

		// $myCollection->getById()
		val entityClassesList = classTypes.filter { OrmUtils.OrmClass.ENTITY.`is`(it, index) }
		if (entityClassesList.isNotEmpty()) {
			return entityClassesList
		}

		return null
	}

	companion object {
		private val relationshipMethods = setOf("get", "toCollection")
		private val collectionPluralMethods = setOf("findBy", "orderBy", "limitBy", "fetchAll", "findAll", "findById")
		private val collectionSingularMethods = setOf("fetch", "getBy", "getById", "getByChecked", "getByIdChecked")
		private val collectionMethods = collectionPluralMethods + collectionSingularMethods
		private val pluralMethods = relationshipMethods + collectionPluralMethods
		private val allMethods = relationshipMethods + collectionMethods
	}
}
