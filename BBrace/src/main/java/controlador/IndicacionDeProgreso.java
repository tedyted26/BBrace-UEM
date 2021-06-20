package controlador;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.ProgressIndicator;

import javafx.scene.layout.HBox;

import javafx.stage.Stage;


public class IndicacionDeProgreso extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        
        
        //PrograssIndicator
        ProgressIndicator pind = new ProgressIndicator();
        pind.indeterminateProperty();
        
        
        //Layout
        HBox hbox = new HBox(15);
        hbox.getChildren().addAll(pind);
        hbox.setPadding(new Insets(75));
        Group root = new Group();
        root.getChildren().addAll(hbox);
        
        
        
        Scene scene = new Scene(root, 200, 200);
        
        //primaryStage.setTitle("JavaFX8 - ProgressBar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
   

    
    public static void main(String[] args) {
        launch(args);
    }
    
}
