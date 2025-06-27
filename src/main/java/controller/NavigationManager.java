package controller;

import javafx.scene.Scene;

import java.util.Stack;

public class NavigationManager {
    private static final Stack<Scene> sceneStack = new Stack<>();

    public static void push(Scene scene){
        sceneStack.push(scene);
    }

    public static Scene pop(){
        return sceneStack.isEmpty() ? null: sceneStack.pop();
    }

    public static boolean hasPrevious(){
        return !sceneStack.isEmpty();
    }

    public static void clear(){
        sceneStack.clear();
    }
}
