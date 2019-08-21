package com.copycatch.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ChooseDirectoryView extends Application {


    @Override
    public void start(Stage primaryStage)
    {
        final Label labelSelectedDirectory = new Label();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if(selectedDirectory == null)
        {
            labelSelectedDirectory.setText("No Directory Selected");
        }

        VBox vBox = new VBox();
        vBox.getChildren().addAll(labelSelectedDirectory);

        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Code Plagiarism Detection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
