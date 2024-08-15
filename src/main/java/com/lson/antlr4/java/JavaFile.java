package com.lson.antlr4.java;


import java.io.File;

/**
 *
 *
 * @author lson
 *
 * @date 2024/8/15
 **/
public class JavaFile {
    private String path;
    private JavaFold parent;
    private String name;

    private boolean error;
    private final File file;

    protected JavaFile(File file) {
        this.file = file;
    }

    public static JavaFile createJavaFold(File file){
        JavaFile javaFile = new JavaFile(file);
        javaFile.setName(file.getName());
        javaFile.setPath(file.getAbsolutePath());
        return javaFile;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JavaFold getParent() {
        return parent;
    }

    public void setParent(JavaFold parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public File getFile() {
        return file;
    }
}
