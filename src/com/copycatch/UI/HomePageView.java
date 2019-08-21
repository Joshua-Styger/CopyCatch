package com.copycatch.UI;


import com.copycatch.processing.ConvertFile;
import com.copycatch.structures.FileStats;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import name.fraser.neil.plaintext.diff_match_patch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HomePageView extends Application
{

    private static String assignmentDirectory;
    private static ArrayList<File> validFiles;
    private static List<FileStats> fileStats;
    private static ListView<OutputCell> outputListView;
    private static Stage dialogStage;
    private static Label progressText;
    private static boolean comparisonRan = false;

    @Override
    public void start(Stage primaryStage)
    {
        initUI(primaryStage);
    }

    private void initUI(Stage primaryStage)
    {
        // Initialize the main GUI window
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/CCIcon.png")));
        VBox root = new VBox();
        GridPane mainArea = new GridPane();
        mainArea.setHgap(8);
        mainArea.setVgap(8);
        mainArea.setPadding(new Insets(8));

        // create all the parts of the GUI
        Button chooseDirectory = new Button("Choose Directory");
        TextField directoryPath = new TextField();
        directoryPath.setPromptText("e.g. C:\\assignments");
        outputListView = new ListView<>();
        Button runProgram = new Button("Run");
        Label spinnerLabal = new Label("Similarity Threshold");
        Spinner<Integer> similarityThreshold = new Spinner<>();

        // Do menus
        MenuBar leftBar = new MenuBar();
        leftBar.getMenus().addAll(new Menu(""));
        leftBar.getMenus().get(0).setDisable(true);
        MenuBar rightBar = new MenuBar();
        rightBar.getMenus().addAll(new Menu("Help"));
        MenuItem about = new MenuItem("About");
        rightBar.getMenus().get(0).getItems().add(about);
        Region spacer = new Region();
        spacer.getStyleClass().add("menu-bar");
        HBox.setHgrow(spacer, Priority.SOMETIMES);
        HBox menubars = new HBox(leftBar, spacer, rightBar);


        similarityThreshold.setEditable(true);
        similarityThreshold.setPrefWidth(80);
        outputListView.setPrefHeight(600);

        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 70);

        similarityThreshold.setValueFactory(valueFactory);


        similarityThreshold.getEditor().textProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue.length() > 3)
            {
                similarityThreshold.getEditor().setText(oldValue);
                return;
            }
            for (int i = 0; i < newValue.length(); i++)
            {
                if (!Character.isDigit(newValue.charAt(i)))
                {
                    if (newValue.charAt(i) == '&')
                    {

                        System.out.println("DirButton: " + chooseDirectory.getWidth());
                        System.out.println("Path: " + directoryPath.getWidth());
                        System.out.println("Thresh Label: " + spinnerLabal.getWidth());
                        System.out.println("Spinner: " + similarityThreshold.getWidth());
                        System.out.println("RunButton: " + runProgram.getWidth());
                    }
                    similarityThreshold.getEditor().setText(oldValue);
                    return;
                }
            }
        });
        similarityThreshold.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue)
            {
                similarityThreshold.increment(0); // won't change value, but will commit editor
            }
        });


        similarityThreshold.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue == null)
            {
                similarityThreshold.getValueFactory().setValue(oldValue);
                return;
            }
            if (comparisonRan && newValue <= 100)
            {
                DisplayResults(newValue);
            }
        });

        DirectoryChooser directoryChooser = new DirectoryChooser();

        ColumnConstraints cons1 = new ColumnConstraints();
        cons1.setHgrow(Priority.NEVER);

        ColumnConstraints cons2 = new ColumnConstraints();
        cons2.setHgrow(Priority.ALWAYS);
        mainArea.getColumnConstraints().addAll(cons1, cons2);

        RowConstraints rcons1 = new RowConstraints();
        rcons1.setVgrow(Priority.NEVER);

        RowConstraints rcons2 = new RowConstraints();
        rcons2.setVgrow(Priority.ALWAYS);

        mainArea.getRowConstraints().addAll(rcons1, rcons2);

        // on directory button click
        chooseDirectory.setOnAction(e ->
        {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null)
            {
                assignmentDirectory = selectedDirectory.getAbsolutePath();
                directoryPath.setText(assignmentDirectory);
            }
        });

        // on run program button click
        runProgram.setOnAction(e ->
        {
            if (directoryPath.getText().equals(""))
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("No Selected Directory");
                alert.setHeaderText(null);
                alert.setContentText("You must select a directory to run the scan on first.");
                alert.showAndWait();
            }
            else
            {
                Path path = Paths.get(directoryPath.getText());
                if (!Files.exists(path))
                {
                    System.out.println("Not a valid path!!!!!");
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Directory Does Not Exist");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select or enter a valid directory for student files.");
                    alert.showAndWait();
                    return;
                }

                if (comparisonRan)
                {
                    // Ask user if they want to run ANOTHER comparison with dialog
                    // Inform them that prior results will be lost.
                    // Get buttonclick result and proceed or return
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("New Comparison Confirmation");
                    alert.setHeaderText(null);
                    alert.setContentText("If you run another comparison, your prior results will be lost. Proceed?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.CANCEL)
                    {
                        return;
                    }
                }
                outputListView.setItems(null);
                fileStats = new ArrayList<>();
                File dir = new File(directoryPath.getText());
                File[] directoryListing = dir.listFiles();
                ConvertFile.InitializeCPPLists();
                ConvertFile.InitializeCommonKeywordsMap();

                System.out.println(assignmentDirectory);

                validFiles = new ArrayList<>();

                for (File assignment : directoryListing)
                {
                    String name = assignment.getName();
                    name = name.substring(name.length() - 3);
                    if (name.equals("cpp"))
                    {
                        validFiles.add(assignment);

                        // Implementing FileStats
                        fileStats.add(new FileStats(assignment.getName()));

                        ConvertFile.textConverter(assignment, fileStats.get(fileStats.size() - 1));

                    }
                }

                if (validFiles.size() < 2)
                {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Not Enough CPP Files Present");
                    alert.setHeaderText(null);
                    alert.setContentText("There must be at least 2 or more CPP files to run comparisons on.");
                    alert.showAndWait();
                    return;
                }

                Task<Void> task = new Task<Void>()
                {
                    @Override
                    public Void call()
                    {
                        final int totalComparisons = (fileStats.size() * (fileStats.size() - 1)) / 2;
                        int completedComparisons = 0;
                        FileStats.SetScoresSize(fileStats.size() - 1);
                        diff_match_patch dmp = new diff_match_patch();
                        long start, end;
                        start = System.currentTimeMillis();
                        for (int i = 0; i < fileStats.size() - 1; i++)
                        {
                            for (int j = i + 1; j <= fileStats.size() - 1; j++)
                            {

                                String[] strs = FileStats.GetLinesAsStringWithMatchedFunctions(fileStats.get(i), fileStats.get(j));

                                String str1 = strs[0];
                                String str2 = strs[1];

                                //String str1 = fileStats.get(i).GetAllLinesAsString();
                                //String str2 = fileStats.get(j).GetAllLinesAsString();

                                // DIFF IMPLEMENTATION
                                LinkedList<diff_match_patch.Diff> diff;
                                if (str1.length() > str2.length())
                                {
                                    diff = dmp.diff_main(str1, str2);
                                }
                                else
                                {
                                    diff = dmp.diff_main(str2, str1);
                                }
                                dmp.diff_cleanupSemantic(diff);
                                // System.out.println(diff);

                                // Old calculation
                                // int distance = FileComparer.CalculateEditDistance(str1, str2);

                                // Diff calculation
                                int distance = dmp.diff_levenshtein(diff);
                                int bigger = Math.max(str1.length(), str2.length());
                                double percent = (bigger - distance) / (double) bigger * 100;
                                if (percent < 0)
                                {
                                    percent = 0;
                                }

                                // double percent = fileStats.get(i).CompareCharCount(fileStats.get(j)) * 100;
                                FileStats.scores[i][j - 1] = Double.parseDouble(String.format("%.2f", percent));
                                completedComparisons++;
                                updateProgress(completedComparisons, totalComparisons);
                            }
                        }
                        end = System.currentTimeMillis();
                        System.out.println((end - start) / 1000.0);
                        return null;
                    }
                };

                task.setOnSucceeded(ep -> DisplayResults(Integer.parseInt(similarityThreshold.getValue().toString())));

                dialogStage = new Stage();
                dialogStage.initStyle(StageStyle.UTILITY);
                dialogStage.setOnCloseRequest(Event::consume);

                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                ProgressBar pbar = new ProgressBar();
                progressText = new Label();

                pbar.progressProperty().bind(task.progressProperty());
                pbar.setPrefWidth(300);
                pbar.progressProperty().addListener((ov, t, newValue) ->
                {
                    String str = ((int) ((double) newValue * 100) + 1) + "%";
                    progressText.setText(str);
                });
                BorderPane bp = new BorderPane();
                bp.setPadding(new Insets(10, 10, 10, 10));
                bp.setLeft(pbar);
                bp.setRight(progressText);
                BorderPane.setAlignment(progressText, Pos.CENTER_RIGHT);
                BorderPane.setAlignment(pbar, Pos.CENTER_LEFT);
                Scene scene = new Scene(bp);
                dialogStage.setScene(scene);
                dialogStage.setHeight(100);
                dialogStage.setWidth(400);
                dialogStage.setTitle("Comparing Files...");
                dialogStage.show();
                new Thread(task).start();
            }
        });

        // add items to window
        mainArea.add(chooseDirectory, 0, 1);
        mainArea.add(directoryPath, 1, 1);
        mainArea.add(spinnerLabal, 2, 1);
        mainArea.add(similarityThreshold, 3, 1);
        mainArea.add(runProgram, 4, 1);
        mainArea.add(outputListView, 0, 2, 5, 2);

        root.getChildren().addAll(menubars, mainArea);


        Scene scene = new Scene(root);
        primaryStage.setTitle("Copy Catch");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }

    private void DisplayResults(int similarityThreshold)
    {
        comparisonRan = true;
        dialogStage.hide();
        double[][] scores = FileStats.scores;

        List<OutputCell> listResults = new ArrayList<>();
        for (int i = 0; i < scores.length; i++)
        {
            for (int j = i; j < scores.length; j++)
            {
                // Only prints those above given threshold
                if (scores[i][j] >= similarityThreshold)
                {
                    listResults.add(new OutputCell(validFiles.get(i), validFiles.get(j + 1), scores[i][j]));
                }
            }
        }
        Comparator<OutputCell> OutputCellComparator = (o1, o2) -> Double.compare(o2.score, o1.score);
        listResults.sort(OutputCellComparator);
        if (listResults.size() == 0)
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No matches over threshold");
            alert.setHeaderText(null);
            alert.setContentText("No files equaling or exceeding  " + similarityThreshold + "% similar elements found.");
            alert.showAndWait();
        }
        ObservableList<OutputCell> observableListResults = FXCollections.observableList(listResults);
        outputListView.setItems(observableListResults);
        System.out.println("Done!");
    }

}
