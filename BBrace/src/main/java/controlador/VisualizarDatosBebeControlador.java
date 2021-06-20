package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import modelo.Bebe;
import modelo.Padre;
import modelo.Sanitario;
import servicios.GestorDePersistencia;

public class VisualizarDatosBebeControlador implements Initializable {

	@FXML
	private GridPane gridPaneParent;

	@FXML
	private JFXTextField etNombre;

	@FXML
	private Label lbNombre;

	@FXML
	private Label lbFechaNacimiento;

	@FXML
	private JFXTextField etFechaNacimiento;

	@FXML
	private JFXTextField etSemanasGestacion;

	@FXML
	private Label lbSemanasGestacion;

	@FXML
	private Label lbObservaciones;

	@FXML
	private HBox hBoxSup;

	@FXML
	private HBox hBoxInf;

	@FXML
	private Label labelTitulo;

	@FXML
	private HBox hBoxBotones;

	@FXML
	private JFXButton btnModificar;

	@FXML
	private JFXButton btnGuardar;

	@FXML
	private JFXTextArea textAreaObservaciones;

	@FXML
	private Label lbEnfermeroACargo;

	@FXML
	private JFXTextField etEnfermeroACargo;

	private Padre padre;
	private Bebe bebe;
	private GestorDePersistencia gp;

	public VisualizarDatosBebeControlador(Bebe bebe, Padre padre) {
		this.bebe = bebe;
		this.padre = padre;

	}

	@FXML
	void guardarDatos(ActionEvent event) {
		
		textAreaObservaciones.setEditable(true);
		btnGuardar.setDisable(true);
		btnModificar.setDisable(false);

		int iterTemp = 0;
		int iter = -1;
		for(Bebe bTemp : padre.getListaBebes()) {
			if(bTemp.getNombre().equals(bebe.getNombre())) iter = iterTemp;
			iterTemp++;
		}

		if(iter > -1) {
			Bebe b = padre.getListaBebes().get(iter);
			if (!b.getObservaciones().equals(textAreaObservaciones.getText())) {
				b.setObservaciones(textAreaObservaciones.getText());
				padre.getListaBebes().remove(iter);
				padre.getListaBebes().add(iter, b);
				gp.escribirPadre(padre);
			}
		}
		textAreaObservaciones.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused-editable"), false);
	}

	@FXML
	void modificarDatos(ActionEvent event) {
		textAreaObservaciones.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused-editable"), true);
		textAreaObservaciones.setEditable(true);
		btnModificar.setDisable(true);
		btnGuardar.setDisable(false);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gp = new GestorDePersistencia();
		btnGuardar.setDisable(true);
		
		etNombre.setEditable(false);
		etFechaNacimiento.setEditable(false);
		etSemanasGestacion.setEditable(false);
		etEnfermeroACargo.setEditable(false);
		textAreaObservaciones.setEditable(false);	
		
		etNombre.setText(bebe.getNombre());
		if (bebe.getSemanasGestacion() == null || bebe.getSemanasGestacion().equals("")) etSemanasGestacion.setPromptText("No se encuentran datos.");
		else etSemanasGestacion.setText(bebe.getSemanasGestacion());
		if (bebe.getObservaciones() == null || bebe.getObservaciones().equals("")) textAreaObservaciones.setPromptText("No se encuentran datos.");	
		else textAreaObservaciones.setText(bebe.getObservaciones());
		if (bebe.getFechaNacimiento() == null) etFechaNacimiento.setPromptText("No se encuentran datos.");
		else etFechaNacimiento.setText(bebe.getFechaNacimiento().toString());
		if (bebe.getListaEnfermerosDni().isEmpty())	etEnfermeroACargo.setPromptText("No se encuentran datos.");	
		else {
			String enfermeros = "";
			for (String dni : bebe.getListaEnfermerosDni()) {
				Sanitario s = gp.getSanitarioByDni(dni);
				if (bebe.getListaEnfermerosDni().indexOf(dni)!=bebe.getListaEnfermerosDni().size()-1) {
					enfermeros = enfermeros + s.getNombre()+", ";
				}
				else enfermeros = enfermeros + s.getNombre();
				
			}
			etEnfermeroACargo.setText(enfermeros);
		}

	}

}
