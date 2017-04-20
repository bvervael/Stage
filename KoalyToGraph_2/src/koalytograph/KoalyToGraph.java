/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koalytograph;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author cmaricou
 */
public class KoalyToGraph extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        //Second window
        Parent root2;
        try {
            root2 = FXMLLoader.load(getClass().getResource("FXMLFileSelecter.fxml"));
            Stage stage2 = new Stage();
            stage2.setTitle("Choose excel file");
            stage2.setScene(new Scene(root2));
            stage2.show();
            stage2.setResizable(false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
