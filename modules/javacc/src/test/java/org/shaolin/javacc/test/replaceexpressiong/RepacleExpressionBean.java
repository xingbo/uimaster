package org.shaolin.javacc.test.replaceexpressiong;

public class RepacleExpressionBean
{
    private String value;
    public RepacleExpressionBean(String value)
    {
        this.value = value;
    }
    public static String NAME = RepacleExpressionBean.class.getName();
    
    public String getValue()
    {
        return value;
    }
}
