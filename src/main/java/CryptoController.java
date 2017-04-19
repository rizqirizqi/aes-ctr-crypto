package main.java;

import com.jfoenix.controls.JFXSpinner;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import java.io.File;

public class CryptoController {

    public File plaintextFile;
    public File keyFile;

    public Label plaintextFileName;
    public Label keyFileName;
    public Label cryptMessage;
    public Label outputFileLocation;
    public JFXSpinner processingSpinner;

    public void openPlaintextFileChooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
				new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
				new FileChooser.ExtensionFilter("Executable Files", "*.exe"),
				new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
				new FileChooser.ExtensionFilter("PDF", "*.pdf"),
				new FileChooser.ExtensionFilter("Text Files", "*.txt"),
				new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov"));
        plaintextFile = fileChooser.showOpenDialog(null);
        if (plaintextFile != null) {
            plaintextFileName.setText(plaintextFile.getName());
        }
    }

    public void openKeyFileChooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        keyFile = fileChooser.showOpenDialog(null);
        if (keyFile != null) {
            keyFileName.setText(keyFile.getName());
        }
    }

    public void encrypt(ActionEvent actionEvent) {
        processingSpinner.setVisible(true);
        if(plaintextFile == null) {
            cryptMessage.setText("Please open the file to encrypt!");
        } else if(keyFile == null) {
            cryptMessage.setText("Please open the key file first!");
        } else {
            try {
                String encryptedFileName = CryptoUtils.encrypt(keyFile, plaintextFile);
                cryptMessage.setText("Encryption success! The encrypted file is saved in:");
                outputFileLocation.setText(encryptedFileName);
            } catch (Exception e) {
                cryptMessage.setText(e.getMessage());
                e.printStackTrace();
            }
        }
        processingSpinner.setVisible(false);
    }

    public void decrypt(ActionEvent actionEvent) {
        processingSpinner.setVisible(true);
        if(plaintextFile == null) {
            cryptMessage.setText("Please open the file to decrypt!");
        } else if(keyFile == null) {
            cryptMessage.setText("Please open the key file first!");
        } else {
            try {
                String decryptedFileName = CryptoUtils.decrypt(keyFile, plaintextFile);
                cryptMessage.setText("Decryption success! The encrypted file is saved in:");
                outputFileLocation.setText(decryptedFileName);
            } catch (Exception e) {
                cryptMessage.setText(e.getMessage());
                e.printStackTrace();
            }
        }
        processingSpinner.setVisible(false);
    }
}
