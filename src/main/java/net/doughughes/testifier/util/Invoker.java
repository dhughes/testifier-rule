package net.doughughes.testifier.util;

import net.doughughes.testifier.exception.CannotAccessFieldException;
import net.doughughes.testifier.exception.CannotAccessMethodException;
import net.doughughes.testifier.exception.CannotFindFieldException;
import net.doughughes.testifier.exception.CannotFindMethodException;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Invoker {

    public static Object invoke(Object object, String method, Object ... args) throws CannotFindMethodException, CannotAccessMethodException {
        Class[] objects = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        Method methodToInvoke = null;
        try {
            methodToInvoke = getMethod(object.getClass(), method, objects);
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
            methodToInvoke.setAccessible(true);
            return methodToInvoke.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CannotAccessMethodException("Cannot access property " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?", e);
        }
    }

    public static Object invokeStatic(Class<?> clazz, String method, Object ... args) throws CannotFindMethodException, CannotAccessMethodException {
        Class[] objects = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        Method methodToInvoke = null;
        try {
            methodToInvoke = getMethod(clazz, method, objects);
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
            methodToInvoke.setAccessible(true);
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
        if(!canAccessFromCaller(object.getClass(), fieldToRead)){
            throw new CannotAccessFieldException("Cannot access property " + property + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?");
        }

        try {
            fieldToRead.setAccessible(true);
            return fieldToRead.get(object);
        } catch (IllegalAccessException e) {
            throw new CannotAccessFieldException("Cannot access property " + property + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?", e);
        }
    }

    private static boolean canAccessFromCaller(Class clazz, Field field) {
        String callingFromClassName;
        try {
            throw new Exception("Intentionally throwing an exception so we can examine the stack trace");
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            callingFromClassName = stackTrace[2].getClassName();
        }

        return verifyAccess(callingFromClassName, clazz, field.getModifiers());
    }

    private static boolean canAccessFromCaller(Class clazz, Method method) {
        String callingFromClassName;
        try {
            throw new Exception("Intentionally throwing an exception so we can examine the stack trace");
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            callingFromClassName = stackTrace[2].getClassName();
        }

        return verifyAccess(callingFromClassName, clazz, method.getModifiers());
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

    /**
     * This method finds methods on a given class based on the name and the
     * specified arguments. It's similar to class.getMethods(), but it
     * will take into account autoboxing primitives.
     * @param clazz
     * @param methodName
     * @param objects
     * @return
     */
    private static Method getMethod(Class<?> clazz, String methodName, Class[] objects) throws NoSuchMethodException {

        List<Method> methods = Arrays.asList(clazz.getMethods());

        // filter to methods with the correct name and which accept the correct number of parameters
        methods = methods.stream()
                // filter to methods with the same name
                .filter(method -> method.getName().equals(methodName) && method.getParameters().length == objects.length)
                .collect(Collectors.toList());

        // no matches? throw an exception
        if(methods.size() == 0) throw new NoSuchMethodException("Could not find a method named '" + methodName + "' that accepts the specified arguments, " + objects);

        Method matchingMethod = null;

        // find a method that accepts the specified arguments
        for(Method method : methods){
            // assume we have a match
            boolean matches = true;

            // iterate over this method's parameters
            for(int x = 0 ; x < method.getParameters().length ; x++){
                // get this parameter's expected type
                Class requiredClass = method.getParameters()[x].getType();
                Class providedClass = objects[x];

                // is this required parameter primitive?
                if(requiredClass.isPrimitive()) {
                    // yes. the provided class must be able to be unboxed to this primitive type
                    try {
                        // get the primitive class for the provided parameter, if possible
                        Class primitiveProvidedClass = (Class) providedClass.getField("TYPE").get(null);

                        // do the parameter types match?
                        matches = requiredClass.equals(primitiveProvidedClass);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        // if we get an error then we know the provided parameter isn't the correct type
                        matches = false;
                    }
                }  else {
                    // no. the required and provided classes do not match
                    matches = requiredClass.equals(providedClass);
                }

                // does this parameter NOT match?
                if(!matches){
                    // stop iterating over the arguments and try the next method.
                    break;
                }
            }

            // if we found a match then we're done!
            if(matches){
                matchingMethod = method;
                break;
            }
        }

        if(matchingMethod == null) throw new NoSuchMethodException("Could not find a method named '" + methodName + "' that accepts the specified arguments, " + objects);

        return matchingMethod;

    }
}
