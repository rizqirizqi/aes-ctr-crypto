package main.java;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class CryptoController {

    public Label filename;

    public void openFileChooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        Stage fcStage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(fcStage);
        fcStage.close();
        if (selectedFile != null) {
            filename.setText(selectedFile.getName());
        }
    }

    public void encrypt(ActionEvent actionEvent) {

    }

    public void decrypt(ActionEvent actionEvent) {

    }
}
