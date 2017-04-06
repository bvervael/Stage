/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koalytograph;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmaricou
 */
public class FXMLFileSelecterController implements Initializable {

    @FXML
    private AnchorPane apane;
    @FXML
    private Button chooseFile;
    @FXML
    private Label label;
    @FXML
    private Button continuButton;

    final FileChooser fileChooser = new FileChooser();
    File file;
    Stage stage2;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        continuButton.setDisable(true);
    }    
    
    @FXML
    private void choose(ActionEvent event) throws IOException {
        stage2 = (Stage) apane.getScene().getWindow();
         FileChooser.ExtensionFilter extFilter = 
                        new FileChooser.ExtensionFilter( "Excel files (*.xlsx)","*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        file = fileChooser.showOpenDialog(stage2);
        if (file != null) {
            label.setText(file.getCanonicalPath());
            continuButton.setDisable(false);
        }else{
            continuButton.setDisable(true);
        }
    }

    @FXML
    private void continu(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLDocumentController controller = new FXMLDocumentController(file);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));  
        fxmlLoader.setController(controller);
        Parent root = (Parent)fxmlLoader.load();   
        Scene scene = new Scene(root);
        stage.setTitle("Oxya");
        stage.setScene(scene);
        stage.show();
        
        LineChart graph = (LineChart) scene.lookup("#graph");
        ListView listView = (ListView) scene.lookup("#listView");
        Button del = (Button) scene.lookup("#del");
        AnchorPane pane= (AnchorPane) scene.lookup("#pane");
        
        scene.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> {
            graph.setPrefWidth((double)newSceneWidth-214);
            listView.setLayoutX((double)newSceneWidth-214);
            del.setLayoutX((double)newSceneWidth-113);
        });
        
        scene.heightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) -> {
            graph.setPrefHeight((double)newSceneHeight-56);
            listView.setPrefHeight((double)newSceneHeight-100);
        });
        
        stage2.close();
    }
    
}
