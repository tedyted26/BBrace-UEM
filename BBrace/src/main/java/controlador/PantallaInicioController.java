package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import servicios.GestorDeEscenas;

public class PantallaInicioController implements Initializable{

    @FXML
    private BorderPane borderPaneParent;

    @FXML
    private HBox menuHSuperior;

    @FXML
    private BorderPane borderPanePadres;

    @FXML
    private HBox menuHPrincipalPadres;

    @FXML
    private HBox hBoxLogo;

    @FXML
    private ImageView imageViewLogo;

    @FXML
    private HBox hboxBotones;

    @FXML
    private JFXButton botonSobreNosotros;

    @FXML
    private JFXButton botonLogin;

    @FXML
    private JFXButton botonRegistro;

    @FXML
    private StackPane stackPanePadre;

    @FXML
    private BorderPane borderPaneSobreNosotros;

    @FXML
    private BorderPane borderPaneLogin;

    @FXML
    private BorderPane borderPaneRegistro;

    @FXML
    private HBox menuHInferior;

    @FXML
    private JFXButton botonPolitica;

    @FXML
    private JFXButton botonTerminos;
    
    private String politicaPrivacidad;
    private String terminosCondiciones;

    
    private GestorDeEscenas ge = new GestorDeEscenas();
    
    @FXML
    void descargaPolitica(ActionEvent event) {
    	
    	ge.cargarPopUp("AceptarRechazar.fxml",new AceptarRechazarControlador(politicaPrivacidad), event);
    	
    }

    @FXML
    void descargaTerminos(ActionEvent event) {
    	
    	ge.cargarPopUp("AceptarRechazar.fxml",new AceptarRechazarControlador(terminosCondiciones), event);

    }

    @FXML
    void cambiarVista(ActionEvent event) {
    	borderPaneSobreNosotros.setVisible(false);
    	borderPaneLogin.setVisible(false);
    	borderPaneRegistro.setVisible(false);
    	if(event.getSource().equals(botonSobreNosotros)) borderPaneSobreNosotros.setVisible(true);
    	if(event.getSource().equals(botonLogin)) borderPaneLogin.setVisible(true);
    	if(event.getSource().equals(botonRegistro)) borderPaneRegistro.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	
    	terminosCondiciones = "https://download1497.mediafire.com/z6gswxoe1u3g/4nmn02gg3s3ay2i/Aviso+legal+y+t%C3%A9rminos+de+uso+BBraceUEM.pdf";
    	politicaPrivacidad = "https://download1588.mediafire.com/syg9026ytujg/bkqf1nruc96k94j/BBraceUEM2020+Privacy+Policy.pdf";
    	
    	ge = new GestorDeEscenas();
  	 	
    	borderPaneSobreNosotros.setVisible(true);
    	borderPaneLogin.setVisible(false);
    	borderPaneRegistro.setVisible(false);

    	//BLOQUE DE RELLENAR VISTAS MEDIANTE INYECCION
    	GridPane panelLogin = (GridPane) ge.cargarVistaSegunPanel("VistaLoggin.fxml", new VistaLogginController(), borderPaneLogin);
    	borderPaneLogin.setCenter(panelLogin);

    	GridPane panelRegistro = (GridPane) ge.cargarVistaSegunPanel("VistaRegistro.fxml", new VistaRegistroController(), borderPaneRegistro);
    	borderPaneRegistro.setCenter(panelRegistro);
    	
    	GridPane panelNosotros = (GridPane) ge.cargarVistaSegunPanel("VistaSobreNosotros.fxml", new VistaSobreNosotrosController(), borderPaneSobreNosotros);
    	borderPaneSobreNosotros.setCenter(panelNosotros);
	}

}