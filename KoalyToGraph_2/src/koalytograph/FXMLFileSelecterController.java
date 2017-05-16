/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koalytograph;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        root.getStylesheets().add("koalytograph/graphCSS.css");
        Scene scene = new Scene(root);
        stage.setTitle("Oxya");
        stage.setScene(scene);
        stage.show();
        stage.setMinHeight(400);
        stage.setMinWidth(960);
        
        stage2.close();
    }
    
}
