package org.openjfx1.BBrace;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import controlador.VentanaInicialControlador;
import javafx.application.Application;
import javafx.stage.Stage;
import servicios.GestorDeEscenas;
import servicios.GestorSQLiteGenerico;



/**
 * JavaFX App
 */
public class App extends Application {

	@Override
	public void start(Stage stage) {	
//		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni_enfermero", "id_bebe"));
//		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList("12345678Z", 11));
//		ArrayList<Object> valores2 = new ArrayList<Object>(Arrays.asList("12345678Z", 14));
//		GestorSQLiteGenerico sq = new GestorSQLiteGenerico();
//		
//		int a =sq.insertValuesIntoTable("tcenfermerobebe", columnas, valores, valores2);
//		System.out.println(a);
		
		GestorDeEscenas ge = new GestorDeEscenas();
		ge.cargarEscena("VentanaInicial.fxml", new VentanaInicialControlador(), stage, true);
		

	}

	public static void main(String[] args) { 
		launch();     
	}

}