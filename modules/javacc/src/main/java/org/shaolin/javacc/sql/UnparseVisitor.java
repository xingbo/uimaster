package org.shaolin.javacc.sql;

import java.io.PrintWriter;
import java.io.Writer;

import org.shaolin.javacc.sql.IOQLNodeVisitor;
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

public class UnparseVisitor implements IOQLNodeVisitor
{
    public UnparseVisitor(Writer writer)
    {
        this.writer = new PrintWriter(writer);
    }
    
    public void startVisitBinaryExpression(OQLBinaryExpression oqlExpr)
        throws QueryParsingException
    {
        writer.print(SQLConstants.OPEN_BRACKET);
    }
    
    public void visitBinaryExpressionOp(String op) throws QueryParsingException
    {
        writer.print(SQLConstants.SPACE + op + SQLConstants.SPACE);
    }
    
    public void endVisitBinaryExpression(OQLBinaryExpression oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.CLOSE_BRACKET);
    }
    
    public void visitCategoryField(OQLCategoryField oqlExpr) throws QueryParsingException
    {
        writer.print(oqlExpr.getObjType() + SQLConstants.SPACE +
            SQLConstants.AS + SQLConstants.SPACE + oqlExpr.getCategoryName());
    }
    
    public void visitCommonField(OQLCommonField oqlExpr) throws QueryParsingException
    {
        writer.print(oqlExpr.getFieldName());
    }
    
    public void visitSystemField(OQLSystemField oqlExpr) throws QueryParsingException
    {
        writer.print("[" + oqlExpr.getFieldName() + "]");
    }
    
    public void visitConditionalExpression(OQLConditionalExpression oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.SPACE + oqlExpr.getOperator() + SQLConstants.SPACE);
    }
    
    public void visitExtendField(OQLExtendField oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.AT + oqlExpr.getFieldName());
    }
    
    public void startVisitFieldName(OQLFieldName oqlExpr) throws QueryParsingException
    {
        fieldPartIndex = 0;
    }
    
    public void endVisitFieldName(OQLFieldName oqlExpr) throws QueryParsingException
    {
    }
    
    public void startVisitFunction(OQLFunction oqlExpr) throws QueryParsingException
    {
        writer.print(oqlExpr.getFunctionName() + SQLConstants.OPEN_BRACKET);
        if (oqlExpr.isCountAllFunction())
        {
            writer.print(SQLConstants.STAR);
        }
    }
    
    public void endVisitFunction(OQLFunction oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.CLOSE_BRACKET);
    }
    
    public void visitIsNullExpression(OQLIsNullExpression oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.SPACE + SQLConstants.IS_NULL);
    }
    
    public void visitLiteral(OQLLiteral oqlExpr) throws QueryParsingException
    {
        writer.print(oqlExpr.getImage());
    }
    
    public void startVisitName(OQLName oqlExpr) throws QueryParsingException
    {
    }

    public void endVisitName(OQLName oqlExpr) throws QueryParsingException
    {
    }
    
    public void visitName(OQLName oqlExpr) throws QueryParsingException
    {
        if (oqlExpr.getTypeName() != null)
        {
            writer.print(SQLConstants.COLON);
        }
    }
    
    public void visitParam(OQLParam oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.COLON + oqlExpr.getParamName());
    }
    
    public void visitFieldPart(OQLFieldPart oqlExpr) throws QueryParsingException
    {
        if (fieldPartIndex > 0)
        {
            writer.print(SQLConstants.DOT);
        }
        fieldPartIndex++;
    }

    public void startVisitCast(OQLFieldPart oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.OPEN_BRACKET);
    }
    
    public void endVisitCast(OQLFieldPart oqlExpr) throws QueryParsingException
    {
        writer.print(SQLConstants.CLOSE_BRACKET);
    }
    
    public void visitTypeName(OQLTypeName oqlExpr) throws QueryParsingException
    {
        writer.print(oqlExpr.getTypeName());
        if (oqlExpr.getCustRDBName() != null)
        {
            writer.print("[");
            writer.print(oqlExpr.getCustRDBName());
            writer.print("]");
        }
    }
    
    public void visitUnaryExpression(OQLUnaryExpression oqlExpr) throws QueryParsingException
    {
        if (oqlExpr.getOperator() != null)
        {
            writer.print(oqlExpr.getOperator());
        }
    }
    
    private PrintWriter writer = null;
    
    private int fieldPartIndex = 0;

    public static final String ___REVISION___ = "$Revision: 1.4 $";
}
