package org.shaolin.javacc.sql;

import org.shaolin.javacc.sql.exception.QueryParsingException;
import org.shaolin.javacc.sql.node.OQLBinaryExpression;
import org.shaolin.javacc.sql.node.OQLCategoryField;
import org.shaolin.javacc.sql.node.OQLCommonField;
import org.shaolin.javacc.sql.node.OQLConditionalExpression;
import org.shaolin.javacc.sql.node.OQLExtendField;
import org.shaolin.javacc.sql.node.OQLFieldName;
import org.shaolin.javacc.sql.node.OQLFieldPart;
import org.shaolin.javacc.sql.node.OQLFunction;
import org.shaolin.javacc.sql.node.OQLIsNullExpression;
import org.shaolin.javacc.sql.node.OQLLiteral;
import org.shaolin.javacc.sql.node.OQLName;
import org.shaolin.javacc.sql.node.OQLParam;
import org.shaolin.javacc.sql.node.OQLSystemField;
import org.shaolin.javacc.sql.node.OQLTypeName;
import org.shaolin.javacc.sql.node.OQLUnaryExpression;

public interface IOQLNodeVisitor {
	// public void visit(OQLExpression oqlExpr) throws QueryParsingException;
	//
	// Maybe we can provide a default implementation for this method
	// {
	// oqlExpr.visitWith(this);
	// }
	// The user can choose to override this method or not

	public void startVisitBinaryExpression(OQLBinaryExpression oqlExpr)
			throws QueryParsingException;

	public void visitBinaryExpressionOp(String op) throws QueryParsingException;

	public void endVisitBinaryExpression(OQLBinaryExpression oqlExpr)
			throws QueryParsingException;

	public void visitCategoryField(OQLCategoryField oqlExpr)
			throws QueryParsingException;

	public void visitCommonField(OQLCommonField oqlExpr)
			throws QueryParsingException;

	public void visitSystemField(OQLSystemField oqlExpr)
			throws QueryParsingException;

	public void visitConditionalExpression(OQLConditionalExpression oqlExpr)
			throws QueryParsingException;

	public void visitExtendField(OQLExtendField oqlExpr)
			throws QueryParsingException;

	public void startVisitFieldName(OQLFieldName oqlExpr)
			throws QueryParsingException;

	public void endVisitFieldName(OQLFieldName oqlExpr)
			throws QueryParsingException;

	public void startVisitCast(OQLFieldPart oqlExpr)
			throws QueryParsingException;

	public void endVisitCast(OQLFieldPart oqlExpr) throws QueryParsingException;

	public void startVisitFunction(OQLFunction oqlExpr)
			throws QueryParsingException;

	public void endVisitFunction(OQLFunction oqlExpr)
			throws QueryParsingException;

	public void visitIsNullExpression(OQLIsNullExpression oqlExpr)
			throws QueryParsingException;

	public void visitLiteral(OQLLiteral oqlExpr) throws QueryParsingException;

	public void startVisitName(OQLName oqlExpr) throws QueryParsingException;

	public void visitName(OQLName oqlExpr) throws QueryParsingException;

	public void endVisitName(OQLName oqlExpr) throws QueryParsingException;

	public void visitParam(OQLParam oqlExpr) throws QueryParsingException;

	public void visitFieldPart(OQLFieldPart oqlExpr)
			throws QueryParsingException;

	public void visitTypeName(OQLTypeName oqlExpr) throws QueryParsingException;

	public void visitUnaryExpression(OQLUnaryExpression oqlExpr)
			throws QueryParsingException;

}
