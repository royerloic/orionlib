package org.royerloic.nlp.annotation;

public interface ExchangeAnnotation<O>
{

	String getText();

	Integer getStart();

	String getMatch();

	Integer getLength();

	O getAnnotation();

	// Equals and HashCode depend on : getText, getText, getMatch
}
