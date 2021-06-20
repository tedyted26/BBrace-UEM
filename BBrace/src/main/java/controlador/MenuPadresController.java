package controlador;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import modelo.Bebe;
import modelo.Notificacion;
import modelo.Padre;
import modelo.UsuarioLoggeado;
import servicios.GestorDeEscenas;
import servicios.PojosMother;

public class MenuPadresController implements Initializable{
	
	private GestorDeEscenas ge;
	
	private String nombreUsuario, tipoUsuario;

    @FXML
    private BorderPane borderPaneParent;

    @FXML
    private HBox menuHSuperior;

    @FXML
    private Label labelNombreUsuario;

    @FXML
    private JFXButton botonNotificaciones;

    @FXML
    private BorderPane borderPanePadres;

    @FXML
    private HBox menuHPrincipalPadres;

    @FXML
    private HBox hBoxLogo;

    @FXML
    private ImageView imageViewLogo;

    @FXML
    private HBox hBoxBotones;

    @FXML
    private JFXButton botonMiBebe;

    @FXML
    private JFXButton botonPulsera;

    @FXML
    private JFXButton botonUsuario;

    @FXML
    private StackPane stackPanePadre;

    @FXML
    private BorderPane borderPaneMiBebe;

    @FXML
    private BorderPane borderPanePulsera;

    @FXML
    private GridPane gridPaneUsuario;

    @FXML
    private JFXButton botonLogOut;

    @FXML
    private HBox menuHInferior;

    @FXML
    private JFXButton botonPolitica;

    @FXML
    private JFXButton botonTerminos;
    
    private String terminosCondiciones;
    private String politicaPrivacidad;
    


	@FXML
	void logOut(ActionEvent event) {
		UsuarioLoggeado.getUsuarioLoggeado().borrarDatos();		
		ge.cargarEscena("VentanaInicial.fxml", new VentanaInicialControlador(), event, true);

	}
	
    @FXML
    void descargaPolitica(ActionEvent event) {
    	
    	ge.cargarPopUp("AceptarRechazar.fxml",new AceptarRechazarControlador(politicaPrivacidad), event);
    	
    }

    @FXML
    void descargaTerminos(ActionEvent event) {
    	
    	ge.cargarPopUp("AceptarRechazar.fxml",new AceptarRechazarControlador(terminosCondiciones), event);

    }

    @FXML
    void abrirNotificaciones(ActionEvent event) {//TODO DANI
    	ge.cargarPopUp("ListaGenericaView.fxml", new ListaGenericaControlador<Notificacion>(PojosMother.generateNotificacion()), event);
    }

    @FXML
    void cambiarVista(ActionEvent event) {
    	borderPaneMiBebe.setVisible(false);
		borderPanePulsera.setVisible(false);
		gridPaneUsuario.setVisible(false);
		if(event.getSource().equals(botonMiBebe)) borderPaneMiBebe.setVisible(true);
		if(event.getSource().equals(botonPulsera)) borderPanePulsera.setVisible(true);
		if(event.getSource().equals(botonUsuario)) gridPaneUsuario.setVisible(true);
    }

	@SuppressWarnings("static-access")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		terminosCondiciones = "https://download1497.mediafire.com/z6gswxoe1u3g/4nmn02gg3s3ay2i/Aviso+legal+y+t%C3%A9rminos+de+uso+BBraceUEM.pdf";
    	politicaPrivacidad = "https://download1588.mediafire.com/syg9026ytujg/bkqf1nruc96k94j/BBraceUEM2020+Privacy+Policy.pdf";
		
		
		ge = new GestorDeEscenas();

		borderPaneMiBebe.setVisible(true);
		borderPanePulsera.setVisible(false);
		gridPaneUsuario.setVisible(false);	

		//BLOQUE DE RELLENAR CAMPOS CON LOS DATOS DEL USUARIO LOGGEADO
		try {
			//NOMBRE EN LA ESQUINA SUPERIOR IZQUIERDA
			if (!UsuarioLoggeado.getUsuarioLoggeado().getNombre().equals(null)&&!UsuarioLoggeado.getUsuarioLoggeado().getTipoEnString().equals(null)) {
				nombreUsuario = UsuarioLoggeado.getUsuarioLoggeado().getNombre();
				tipoUsuario = UsuarioLoggeado.getUsuarioLoggeado().getTipoEnString();
			}
			else {
				nombreUsuario = "%userName%";
				tipoUsuario = "%user%";
			}
			labelNombreUsuario.setText(nombreUsuario+" ("+tipoUsuario+")");
			botonNotificaciones.setText(null);//TODO DANI
			botonNotificaciones.setGraphic(new ImageView(new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"bell.png")));
			botonNotificaciones.setTooltip(new Tooltip("Ver notificaciones"));
			
		}catch (Exception e) {
			e.printStackTrace();
		}	
		
		//BLOQUE DE RELLENAR PANELES CON INYECCION DE VISTAS
		StackPane panelSensores = (StackPane) ge.cargarVistaSegunPanel("VistaSensores.fxml", new VistaSensoresController(), borderPaneMiBebe);
		borderPaneMiBebe.setCenter(panelSensores);
				
		BorderPane panelPulseras = (BorderPane) ge.cargarVistaSegunPanel("ListaGenericaView.fxml", new ListaGenericaControlador<Bebe>(PojosMother.generateBebe()), borderPanePulsera);
		borderPanePulsera.setCenter(panelPulseras);
		borderPanePulsera.setMargin(panelPulseras, new Insets(0, 40, 0, 40));
		
		GridPane panelUsuario = (GridPane) ge.cargarVistaSegunPanel("UsuarioPadres.fxml", 
				new UsuarioPadresControlador((Padre)UsuarioLoggeado.getUsuarioLoggeado().getUsuario(), true), gridPaneUsuario);
		gridPaneUsuario.add(panelUsuario, 1, 1, 1, 1);
		
		
		}	

}
