package net.doughughes.testifier.util;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitor;

import java.util.List;

import static com.github.javaparser.ast.internal.Utils.isNullOrEmpty;

/**
 * Created by doug on 11/7/16.
 */
public class DescriptionVisitor implements VoidVisitor<Object> {

    private StringBuilder description = new StringBuilder();

    private void visitChildren(List children) {
        if (!isNullOrEmpty(children)) {
            for (Object child : children) {
                ((Node) child).accept(this, null);
                // add a blank space
                description.append(" ");
            }
        }
    }

    private void visit(Node node){
        if(node instanceof NamedNode) {
            description
                    .append(node.getClass().getSimpleName())
                    .append("[")
                    .append(((NamedNode) node).getName())
                    .append("]")
                    .append(" ");

        } else if(node instanceof StringLiteralExpr){
            description
                    .append(node.getClass().getSimpleName())
                    .append("[")
                    .append(((StringLiteralExpr) node).getValue())
                    .append("]")
                    .append(" ");
        } else {
            description
                    .append(node.getClass().getSimpleName())
                    .append(" ");
        }

        visitChildren(node.getChildrenNodes());
    }

    private void visitAsBlock(Node node){
        visit(node);

        description
                .append("/")
                .append(node.getClass().getSimpleName());

    }

    @Override
    public String toString() {
        return description.toString().replaceAll("  ", " ").trim();
    }

    @Override
    public void visit(CompilationUnit n, Object arg) {

        // visit the package statement
        if (n.getPackage() != null) {
            n.getPackage().accept(this, arg);
        }

        // visit imports
        visitChildren(n.getImports());

        // visit the types
        visitChildren(n.getTypes());
    }

    @Override
    public void visit(PackageDeclaration n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(TypeParameter n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(LineComment n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(BlockComment n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        visitAsBlock(n);
    }

    @Override
    public void visit(EnumDeclaration n, Object arg) {
        visitAsBlock(n);
    }

    @Override
    public void visit(EmptyTypeDeclaration n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(EnumConstantDeclaration n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(AnnotationDeclaration n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(AnnotationMemberDeclaration n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(VariableDeclarator n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(VariableDeclaratorId n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ConstructorDeclaration n, Object arg) {
        visitAsBlock(n);
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        visitAsBlock(n);
    }

    @Override
    public void visit(Parameter n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(MultiTypeParameter n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(EmptyMemberDeclaration n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(InitializerDeclaration n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(JavadocComment n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ClassOrInterfaceType n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(PrimitiveType n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ReferenceType n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(IntersectionType n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(UnionType n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(VoidType n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(WildcardType n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(UnknownType n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ArrayAccessExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ArrayCreationExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ArrayInitializerExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(AssignExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(BinaryExpr n, Object arg) {
        n.getLeft().accept(this, null);

        description
                .append(" ")
                .append(n.getOperator())
                .append(" ");

        n.getRight().accept(this, null);
    }

    @Override
    public void visit(CastExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ClassExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ConditionalExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(EnclosedExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(FieldAccessExpr n, Object arg) {
        visit(n);

    }

    @Override
    public void visit(InstanceOfExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(StringLiteralExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(IntegerLiteralExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(LongLiteralExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(IntegerLiteralMinValueExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(LongLiteralMinValueExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(CharLiteralExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(DoubleLiteralExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(BooleanLiteralExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(NullLiteralExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(MethodCallExpr n, Object arg) {
        visitAsBlock(n);

    }

    @Override
    public void visit(NameExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ObjectCreationExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(QualifiedNameExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ThisExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(SuperExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(UnaryExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(MarkerAnnotationExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(SingleMemberAnnotationExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(NormalAnnotationExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(MemberValuePair n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(TypeDeclarationStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(AssertStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(BlockStmt n, Object arg) {
        visitAsBlock(n);
    }

    @Override
    public void visit(LabeledStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(EmptyStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ExpressionStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(SwitchStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(SwitchEntryStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(BreakStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ReturnStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(IfStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(WhileStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ContinueStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(DoStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ForeachStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ForStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(ThrowStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(SynchronizedStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(TryStmt n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(CatchClause n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(LambdaExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(MethodReferenceExpr n, Object arg) {
        visit(n);
    }

    @Override
    public void visit(TypeExpr n, Object arg) {
        visit(n);
    }
}
