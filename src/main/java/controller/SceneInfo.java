package controller;

import javafx.scene.Scene;

public class SceneInfo {
    private final Scene scene;
    private final String title;

    public SceneInfo(Scene scene, String title) {
        this.scene = scene;
        this.title = title;
    }

    public Scene getScene() {
        return scene;
    }

    public String getTitle() {
        return title;
    }
}
