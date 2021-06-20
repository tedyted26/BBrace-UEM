package controlador;

import java.io.IOException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Bebe;
import modelo.Pulsera;
import modelo.Sensor;
import servicios.GestorDeEscenas;

import com.jfoenix.controls.JFXButton;


public class TarjetaBebeControlador <T> extends ListCell<T> {

	@FXML
	private AnchorPane card_pane;

	@FXML
	private AnchorPane panePulsera;

	@FXML
	private AnchorPane datosPulseraPane;

	@FXML
	private Label temperaturaLabel;

	@FXML
	private Label frecuenciaLabel;

	@FXML
	private Label oxigenoLabel;

	@FXML
	private Label nombreBebeLabel;

	@FXML
	private JFXButton desactivarPulseraBoton;

	@FXML
	private JFXButton configurarPulseraBoton;

	FXMLLoader mLLoader;

	//Elimina la pulsera de la lista
	@FXML
	void desactivarPulsera(ActionEvent event) {
		Stage stage = (Stage) oxigenoLabel.getScene().getWindow();
		Alert.AlertType type = Alert.AlertType.CONFIRMATION;
		Alert alert = new Alert(type, "");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(stage);

		alert.getDialogPane().setContentText("Pulse Aceptar para continuar");
		alert.getDialogPane().setHeaderText("¿Seguro que quiere desactivar la pulsera?");

		Optional<ButtonType> result = alert.showAndWait();
		if(result.get()== ButtonType.OK) {
			getListView().getItems().remove(getItem());
		}

	}
	//Cada vez que el item es actualizado salta esta funcion
	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);


		if(empty || item == null) {
			setText(null);
			setGraphic(null);
		}else {
			if(mLLoader == null) {
				//Carga la vista y el controlador (this)
				mLLoader = new FXMLLoader(getClass().
						getResource(GestorDeEscenas.getRutaRelativaDeVistasFXML()
								+"TarjetaBebe.fxml"));
				mLLoader.setController(this);


				try {
					mLLoader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			Bebe bebe = (Bebe) item;
			setCamposPulsera(bebe);

			setText(null);
			setGraphic(card_pane);
		}

	}
	//Da valores a los campos de la pulsera
	private void setCamposPulsera(Bebe bebe) {
		Pulsera pulsera = bebe.getPulsera();

		try {
			Sensor tempInterna = pulsera.getTemperaturaInt();
			temperaturaLabel.setText(tempInterna.getUltimaUnidad().getValor()+"º");
			if(tempInterna.getUltimaUnidad().getValor()>=tempInterna.getLimite_anormal_superior()||
					tempInterna.getUltimaUnidad().getValor()<=tempInterna.getLimite_anormal()) {
				temperaturaLabel.getStyleClass().clear();
				temperaturaLabel.getStyleClass().add("texto-frecuencia-alerta");
			}
			Sensor pulsometro = pulsera.getPulsometro();
			frecuenciaLabel.setText(pulsometro.getUltimaUnidad().getValor()+" LPM");
			if(pulsometro.getUltimaUnidad().getValor()>=pulsometro.getLimite_anormal_superior()||
					pulsometro.getUltimaUnidad().getValor()<=pulsometro.getLimite_anormal()) {
				frecuenciaLabel.getStyleClass().clear();
				frecuenciaLabel.getStyleClass().add("texto-frecuencia-alerta");
			}
			Sensor oximetro = pulsera.getOximetro();
			oxigenoLabel.setText(oximetro.getUltimaUnidad().getValor()+"%");
			if(oximetro.getUltimaUnidad().getValor()>=oximetro.getLimite_anormal_superior()||
					oximetro.getUltimaUnidad().getValor()<=oximetro.getLimite_anormal()) {
				oxigenoLabel.getStyleClass().clear();
				oxigenoLabel.getStyleClass().add("texto-frecuencia-alerta");
			}

		}catch(Exception e) {
			temperaturaLabel.setText("0º");
			frecuenciaLabel.setText("0º");
			oxigenoLabel.setText("0º");
		}

		nombreBebeLabel.setText(bebe.getNombre());
	}
}

