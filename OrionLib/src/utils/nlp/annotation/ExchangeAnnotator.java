package utils.nlp.annotation;

import java.util.Collection;

public interface ExchangeAnnotator
{
	Collection<? extends ExchangeAnnotation> annotate(String string);

}
