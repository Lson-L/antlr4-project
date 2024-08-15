package com.lson.antlr4.java;

import com.lson.antlr4.java.generate.JavaParser;
import com.lson.antlr4.java.generate.JavaParserBaseListener;
import lombok.Getter;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.event.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 *
 *
 * @author lson
 *
 * @date 2024/8/13
 **/
@Getter
public class JavaLoggerListener extends JavaParserBaseListener {
    @Getter
    private boolean error;

    @Getter
    final TokenStreamRewriter rewriter;
    final TokenStream tokens;

    @Getter
    boolean needReWriter = false;

    private boolean hasImportLog = false;

    private String logIdentifierVar;

    private final Stack<String> currentFieldTypeStack = new Stack<>();

    Map<Level, Integer> logLevelMap = new HashMap<>();

    /** 创建一个绑定到词法符号流上的TokenStreamRewriter
     * 位于词法分析器和语法分析器之间
     */
    public JavaLoggerListener(TokenStream tokens) {
        this.tokens = tokens;
        rewriter = new TokenStreamRewriter(tokens);
        for (Level value : Level.values()) {
            logLevelMap.put(value, 0);
        }
    }

    private void incrementLogLevel(Level level) {
        logLevelMap.put(level, logLevelMap.get(level) + 1);
    }

    private void decrementLogLevel(Level level) {
        logLevelMap.put(level, logLevelMap.get(level) - 1);
        if (logLevelMap.get(level) < 0) {
            throw new RuntimeException("log level error");
        }
    }

    private boolean isLogLevelEnable(Level level) {
        return logLevelMap.get(level) > 0;
    }

    @Override
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        super.enterCompilationUnit(ctx);
    }


    @Override
    public void exitImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        JavaParser.QualifiedNameContext qualifiedNameContext = ctx.qualifiedName();
        String text = qualifiedNameContext.getText();
        if (StringUtils.equals(text, "org.slf4j.Logger")) {
            hasImportLog = true;
        }
    }

    @Override
    public void exitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
        if (!currentFieldTypeStack.isEmpty()) {
            String text = ctx.getText();
            String peek = currentFieldTypeStack.peek();
            if (hasImportLog && StringUtils.equals(text, "Logger")) {
                logIdentifierVar = peek;
            }
            if (StringUtils.equals(text, "org.slf4j.Logger")) {
                logIdentifierVar = peek;
            }
        }
    }


    @Override
    public void exitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        currentFieldTypeStack.pop();
    }

    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        StringBuilder varName = new StringBuilder();
        for (JavaParser.VariableDeclaratorContext variableDeclaratorContext : ctx.variableDeclarators().variableDeclarator()) {
            JavaParser.VariableDeclaratorIdContext variableDeclaratorIdContext = variableDeclaratorContext.variableDeclaratorId();
            if (!varName.isEmpty()) {
                varName.append(",");
            }
            varName.append(variableDeclaratorIdContext.getText());
        }
        currentFieldTypeStack.push(varName.toString());
    }


    private void logMethodCall(JavaParser.StatementContext statementContext) {
        if (statementContext.expression().isEmpty()) {
            return;
        }
        JavaParser.ExpressionContext ctx = statementContext.expression(0);
        if (ctx.bop == null) {
            return;
        }
        if (StringUtils.equals(ctx.bop.getText(), ".")) {
            JavaParser.ExpressionContext expression = ctx.expression(0);
            if (Objects.equals(expression.getText(), logIdentifierVar)) {
                JavaParser.MethodCallContext methodCallContext = ctx.methodCall();
                TerminalNode methodName = methodCallContext.identifier().IDENTIFIER();
                Level logLevel = findLevelMethod(methodName.getText());
                if (logLevel == null) {
                    return;
                }
                if (!isLogLevelEnable(logLevel)) {
                    if (isArgumentListIsInvokeMethod(methodCallContext.arguments())) {
                        String beforeInsert = String.format("""
                                        if(%s.is%sEnabled()){""",
                                logIdentifierVar, WordUtils.capitalize(logLevel.name().toLowerCase()));

                        Token start = statementContext.getStart();
                        Token token = tokens.get(start.getTokenIndex() - 1);
                        String ws = getWs(token);
//                        int startIndex = start.getCharPositionInLine();
                        needReWriter = true;
                        rewriter.insertBefore(start, beforeInsert + ws + "\t");
                        rewriter.insertAfter(statementContext.getStop(), ws + "}");
                    }
                }
            }
        }
    }

    @Override
    public void exitExpression(JavaParser.ExpressionContext ctx) {
        super.exitExpression(ctx);
    }


    public String getWs(Token token) {
        String text = token.getText();
        int i = text.lastIndexOf("\n");
        if (i != -1) {
            return text.substring(i);
        }
        return "";
    }

//    public String createSpace(int count) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(" ".repeat(Math.max(0, count)));
//        return sb.toString();
//    }


    private Level findLevelMethod(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        if (text.equals("error")) {
            return Level.ERROR;
        }
        if (text.equals("warn")) {
            return Level.WARN;
        }
        if (text.equals("info")) {
            return Level.INFO;
        }
        if (text.equals("debug")) {
            return Level.DEBUG;
        }
        if (text.equals("trace")) {
            return Level.TRACE;
        }
        return null;
    }


    public boolean isArgumentListIsInvokeMethod(JavaParser.ArgumentsContext argumentListContext) {
        if (argumentListContext == null) {
            return false;
        }
        JavaParser.ExpressionListContext expressionListContext = argumentListContext.expressionList();
        if (expressionListContext != null) {
            for (JavaParser.ExpressionContext expressionContext : expressionListContext.expression()) {
                if (expressionIsInvokeMethod(expressionContext)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean expressionIsInvokeMethod(JavaParser.ExpressionContext expressionContext) {
        JavaParser.MethodCallContext methodCallContext = expressionContext.methodCall();
        return methodCallContext != null;
    }


    private void checkLogLevelStatement(JavaParser.StatementContext ctx, boolean enter) {
        TerminalNode anIf = ctx.IF();
        if (anIf != null) {
            JavaParser.ParExpressionContext parExpressionContext = ctx.parExpression();
            Level level = findLevelStatement(parExpressionContext.getText());
            if (level != null) {
                if (enter) {
                    incrementLogLevel(level);
                } else {
                    decrementLogLevel(level);
                }
            }
        }
    }

    @Override
    public void enterStatement(JavaParser.StatementContext ctx) {
        checkLogLevelStatement(ctx, true);
    }

    @Override
    public void exitStatement(JavaParser.StatementContext ctx) {
        logMethodCall(ctx);
        checkLogLevelStatement(ctx, false);
    }

    private Level findLevelStatement(String expression) {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        if (expression.contains(logIdentifierVar + ".isErrorEnabled()")) {
            return Level.ERROR;
        }
        if (expression.contains(logIdentifierVar + ".isWarnEnabled()")) {
            return Level.WARN;
        }
        if (expression.contains(logIdentifierVar + ".isInfoEnabled()")) {
            return Level.INFO;
        }
        if (expression.contains(logIdentifierVar + ".isDebugEnabled()")) {
            return Level.DEBUG;
        }
        if (expression.contains(logIdentifierVar + ".isTraceEnabled()")) {
            return Level.TRACE;
        }
        return null;
    }


    @Override
    public void visitErrorNode(ErrorNode node) {
        super.visitErrorNode(node);
        error = true;
    }

}
