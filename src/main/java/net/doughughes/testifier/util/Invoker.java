package net.doughughes.testifier.util;

import net.doughughes.testifier.exception.CannotAccessFieldException;
import net.doughughes.testifier.exception.CannotAccessMethodException;
import net.doughughes.testifier.exception.CannotFindFieldException;
import net.doughughes.testifier.exception.CannotFindMethodException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class Invoker {

    public static Object invoke(Object object, String method, Object ... args) throws CannotFindMethodException, CannotAccessMethodException {
        Class[] objects = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        Method methodToInvoke = null;
        try {
            methodToInvoke = object.getClass().getDeclaredMethod(method, objects);
        } catch (NoSuchMethodException e) {
            if(objects.length > 0) {
                throw new CannotFindMethodException("Cannot find method " + method + "() that accepts these arguments " + Arrays.toString(objects), e);
            } else {
                throw new CannotFindMethodException("Cannot find method " + method + "() that accepts no arguments.", e);
            }
        }

        try {
            return methodToInvoke.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            String modifiers = Modifier.toString(methodToInvoke.getModifiers());
            throw new CannotAccessMethodException("Cannot access property " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?", e);
        }
    }

    public static Object invokeStatic(Class<?> clazz, String method, Object ... args) throws CannotFindMethodException, CannotAccessMethodException {
        Class[] objects = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        Method methodToInvoke = null;
        try {
            methodToInvoke = clazz.getDeclaredMethod(method, objects);
        } catch (NoSuchMethodException e) {
            if(objects.length > 0) {
                throw new CannotFindMethodException("Cannot find method " + method + "() that accepts these arguments " + Arrays.toString(objects), e);
            } else {
                throw new CannotFindMethodException("Cannot find method " + method + "() that accepts no arguments.", e);
            }
        }

        try {
            return methodToInvoke.invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            String modifiers = Modifier.toString(methodToInvoke.getModifiers());
            throw new CannotAccessMethodException("Cannot access property " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?", e);
        }
    }

    public static Object readProperty(Object object, String property) throws CannotFindFieldException, CannotAccessFieldException {
        Field fieldToRead = null;

        try {
            fieldToRead = object.getClass().getDeclaredField(property);
        } catch (NoSuchFieldException e) {
            throw new CannotFindFieldException("Cannot find property " + property, e);
        }

        try {
            return fieldToRead.get(object);
        } catch (IllegalAccessException e) {
            String modifiers = Modifier.toString(fieldToRead.getModifiers());
            throw new CannotAccessFieldException("Cannot access property " + property + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?", e);
        }
    }
}
