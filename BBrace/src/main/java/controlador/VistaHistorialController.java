package controlador;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import modelo.Bebe;
import modelo.Sensor;
import servicios.GestorDeEscenas;

public class VistaHistorialController implements Initializable{

	private Bebe bebeSeleccionado;
	
	private GestorDeEscenas ge;
	
	private ArrayList <Pane> listaPaneles;
	
	public VistaHistorialController(Bebe bebeSeleccionado) {
		this.bebeSeleccionado = bebeSeleccionado;
	}
	
    @FXML
    private GridPane gridPaneHistorial;

    @FXML
    private HBox hboxDias;

    @FXML
    private Label labelDatos;

    @FXML
    private JFXComboBox<String> comboBoxDias;

    @FXML
    private HBox hboxNombre;

    @FXML
    private Label labelHistorial;

    @FXML
    private Label labelNombreBebe;

    //esto lo mismo no funca
    @FXML
    void seleccionarDias(ActionEvent event) {
    	int dias = 0;
    	if (comboBoxDias.getSelectionModel().getSelectedIndex()==0) dias = 1;
    	if (comboBoxDias.getSelectionModel().getSelectedIndex()==1) dias = 7;
    	if (comboBoxDias.getSelectionModel().getSelectedIndex()==2) dias = 14;
    	if (comboBoxDias.getSelectionModel().getSelectedIndex()==3) dias = 30;
    	if (comboBoxDias.getSelectionModel().getSelectedIndex()==4) dias = 0;
    	eliminar();
    	
    	Sensor s;
    	
    	s = bebeSeleccionado.getPulsera().getTemperaturaInt();
    	gridPaneHistorial.add(generarListas(s, dias), 1, 1);
    	
    	s = bebeSeleccionado.getPulsera().getPulsometro();
    	gridPaneHistorial.add(generarListas(s, dias), 3, 1);
    	
    	s = bebeSeleccionado.getPulsera().getOximetro();
    	gridPaneHistorial.add(generarListas(s, dias), 5, 1);
    }

    @FXML
    void volverAFicha(ActionEvent event) {
    	ge.cargarVistaEnPane("VistaSensores.fxml", new VistaSensoresController(), gridPaneHistorial);
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ge = new GestorDeEscenas();
		listaPaneles = new ArrayList<>();
		comboBoxDias.setVisible(false);
		labelDatos.setVisible(false);
		//cargamos comboBox
//		comboBoxDias.getItems().addAll("Último día","Últimos 7 días","Últimos 14 días","Últimos 30 días","Todos");
//		comboBoxDias.getSelectionModel().select(0);
		
		try {
			labelNombreBebe.setText(bebeSeleccionado.getNombre());
			
			//TODO optimizar al for cuando el sensor de temperatura exterior desaparezca
//			for (Sensor s: bebeSeleccionado.getPulsera().getAllSensores()) {
//				
//			}
			Sensor s;
			
			s = bebeSeleccionado.getPulsera().getTemperaturaInt();
			gridPaneHistorial.add(generarListas(s, 0), 1, 1);
			
			s = bebeSeleccionado.getPulsera().getPulsometro();
			gridPaneHistorial.add(generarListas(s, 0), 3, 1);
			
			s = bebeSeleccionado.getPulsera().getOximetro();
			gridPaneHistorial.add(generarListas(s, 0), 5, 1);
			
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private Pane generarListas(Sensor s,int diasAtras) {
		Pane panel = ge.cargarVistaSegunPanel("ListaGenericaView.fxml", new ListaGenericaControlador<Sensor>(s), gridPaneHistorial);
		listaPaneles.add(panel);
		return panel;
		
	}
	
	private void eliminar() {
		gridPaneHistorial.getChildren().removeAll(listaPaneles);
	}

}

