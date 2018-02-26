package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
@FXML private TableView<TestFile> ResTbl;
@FXML private TableColumn<TestFile,String> FileCol;
    @FXML private TableColumn<TestFile,Double> ProbCol;
    @FXML private TableColumn<TestFile,String> ClassCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
