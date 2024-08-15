package com.lson.antlr4.java;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 *
 *
 * @author lson
 *
 * @date 2024/8/15
 **/
@Getter
@Setter
public class JavaFile {
    private String path;
    private JavaFold parent;
    private String name;

    private boolean error;
    private final File file;

    public JavaFile(File file) {
        this.file = file;
    }

    public static JavaFile createJavaFold(File file){
        JavaFile javaFile = new JavaFile(file);
        javaFile.setName(file.getName());
        javaFile.setPath(file.getAbsolutePath());
        return javaFile;
    }
}
