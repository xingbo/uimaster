package org.shaolin.javacc.util;

//imports
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.utils.ClassLoaderUtil;
import org.shaolin.javacc.context.ICacheableContext;
import org.shaolin.javacc.exception.ParsingException;

/**
 * The util class for expression parsing and evaluation
 * 
 * @author Xiao Yi
 */
public class ExpressionUtil
{
    /**
     * Determine whether the operand of type operandClass can be a numeric
     * operand
     * 
     * @param operandClass
     *            the class type of the numeric operand
     * @return whether the operand of type operand Class can be a numeric
     *         operand
     */
    public static boolean isNumeric(Class operandClass)
    {
        if (operandClass == null || !operandClass.isPrimitive() || operandClass == boolean.class
                || operandClass == void.class)
        {
            return false;
        }

        return true;
    }

    /**
     * Determine whether the operand Class is reference type compatible
     * 
     * @param operandClass
     *            the class type of the operand
     * @return whether the operand Class is reference type compatibled
     */
    public static boolean isReferenceType(Class operandClass)
    {
        if (operandClass == null || !operandClass.isPrimitive())
        {
            return true;
        }

        return false;
    }

    /**
     * performs binary numeric promotion it also performs unary numeric
     * promotion when class1 and class2 are the same
     * 
     * @param class1
     *            class type of left operand
     * @param class2
     *            class type of right operand
     * @return class type of operation result
     * @throws ParsingException
     *             if one of the operands is not numeric compatible
     */
    public static Class getNumericReturnClass(Class class1, Class class2) throws ParsingException
    {
        Class returnClass = null;

        int lPrecision = 0;
        int rPrecision = 0;
        int returnPrecision = 0;

        // two operand class type must be numeric operands
        if (isNumeric(class1) && isNumeric(class2))
        {
            lPrecision = getNumericPrecision(class1);

            rPrecision = getNumericPrecision(class2);

            if (lPrecision > rPrecision)
            {
                returnPrecision = lPrecision;
            }
            else
            {
                returnPrecision = rPrecision;
            }

            switch (returnPrecision)
            {
            case LONG_PRECISION:
                returnClass = long.class;
                break;
            case FLOAT_PRECISION:
                returnClass = float.class;
                break;
            case DOUBLE_PRECISION:
                returnClass = double.class;
                break;
            default:
                returnClass = int.class;
                break;
            }
        }
        else
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_051);
            //throw new ParsingException("Operands of Numeric Operator must be number");
        }

        return returnClass;
    }

    /**
     * Get the a return object of the type valueClass with the same numeric
     * value as the originObject
     * 
     * @param originObject
     *            the original object
     * @param valueClass
     *            the desired return object type class
     * @return an object of the type valueClass with the same numeric value as
     *         the originObject
     * @throws ParsingException
     *             if the originObject is not numeric compatible
     * 
     */
    public static Object getNumericReturnObject(Object originObject, Class valueClass)
            throws ParsingException
    {
        Object result = null;

        if (originObject != null && originObject.getClass() == valueClass)
        {
            result = originObject;
        }
        else if (originObject instanceof Number)
        {
            result = getNumericReturnObject((Number) originObject, valueClass);
        }
        else if (originObject instanceof Character)
        {
            char value = ((Character) originObject).charValue();
            result = getNumericReturnObject(new Integer(value), valueClass);
        }
        else
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_028);
        }

        return result;
    }

    /**
     * Get the a numeric return object of the type valueClass with the specified
     * value
     * 
     * @param value
     *            the numeric value
     * @param valueClass
     *            the desired return object type class
     * @return an object of the type valueClass with with the specified value
     */
    private static Object getNumericReturnObject(Number value, Class valueClass)
    {
        Object result = null;

        if (valueClass == byte.class)
        {
            result = new Byte(value.byteValue());
        }
        else if (valueClass == short.class)
        {
            result = new Short(value.shortValue());
        }
        else if (valueClass == char.class)
        {
            result = new Character((char) value.intValue());
        }
        else if (valueClass == int.class)
        {
            result = new Integer(value.intValue());
        }
        else if (valueClass == long.class)
        {
            result = new Long(value.longValue());
        }
        else if (valueClass == float.class)
        {
            result = new Float(value.floatValue());
        }
        else if (valueClass == double.class)
        {
            result = new Double(value.doubleValue());
        }

        return result;
    }

    /**
     * Get the numeric precision level of a number class
     * 
     * @param numberClass
     *            the number type class
     * @return the precision level
     * @throws ParsingException
     *             if the numberClass is not a numeric compatible class
     */
    public static int getNumericPrecision(Class numberClass) throws ParsingException
    {
        int precision;

        /*
         * if(className.equals("byte")) { precision = BYTE_PRECISION; } else
         * if(className.equals("short")) { precision = SHORT_PRECISION; } else
         * if(className.equals("char")) { precision = CHAR_PRECISION; } else
         * if(className.equals("int")) { precision = INTEGER_PRECISION; } else
         * if(className.equals("long")) { precision = LONG_PRECISION; } else
         * if(className.equals("float")) { precision = FLOAT_PRECISION; } else
         * if(className.equals("double")) { precision = DOUBLE_PRECISION; }
         */

        if (numberClass == byte.class)
        {
            precision = BYTE_PRECISION;
        }
        else if (numberClass == short.class)
        {
            precision = SHORT_PRECISION;
        }
        else if (numberClass == char.class)
        {
            precision = CHAR_PRECISION;
        }
        else if (numberClass == int.class)
        {
            precision = INTEGER_PRECISION;
        }
        else if (numberClass == long.class)
        {
            precision = LONG_PRECISION;
        }
        else if (numberClass == float.class)
        {
            precision = FLOAT_PRECISION;
        }
        else if (numberClass == double.class)
        {
            precision = DOUBLE_PRECISION;
        }
        else
        {
            throw new ParsingException(numberClass + " is not a primitive type class of number");
        }

        return precision;
    }

    /**
     * Find a class with the specified className
     * 
     * @param className
     *            the class's name
     * @return the corresponding class of the specified className
     * @throws ParsingException
     *             if no class found with the specified className
     * @see #findClass(String, boolean)
     * @see #parseClass(String)
     */
    public static Class findClass(String className) throws ParsingException
    {
        return findClass(className, null, false);
    }

    /**
     * Find a class or inner class with the specified className If it's an inner
     * class, it must be public and static
     * 
     * @param className
     *            the class's name
     * @param isInnerClass
     *            whether it's an inner class
     * @return the corresponding class of the specified className
     * @throws ParsingException
     *             if no class found with the specified className
     * @see #findClass(String)
     * @see #parseClass(String)
     */
    public static Class findClass(String className, boolean isInnerClass) throws ParsingException
    {
        return findClass(className, null, isInnerClass);
    }

    /**
     * Find a class or inner class with the specified className It's different
     * from findClass(String) in that findClass(String) just simply load the
     * class of the specified name, but parseClass(String) find a class step by
     * step
     * 
     * @param className
     *            the class's name
     * @return the corresponding class of the specified className
     * @throws ParsingException
     *             if no class found with the specified className
     * @see #findClass(String)
     */
    public static Class parseClass(String className) throws ParsingException
    {
        int dimension = 0;
        while (className.endsWith("[]"))
        {
            dimension++;
            className = className.substring(0, className.length() - 2);
        }

        Class foundClass = null;
        String cName = className;
        List innerNameList = new ArrayList();
        while (true)
        {
            try
            {
                foundClass = findClass(cName, false);
                break;
            }
            catch (ParsingException e)
            {
                foundClass = null;
            }
            int index = cName.lastIndexOf(".");
            if (index == -1)
            {
                break;
            }
            innerNameList.add(cName.substring(index + 1));
            cName = cName.substring(0, index);
        }

        if (foundClass != null)
        {
            for (int i = innerNameList.size() - 1; i >= 0; i--)
            {
                try
                {
                    foundClass = findClass(foundClass.getName() + "$" + innerNameList.get(i), true);
                }
                catch (ParsingException e)
                {
                    foundClass = null;
                    break;
                }
            }
        }

        if (foundClass == null)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_030,new Object[]{className});
        }
        for (int i = 0; i < dimension; i++)
        {
            Object arrayObject = Array.newInstance(foundClass, 0);
            foundClass = arrayObject.getClass();
        }

        return foundClass;
    }

    /**
     * Determine whether the class of the specified className is a primitive
     * type class
     * 
     * @param className
     *            the class's name
     * @return whether the class of the specified className is a primitive type
     *         class
     */
    public static boolean isPrimitiveClass(String className)
    {
        if (className != null && primitiveClassMap.containsKey(className))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Get the primitive type class with the specified className
     * 
     * @param className
     *            the class's name
     * @return the corresponding class of the specified className
     * @throws ParsingException
     *             if className is not primitive type class name
     */
    public static Class getPrimitiveClass(String className) throws ParsingException
    {
        Class result = null;

        result = (Class) primitiveClassMap.get(className);

        if (result == null)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_066,new Object[]{className});
        }

        return result;
    }

    /**
     * Get the constructor of the specified class and the specified argument
     * type list
     * 
     * @param constructorClass
     *            the class's name
     * @param argsClasses
     *            list of argument type classes
     * @return the corresponding constructor
     * @throws ParsingException
     *             if no suitable constructor found
     */
    public static Constructor findConstructor(Class constructorClass, List argsClasses)
            throws ParsingException
    {
        Class[] constructorArgs = (Class[]) argsClasses.toArray(new Class[] {});
        int argNum = constructorArgs.length;

        Constructor[] constructors = constructorClass.getConstructors();
        List foundConstructors = new ArrayList();

        for (int i = 0; i < constructors.length; i++)
        {
            Constructor constructor = constructors[i];

            Class[] paramClasses = constructor.getParameterTypes();
            if (isAssignableFrom(paramClasses, constructorArgs))
            {
                foundConstructors.add(constructor);
            }
        }

        if (foundConstructors.size() == 0)
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Can't find constructor ");
            buffer.append(constructorClass.getName());
            buffer.append("(");
            for (int i = 0; i < constructorArgs.length; i++)
            {
                buffer.append(constructorArgs[i].getName());
                if (i != constructorArgs.length - 1)
                {
                    buffer.append(", ");
                }
            }
            buffer.append(")");

            throw new ParsingException(buffer.toString());
        }

        constructors = (Constructor[]) foundConstructors.toArray(new Constructor[] {});
        foundConstructors.clear();

        for (int i = 0; i < constructors.length; i++)
        {
            Constructor constructor = constructors[i];
            Class[] argClasses1 = constructor.getParameterTypes();

            boolean isOverRide = false;
            for (int j = 0; j < constructors.length; j++)
            {
                if (i != j)
                {
                    Constructor constructor1 = constructors[j];
                    Class[] argClasses2 = constructor1.getParameterTypes();
                    if (isAssignableFrom(argClasses1, argClasses2))
                    {
                        isOverRide = true;
                        break;
                    }
                }
            }
            if (!isOverRide)
            {
                foundConstructors.add(constructor);
            }
        }

        if (foundConstructors.size() != 1)
        {
            StringBuffer buffer = new StringBuffer();

            buffer.append("found more than one ambiguous constructors\n");

            for (int i = 0, n = foundConstructors.size(); i < n; i++)
            {
                Constructor constructor = (Constructor) foundConstructors.get(i);
                buffer.append(constructor.toString());
                buffer.append("\n");
            }

            throw new ParsingException(buffer.toString());
        }

        return (Constructor) foundConstructors.get(0);
    }

    public static Method findMethod(Class parentClass, String methodName, boolean mustBeStatic,
            List argClasses) throws ParsingException
    {
        Class[] methodArgs = (Class[]) argClasses.toArray(new Class[] {});
        int argNum = methodArgs.length;

        // first check whether there's an exact matching method
        try
        {
            Method method = parentClass.getMethod(methodName, methodArgs);
            if (mustBeStatic)
            {
                int modifier = method.getModifiers();
                if (Modifier.isStatic(modifier))
                {
                    return method;
                }
            }
            else
            {
                return method;
            }
        }
        catch (NoSuchMethodException e)
        {
        }

        Method[] methods = parentClass.getMethods();
        List foundMethods = new ArrayList();

        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];
            if (method.getName().equals(methodName))
            {
                if (mustBeStatic)
                {
                    int modifier = method.getModifiers();
                    if (!Modifier.isStatic(modifier))
                    {
                        continue;
                    }
                }

                Class[] paramClasses = method.getParameterTypes();
                if (isAssignableFrom(paramClasses, methodArgs))
                {
                    foundMethods.add(method);
                }
            }
        }

        // when the class is interface, add java.lang.Object's methods
        if (parentClass.isInterface())
        {
            methods = java.lang.Object.class.getMethods();
            for (int i = 0; i < methods.length; i++)
            {
                Method method = methods[i];
                if (method.getName().equals(methodName))
                {
                    if (mustBeStatic)
                    {
                        int modifier = method.getModifiers();
                        if (!Modifier.isStatic(modifier))
                        {
                            continue;
                        }
                    }

                    Class[] paramClasses = method.getParameterTypes();
                    if (isAssignableFrom(paramClasses, methodArgs))
                    {
                        foundMethods.add(method);
                    }
                }
            }
        }

        if (foundMethods.size() == 0)
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Can't find method ");
            buffer.append(methodName);
            buffer.append("(");
            for (int i = 0; i < methodArgs.length; i++)
            {
                buffer.append(methodArgs[i] == null ? "null" : methodArgs[i].getName());
                if (i != methodArgs.length - 1)
                {
                    buffer.append(", ");
                }
            }
            buffer.append(")");
            buffer.append(" for class ");
            buffer.append(parentClass.getName());

            throw new ParsingException(buffer.toString());
        }

        methods = (Method[]) foundMethods.toArray(new Method[] {});
        foundMethods.clear();

        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];
            boolean isOverRide = false;
            for (int j = 0; j < methods.length; j++)
            {
                if (i != j)
                {
                    Method method1 = methods[j];
                    if (isOverRideBy(method, method1))
                    {
                        isOverRide = true;
                        break;
                    }
                }
            }
            if (!isOverRide)
            {
                foundMethods.add(method);
            }
        }

        if (foundMethods.size() != 1)
        {
            StringBuffer buffer = new StringBuffer();

            buffer.append("found more than one ambiguous methods\n");

            for (int i = 0, n = foundMethods.size(); i < n; i++)
            {
                Method method = (Method) foundMethods.get(i);
                buffer.append(method.toString());
                buffer.append("\n");
            }

            throw new ParsingException(buffer.toString());
        }

        return (Method) foundMethods.get(0);
    }

    public static boolean isAssignableFrom(Class[] parentClasses, Class[] classes)
    {
        boolean result = true;
        if (classes.length != parentClasses.length)
        {
            result = false;
        }
        else
        {
            int num = classes.length;
            for (int i = 0; i < num; i++)
            {
                Class class1 = classes[i];
                Class parentClass = parentClasses[i];

                if (!isAssignableFrom(parentClass, class1))
                {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    public static boolean isAssignableFrom(Class parentClass, Class class1)
    {
        if (parentClass == null)
        {
            return false;
        }
        if (class1 == null)
        {
            return !parentClass.isPrimitive();
        }
        if (isNumeric(class1) && isNumeric(parentClass))
        {
            boolean result = false;
            try
            {
                int class1Prec = getNumericPrecision(class1);
                int parentPrec = getNumericPrecision(parentClass);
                if (class1Prec <= parentPrec)
                {
                    result = true;
                    if (parentPrec < INTEGER_PRECISION)
                    {
                        // when it's a match between char, short and byte
                        // char can't convert with the other two
                        if (parentPrec * class1Prec < 0)
                        {
                            result = false;
                        }
                    }
                }
            }
            catch (ParsingException e)
            {
                // won't happen
            }
            return result;
        }
        return parentClass.isAssignableFrom(class1);
    }

    public static boolean isOverRideBy(Method method, Method overrideMethod)
    {
        Class class1 = method.getDeclaringClass();
        Class[] args1 = method.getParameterTypes();

        Class class2 = overrideMethod.getDeclaringClass();
        Class[] args2 = overrideMethod.getParameterTypes();

        if (class1.isAssignableFrom(class2) && isAssignableFrom(args1, args2))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Field findField(Class parentClass, String fieldName, boolean mustBeStatic)
            throws ParsingException
    {
        Field foundField = null;

        Field[] fields = parentClass.getFields();
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            if (field.getName().equals(fieldName))
            {
                if (mustBeStatic)
                {
                    int modifier = field.getModifiers();
                    if (!Modifier.isStatic(modifier))
                    {
                        continue;
                    }
                }

                foundField = field;
                break;
            }
        }

        if (foundField == null)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_035,new Object[]{fieldName,parentClass.getName()});
        }

        return foundField;
    }

    /**
     * Determine whether the fromclass can be casted to targetclass
     * 
     * @param targetClass
     *            the target type class fromClass the origin type class
     * @return whether the fromclass can be casted to targetclass
     */
    public static boolean isCastableFrom(Class targetClass, Class fromClass)
    {
        boolean result = false;

        if (isNumeric(targetClass) && isNumeric(fromClass))
        {
            result = true;
        }
        else if (targetClass == boolean.class && fromClass == boolean.class)
        {
            result = true;
        }
        else if (isReferenceType(targetClass) && isReferenceType(fromClass))
        {
            if (targetClass != null)
            {
                if (fromClass == null || targetClass.isAssignableFrom(fromClass)
                        || fromClass.isAssignableFrom(targetClass) || targetClass.isInterface())
                {
                    result = true;
                }
            }
        }

        return result;
    }

    public static List getImportClasses()
    {
        return importedClass;
    }

    public static void addImportClass(String className)
    {
        importedClass.add(className);
    }

    public static void removeImportClass(String className)
    {
        importedClass.remove(className);
    }

    public static Class loadClass(String className) throws ClassNotFoundException
    {
        Class clazz = (Class) primitiveClassMap.get(className);
        if (clazz == null)
        {
            clazz = (Class) javaLangMap.get(className);
            if (clazz == null)
            {
                clazz = ClassLoaderUtil.loadClass(className);
            }
        }
        return clazz;
    }

    public static void setCurrentClassLoader(ClassLoader classLoader)
    {
        ClassLoaderUtil.setCurrentClassLoader(classLoader);
    }

    public static final int BYTE_PRECISION = -2;
    public static final int SHORT_PRECISION = -1;
    public static final int CHAR_PRECISION = 1;
    public static final int INTEGER_PRECISION = 2;
    public static final int LONG_PRECISION = 3;
    public static final int FLOAT_PRECISION = 4;
    public static final int DOUBLE_PRECISION = 5;

    private static List importedClass;

    private static Map primitiveClassMap = new HashMap();
    private static final Map javaLangMap = new HashMap(80);

    static
    {
        importedClass = new ArrayList();
        // importedClass.add("java.lang.*");

        primitiveClassMap.put("boolean", boolean.class);
        primitiveClassMap.put("byte", byte.class);
        primitiveClassMap.put("short", short.class);
        primitiveClassMap.put("char", char.class);
        primitiveClassMap.put("int", int.class);
        primitiveClassMap.put("long", long.class);
        primitiveClassMap.put("float", float.class);
        primitiveClassMap.put("double", double.class);
        primitiveClassMap.put("void", void.class);

        javaLangMap.put("CharSequence", CharSequence.class);
        javaLangMap.put("Cloneable", Cloneable.class);
        javaLangMap.put("Comparable", Comparable.class);
        javaLangMap.put("Runnable", Runnable.class);
        javaLangMap.put("Boolean", Boolean.class);
        javaLangMap.put("Byte", Byte.class);
        javaLangMap.put("Character", Character.class);
        javaLangMap.put("Class", Class.class);
        javaLangMap.put("ClassLoader", ClassLoader.class);
        javaLangMap.put("Compiler", Compiler.class);
        javaLangMap.put("Double", Double.class);
        javaLangMap.put("Float", Float.class);
        javaLangMap.put("InheritableThreadLocal", InheritableThreadLocal.class);
        javaLangMap.put("Integer", Integer.class);
        javaLangMap.put("Long", Long.class);
        javaLangMap.put("Math", Math.class);
        javaLangMap.put("Number", Number.class);
        javaLangMap.put("Object", Object.class);
        javaLangMap.put("Package", Package.class);
        javaLangMap.put("Process", Process.class);
        javaLangMap.put("Runtime", Runtime.class);
        javaLangMap.put("RuntimePermission", RuntimePermission.class);
        javaLangMap.put("SecurityManager", SecurityManager.class);
        javaLangMap.put("Short", Short.class);
        javaLangMap.put("StackTraceElement", StackTraceElement.class);
        javaLangMap.put("StrictMath", StrictMath.class);
        javaLangMap.put("String", String.class);
        javaLangMap.put("StringBuffer", StringBuffer.class);
        javaLangMap.put("System", System.class);
        javaLangMap.put("Thread", Thread.class);
        javaLangMap.put("ThreadGroup", ThreadGroup.class);
        javaLangMap.put("ThreadLocal", ThreadLocal.class);
        javaLangMap.put("Throwable", Throwable.class);
        javaLangMap.put("Void", Void.class);
        javaLangMap.put("ArithmeticException", ArithmeticException.class);
        javaLangMap.put("ArrayIndexOutOfBoundsException", ArrayIndexOutOfBoundsException.class);
        javaLangMap.put("ArrayStoreException", ArrayStoreException.class);
        javaLangMap.put("ClassCastException", ClassCastException.class);
        javaLangMap.put("ClassNotFoundException", ClassNotFoundException.class);
        javaLangMap.put("CloneNotSupportedException", CloneNotSupportedException.class);
        javaLangMap.put("Exception", Exception.class);
        javaLangMap.put("IllegalAccessException", IllegalAccessException.class);
        javaLangMap.put("IllegalArgumentException", IllegalArgumentException.class);
        javaLangMap.put("IllegalMonitorStateException", IllegalMonitorStateException.class);
        javaLangMap.put("IllegalStateException", IllegalStateException.class);
        javaLangMap.put("IllegalThreadStateException", IllegalThreadStateException.class);
        javaLangMap.put("IndexOutOfBoundsException", IndexOutOfBoundsException.class);
        javaLangMap.put("InstantiationException", InstantiationException.class);
        javaLangMap.put("InterruptedException", InterruptedException.class);
        javaLangMap.put("NegativeArraySizeException", NegativeArraySizeException.class);
        javaLangMap.put("NoSuchFieldException", NoSuchFieldException.class);
        javaLangMap.put("NoSuchMethodException", NoSuchMethodException.class);
        javaLangMap.put("NullPointerException", NullPointerException.class);
        javaLangMap.put("NumberFormatException", NumberFormatException.class);
        javaLangMap.put("RuntimeException", RuntimeException.class);
        javaLangMap.put("SecurityException", SecurityException.class);
        javaLangMap.put("StringIndexOutOfBoundsException", StringIndexOutOfBoundsException.class);
        javaLangMap.put("UnsupportedOperationException", UnsupportedOperationException.class);
        javaLangMap.put("AbstractMethodError", AbstractMethodError.class);
        javaLangMap.put("AssertionError", AssertionError.class);
        javaLangMap.put("ClassCircularityError", ClassCircularityError.class);
        javaLangMap.put("ClassFormatError", ClassFormatError.class);
        javaLangMap.put("Error", Error.class);
        javaLangMap.put("ExceptionInInitializerError", ExceptionInInitializerError.class);
        javaLangMap.put("IllegalAccessError", IllegalAccessError.class);
        javaLangMap.put("IncompatibleClassChangeError", IncompatibleClassChangeError.class);
        javaLangMap.put("InstantiationError", InstantiationError.class);
        javaLangMap.put("InternalError", InternalError.class);
        javaLangMap.put("LinkageError", LinkageError.class);
        javaLangMap.put("NoClassDefFoundError", NoClassDefFoundError.class);
        javaLangMap.put("NoSuchFieldError", NoSuchFieldError.class);
        javaLangMap.put("NoSuchMethodError", NoSuchMethodError.class);
        javaLangMap.put("OutOfMemoryError", OutOfMemoryError.class);
        javaLangMap.put("StackOverflowError", StackOverflowError.class);
        javaLangMap.put("ThreadDeath", ThreadDeath.class);
        javaLangMap.put("UnknownError", UnknownError.class);
        javaLangMap.put("UnsatisfiedLinkError", UnsatisfiedLinkError.class);
        javaLangMap.put("UnsupportedClassVersionError", UnsupportedClassVersionError.class);
        javaLangMap.put("VerifyError", VerifyError.class);
        javaLangMap.put("VirtualMachineError", VirtualMachineError.class);
    }

    /**
     * Get the class in java.lang package with the specified className
     * 
     * @param className
     *            the class's name
     * @return the corresponding class of the specified className
     * @throws ParsingException
     *             if className is not primitive type class name
     */
    public static Class getLangClass(String className)
    {
        return (Class) javaLangMap.get(className);
    }

    public static Class findClass(String className, ICacheableContext context) throws ParsingException
    {
        return findClass(className, context, false);
    }

    public static Class findClass(String className, ICacheableContext context, boolean isInnerClass)
            throws ParsingException
    {
        return _findClass(className, isInnerClass, context);
    }

    /**
     * relly do find class
     * 
     * @param className
     * @param isInnerClass
     * @param ctx
     * @return
     * @throws ParsingException
     */
    private static Class _findClass(String className, boolean isInnerClass, ICacheableContext ctx)
            throws ParsingException
    {
        Class foundClass = null;

        int dimension = 0;
        while (className.endsWith("[]"))
        {
            dimension++;
            className = className.substring(0, className.length() - 2);
        }

        if (ctx != null)
        {
            foundClass = ctx.loadClass(className);
        }
        else
        {
            try
            {
                foundClass = loadClass(className);
            }
            catch (ClassNotFoundException e)
            {
            }
        }
        if (foundClass == null)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_030,new Object[]{className});
        }
        if (isInnerClass)
        {
            int modifier = foundClass.getModifiers();
            if (!(Modifier.isPublic(modifier) && Modifier.isStatic(modifier)))
            {
            	throw new ParsingException(ExceptionConstants.EBOS_OOEE_030,new Object[]{className});
            }
        }

        for (int i = 0; i < dimension; i++)
        {
            Object arrayObject = Array.newInstance(foundClass, 0);
            foundClass = arrayObject.getClass();
        }

        return foundClass;
    }
    
    public static String trimExpression(String expressionString, Map classTypes) 
    {
        if (classTypes != null)
        {
            Map shortClassNames = new HashMap();

            for (Iterator i = classTypes.keySet().iterator(); i.hasNext();)
            {
                String className = (String) i.next();
                if (className.indexOf(".") == -1)
                {
                    try
                    {
                        ExpressionUtil.loadClass(className);
                    }
                    catch (ClassNotFoundException e)
                    {
                        shortClassNames.put(className, ((Class)classTypes.get(className)).getName());
                    }
                }
            }

            expressionString = replaceExpressionString(expressionString, shortClassNames);
        }

        return expressionString;
    }

    private static String replaceExpressionString(String expressionString, Map replaceStringMap)
    {
        if (expressionString == null || expressionString.length() == 0 || replaceStringMap == null || replaceStringMap.isEmpty())
        {
            return expressionString;
        }
        StringBuffer classBuffer = new StringBuffer();
        boolean ignoredQuotationContent = false;
        StringBuffer result = new StringBuffer();
        int len = expressionString.length();
        for (int i = 0; i < len; i++)
        {
            char c = expressionString.charAt(i);
            switch (c)
            {
            case '"':
                ignoredQuotationContent = !ignoredQuotationContent;
                result.append('"');
                break;
            case '\\':
                result.append('\\');
                if (i + 1 < len)
                {
                    ++i;
                    c = expressionString.charAt(i);
                    result.append(c);
                }
                else
                {
                	throw new I18NRuntimeException(ExceptionConstants.EBOS_OOEE_022);
                   // throw new IllegalArgumentException("\\ should not end in the expression");
                }
                break;
            case '\'':
                result.append('\'');
                if (!ignoredQuotationContent)
                {
                    if (i + 2 > len || expressionString.charAt(i + 2) != '\'')
                    {
                    	throw new I18NRuntimeException(ExceptionConstants.EBOS_OOEE_021);
                    }
                    result.append(expressionString.charAt(i + 1));
                    result.append('\'');
                    i += 2;
                }
                break;
            default:
                if (ignoredQuotationContent)
                {
                    result.append(c);
                    continue;
                }
                if (Character.isJavaIdentifierPart(c))
                {
                    classBuffer.append(c);
                }
                else
                {
                    boolean additive = true;
                    if (c == '.')
                    {
                        classBuffer.append(".");
                        additive = false;
                    }
                    if (additive && classBuffer.length() > 0)
                    {
                        String fullClassName = replace(new String(classBuffer), replaceStringMap);
                        result.append(fullClassName);
                        classBuffer.setLength(0);
                    }
                    
                    if (additive)
                    {
                        result.append(c);
                    }
                }
                break;
            }

        }

        return result.toString();
    }

    /**
     * replace class Name by class full Name 
     * for example change List to java.util.List
     * if class Name contains . then get the String that before the first "." to be replaced 
     * for example "List.invoke" get "List" string and replace it with java.util.List
     * if current string is "java.util.List" then get "java" string and try to replace it
     * so in most case it will be OK.
     * but if there existed a key with "java" in the classTypes Map, then "java" string also will be replaced. so it's a bug
     */
    private static String replace(String orign, Map replaceStringMap)
    {

        int index = orign.indexOf(".");
        String checkName = orign;
        if (index != -1)
        {
            checkName = orign.substring(0, index);
        }
        String result = orign;
        if (replaceStringMap.get(checkName) != null)
        {
            String classFullName = ((String) replaceStringMap.get(checkName));
            result = classFullName
                    + (index > 0 ? orign.substring(index) : orign.substring(checkName.length()));
        }

        return result;
    }

}
