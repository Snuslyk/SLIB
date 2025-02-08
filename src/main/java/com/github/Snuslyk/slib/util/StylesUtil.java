package com.github.Snuslyk.slib.util;

import javafx.scene.Node;

public class StylesUtil {
    // Добавить стиль
    public static void add(Node node, String style){
        if (node.getStyleClass().contains(style)) return;
        node.getStyleClass().add(style);
    }

    public static void add(Node node, String... styles){
        for (String s : styles){
            add(node, s);
        }
    }

}
