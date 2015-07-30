package org.nextras.orm.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIcons;
import org.jetbrains.annotations.NotNull;


public class ModifiersProvider extends CompletionProvider<CompletionParameters>
{
	@Override
	protected void addCompletions(@NotNull CompletionParameters params,
	                              ProcessingContext context,
	                              @NotNull CompletionResultSet result)
	{
		LookupElementBuilder el = LookupElementBuilder.create("default").withIcon(PhpIcons.FIELD).withTypeText("default", true);
		result.addElement(el);
	}
}
