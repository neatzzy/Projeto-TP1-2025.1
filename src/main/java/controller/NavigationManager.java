package controller;

import javafx.stage.Stage;
import java.util.Stack;

public class NavigationManager {
    private static final Stack<SceneInfo> sceneStack = new Stack<>();

    public static void push(SceneInfo sceneInfo) {
        sceneStack.push(sceneInfo);
    }

    public static void popAndApply(Stage stage) {
        if (!sceneStack.isEmpty()) {
            SceneInfo previous = sceneStack.pop();
            stage.setScene(previous.getScene());
            stage.setTitle(previous.getTitle());
        }
    }

    public static boolean hasPrevious() {
        return !sceneStack.isEmpty();
    }

    public static void clear() {
        sceneStack.clear();
    }
}
