package org.shaolin.bmdp.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.shaolin.bmdp.persistence.query.generator.ParamInfo;
import org.shaolin.bmdp.persistence.query.generator.QueryExpression;
import org.shaolin.bmdp.persistence.query.generator.QueryExpressionNodeList;
import org.shaolin.bmdp.persistence.query.generator.SearchQuery;
import org.shaolin.bmdp.persistence.query.operator.LogicalOperator;
import org.shaolin.bmdp.persistence.query.operator.Operator;


/*
----------------------Feature-----------------------
1.  operators (and reversed operator)
IS_NULL
EQUALS
EQUALS_IGNORE_CASE
LESS_THAN
GREATER_THAN
LESS_THAN_OR_EQUALS
GREATER_THAN_OR_EQUALS
CONTAINS_WORD
CONTAINS_WORD_IGNORE_CASE
CONTAINS_PARTIAL
CONTAINS_PARTIAL_IGNORE_CASE
COLLECTION_CONTAINS
IN
BETWEEN

2.  QueryExpression
a) field from object of queried class. (ex. "lastName")
b) field from referenced object of source objects
  (ex. "user.lastName", "user.father.lastName")
c) field from collection item object of source objects.
  (ex. "employees", "orders.productID", "orders.orderItems.productID")
d) field from child or parent object of source objects
   in a category. (ex. "{child_items in ProductCategory}")
e) extend field (ex. "@order")
f) perform a database function on field names.
  (ex. count(orders.ID), max(salary))

3.  QueryExpression node list
and, or.
Reverse of and, or
*/

public class ExpressionTest 
{

    protected void setUp()
    {
    }

    protected void tearDown()
    {}


    //IS_NULL
    @Test
    public void testOperator01()
        throws Exception
    {
        SearchQuery sQuery = new SearchQuery("");
        QueryExpression aNameExpr = new QueryExpression("xmlobjField", Operator.IS_NULL);
        QueryExpression bNameExpr = new QueryExpression("constantField", Operator.IS_NULL);

        sQuery.setFilter(aNameExpr);

        sQuery.setFilter(bNameExpr);
        
        System.out.println(" " + sQuery.getFilter());

    }

    //EQUALS EQUALS_IGNORE_CASE
    @Test
    public void testOperator02()
        throws Exception
    {
    	SearchQuery sQuery = new SearchQuery("");
        QueryExpression aNameExpr = new QueryExpression("name", Operator.EQUALS,"test1");
        aNameExpr.setValueAsParam(true);
        QueryExpression bNameExpr = new QueryExpression("name", Operator.EQUALS,"Test1");
        QueryExpression cNameExpr = new QueryExpression("name", Operator.EQUALS_IGNORE_CASE,"TEST1");
        QueryExpression dNameExpr = new QueryExpression("name", Operator.START_WITH,"tes");

        sQuery.setFilter(aNameExpr);

        System.out.println(" " + sQuery.getFilter());
    }

