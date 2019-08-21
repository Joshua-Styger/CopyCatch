package com.copycatch.UI;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class OutputCell extends HBox {
    Label label = new Label();
    Button viewFiles = new Button();
    double score;

    public OutputCell(File firstFile, File secondFile, double score) {
        super();
        this.score = score;
        label.setText(GetXCharString(firstFile.getName()) + " is " + score
                + "% similar to assignment " + GetXCharString(secondFile.getName()));
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        viewFiles.setText("View files");

        viewFiles.setOnAction(e -> {
            Stage dialog = new Stage();
            DisplayTextFiles.viewFiles(dialog, firstFile, secondFile);
        });

        this.getChildren().addAll(label, viewFiles);
    }

    private String GetXCharString(String str)
    {
    	if (str.length() > 15)
    	{
    		return str.substring(0, 15) + '~';
    	}
    	else
    	{
    		String s = "%-" + 15 + "s";
    		return String.format(s, str);
    	}
    				
    }
}
