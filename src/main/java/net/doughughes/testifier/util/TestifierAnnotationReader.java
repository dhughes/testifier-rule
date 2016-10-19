package net.doughughes.testifier.util;


import net.doughughes.testifier.annotation.Testifier;
import org.junit.runner.Description;

public class TestifierAnnotationReader {

    private final Testifier classAnnotation;
    private final Testifier methodAnnotation;

    public TestifierAnnotationReader(Testifier classAnnotation, Testifier methodAnnotation) {
        this.classAnnotation = classAnnotation;
        this.methodAnnotation = methodAnnotation;
    }

    public Class getClazz() {
        if(methodAnnotation.clazz() != null){
            return methodAnnotation.clazz();
        } else {
            return classAnnotation.clazz();
        }
    }

    public String getSourcePath() {
        if(!methodAnnotation.sourcePath().isEmpty()){
            return methodAnnotation.sourcePath();
        } else {
            return classAnnotation.sourcePath();
        }
    }

    public String getMethod() {
        if(!methodAnnotation.method().isEmpty()){
            return methodAnnotation.method();
        } else {
            return classAnnotation.method();
        }
    }

    public boolean getConstructor() {
        return methodAnnotation.constructor() || classAnnotation.constructor();
    }

    public Class[] getArgs() {
        if(methodAnnotation.args().length != 0){
            return methodAnnotation.args();
        } else {
            return classAnnotation.args();
        }
    }
}
