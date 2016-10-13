package net.doughughes.testifier.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

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

        // this is some ugly code. I suspect I don't really understand well how the javaparser
        // library is working. There's probably an easier or better way to do this, but I'm not sure
        // what it is at this moment.
        BodyDeclaration matchingMethod = cu.getTypes().get(0).getMembers().stream()
                // get methods with the right name
                .filter(member -> member instanceof MethodDeclaration
                        && ((MethodDeclaration) member).getName().equals(methodName))
                // get the one method with the matching argument types
                .filter(method -> {
                    // get the arguments on this method
                    List<Parameter> parameters = ((MethodDeclaration) method).getParameters();

                    // are the number of arguments on this method the same as the method we're looking for?
                    if(parameters.size() == args.length) {
                        // yay! they match size! But what about data types?
                        for (int x = 0; x < args.length; x++) {
                            // for whatever reason I can't find a way in javaparser to get a class representing
                            // the parameter type. As such, I have to match it by name as a string.
                            if (!parameters.get(x).getType().toString().equals(args[x].getSimpleName()) &&
                                    !parameters.get(x).getType().toString().equals(args[x].getName())) {
                                // this parameter was not of the expected type so this method doesn't match
                                return false;
                            }
                        }
                        // all types matched, this is the method we're looking for!
                        return true;
                    } else {
                        // the number of arguments aren't even the same so this isn't the method we're looking for.
                        return false;
                    }
                }).findFirst().get();

        // only one method should match
        return matchingMethod.toString();
    }

    public String getClassSource() {
        return cu.toString();
    }
}
