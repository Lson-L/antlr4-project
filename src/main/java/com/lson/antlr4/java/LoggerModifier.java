package com.lson.antlr4.java;

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
            fire(originalFile);
            System.out.println("modify time:" + ((System.currentTimeMillis() - start) / 1000.0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new LoggerModifier(new File("D:\\workspace\\qes3_client")).startModify();
    }

    private void fire(File file) throws Exception{
        if(file.isFile()){
            if (!file.getName().endsWith(".java")) {
                return;
            }
            String input = FileUtils.readFileToString(file, "UTF-8");
            AntlrWrapper<JavaParser, JavaLexer> antlrWrapper = AntlrUtils.getAntlrWrapper(JavaLexer::new, JavaParser::new, input);
            JavaParser parser = antlrWrapper.getParser();
            ParseTree tree = parser.compilationUnit();

            JavaLoggerListener listener = new JavaLoggerListener(antlrWrapper.getTokenStream());
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, tree);
            if(listener.isError()){
                System.err.println("发现错误的文件："+file.getName());
            }
            if (listener.isNeedReWriter()) {
                TokenStreamRewriter rewriter = listener.getRewriter();
                FileUtils.write(file, rewriter.getText(), "UTF-8");
//                System.out.println(rewriter.getText());
            }
            count++;
            System.out.println("已经扫描完成:"+count);
        }else{
            File[] files = file.listFiles();
            for (File f : files) {
                fire(f);
            }
        }
    }
}
