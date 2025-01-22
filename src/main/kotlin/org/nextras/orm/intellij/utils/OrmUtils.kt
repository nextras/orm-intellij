package org.nextras.orm.intellij.utils

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.MemberReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.resolve.types.PhpType

object OrmUtils {
	enum class OrmClass constructor(val className: String) {
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

	fun findQueriedEntities(ref: MemberReference): Collection<PhpClass> {
		val phpIndex = PhpIndex.getInstance(ref.project)
		val completedType = phpIndex.completeType(ref.project, ref.type, null)
		val types = completedType.typesWithParametrisedParts.mapNotNull { type ->
			if (!type.contains(OrmClass.COLLECTION.className)) return@mapNotNull null
			PhpType.getParametrizedParts(type).firstOrNull()
		}
		return types.flatMap { PhpIndexUtils.getByType(PhpType().add(it), phpIndex) }
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
		val candidateTypes = mutableListOf<String>()
		for (type in field.type.typesWithParametrisedParts) {
			if (type.contains("Nextras\\Orm\\Relationship") && type.contains("<")) {
				candidateTypes.add(type.dropWhile { it != '<' }.removePrefix("<").removeSuffix(">"))
			}
		}
		for (type in field.type.types) {
			if (type.contains("Nextras\\Orm\\Relationship")) {
				continue
			}
			val addType = if (type.endsWith("[]")) {
				type.substring(0, type.length - 2)
			} else {
				type
			}
			candidateTypes.add(addType)
		}
		for (candidateType in candidateTypes) {
			for (entityCls in PhpIndexUtils.getByType(PhpType().add(candidateType), index)) {
				if (!OrmClass.ENTITY.`is`(entityCls, index) && !OrmClass.EMBEDDABLE.`is`(entityCls, index)) {
					continue
				}
				entities.add(entityCls)
			}
		}
	}
}
