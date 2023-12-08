package org.nextras.orm.intellij.utils

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.MemberReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.resolve.types.PhpType

object OrmUtils {
	enum class OrmClass constructor(private val className: String) {
		COLLECTION("\\Nextras\\Orm\\Collection\\ICollection"),
		MAPPER("\\Nextras\\Orm\\Mapper\\IMapper"),
		REPOSITORY("\\Nextras\\Orm\\Repository\\IRepository"),
		ENTITY("\\Nextras\\Orm\\Entity\\IEntity"),
		HAS_MANY("\\Nextras\\Orm\\Relationships\\HasMany"),
		EMBEDDABLE("\\Nextras\\Orm\\Entity\\Embeddable\\IEmbeddable");

		fun `is`(cls: PhpClass, index: PhpIndex): Boolean {
			val classes = index.getAnyByFQN(className)
			val instanceOf = when {
				classes.isEmpty() -> null
				else -> classes.iterator().next()
			} ?: return false
			return instanceOf.type.isConvertibleFrom(cls.type, index)
		}
	}

	fun findRepositoryEntities(repositories: Collection<PhpClass>): Collection<PhpClass> {
		val entities = HashSet<PhpClass>(1)
		for (repositoryClass in repositories) {
			val entityNamesMethod = repositoryClass.findMethodByName("getEntityClassNames") ?: return emptyList()
			if (entityNamesMethod.lastChild !is GroupStatement) {
				continue
			}
			if ((entityNamesMethod.lastChild as GroupStatement).firstPsiChild !is PhpReturn) {
				continue
			}
			if ((entityNamesMethod.lastChild as GroupStatement).firstPsiChild!!.firstPsiChild !is ArrayCreationExpression) {
				continue
			}

			val arr =
				(entityNamesMethod.lastChild as GroupStatement).firstPsiChild!!.firstPsiChild as ArrayCreationExpression?
			val phpIndex = PhpIndex.getInstance(repositoryClass.project)
			for (el in arr!!.children) {
				if (el.firstChild !is ClassConstantReference) {
					continue
				}
				val ref = el.firstChild as ClassConstantReference
				if (ref.name != "class") {
					continue
				}
				entities.addAll(PhpIndexUtils.getByType(ref.classReference!!.type, phpIndex))
			}
		}
		return entities
	}

	fun findQueriedEntities(ref: MemberReference): Collection<PhpClass> {
		val classReference = ref.classReference ?: return emptyList()
		val entities = HashSet<PhpClass>()
		val phpIndex = PhpIndex.getInstance(ref.project)
		val classes = PhpIndexUtils.getByType(classReference.type, phpIndex)

		val repositories = classes.filter { cls -> OrmClass.REPOSITORY.`is`(cls, phpIndex) }
		if (repositories.isNotEmpty()) {
			entities.addAll(findRepositoryEntities(repositories))
		}

		for (type in classReference.type.types) {
			if (!type.endsWith("[]")) {
				continue
			}
			val typeWithoutArray = PhpType().add(type.substring(0, type.length - 2))
			val maybeEntities = PhpIndexUtils.getByType(typeWithoutArray, phpIndex)
			entities.addAll(
				maybeEntities.filter { cls ->
					OrmClass.ENTITY.`is`(cls, phpIndex)
				}
			)
		}

		return entities
	}

	fun findQueriedEntities(reference: MethodReference, cls: String?, path: Array<String>): Collection<PhpClass> {
		val rootEntities = if (cls == null || cls == "this") {
			findQueriedEntities(reference)
		} else {
			val index = PhpIndex.getInstance(reference.project)
			PhpIndexUtils.getByType(PhpType().add(cls), index)
		}

		if (rootEntities.isEmpty()) {
			return emptyList()
		}

		return when {
			path.size <= 1 -> rootEntities
			else -> findTargetEntities(rootEntities, path, 0)
		}
	}

	fun parsePathExpression(expression: String): Pair<String?, Array<String>> {
		val delimiterPos = expression.indexOf("::")
		val sourceClass = expression.substring(0, delimiterPos.coerceAtLeast(0))
		val path =
			if (delimiterPos == -1) expression else expression.substring((delimiterPos + 2).coerceAtMost(expression.length))
		return Pair(
			sourceClass.ifEmpty { null },
			path.split("->").toTypedArray()
		)
	}

	private fun findTargetEntities(
		currentEntities: Collection<PhpClass>,
		path: Array<String>,
		pos: Int
	): Collection<PhpClass> {
		if (path.size == pos + 1) {
			return currentEntities
		}
		val entities = HashSet<PhpClass>()
		for (cls in currentEntities) {
			val field = cls.findFieldByName(path[pos], false) as? PhpDocProperty ?: continue
			addEntitiesFromField(entities, field)
		}
		return findTargetEntities(entities, path, pos + 1)
	}

	private fun addEntitiesFromField(entities: MutableCollection<PhpClass>, field: PhpDocProperty) {
		val index = PhpIndex.getInstance(field.project)
		for (type in field.type.types) {
			if (type.contains("Nextras\\Orm\\Relationship")) {
				continue
			}
			val addType = if (type.endsWith("[]")) {
				type.substring(0, type.length - 2)
			} else {
				type
			}
			for (entityCls in PhpIndexUtils.getByType(PhpType().add(addType), index)) {
				if (!OrmClass.ENTITY.`is`(entityCls, index) && !OrmClass.EMBEDDABLE.`is`(entityCls, index)) {
					continue
				}
				entities.add(entityCls)
			}
		}
	}
}
