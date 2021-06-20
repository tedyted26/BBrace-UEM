package controlador;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import modelo.Sensor;
import modelo.Unidad;
import servicios.GestorDeEscenas;

public class TarjetaHistorialController <T> extends ListCell<T> {

	@FXML
	private GridPane gridPane;

	@FXML
	private Label labelFecha;

	@FXML
	private Label labelHora;

	@FXML
	private Label labelDato;
	
	private FXMLLoader mLLoader;
	private Sensor s;
	
	public TarjetaHistorialController(Sensor s) {
		this.s = s;
	}
	
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
								+"TarjetaHistorial.fxml"));
				mLLoader.setController(this);
				try {
					mLLoader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			labelFecha.setText(String.valueOf(((Unidad) item).getStringFecha()));
			labelHora.setText(String.valueOf(((Unidad) item).getStringHora()));
			labelDato.setText(String.valueOf(((Unidad) item).getValor()));
			
			if (((Unidad) item).getValor()>=s.getLimite_anormal_superior()||((Unidad) item).getValor()<=s.getLimite_anormal()) {
				labelDato.getStyleClass().clear();
				labelDato.getStyleClass().add("texto-secundario-alerta");
			}
			else {
				labelDato.getStyleClass().clear();
				labelDato.getStyleClass().add("texto-secundario");
			}
			setText(null);
			setGraphic(gridPane);
		}

	}

}
