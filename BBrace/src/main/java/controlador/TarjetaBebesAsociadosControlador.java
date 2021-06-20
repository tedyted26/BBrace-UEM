package controlador;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import modelo.Bebe;
import modelo.Padre;
import servicios.GestorDeEscenas;
import servicios.GestorDePersistencia;

public class TarjetaBebesAsociadosControlador <T> extends ListCell<T>{

    @FXML
    private GridPane gridPane;

    @FXML
    private Label labelNombreBebe;

    @FXML
    private Label labelPadre;

    @FXML
    private Label labelDniPadre;

    @FXML
    private Label labelNombrePadre;
    
    private FXMLLoader mLLoader;
    private GestorDePersistencia gp = new GestorDePersistencia();
    
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
								+"TarjetaBebesAsociados.fxml"));
				mLLoader.setController(this);
				try {
					mLLoader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			Bebe b = (Bebe)item;
			Padre p = gp.getPadreByDni(String.valueOf(((Bebe)item).getDniPadre()),false);

			labelNombreBebe.setText(b.getNombre());
			labelNombrePadre.setText(p.getNombre());
			labelDniPadre.setText(p.getDni());
			
			setText(null);
			setGraphic(gridPane);
		}

	}

}
