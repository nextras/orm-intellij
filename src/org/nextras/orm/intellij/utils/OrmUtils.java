package org.nextras.orm.intellij.utils;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OrmUtils
{
	private static final Pattern entityPattern = Pattern.compile("(?:@entity\\s+([a-zA-Z0-9_\\\\]+))");

	public static boolean isEntity(PhpClass cls, PhpIndex phpIndex)
	{
		PhpClass entityInterface = PhpClassUtils.getInterface(phpIndex, "\\Nextras\\Orm\\Entity\\IEntity");
		return PhpClassUtils.isImplementationOfInterface(cls, entityInterface);
	}


	public static boolean isRepository(PhpClass cls, PhpIndex phpIndex)
	{
		PhpClass entityInterface = PhpClassUtils.getInterface(phpIndex, "\\Nextras\\Orm\\Repository\\IRepository");
		return PhpClassUtils.isImplementationOfInterface(cls, entityInterface);
	}


	public static boolean isMapper(PhpClass cls, PhpIndex phpIndex)
	{
		PhpClass entityInterface = PhpClassUtils.getInterface(phpIndex, "\\Nextras\\Orm\\Mapper\\IMapper");
		return PhpClassUtils.isImplementationOfInterface(cls, entityInterface);
	}


	public static String findRepositoryEntities(PhpClass repositoryClass)
	{
		if (repositoryClass.getDocComment() == null) {
			return null;
		}

		String phpDoc = repositoryClass.getDocComment().getText();
		Matcher matcher = entityPattern.matcher(phpDoc);
		while (matcher.find()) {
			String entityClass = matcher.group(1);
			return entityClass;
		}

		return null;
	}
}
