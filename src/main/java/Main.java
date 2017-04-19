package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/index.fxml"));
        primaryStage.setTitle("AES CTR Crypto");
		primaryStage.setMinWidth(610);
		primaryStage.setMinHeight(460);
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();
    }

    public static void main(String[] args) {
        removeJavaCipherKeyRestriction();
        launch(args);
    }

    private static void removeJavaCipherKeyRestriction() {
        try {
            Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(null, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
