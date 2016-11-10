package net.doughughes.testifier.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Invoker {

    public static Object invoke(Object object, String method, Object ... args) throws Exception {
        Class[] objects = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        Method methodToInvoke = null;
        try {
            methodToInvoke = object.getClass().getMethod(method, objects);
        } catch (NoSuchMethodException e) {
            throw new Exception("Cannot find method " + method + "() that accepts these arguments " + Arrays.toString(objects), e);
        }

        return methodToInvoke.invoke(object, args);
    }

    public static Object invokeStatic(Class clazz, String method, Object ... args) throws Exception {
        Class[] objects = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        Method methodToInvoke = null;
        try {
            methodToInvoke = clazz.getMethod(method, objects);
        } catch (NoSuchMethodException e) {
            throw new Exception("Cannot find method " + method + "() that accepts these arguments " + Arrays.toString(objects), e);
        }

        return methodToInvoke.invoke(null, args);
    }

    public static Object readProperty(Object object, String property) throws Exception {
        Field fieldToRead = null;

        try {
            fieldToRead = object.getClass().getField(property);
        } catch (NoSuchFieldException e) {
            throw new Exception("Cannot find property " + property, e);
        }

        return fieldToRead.get(object);
    }
}
