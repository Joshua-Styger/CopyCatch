package com.copycatch.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by tranb7 on 5/29/18.
 */
public class DisplayTextFiles
{

    public static void viewFiles(Stage primaryStage, File assignmentOne, File assignmentTwo)
    {
        GridPane root = new GridPane();
        SplitPane window = new SplitPane();
        SplitPane window2 = new SplitPane();
        Label f1 = new Label(assignmentOne.getName());
        Label f2 = new Label(assignmentTwo.getName());
        CodeArea fileOne = new CodeArea();
        fileOne.setParagraphGraphicFactory(LineNumberFactory.get(fileOne));
        CodeArea fileTwo = new CodeArea();
        fileTwo.setParagraphGraphicFactory(LineNumberFactory.get(fileTwo));
        fileOne.setEditable(false);
        fileTwo.setEditable(false);
        fileTwo.setStyle("-fx-background-color: #DCDEE0;");
        fileOne.setStyle("-fx-background-color: #DCDEE0;");

        try
        {

            String content = new String(Files.readAllBytes(Paths.get(assignmentOne.getAbsolutePath())));
            fileOne.replaceText(0, 0, content);
        }
        catch (IOException ignored)
        {

        }

        try
        {
            String content = new String(Files.readAllBytes(Paths.get(assignmentTwo.getAbsolutePath())));
            fileTwo.replaceText(0, 0, content);
        } catch (IOException ignored)
        {

        }

        root.setHgap(8);
        root.setVgap(8);
        ColumnConstraints cons1 = new ColumnConstraints();
        cons1.setHgrow(Priority.ALWAYS);

        root.getColumnConstraints().addAll(cons1);

        RowConstraints rcons1 = new RowConstraints();
        rcons1.setVgrow(Priority.NEVER);
        RowConstraints rcons2 = new RowConstraints();
        rcons2.setVgrow(Priority.ALWAYS);

        root.getRowConstraints().addAll(rcons1, rcons2);

        root.add(window2, 0, 0);
        root.add(window, 0, 1);
        GridPane.setMargin(window, new Insets(0, 10, 10, 10));
        GridPane.setMargin(window2, new Insets(10, 10, 0, 10));
        window2.setStyle("-fx-box-border: transparent;");
        //root.add(f1, 0, 0);
        //root.add(f2, 1, 0);
        Scene scene = new Scene(root, 1000, 1000);
        VirtualizedScrollPane<CodeArea> leftScreen = new VirtualizedScrollPane<>(fileOne);
        VirtualizedScrollPane<CodeArea> rightScreen = new VirtualizedScrollPane<>(fileTwo);

        window.getItems().addAll(leftScreen, rightScreen);
        leftScreen.scrollYToPixel(0);
        rightScreen.scrollYToPixel(0);
        window.setDividerPositions(0.5f);
        f1.setAlignment(Pos.CENTER);
        f2.setAlignment(Pos.CENTER);
        f1.setMaxWidth(Double.MAX_VALUE);
        f2.setMaxWidth(Double.MAX_VALUE);
        window2.getItems().addAll(f1, f2);
        window2.setDividerPositions(0.5f);
        window.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) ->
				window2.setDividerPositions(window.getDividerPositions()));
        window2.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) ->
				window2.setDividerPositions(window.getDividerPositions()));
        for (Node node : window2.lookupAll(".split-pane-divider"))
        {
            node.setVisible(false);
        }
        primaryStage.setScene(scene);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        // set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setTitle("Copy Catch Code Viewer");
        primaryStage.show();
    }

}
