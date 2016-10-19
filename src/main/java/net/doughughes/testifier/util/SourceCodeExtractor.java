package net.doughughes.testifier.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceCodeExtractor {

    private String sourcePath;
    private CompilationUnit cu;

    public SourceCodeExtractor(String sourcePath) throws ParseException, IOException {
        this.sourcePath = sourcePath;

        // creates an input stream for the file to be parsed
        // note: huh. I've never seen this syntax before. Kinda interesting!
        try (FileInputStream in = new FileInputStream(sourcePath)) {
            // parse the file
            this.cu = JavaParser.parse(in);
        }
    }

    public String getMethodSource(String methodName, Class... args) throws IOException, ParseException {
        BodyDeclaration method = getMethodStructure(methodName, args);
        if(method != null) {
            return getMethodStructure(methodName, args).toString();
        } else {
            return "";
        }
    }

    public BodyDeclaration getMethodStructure(String methodName, Class... args) throws IOException, ParseException {

        // only one method should match
        Optional<BodyDeclaration> body = cu.getTypes().get(0).getMembers().stream()
                // get methods with the right name
                .filter(member -> member instanceof MethodDeclaration
                        && ((MethodDeclaration) member).getName().equals(methodName))
                // get the one method with the matching argument types
                .filter(method -> argumentsMatchPattern(method, args))
                .findFirst();


        if(body.isPresent()){
            return body.get();
        } else {
            return null;
        }

    }

    public String getConstructorSource(Class[] args) throws IOException, ParseException {
        BodyDeclaration constructor = getConstructorStructure(args);

        if(constructor != null) {
            return constructor.toString();
        } else {
            return "";
        }
    }

    public BodyDeclaration getConstructorStructure(Class... args) throws IOException, ParseException {

        // only one constructor should match
        Optional<BodyDeclaration> body = cu.getTypes().get(0).getMembers().stream()
                // get constructors with the right name
                .filter(member -> member instanceof ConstructorDeclaration)
                // get the one constructor with the matching argument types
                .filter(constructor -> argumentsMatchPattern(constructor, args))
                .findFirst();

        if(body.isPresent()){
            return body.get();
        } else {
            return null;
        }

    }

    private boolean argumentsMatchPattern(BodyDeclaration declaration, Class[] args){

        // this is some ugly code. I suspect I don't really understand well how the javaparser
        // library is working. There's probably an easier or better way to do this, but I'm not sure
        // what it is at this moment.

        // get the arguments on this constructor
        List<Parameter> parameters = new ArrayList<>();

        if (declaration instanceof ConstructorDeclaration){
            parameters = ((ConstructorDeclaration) declaration).getParameters();
        } else if(declaration instanceof MethodDeclaration) {
            parameters = ((MethodDeclaration) declaration).getParameters();
        } else {
            return false;
        }

        // are the number of arguments on this item the same as what we're looking for?
        if(parameters.size() == args.length) {
            // yay! they match size! But what about data types?
            for (int x = 0; x < args.length; x++) {
                // for whatever reason I can't find a way in javaparser to get a class representing
                // the parameter type. As such, I have to match it by name as a string.
                if (!parameters.get(x).getType().toString().equals(args[x].getSimpleName()) &&
                        !parameters.get(x).getType().toString().equals(args[x].getName())) {
                    // this parameter was not of the expected type so this doesn't match
                    return false;
                }
            }
            // all types matched, this isn't what we're looking for!
            return true;
        } else {
            // the number of arguments aren't even the same so this isn't what we're looking for.
            return false;
        }
    }

    public String getMethodDescription(String methodName, Class... args) throws IOException, ParseException {
        BodyDeclaration structure = getMethodStructure(methodName, args);

        return getDescription(structure);
    }

    public String getDescription(Node expression){
        StringBuilder description = new StringBuilder();

        // describe this
        if(expression instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expression;

            description
                    .append(getDescription(binaryExpr.getLeft()))
                    .append(" ")
                    .append(binaryExpr.getOperator())
                    .append(" ")
                    .append(getDescription(binaryExpr.getRight()));

        } else if(expression instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) expression;

            description
                    .append(expression.getClass().getSimpleName())
                    .append("[")
                    .append(methodDeclaration.getName())
                    .append("]")
                    .append(" ")
                    .append(getChildDescriptions(expression.getChildrenNodes()));

        } else if(expression instanceof MethodCallExpr){
            MethodCallExpr methodCallExpr = (MethodCallExpr) expression;

            description
                    .append(expression.getClass().getSimpleName())
                    .append("[")
                    .append(methodCallExpr.getName())
                    .append("]")
                    .append(" ")
                    .append(getChildDescriptions(expression.getChildrenNodes()))
                    .append(" /")
                    .append(expression.getClass().getSimpleName());

        } else if(expression instanceof FieldAccessExpr){
            FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) expression;

            description
                    .append(expression.getClass().getSimpleName())
                    .append("[")
                    .append(fieldAccessExpr.getField())
                    .append("]")
                    .append(" ")
                    .append(getChildDescriptions(expression.getChildrenNodes()));

        } else if(expression instanceof NameExpr){
            NameExpr nameExpr = (NameExpr) expression;

            description
                    .append(expression.getClass().getSimpleName())
                    .append("[")
                    .append(nameExpr.getName())
                    .append("]")
                    .append(" ")
                    .append(getChildDescriptions(expression.getChildrenNodes()));

        } else if(expression instanceof BlockStmt ||
                expression instanceof ExpressionStmt ||
                expression instanceof EnclosedExpr) {

            description
                    .append(expression.getClass().getSimpleName())
                    .append(" ")
                    .append(getChildDescriptions(expression.getChildrenNodes()))
                    .append(" ")
                    .append("/")
                    .append(expression.getClass().getSimpleName());

        } else if(expression instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr) expression;

            description
                    .append(expression.getClass().getSimpleName())
                    .append("[")
                    .append(unaryExpr.getOperator().toString())
                    .append("]")
                    .append(" ")
                    .append(getChildDescriptions(expression.getChildrenNodes()));
            ;

        } else if(expression instanceof StringLiteralExpr) {
            StringLiteralExpr stringLiteralExpr = (StringLiteralExpr)expression;

            description
                    .append(expression.getClass().getSimpleName())
                    .append("[")
                    .append(stringLiteralExpr.getValue())
                    .append("]");

        } else {
            description
                    .append(expression.getClass().getSimpleName())
                    .append(" ")
                    .append(getChildDescriptions(expression.getChildrenNodes()));
        }

        return description.toString().replaceAll("\\s+", " ");
    }

    private String getChildDescriptions(List<Node> expressions){
        StringBuilder description = new StringBuilder();

        for(Node child : expressions){
            // add a blank space
            description.append(getDescription(child)).append(" ");
        }

        return description.toString();
    }

    public String getClassSource() {
        return cu.toString();
    }

    private Stream<Node> flattenStructure(Node structure) {
        return Stream.concat(
                Stream.of(structure),
                structure.getChildrenNodes().stream().flatMap(this::flattenStructure)
        );
    }

    public List<Node> filterFlattenedNodes(Class clazz, String methodName, Class... args) throws IOException, ParseException {
        BodyDeclaration structure = getMethodStructure(methodName, args);
        return flattenStructure(structure)
                .filter(node -> node.getClass().isAssignableFrom(clazz))
                .collect(Collectors.toList());
    }
}
