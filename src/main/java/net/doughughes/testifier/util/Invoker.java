package net.doughughes.testifier.util;

import net.doughughes.testifier.exception.*;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Invoker {

    public static Object instantiate(Class<?> clazz, Object ... args) throws CannotFindConstructorException, CannotInstantiateClassException, CannotAccessMethodException {
        Class[] objects = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        Constructor constructor = null;
        try {
            constructor = getConstructor(clazz, objects);
        } catch (NoSuchMethodException e) {
            if(objects.length > 0) {
                throw new CannotFindConstructorException("Cannot find constructor for class '" + clazz.getCanonicalName() + " that accepts arguments " + Arrays.toString(objects), e);
            } else {
                throw new CannotFindConstructorException("Cannot find constructor for class '" + clazz.getCanonicalName() + " that accepts no arguments.", e);
            }
        }
        String modifiers = Modifier.toString(constructor.getModifiers());

        // validate access is allowed
        // if the packages are the same and the property is not private, then we can access the property
        if(!canAccessFromCaller(clazz, constructor)){
            throw new CannotAccessMethodException("Cannot access constructor for class '" + clazz.getCanonicalName() + " that accepts arguments " + Arrays.toString(objects) + ". Perhaps the access modifier (" + modifiers + ") is not correct?");
        }

        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CannotInstantiateClassException("Cannot instantiate class " + clazz.getCanonicalName() + ". Perhaps the constructor's access modifier (" + modifiers + ") is not correct?", e);
        }
    }

    public static Object invoke(Object object, String method, Object ... args) throws CannotFindMethodException, CannotAccessMethodException, CannotInvokeMethodException {
        Class[] objects = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        Method methodToInvoke = null;
        try {
            methodToInvoke = getMethod(object.getClass(), method, objects);
        } catch (NoSuchMethodException e) {
            if (objects.length > 0) {
                throw new CannotFindMethodException("Cannot find method " + method + "() that accepts these arguments " + Arrays.toString(objects), e);
            } else {
                throw new CannotFindMethodException("Cannot find method " + method + "() that accepts no arguments.", e);
            }
        }
        String modifiers = Modifier.toString(methodToInvoke.getModifiers());

        // validate access is allowed
        // if the packages are the same and the property is not private, then we can access the property
        if (!canAccessFromCaller(object.getClass(), methodToInvoke)) {
            throw new CannotAccessMethodException("Cannot access method " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?");
        }

        try {
            methodToInvoke.setAccessible(true);
            return methodToInvoke.invoke(object, args);
        } catch (IllegalAccessException e) {
            throw new CannotAccessMethodException("Cannot access method " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?");
        } catch (InvocationTargetException e) {
            throw new CannotInvokeMethodException(e.getTargetException());
        }
    }

    public static Enum getEnumValue(Class clazz, String name) throws CannotFindEnumException {
        try {
            return Enum.valueOf(clazz, name);
        } catch(Exception e){
            throw new CannotFindEnumException("Cannot find constant " + name + " for enum " + clazz.getName());
        }
    }

    public static Object invokeStatic(Class<?> clazz, String method, Object ... args) throws CannotFindMethodException, CannotAccessMethodException, CannotInvokeMethodException, MethodIsNotStaticException {
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
            throw new CannotAccessMethodException("Cannot access method " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?");
        }

        // validate method is static
        if(!Modifier.isStatic(methodToInvoke.getModifiers())){
            throw new MethodIsNotStaticException(method + "() method is not static.");
        }

        try {
            methodToInvoke.setAccessible(true);
            return methodToInvoke.invoke(null, args);
        } catch (IllegalAccessException e) {
            throw new CannotAccessMethodException("Cannot access method " + method + ". Perhaps the access modifier(s) (" + modifiers + ") is/are not correct?");
        } catch (InvocationTargetException e){
            throw new CannotInvokeMethodException(e.getTargetException());
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
        String callingFromClassName = getCallingFromClassName();
        return verifyAccess(callingFromClassName, clazz, method.getModifiers());
    }

    private static boolean canAccessFromCaller(Class<?> clazz, Constructor constructor) {
        String callingFromClassName = getCallingFromClassName();
        return verifyAccess(callingFromClassName, clazz, constructor.getModifiers());
    }

    private static String getCallingFromClassName() {
        try {
            throw new Exception("Intentionally throwing an exception so we can examine the stack trace");
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            return stackTrace[2].getClassName();
        }
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
            // if we found a match then we're done!
            if(compareParameters(method.getParameters(), objects)){
                matchingMethod = method;
                break;
            }
        }

        if(matchingMethod == null) throw new NoSuchMethodException("Could not find a method named '" + methodName + "' that accepts the specified arguments, " + objects);

        return matchingMethod;

    }



    private static Constructor getConstructor(Class<?> clazz, Class[] objects) throws NoSuchMethodException {


        List<Constructor> constructors = Arrays.asList(clazz.getConstructors());

        // filter to constructors which accept the correct number of parameters
        constructors = constructors.stream()
                // filter to methods with the same name
                .filter(constructor -> constructor.getParameters().length == objects.length)
                .collect(Collectors.toList());

        // no matches? throw an exception
        if(constructors.size() == 0) throw new NoSuchMethodException("Could not find a constructor that accepts the specified arguments, " + objects);

        Constructor matchingConstructor = null;

        // find a method that accepts the specified arguments
        for(Constructor constructor : constructors){
            // if we found a match then we're done!
            if(compareParameters(constructor.getParameters(), objects)){
                matchingConstructor = constructor;
                break;
            }
        }

        if(matchingConstructor == null) throw new NoSuchMethodException("Could not find a constructor that accepts the specified arguments, " + objects);

        return matchingConstructor;

    }

    private static boolean compareParameters(Parameter[] parameters, Class[] objects) {
        // assume this is a match!
        boolean matches = true;

        // iterate over this method's parameters
        for(int x = 0 ; x < parameters.length ; x++){
            // get this parameter's expected type
            Class requiredClass = parameters[x].getType();
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
                return false;
            }
        }

        return true;
    }
}
