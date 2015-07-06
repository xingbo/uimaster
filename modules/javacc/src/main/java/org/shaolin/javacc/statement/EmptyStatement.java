package org.shaolin.javacc.statement;

import org.shaolin.javacc.context.OOEESmtEvaluationContext;
import org.shaolin.javacc.context.OOEESmtParsingContext;
import org.shaolin.javacc.util.traverser.Traverser;

/**
 * The class for the empty statement node
 * 
 */
public class EmptyStatement extends Statement {

	public ExecutionResult execute(OOEESmtEvaluationContext evaluationContext) {
		ExecutionResult execResult = new ExecutionResult();
		return execResult;
	}

	public void parse(OOEESmtParsingContext parsingContext) {

	}

	public void traverse(Traverser traverser) {
	}

	/* The serial ID */
	private static final long serialVersionUID = 1357924680L;

}
