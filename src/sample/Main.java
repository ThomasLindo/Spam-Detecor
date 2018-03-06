//this is the main class where everything goes down
package sample;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends Application {
    //doubles to keep track of accuracy and precision percentages
    private double Precision = 0.0;
    private double Accuracy = 0.0;
    //doubles to keep track of the numbers needed to get precision and accuracy
    private double TruePos = 0;
    private double TrueNeg = 0;
    private double FalsePos = 0;
    //table view to hold test file data
    private TableView<TestFile> testResults;

    @Override
    public void start(Stage primaryStage) throws Exception {
//determine if ther is a file argument
        if (getParameters().getRaw().size() < 1) {
            System.err.println("Usage: java WordCounter <dir> <outfile>");
            System.exit(0);
        }
        //get the file and create the spam detector class
        File file = new File(getParameters().getRaw().get(0));
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("spamDet");

        SpamDetector spamDetector = new SpamDetector();

//if the file argument is a directory, find the train folder in it and process all the files in it
        if (file.isDirectory()) {
            File[] contents = file.listFiles();
            for (File current : contents) {
                String name = current.getName().toLowerCase();
                if (name.equals("train")) {
                    try {
                        spamDetector.processTrainFiles(current);


                    } catch (FileNotFoundException e) {
                        System.err.println("Invalid input dir: " + current.getAbsolutePath());
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            spamDetector.calculatePercentages();
//find the test folder and process all files in it
            for (File current : contents) {
                String name = current.getName().toLowerCase();
                if (name.equals("test")) {
                    try {
                        spamDetector.processTestFiles(current);

                    } catch (FileNotFoundException e) {
                        System.err.println("Invalid input dir: " + current.getAbsolutePath());
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            System.out.println("this is a FILE not a FOLDER");
        }
        //get the results of processing the test files
        ObservableList<TestFile> results = spamDetector.getResults();
        //go through each file and determine their class to detrmine precision and accuracy
        for (TestFile result : results) {
            if (result.getActualClass().equals("Spam") && result.getSpamProbability() >= 0.8) {
                TruePos++;
            } else if (result.getActualClass().equals("Ham") && result.getSpamProbability() < 0.5) {
                TrueNeg++;
            } else if (result.getActualClass().equals("Ham") && result.getSpamProbability() >= 0.8) {
                FalsePos++;
            }
        }
        //caulcaulate precision and accuracy
        Precision = TruePos / (FalsePos + TruePos);
        Accuracy = (TruePos + TrueNeg) / (double) results.size();

//create all graphical elements and position them
        Label PreLabel = new Label("Precision");
        PreLabel.setTranslateY(50);
        PreLabel.setTranslateX(450);
        Label AccLabel = new Label("Accuracy");
        TextField PreTxt = new TextField();
        TextField AccTxt = new TextField();
        PreTxt.setTranslateY(45);
        PreTxt.setTranslateX(510);
        PreTxt.setText(Double.toString(Precision));
        PreTxt.setEditable(false);
        AccTxt.setTranslateY(75);
        AccTxt.setTranslateX(510);
        AccTxt.setText(Double.toString(Accuracy));
        AccTxt.setEditable(false);
        AccLabel.setTranslateY(75);
        AccLabel.setTranslateX(450);
        //create table columns
        TableColumn<TestFile, String> Col1 = new TableColumn<>("FileName");
        Col1.setPrefWidth(200.0);
        Col1.setCellValueFactory(new PropertyValueFactory<>("filename"));
        TableColumn<TestFile, Double> Col2 = new TableColumn<>("Spam Probability");
        Col2.setPrefWidth(100.0);
        Col2.setCellValueFactory(new PropertyValueFactory<>("spamProbability"));
        TableColumn<TestFile, String> Col3 = new TableColumn<>("Actual Class");
        Col3.setPrefWidth(100.0);
        Col3.setCellValueFactory(new PropertyValueFactory<>("actualClass"));
        //add columns to table view and display everything
        testResults = new TableView<>();
        testResults.getColumns().add(Col1);
        testResults.getColumns().add(Col2);
        testResults.getColumns().add(Col3);
        Group canvas = new Group();
        canvas.getChildren().add(testResults);
        canvas.getChildren().add(PreLabel);
        canvas.getChildren().add(AccLabel);
        canvas.getChildren().add(AccTxt);
        canvas.getChildren().add(PreTxt);
        primaryStage.setScene(new Scene(canvas, 800, 400));
        primaryStage.show();
        testResults.setItems(spamDetector.getResults());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
