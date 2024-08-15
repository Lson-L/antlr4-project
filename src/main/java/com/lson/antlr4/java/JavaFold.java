package com.lson.antlr4.java;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author lson
 *
 * @date 2024/8/15
 **/
@Getter
@Setter
public class JavaFold extends JavaFile {

    private List<JavaFile> children = new ArrayList<>();

    private int count;

    public JavaFold(File file) {
        super(file);
    }

    public static JavaFold createJavaFold(File file) {
        JavaFold javaFold = new JavaFold(file);
        javaFold.setName(file.getName());
        javaFold.setPath(file.getAbsolutePath());
        return javaFold;
    }

    public void incrementCount() {
        count++;
        if (getParent() != null) {
            getParent().incrementCount();
        }
    }

    public boolean isOnlyFold() {
        for (JavaFile child : getChildren()) {
            if (child.getClass() == JavaFile.class) {
                return false;
            }
        }
        return true;
    }

}
