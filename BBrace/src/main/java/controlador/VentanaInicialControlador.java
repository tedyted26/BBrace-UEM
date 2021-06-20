package controlador;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import servicios.GestorDeEscenas;

public class VentanaInicialControlador implements Initializable{
	
	private GestorDeEscenas ge;

    @FXML
    private BorderPane borderPaneParent;

    @FXML
    private HBox menuHSuperior;

    @FXML
    private HBox menuHInferior;

    @FXML
    private JFXButton botonPolitica;

    @FXML
    private JFXButton botonTerminos;

    @FXML
    private StackPane stackPane;

    @FXML
    private Pane panelBebe;

    @FXML
    private GridPane gridPane;

    @FXML
    private VBox vbox;

    @FXML
    private ImageView imageViewLogo;

    @FXML
    private JFXButton botonInicioAplicacion;
    private String politicaPrivacidad;
    private String terminosCondiciones;


    @FXML
    void inicioAplicacion(ActionEvent event) {  	

    	ge.cargarEscena("PantallaInicio.fxml", new PantallaInicioController(), event, true);
    }

    @FXML
    void descargaPolitica(ActionEvent event) {

    	ge.cargarPopUp("AceptarRechazar.fxml",new AceptarRechazarControlador(politicaPrivacidad), event);

    }

    @FXML
    void descargaTerminos(ActionEvent event) {

    	ge.cargarPopUp("AceptarRechazar.fxml",new AceptarRechazarControlador(terminosCondiciones), event);

    }


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		terminosCondiciones = "https://download1497.mediafire.com/z6gswxoe1u3g/4nmn02gg3s3ay2i/Aviso+legal+y+t%C3%A9rminos+de+uso+BBraceUEM.pdf";
    	politicaPrivacidad = "https://download1588.mediafire.com/syg9026ytujg/bkqf1nruc96k94j/BBraceUEM2020+Privacy+Policy.pdf";
    	
		ge = new GestorDeEscenas();
		double anchoVista = GestorDeEscenas.getScreen().getWidth();
		double altoVista = GestorDeEscenas.getScreen().getHeight();
		
		String bebe = getRandomBaby();
		
		Image imagen = new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML() + "backgrounds/" + bebe, 
				anchoVista, (4/6)*altoVista, true, true);
	    
		panelBebe.setBackground(new Background(new BackgroundImage(imagen, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
		panelBebe.setOpacity(0.20);

		
	}
	
	private String getRandomBaby() {		
		int[] bebes = {1,2,3,4,5};
		
		Random rand = new Random();
		Integer randomInt = bebes[rand.nextInt(bebes.length)];
		
		return randomInt+".jpg";
	}

}

