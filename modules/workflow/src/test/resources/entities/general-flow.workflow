<?xml version="1.0" ?>
<ns2:Workflow xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:ns6="http://bmdp.shaolin.org/datamodel/PageDiagram" xmlns:ns5="http://bmdp.shaolin.org/datamodel/Page"
	xmlns:ns4="http://bmdp.shaolin.org/datamodel/RDBDiagram" xmlns:ns3="http://bmdp.shaolin.org/datamodel/BEDiagram"
	xmlns:ns2="http://bmdp.shaolin.org/datamodel/Workflow" xmlns="http://bmdp.shaolin.org/datamodel/Common"
	xsi:schemaLocation="">
	<entityName>general-flow</entityName>
	<systemVersion>0</systemVersion>
	<ns2:conf>
		<ns2:bootable>true</ns2:bootable>
		<ns2:session-service name="defaultSession"
			type="org.shaolin.bmdp.workflow.internal.MockSessionService"
			sessionClass="org.shaolin.bmdp.workflow.internal.MockSession"></ns2:session-service>
	</ns2:conf>
	<ns2:flow name="flow1" eventConsumer="producer">
		<ns2:conf>
			<ns2:param name="var1" category="JavaPrimitive">
				<type entityName="java.lang.String"></type>
			</ns2:param>
			<ns2:param name="var2" category="JavaPrimitive">
				<type entityName="java.lang.Long"></type>
			</ns2:param>
		</ns2:conf>
		<ns2:start-node name="initSession" producer="producer"
			eventClass="org.shaolin.bmdp.workflow.spi.DefaultEvent">
			<ns2:filter name="filter1">
				<ns2:expression>
					<expressionString><![CDATA["NodeTest".equals(@event.getAttribute("NodeName"))]]></expressionString>
				</ns2:expression>
			</ns2:filter>
			<ns2:process>
				<ns2:expression>
					<expressionString><![CDATA[{
						System.out.println("initial the workflow user data session in start node.");
						@var1 = "This is var1";
						@var2 = new	Long(10);
						System.out.println("@var1: " + @var1 + ", @var2: " + @var2);
						}
					]]></expressionString>
				</ns2:expression>
			</ns2:process>
			<ns2:dest name="logicNode1"></ns2:dest>
		</ns2:start-node>
		<ns2:node name="logicNode1">
			<ns2:process>
				<ns2:var name="var1" category="JavaPrimitive" xsi:type="ParamType"
					scope="InOut">
					<type entityName="java.lang.String"></type>
				</ns2:var>
				<ns2:var name="var2" category="JavaPrimitive" xsi:type="ParamType"
					scope="InOut">
					<type entityName="java.lang.Long"></type>
				</ns2:var>
				<ns2:expression>
					<expressionString><![CDATA[{
						System.out.println("this is logic node.");
						$var1 = "This is local var1";
						$var2 = new Long(5);
						System.out.println("@var1: " + @var1 + ", @var2: " + @var2);
						System.out.println("$var1: " + $var1 + ", $var2: " + $var2);
						@event.setAttribute("Response", "NodeTest");
						}
					]]></expressionString>
				</ns2:expression>
			</ns2:process>
			<ns2:dest name="conditionNode1"></ns2:dest>
		</ns2:node>
		<ns2:condition name="conditionNode1">
			<ns2:process>
				<ns2:expression>
					<expressionString><![CDATA[System.out.println("this is logic node.");]]></expressionString>
				</ns2:expression>
			</ns2:process>
			<ns2:dest name="endNode">
				<ns2:expression>
					<expressionString>@var1.equals("This is var1")</expressionString>
				</ns2:expression>
			</ns2:dest>
			<ns2:dest name="endNode">
				<ns2:expression>
					<expressionString>@var2 == 0</expressionString>
				</ns2:expression>
			</ns2:dest>
		</ns2:condition>
		<ns2:end-node name="endNode"></ns2:end-node>
	</ns2:flow>
</ns2:Workflow>
