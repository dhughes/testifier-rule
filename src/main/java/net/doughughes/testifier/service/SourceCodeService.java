package net.doughughes.testifier.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.TreeVisitor;
import net.doughughes.testifier.exception.CannotFindFieldException;
import net.doughughes.testifier.exception.CannotFindMethodException;
import net.doughughes.testifier.util.DescriptionVisitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SourceCodeService {

    private String className;
    private String sourcePath;
    private String rawSource;
    private CompilationUnit compilationUnit;

    public SourceCodeService(String className, String sourcePath) throws FileNotFoundException, ParseException {
        this.className = className;
        this.sourcePath = sourcePath;
        // get an input stream for the source file
        FileInputStream in = new FileInputStream(sourcePath);

        // get the raw source from the specified file
        this.rawSource = new Scanner(in).useDelimiter("\\Z").next();;

        // parse the source
        this.compilationUnit = JavaParser.parse(new StringReader(this.rawSource));
    }

    public String getClassName() {
        return className;
    }

    public String getSource() {
        return this.rawSource;
    }

    public String getMethodSource(String methodName, Class... args) throws IOException, ParseException, CannotFindMethodException {
        BodyDeclaration method = getMethodStructure(methodName, args);
        if(method != null) {
            return getMethodStructure(methodName, args).toStringWithoutComments();
        } else {
            return "";
        }
    }

    public FieldDeclaration getPropertyStructure(String propertyName) throws CannotFindFieldException {

        final FieldDeclaration[] matchedProperty = {null};

        TreeVisitor visitor = new TreeVisitor() {

            @Override
            public void process(Node node) {
                if(FieldDeclaration.class.isInstance(node)) {
                    FieldDeclaration property = (FieldDeclaration) node;

                    if(property.getVariables().get(0).getId().getName().equals(propertyName)){
                        matchedProperty[0] = property;
                    }
                }
            }
        };

        visitor.visitDepthFirst(this.compilationUnit);

        if(matchedProperty[0] == null){
            throw new CannotFindFieldException("Cannot find a property named '" + propertyName + "' on class '" + getClassName() + "'.");
        }

        return matchedProperty[0];
    }

    public String getDescriptionOfProperty(String propertyName) throws CannotFindFieldException {
        DescriptionVisitor describeVisitor = new DescriptionVisitor();

        describeVisitor.visit(getPropertyStructure(propertyName), null);

        return describeVisitor.toString();
    }

    public MethodDeclaration getMethodStructure(String methodName, Class... args) throws CannotFindMethodException {

        List<MethodDeclaration> methodDeclarations = new ArrayList<>();

        // convert the list of arguments as classes that we're looking for to a list of strings
        List<String> requiredParameterTypeStrings = Arrays.stream(args).map(Class::getSimpleName).collect(Collectors.toList());

        TreeVisitor visitor = new TreeVisitor() {

            @Override
            public void process(Node node) {
                // we're only concerned with nodes that are Method Declarations
                if(MethodDeclaration.class.isInstance(node)) {

                    // cast our node to be a MethodDeclaration
                    MethodDeclaration method = (MethodDeclaration) node;

                    // does this method's name match the name we're looking for?
                    if(method.getName().equals(methodName)) {
                        // get a list of argument data types (without generics) on the method we found
                        List<String> methodParameterTypeStrings = method.getParameters().stream().map(parameter -> parameter.getType().toString().replaceAll("<.*?>", "")).collect(Collectors.toList());

                        // does the list of arguments on the method match the list of arguments we're looking for?
                        if(methodParameterTypeStrings.equals(requiredParameterTypeStrings)) {
                            // add this to the set of matched methods (there really should be only one)
                            methodDeclarations.add((MethodDeclaration) node);
                        }
                    }
                }
            }
        };

        // visit each node in the document
        visitor.visitDepthFirst(this.compilationUnit);

        // did we find something?
        if(methodDeclarations.size() == 0){
            // that's an error
            throw new CannotFindMethodException("Cannot find a method named '" + methodName + "' on class '" + getClassName() + "'.");
        }

        // return what we found
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