    //LESS_THAN GREATER_THAN LESS_THAN_OR_EQUALS GREATER_THAN_OR_EQUALS
    @Test
    public void testOperator03()
        throws Exception
    {
        QueryExpression aNameExpr = new QueryExpression("intField", Operator.LESS_THAN,new Integer(1001));
        QueryExpression bNameExpr = new QueryExpression("st:intField", Operator.GREATER_THAN,new Integer(1001));
        QueryExpression cNameExpr = new QueryExpression("Object:intField", Operator.LESS_THAN_OR_EQUALS,new Integer(1001));
        QueryExpression dNameExpr = new QueryExpression("intField", Operator.GREATER_THAN_OR_EQUALS,new Integer(1001));
        QueryExpression eNameExpr = new QueryExpression("intField < 1001");
        QueryExpression fNameExpr = new QueryExpression("intField > 1001");
        QueryExpression gNameExpr = new QueryExpression("intField <= 1001");
        QueryExpression hNameExpr = new QueryExpression("intField >= 1001");
        QueryExpression iNameExpr = new QueryExpression("intField = 1001");
        QueryExpression jNameExpr = new QueryExpression("20 * (-intField + 2000) / 4 - 7 != 5 * (-1001 + 2000) - 7");

        SearchQuery sQuery = new SearchQuery("");
        
        sQuery.setFilter(aNameExpr);
        sQuery.getFilter().append(bNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(cNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(dNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(eNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(fNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(gNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(hNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(iNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(jNameExpr, LogicalOperator.AND);
        
        System.out.println(" " + sQuery.getFilter());
    }


    //CONTAINS_WORD CONTAINS_WORD_IGNORE_CASE CONTAINS_PARTIAL CONTAINS_PARTIAL_IGNORE_CASE
    @Test
    public void testOperator04()
        throws Exception
    {
        QueryExpression aNameExpr = new QueryExpression("name", Operator.CONTAINS_WORD,"test1");
        QueryExpression bNameExpr = new QueryExpression("name", Operator.CONTAINS_WORD,"test");
        QueryExpression cNameExpr = new QueryExpression("name", Operator.CONTAINS_WORD_IGNORE_CASE,"TEST");
        QueryExpression dNameExpr = new QueryExpression("name", Operator.CONTAINS_PARTIAL,"test");
        QueryExpression eNameExpr = new QueryExpression("name", Operator.CONTAINS_PARTIAL_IGNORE_CASE,"Te");

        SearchQuery sQuery = new SearchQuery("");
        sQuery.setFilter(aNameExpr);
        sQuery.getFilter().append(bNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(cNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(dNameExpr, LogicalOperator.AND);
        sQuery.getFilter().append(eNameExpr, LogicalOperator.AND);
        
        System.out.println(" " + sQuery.getFilter());

    }

    // IN BETWEEN
    @Test
    public void testOperator06()
        throws Exception
    {
        List inlist=new ArrayList();
        inlist.add(new Integer(1001));
        inlist.add(new Integer(1002));
        inlist.add(new Integer(1212));
        List betweenlist=new ArrayList();
        betweenlist.add("1001");
        betweenlist.add("1002");
        QueryExpression aNameExpr = new QueryExpression("intField", Operator.IN,inlist);
        QueryExpression bNameExpr = new QueryExpression("intField", Operator.BETWEEN,betweenlist);

        SearchQuery sQuery = new SearchQuery("");
        sQuery.setFilter(aNameExpr);
        sQuery.getFilter().append(bNameExpr, LogicalOperator.AND);
        
        System.out.println(" " + sQuery.getFilter());

    }
    
    @Test
    public void testDateBinding() throws Exception
    {
        QueryExpression expr1 = new QueryExpression("datetimeField", Operator.EQUALS, currTime);
        QueryExpression expr2 = new QueryExpression("datetimeField", Operator.GREATER_THAN_OR_EQUALS, currTime);
        expr2.setValueAsParam(true);
        QueryExpression expr3 = new QueryExpression("datetimeField", Operator.GREATER_THAN_OR_EQUALS, ":dateTime", true);
        
        QueryExpression expr4 = new QueryExpression("timestampField", Operator.EQUALS, currTime);
        QueryExpression expr5 = new QueryExpression("timestampField", Operator.EQUALS, currTime);
        expr5.setValueAsParam(true);
        QueryExpression expr6 = new QueryExpression("timestampField", Operator.EQUALS, ":timeStamp", true);
        
        QueryExpressionNodeList filter = new QueryExpressionNodeList(expr1);
        filter.append(expr2, LogicalOperator.AND);
        filter.append(expr3, LogicalOperator.AND);
        filter.append(expr4, LogicalOperator.AND);
        filter.append(expr5, LogicalOperator.AND);
        filter.append(expr6, LogicalOperator.AND);
        
        SearchQuery sQuery = new SearchQuery("");
        sQuery.setFilter(filter);
        
        System.out.println(" " + sQuery.getFilter());

    }
    

    @Test
    public void testComplexFilter01()
        throws Exception
    {
        SearchQuery sQuery = new SearchQuery("TestBE as t1");
        
        sQuery.addFrom("BE_COMPLEXFIELDOBJECT:sfoList as t2");
        sQuery.addFrom("BE_SIMPLEFIELDOBJECT as t3");
        
        QueryExpression exp1 = new QueryExpression("BE_COMPLEXFIELDOBJECT:[oid] = t2:[oid]");
        QueryExpression exp2 = new QueryExpression("t2:[item_oid] = t3:[oid]");
        QueryExpression exp3 = new QueryExpression("BE_SIMPLEFIELDOBJECT:intField = :param1");
        QueryExpressionNodeList node1 = new QueryExpressionNodeList(exp1);
        node1.append(exp2, LogicalOperator.AND);
        node1.append(exp3, LogicalOperator.AND);
        
        //build sub query
        SearchQuery sQuery2 = new SearchQuery("BE_COMPLEXFIELDOBJECT as t11");
        
        QueryExpression exp100 = new QueryExpression("BE_SIMPLEFIELDOBJECT:intField = :param1");
        QueryExpression exp101 = new QueryExpression("longField = 1");
        QueryExpression exp102 = new QueryExpression("t3:sequenceField = 1");
        QueryExpressionNodeList node100 = new QueryExpressionNodeList(exp100);
        node100.append(exp101, LogicalOperator.OR);
        node100.append(exp102, LogicalOperator.OR);
        
        QueryExpression exp103 = new QueryExpression("BE_COMPLEXFIELDOBJECT:name = 'abc'");
        QueryExpression exp104 = new QueryExpression("name = 'efg'");
        QueryExpression exp105 = new QueryExpression("t1:name = 'ppp'");
        QueryExpressionNodeList node101 = new QueryExpressionNodeList(exp103);
        node101.append(exp104, LogicalOperator.OR);
        node101.append(exp105, LogicalOperator.OR);
        
        QueryExpression exp106 = new QueryExpression("t1:complexObj.complexObj.complexObj.name='999'");
        QueryExpressionNodeList subFilter = new QueryExpressionNodeList(node100);
        subFilter.append(node101, LogicalOperator.OR);
        subFilter.append(exp106, LogicalOperator.OR);
        sQuery2.setFilter(subFilter);
        
        QueryExpression exp4 = new QueryExpression("complexObj.complexObj.complexObj.name", Operator.CONTAINS_WORD, ":param2", true);
        QueryExpression exp5 = new QueryExpression(null, Operator.EXISTS.reverse(), sQuery2);
        QueryExpressionNodeList node2 = new QueryExpressionNodeList(exp4);
        node2.append(exp5, LogicalOperator.AND);
        
        QueryExpression exp6 = new QueryExpression("complexObj.complexObj.simpleObj.name='he''llo'");
        QueryExpressionNodeList node3 = new QueryExpressionNodeList(node2);
        node3.append(exp6, LogicalOperator.OR);
        
        List intList = new ArrayList();
        intList.add(new Integer(20));
        intList.add(new Integer(10));
        QueryExpression exp7 = new QueryExpression("complexObj.complexObj.complexObj.name", Operator.BETWEEN.reverse(), intList);
        QueryExpression exp8 = new QueryExpression("complexObj.complexObj.complexObj.name=:param2");
        QueryExpressionNodeList node4 = new QueryExpressionNodeList(node3);
        node4.append(exp7, LogicalOperator.OR);
        node4.append(exp8, LogicalOperator.OR);
        
        QueryExpressionNodeList filter = new QueryExpressionNodeList(node1);
        filter.append(node4, LogicalOperator.OR);
        
        sQuery.setFilter(filter);
        
        System.out.println("SQL :" +  sQuery.getFilter());
        
        /**
        "SELECT DISTINCT t1.BOID " +
            "FROM COMPLEXFIELDOBJECT t1, CFO_sfoList t2, SIMPLEFIELDOBJECT t3, " +
            "COMPLEXFIELDOBJECT tbl1, COMPLEXFIELDOBJECT tbl2, COMPLEXFIELDOBJECT " + 
            "tbl3, SIMPLEFIELDOBJECT tbl4 WHERE (t1.BOID = t2.BOID AND " +
            "t2.BITEM_OID = t3.BOID AND t3.intColumn =  ? ) OR " +
            "(t1.COMPLEXOBJOID = tbl1.BOID AND tbl1.COMPLEXOBJOID = tbl2.BOID " +
            "AND (((tbl2.COMPLEXOBJOID = tbl3.BOID AND (tbl3.name LIKE  ?  || " +
            "' %' OR tbl3.name LIKE '% ' ||  ?  OR tbl3.name LIKE '% ' || " +
            " ?  || ' %' AND NOT EXISTS(SELECT t11.BOID FROM " +
            "COMPLEXFIELDOBJECT t11 WHERE (t3.intColumn =  ?  OR t3.longColumn " +
            "= 1 OR t3.sequenceColumn = 1) OR (t11.name = 'abc' OR t11.name" +
            " = 'efg' OR t1.name = 'ppp') OR tbl3.name = '999'))) OR " +
            "(tbl2.SIMPLEOBJOID = tbl4.BOID AND tbl4.name = 'he''llo')) OR " +
            "(tbl2.COMPLEXOBJOID = tbl3.BOID AND tbl3.name NOT BETWEEN 20 AND 10) " +
            "OR (tbl2.COMPLEXOBJOID = tbl3.BOID AND tbl3.name =  ? )))");
            */
    }

    private static Map convertParamMap(Map paramMap)
    {
        Map m = new HashMap(paramMap.size());
        for (Iterator it = paramMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry)it.next();
            String paramName = (String)entry.getKey();
            ParamInfo info = (ParamInfo)entry.getValue();
            m.put(paramName, info.getIndexList());
        }
        return m;
    }

    private static java.util.Date currTime = new java.util.Date();




}