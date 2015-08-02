<?xml version="1.0" ?>
<ns2:Workflow xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:ns6="http://bmdp.shaolin.org/datamodel/PageDiagram" xmlns:ns5="http://bmdp.shaolin.org/datamodel/Page"
	xmlns:ns4="http://bmdp.shaolin.org/datamodel/RDBDiagram" xmlns:ns3="http://bmdp.shaolin.org/datamodel/BEDiagram"
	xmlns:ns2="http://bmdp.shaolin.org/datamodel/Workflow" xmlns="http://bmdp.shaolin.org/datamodel/Common"
	xsi:schemaLocation="">
	<entityName>mission-flow</entityName>
	<systemVersion>0</systemVersion>
	<ns2:conf>
		<ns2:bootable>true</ns2:bootable>
		<ns2:session-service name="defaultSession"
			type="org.shaolin.bmdp.workflow.internal.MockSessionService"
			sessionClass="org.shaolin.bmdp.workflow.internal.MockSession"></ns2:session-service>
	</ns2:conf>
	<ns2:flow name="flow1" eventConsumer="producer1">
		<ns2:conf>
			<ns2:param name="var1" category="JavaPrimitive">
				<type entityName="java.lang.String"></type>
			</ns2:param>
		</ns2:conf>
		<ns2:start-node name="init" eventClass="org.shaolin.bmdp.workflow.spi.DefaultEvent">
			<ns2:filter name="filter1">
				<ns2:expression>
					<expressionString><![CDATA["mission-flow".equals(@event.getAttribute("NodeName"))]]></expressionString>
				</ns2:expression>
			</ns2:filter>
			<ns2:process>
				<ns2:expression>
					<expressionString><![CDATA[{
						System.out.println("initial the workflow user data session on start node.");
						System.out.println("place an order.");
						}
					]]></expressionString>
				</ns2:expression>
			</ns2:process>
			<ns2:dest name="createdOrder"></ns2:dest>
		</ns2:start-node>
		<!-- schedule somebody to approved the order once it created. anybody can place an order from business perspective. -->
		<ns2:mission-node name="createdOrder" expiredDays="0" expiredHours="1" partyType="productionManager">
			<ns2:process>
				<ns2:var name="orderObject" category="JavaPrimitive" xsi:type="ParamType"	scope="InOut">
					<type entityName="java.lang.Object"></type>
				</ns2:var>
				<ns2:expression>
					<expressionString><![CDATA[{
						System.out.println("approved the created order: " + $orderObject);
						
						}
					]]></expressionString>
				</ns2:expression>
			</ns2:process>
			<ns2:dest name="onProduction"></ns2:dest>
		</ns2:mission-node>
		<ns2:mission-node name="onProduction" expiredDays="0" expiredHours="1" partyType="productionManager">
			<ns2:process>
				<ns2:var name="orderObject" category="JavaPrimitive" xsi:type="ParamType"	scope="InOut">
					<type entityName="java.lang.Object"></type>
				</ns2:var>
				<ns2:expression>
					<expressionString><![CDATA[{
						System.out.println("production finished: " + $orderObject);
						
						}
					]]></expressionString>
				</ns2:expression>
			</ns2:process>
			<ns2:dest name="onDelivery"></ns2:dest>
		</ns2:mission-node>
		<ns2:mission-node name="onDelivery" expiredDays="0" expiredHours="1" partyType="productionManager">
			<ns2:process>
				<ns2:var name="orderObject" category="JavaPrimitive" xsi:type="ParamType"	scope="InOut">
					<type entityName="java.lang.Object"></type>
				</ns2:var>
				<ns2:expression>
					<expressionString><![CDATA[{
						System.out.println("delivery finished: " + $orderObject);
						
						}
					]]></expressionString>
				</ns2:expression>
			</ns2:process>
			<ns2:dest name="endNode"></ns2:dest>
		</ns2:mission-node>
		<ns2:end-node name="endNode"></ns2:end-node>
	</ns2:flow>
</ns2:Workflow>
