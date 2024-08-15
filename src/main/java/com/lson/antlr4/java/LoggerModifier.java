package com.lson.antlr4.java;

import com.alibaba.fastjson2.JSON;
import com.lson.antlr4.java.generate.JavaLexer;
import com.lson.antlr4.java.generate.JavaParser;
import com.lson.antlr4.java.utils.AntlrUtils;
import com.lson.antlr4.java.utils.AntlrWrapper;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 *
 *
 * @author lson
 *
 * @date 2024/8/14
 **/
public class LoggerModifier {

    private final File originalFile;

    public LoggerModifier(File originalFile) {
        this.originalFile = originalFile;
    }

    private int count = 0;

    public void startModify() {
        try {
            long start = System.currentTimeMillis();
            JavaFold javaFold = JavaFold.createJavaFold(originalFile);

            fire(originalFile, javaFold);
            System.out.println("modify time:" + ((System.currentTimeMillis() - start) / 1000.0));
            printJavaFold(javaFold);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void printJavaFold(JavaFold javaFold) {
        toPrint(javaFold, "", 0);
    }

    private void toPrint(JavaFile javaFile, String pre, int level) {
        if (javaFile instanceof JavaFold javaFold) {
            if(!javaFold.isOnlyFold()){
                if (javaFold.getCount()>100) {
                    System.out.println(javaFold.getPath()+"-->"+javaFold.getCount());
                }
            }
            for (JavaFile child : javaFold.getChildren()) {
                toPrint(child, pre, level);
            }
        }
//        if (javaFile instanceof JavaFold javaFold) {
//            if (javaFold.isOnlyFold()) {
//                pre += javaFold.getName() + "/";
//            } else {
//                System.out.println(createPre(level) +">"+ pre + javaFile.getName()+":"+javaFold.getCount());
//            }
//
//            for (JavaFile child : javaFold.getChildren()) {
//                if(!javaFold.isOnlyFold()){
//                    level++;
//                }
//                toPrint(child, pre, level);
//            }
//        }
    }

    private String createPre(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("--");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        new LoggerModifier(new File("D:\\workspace\\qes3_client")).startModify();
    }

    private void fire(File file, JavaFold javaFold) throws Exception {
        if (file.isFile()) {
            if (!file.getName().endsWith(".java")) {
                return;
            }
            JavaFile javaFile = JavaFile.createJavaFold(file);
            javaFile.setParent(javaFold);
            javaFold.incrementCount();
            javaFold.getChildren().add(javaFile);
            String input = FileUtils.readFileToString(file, "UTF-8");
            AntlrWrapper<JavaParser, JavaLexer> antlrWrapper = AntlrUtils.getAntlrWrapper(JavaLexer::new, JavaParser::new, input);
            JavaParser parser = antlrWrapper.getParser();
            ParseTree tree = parser.compilationUnit();

            JavaLoggerListener listener = new JavaLoggerListener(antlrWrapper.getTokenStream());
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, tree);
            if (listener.isError()) {
                System.err.println("发现错误的文件：" + file.getName());
            }
            if (listener.isNeedReWriter()) {
                TokenStreamRewriter rewriter = listener.getRewriter();
                FileUtils.write(file, rewriter.getText(), "UTF-8");
//                System.out.println(rewriter.getText());
            }
            count++;
            System.out.println("已经扫描完成:" + count);
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                JavaFold fold;
                if (f.isDirectory()) {
                    fold = JavaFold.createJavaFold(f);
                    fold.setParent(javaFold);
                    javaFold.getChildren().add(fold);
                } else {
                    fold = javaFold;
                }
                fire(f, fold);
            }
        }
    }
}
