package org.nextras.orm.intellij.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;


public class OrmCompletionContributor extends CompletionContributor
{
	public OrmCompletionContributor()
	{
		extend(CompletionType.BASIC, ModifiersPatterns.getModifierPattern(), new ModifiersProvider());
	}
}
