package org.nextras.orm.intellij.utils;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PhpIndexUtils
{

	public static Collection<PhpClass> getByType(PhpType type, PhpIndex phpIndex)
	{
		return getByType(type, phpIndex, new HashSet<String>(), (Set<String>) null, 0);
	}

	public static Collection<PhpClass> getByType(PhpType type, PhpIndex phpIndex, @Nullable Set<String> phpIndexVisited, int phpIndexDepth)
	{
		return getByType(type, phpIndex, new HashSet<String>(), phpIndexVisited, phpIndexDepth);
	}

	private static Collection<PhpClass> getByType(PhpType type, PhpIndex phpIndex, Set<String> visited, @Nullable Set<String> phpIndexVisited, int phpIndexDepth)
	{
		Set<String> types = type.getTypes();
		return getByType(types.toArray(new String[types.size()]), phpIndex, visited, phpIndexVisited, phpIndexDepth);
	}

	private static Collection<PhpClass> getByType(String[] types, PhpIndex phpIndex, Set<String> visited, @Nullable Set<String> phpIndexVisited, int phpIndexDepth)
	{
		Collection<PhpClass> classes = new HashSet<>();
		for (String className : types) {
			if (className.equals("?") || visited.contains(className)) {
				//do nothing
			} else if (className.startsWith("#")) {
				visited.add(className);
				classes.addAll(getBySignature(className, phpIndex, visited, phpIndexVisited, phpIndexDepth));
			} else {
				classes.addAll(phpIndex.getAnyByFQN(className));
			}
		}

		return classes;
	}

	private static Collection<PhpClass> getBySignature(String sig, PhpIndex phpIndex, Set<String> visited, @Nullable Set<String> phpIndexVisited, int phpIndexDepth)
	{
		Collection<PhpClass> classes = new HashSet<>();
		for (PhpNamedElement el : phpIndex.getBySignature(sig)) {
			classes.addAll(getByType(el.getType(), phpIndex, visited, phpIndexVisited, phpIndexDepth));
		}

		return classes;
	}
}
