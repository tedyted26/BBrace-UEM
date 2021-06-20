package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class VistaSobreNosotrosController implements Initializable {

    @FXML
    private GridPane gridPane;

    @FXML
    private Label labelTextoSobreNosotros;

    @FXML
    private Label labelBienvenidos;
    @FXML
    private Label label2;

    @FXML
    private Label label3;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		labelTextoSobreNosotros.setFocusTraversable(false);
		labelBienvenidos.setFocusTraversable(false);
		
	}

}
