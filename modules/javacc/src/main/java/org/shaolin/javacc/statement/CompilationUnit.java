package org.shaolin.javacc.statement;

import java.io.Serializable;

import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.ParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.traverser.Traverser;

public interface CompilationUnit extends Serializable {

	Object execute(EvaluationContext evaluationContext)
			throws EvaluationException;

	void parse(ParsingContext parsingContext) throws ParsingException;

	Class getValueClass() throws ParsingException;

	String getExpressionString();

	void traverse(Traverser tranverser);

}