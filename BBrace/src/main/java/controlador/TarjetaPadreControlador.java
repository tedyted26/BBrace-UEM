package controlador;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import modelo.Padre;
import servicios.GestorDeEscenas;

public class TarjetaPadreControlador<T> extends ListCell<T> {

    @FXML
    private GridPane gridPane;

    @FXML
    private Label nombreLabel;

    @FXML
    private Label dniLabel;
    
    FXMLLoader mLLoader;
    
    @Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		
		if(empty || item == null) {
			setText(null);
			setGraphic(null);
		}else {
			if(mLLoader == null) {
				mLLoader = new FXMLLoader(getClass().
						getResource(GestorDeEscenas.getRutaRelativaDeVistasFXML()
								+"TarjetaPadre.fxml"));
				mLLoader.setController(this);
				
				
				try {
					mLLoader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			nombreLabel.setText(String.valueOf(((Padre) item).getNombre()));
			dniLabel.setText(String.valueOf(((Padre) item).getDni()));
			
			setText(null);
			setGraphic(gridPane);
		}
		
	}
    
    

}
