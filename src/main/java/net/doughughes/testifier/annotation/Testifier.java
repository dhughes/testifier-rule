package net.doughughes.testifier.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Testifier {
    String sourcePath() default "";

    Class clazz() default Object.class;

    String method() default "";

    Class[] args() default {};
}
