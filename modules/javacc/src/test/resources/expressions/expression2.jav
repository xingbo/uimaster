import org.shaolin.javacc.test.replaceexpressiong.RepacleExpressionBean;

String 
{
	String value = "Hello ";
	RepacleExpressionBean bean = new RepacleExpressionBean(value);
	value = bean.getValue() + RepacleExpressionBean.NAME;
	return value;
}