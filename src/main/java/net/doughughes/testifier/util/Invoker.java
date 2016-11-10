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
        String modifiers = Modifier.toString(methodToInvoke.getModifiers());

        // validate access is allowed
        // if the packages are the same and the property is not private, then we can access the property
        if(!canAccessFromCaller(object.getClass(), methodToInvoke)){
            throw new CannotAccessMethodException("Cannot access property " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?");
        }

        try {
            return methodToInvoke.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
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
        String modifiers = Modifier.toString(methodToInvoke.getModifiers());

        // validate access is allowed
        // if the packages are the same and the property is not private, then we can access the property
        if(!canAccessFromCaller(clazz, methodToInvoke)){
            throw new CannotAccessMethodException("Cannot access property " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?");
        }

        try {
            return methodToInvoke.invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CannotAccessMethodException("Cannot access property " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?", e);
        }
    }

    public static Object readProperty(Object object, String property) throws CannotFindFieldException, CannotAccessFieldException {
        Field fieldToRead = null;

        // get the target and calling class' packages so we can validate access

        try {
            fieldToRead = object.getClass().getDeclaredField(property);
        } catch (NoSuchFieldException e) {
            throw new CannotFindFieldException("Cannot find property " + property, e);
        }
        String modifiers = Modifier.toString(fieldToRead.getModifiers());

        // validate access is allowed
        // if the packages are the same and the property is not private, then we can access the property
        if(!canAccessFromCaller(object, fieldToRead)){
            throw new CannotAccessFieldException("Cannot access property " + property + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?");
        }

        try {
            fieldToRead.setAccessible(true);
            return fieldToRead.get(object);
        } catch (IllegalAccessException e) {
            throw new CannotAccessFieldException("Cannot access property " + property + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?", e);
        }
    }

    private static boolean canAccessFromCaller(Object object, Field field) {
        String callingFromClassName;
        try {
            throw new Exception("Intentionally throwing an exception so we can examine the stack trace");
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            callingFromClassName = stackTrace[2].getClassName();
        }

        return verifyAccess(callingFromClassName, object.getClass(), field.getModifiers());
    }

    private static boolean canAccessFromCaller(Object object, Method method) {
        String callingFromClassName;
        try {
            throw new Exception("Intentionally throwing an exception so we can examine the stack trace");
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            callingFromClassName = stackTrace[2].getClassName();
        }

        return verifyAccess(callingFromClassName, object.getClass(), method.getModifiers());
    }

    private static boolean verifyAccess(String callingFromClassName, Class targetClass, int modifiers){
        try {
            Package callingFromPackage = Class.forName(callingFromClassName).getPackage();

            // if the target object's field is private then the method is not accessible
            // or, if the field is protected and the packages don't match the method is not accessible
            if(Modifier.isPrivate(modifiers) ||
                    (Modifier.isProtected(modifiers) && !targetClass.getPackage().equals(callingFromPackage))){
                return false;
            }
        } catch (ClassNotFoundException e) {
            return false;
        }

        return true;
    }
}
