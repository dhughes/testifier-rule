package net.doughughes.testifier.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.TreeVisitor;
import net.doughughes.testifier.exception.CannotFindMethodException;
import net.doughughes.testifier.util.DescriptionVisitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SourceCodeService {

    private String className;
    private String sourcePath;
    private CompilationUnit compilationUnit;

    public SourceCodeService(String className, String sourcePath) throws FileNotFoundException, ParseException {
        this.className = className;
        this.sourcePath = sourcePath;
        // get an input stream for the source file
        FileInputStream in = new FileInputStream(sourcePath);
        // parse the file
        this.compilationUnit =  JavaParser.parse(in);
    }

    public String getClassName() {
        return className;
    }

    public String getSource() {
        return this.compilationUnit.toString();
    }

    public String getMethodSource(String methodName, Class... args) throws IOException, ParseException, CannotFindMethodException {
        BodyDeclaration method = getMethodStructure(methodName, args);
        if(method != null) {
            return getMethodStructure(methodName, args).toStringWithoutComments();
        } else {
            return "";
        }
    }

    public MethodDeclaration getMethodStructure(String methodName, Class... args) throws CannotFindMethodException {

        List<MethodDeclaration> methodDeclarations = new ArrayList<>();

        TreeVisitor visitor = new TreeVisitor() {

            @Override
            public void process(Node node) {
                if(MethodDeclaration.class.isInstance(node)) {
                    MethodDeclaration method = (MethodDeclaration) node;
                    if(method.getName().equals(methodName)) {
                        List<String> methodParameterTypeStrings = method.getParameters().stream().map(parameter -> parameter.getType().toString()).collect(Collectors.toList());
                        List<String> requiredParameterTypeStrings = Arrays.stream(args).map(Class::getSimpleName).collect(Collectors.toList());
                        if(methodParameterTypeStrings.equals(requiredParameterTypeStrings)) {
                            methodDeclarations.add((MethodDeclaration) node);
                        }
                    }
                }
            }
        };

        visitor.visitDepthFirst(this.compilationUnit);

        if(methodDeclarations.size() == 0){
            throw new CannotFindMethodException("Cannot find a method named '" + methodName + "' on class '" + getClassName() + "'.");
        }

        return methodDeclarations.get(0);
    }

    public String getDescriptionOfMethod(String methodName, Class... args) throws CannotFindMethodException {
        DescriptionVisitor describeVisitor = new DescriptionVisitor();

        describeVisitor.visit(getMethodStructure(methodName, args), null);

        return describeVisitor.toString();
    }

    public String getSourcePath() {
        return sourcePath;
    }
}
