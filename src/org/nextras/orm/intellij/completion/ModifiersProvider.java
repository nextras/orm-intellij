package org.nextras.orm.intellij.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIcons;
import org.jetbrains.annotations.NotNull;


public class ModifiersProvider extends CompletionProvider<CompletionParameters>
{
	public static ModifierInsertHandler withParams = new ModifierInsertHandler(true);
	public static ModifierInsertHandler withoutParams = new ModifierInsertHandler(false);

	@Override
	protected void addCompletions(@NotNull CompletionParameters params,
	                              ProcessingContext context,
	                              @NotNull CompletionResultSet result)
	{
		result.addElement(LookupElementBuilder.create("enum").withInsertHandler(withParams));
		result.addElement(LookupElementBuilder.create("default").withInsertHandler(withParams));
		result.addElement(LookupElementBuilder.create("virtual").withInsertHandler(withoutParams));
		result.addElement(LookupElementBuilder.create("container").withInsertHandler(withParams));
		result.addElement(LookupElementBuilder.create("1:m").withInsertHandler(withParams));
		result.addElement(LookupElementBuilder.create("m:1").withInsertHandler(withParams));
		result.addElement(LookupElementBuilder.create("1:1").withInsertHandler(withParams));
		result.addElement(LookupElementBuilder.create("1:1d").withInsertHandler(withParams));
		result.addElement(LookupElementBuilder.create("m:n").withInsertHandler(withParams));
		result.addElement(LookupElementBuilder.create("primary").withInsertHandler(withoutParams));
	}

}
