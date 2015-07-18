package org.nextras.orm.intellij.utils;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PhpClassUtils
{
	@Nullable
	static public PhpClass getInterface(PhpIndex phpIndex, String className)
	{
		Collection<PhpClass> classes = phpIndex.getInterfacesByFQN(className);
		return classes.isEmpty() ? null : classes.iterator().next();
	}


	static public boolean isImplementationOfInterface(PhpClass phpClass, PhpClass phpInterface)
	{
		if (phpClass == phpInterface) {
			return true;
		}

		for (PhpClass implementedInterface : phpClass.getImplementedInterfaces()) {
			if (isImplementationOfInterface(implementedInterface, phpInterface)) {
				return true;
			}
		}

		if (null == phpClass.getSuperClass()) {
			return false;
		}

		return isImplementationOfInterface(phpClass.getSuperClass(), phpInterface);
	}
}
