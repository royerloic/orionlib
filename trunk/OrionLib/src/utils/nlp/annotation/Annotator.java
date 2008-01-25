package utils.nlp.annotation;

import java.util.Collection;

public interface Annotator extends ExchangeAnnotator
{
	Collection<? extends Annotation> annotate(String pText);

	Collection<? extends Annotation> annotate(String pSentence,
																						int pSentenceOffset);
}
