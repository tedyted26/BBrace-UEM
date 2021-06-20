package controlador;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AceptarRechazarControlador implements Initializable {
	
	private String urlDescarga;

    @FXML
    private GridPane gridPaneParent;

    @FXML
    private JFXButton btnAceptar;

    @FXML
    private JFXButton btnRechazar;

    @FXML
    private Label labelFrase;
    
    
    

	public AceptarRechazarControlador(String urlDescarga) {
		this.urlDescarga = urlDescarga;
	}

	@FXML
	void aceptar(ActionEvent event) {
		
		if(Desktop.isDesktopSupported()){
	        try {       	
	            Desktop.getDesktop().browse(new URI(urlDescarga));
	        } catch (IOException e1) {
	            e1.printStackTrace();
	            System.err.println(e1);
	        } catch (URISyntaxException e1) {
	            e1.printStackTrace();
	            System.err.println(e1);
	        }
	    }
		Stage stage = (Stage) btnRechazar.getScene().getWindow();
		stage.close();
		}

	@FXML
	void rechazar(ActionEvent event) {
		
		Stage stage = (Stage) btnRechazar.getScene().getWindow();
		stage.close();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		labelFrase.setText("Se le redirigirá a una descarga de un archivo PDF.\n¿Está seguro de querer continuar?");		

	}

}
