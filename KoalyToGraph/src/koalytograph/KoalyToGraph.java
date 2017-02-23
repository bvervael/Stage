/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koalytograph;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author cmaricou
 */
public class KoalyToGraph extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
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
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
