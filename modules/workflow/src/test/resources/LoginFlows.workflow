<?xml version="1.0" ?>
<ns2:Workflow xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:ns6="http://bmdp.shaolin.org/datamodel/PageDiagram" xmlns:ns5="http://bmdp.shaolin.org/datamodel/Page"
	xmlns:ns4="http://bmdp.shaolin.org/datamodel/RDBDiagram" xmlns:ns3="http://bmdp.shaolin.org/datamodel/BEDiagram"
	xmlns:ns2="http://bmdp.shaolin.org/datamodel/Workflow" xmlns="http://bmdp.shaolin.org/datamodel/Common"
	xsi:schemaLocation="">
	<entityName>org.shaolin.vogerp.herewego.diagram.LoginFlows</entityName>
	<systemVersion>0</systemVersion>
	<ns2:conf>
		<!-- the event consumer marked flow that must have the bootable=true flag.-->
		<ns2:bootable>true</ns2:bootable>
		<ns2:service name="userService" scope="InOut" category="JavaClass">
			<type entityName="org.shaolin.vogerp.commonmodel.IUserService"></type>
		</ns2:service>
	</ns2:conf>
	<ns2:flow name="LoginFlow" eventConsumer="org.shaolin.vogerp.herewego.page.Main.loginSuccess">
		<ns2:conf>
			<ns2:param name="var1" scope="InOut" category="JavaPrimitive">
				<type entityName="java.lang.String"></type>
			</ns2:param>
			<ns2:param name="var2" scope="InOut" category="JavaPrimitive">
				<type entityName="java.lang.Long"></type>
			</ns2:param>
		</ns2:conf>
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
						}
					]]></expressionString>
				</ns2:expression>
			</ns2:process>
			<ns2:dest name="conditionNode1"></ns2:dest>
		</ns2:node>
		<ns2:condition name="conditionNode1">
			<ns2:process>
				<ns2:expression>
					<expressionString>System.out.println("this is logic node.");</expressionString>
				</ns2:expression>
			</ns2:process>
			<ns2:dest name="endNode">
				<ns2:expression>
					<expressionString>@var1.equals("This is var1")</expressionString>
				</ns2:expression>
			</ns2:dest>
		</ns2:condition>
		<!-- if current flow is statefull, then it ended by the end-node invocation. -->
		<ns2:end-node name="endNode"></ns2:end-node>
	</ns2:flow>
</ns2:Workflow>
