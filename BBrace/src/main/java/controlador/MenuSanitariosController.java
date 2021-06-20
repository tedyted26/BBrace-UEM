package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import modelo.Bebe;
import modelo.Notificacion;
import modelo.Padre;
import modelo.Sanitario;
import modelo.TipoSanitario;
import modelo.Usuario;
import modelo.UsuarioLoggeado;
import servicios.GestorDeEscenas;
import servicios.GestorDePersistencia;
import servicios.PojosMother;

public class MenuSanitariosController implements Initializable {

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
    private JFXButton botonPacientes;

    @FXML
    private JFXButton botonUsuario;

    @FXML
    private StackPane stackPanePadre;

    @FXML
    private GridPane gridPaneUsuario;

    @FXML
    private JFXButton botonLogOut;

    @FXML
    private GridPane gridPanePacientes;

    @FXML
    private JFXButton botonVerBebes;

    @FXML
    private Label labelAviso;

    @FXML
    private GridPane gridPaneDatosBebes;

    @FXML
    private JFXButton botonAtrasBebe;

    @FXML
    private HBox menuHInferior;

    @FXML
    private JFXButton botonPolitica;

    @FXML
    private JFXButton botonTerminos;


	private String politicaPrivacidad;
	private String terminosCondiciones;
	private ListaGenericaControlador<Usuario> listaPacientesAsociados;
	private ListaGenericaControlador<Bebe> listaBebesAsociados;
	private ListaGenericaControlador<Usuario> listaEnfermerosAsociados;
	private Pane panelPadresAsociadosParent;
	private Pane panelEnfermerosAsociadosParent;
	private Pane panelPerfilPadreParent;
	private GestorDePersistencia gp;

	@FXML
	void irAtras(ActionEvent event) {
		gridPaneDatosBebes.setVisible(false);
		gridPanePacientes.setVisible(true);
	}

	@FXML
	void verBebes(ActionEvent event) {
		gridPaneDatosBebes.setVisible(true);
		gridPanePacientes.setVisible(false);
		Padre p;
		if (((Sanitario)UsuarioLoggeado.getUsuarioLoggeado().getUsuario()).getTipoSanitario().equals(TipoSanitario.MEDICO)) {
			p = (Padre) listaPacientesAsociados.getListView().getSelectionModel().getSelectedItem();
		}
		else {
			p = gp.getPadreByDni(((Bebe)listaBebesAsociados.getListView().getSelectionModel().getSelectedItem()).getDniPadre());
		}
		StackPane panelBebes = (StackPane) ge.cargarVistaSegunPanel("VistaSensores.fxml", new VistaSensoresController(gp.getPadreByDni(p.getDni())), gridPaneDatosBebes);
		gridPaneDatosBebes.add(panelBebes, 0, 1, 3, 1);
	}

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
	void abrirNotificaciones(ActionEvent event) {
		ge.cargarPopUp("ListaGenericaView.fxml", new ListaGenericaControlador<Notificacion>(PojosMother.generateNotificacion()), event);
	}

	@FXML
	void cambiarVista(ActionEvent event) {
		gridPanePacientes.setVisible(false);
		gridPaneUsuario.setVisible(false);
		gridPaneDatosBebes.setVisible(false);
		if (event.getSource().equals(botonPacientes)) gridPanePacientes.setVisible(true);
		if (event.getSource().equals(botonUsuario)) gridPaneUsuario.setVisible(true);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		terminosCondiciones = "https://download1497.mediafire.com/z6gswxoe1u3g/4nmn02gg3s3ay2i/Aviso+legal+y+t%C3%A9rminos+de+uso+BBraceUEM.pdf";
		politicaPrivacidad = "https://download1588.mediafire.com/syg9026ytujg/bkqf1nruc96k94j/BBraceUEM2020+Privacy+Policy.pdf";

		ge = new GestorDeEscenas();
		gp = new GestorDePersistencia();

		gridPanePacientes.setVisible(true);
		gridPaneUsuario.setVisible(false);
		gridPaneDatosBebes.setVisible(false);
		botonVerBebes.setVisible(false);

		//BLOQUE DE RELLENAR CAMPOS CON LOS DATOS DEL USUARIO LOGGEADO
		if (!UsuarioLoggeado.getUsuarioLoggeado().getNombre().equals(null)&&!UsuarioLoggeado.getUsuarioLoggeado().getTipoEnString().equals(null)) {
			nombreUsuario = UsuarioLoggeado.getUsuarioLoggeado().getNombre();
			tipoUsuario = UsuarioLoggeado.getUsuarioLoggeado().getTipoSanitario().toString();
		}
		else {
			nombreUsuario = "%userName%";
			tipoUsuario = "%user%";
		}
		labelNombreUsuario.setText(nombreUsuario+" ("+tipoUsuario+")");

		//BLOQUE TRY CATCH POR SI NO SE ENCUENTRA LA IMAGEN
		try {
			botonNotificaciones.setText(null);
			botonNotificaciones.setGraphic(new ImageView(new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"bell.png")));
			botonNotificaciones.setTooltip(new Tooltip("Ver notificaciones"));
		}catch(Exception e) {
			botonNotificaciones.setText("Notificaciones");
		}

		//PANEL DE PERFIL 
		GridPane panelUsuario = (GridPane) ge.cargarVistaSegunPanel("UsuarioPadres.fxml", 
				new UsuarioPadresControlador((Sanitario)UsuarioLoggeado.getUsuarioLoggeado().getUsuario(), true), gridPaneUsuario);
		gridPaneUsuario.add(panelUsuario, 1, 1, 1, 1);
		
		//PANEL DE DATOS DE PACIENTES ASOCIADOS
		setDatosPacientes();				

	}

	/**
	 * Carga todo el contenido de la ventana de pacientes. Incluye perfil y boton para ver los datos de los bebes (lleva a sensores e historial)
	 */
	private void setDatosPacientes() {
		if (((Sanitario)UsuarioLoggeado.getUsuarioLoggeado().getUsuario()).getTipoSanitario().equals(TipoSanitario.MEDICO)){
			listaPacientesAsociados = new ListaGenericaControlador<Usuario> ((Sanitario)UsuarioLoggeado.getUsuarioLoggeado().getUsuario());
			panelPadresAsociadosParent = ge.cargarVistaSegunPanel("ListaGenericaView.fxml", listaPacientesAsociados, gridPanePacientes);

			listaPacientesAsociados.getListView().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Usuario>() {
				@Override
				public void changed(ObservableValue<? extends Usuario> arg0, Usuario arg1, Usuario arg2) {
					panelPerfilPadreParent.getChildren().clear();
					botonVerBebes.setVisible(false);
					if(arg2 != null) {				
					Pane panelPerfilPaciente = ge.cargarVistaSegunPanel("UsuarioPadres.fxml", new UsuarioPadresControlador((Padre)arg2, false), panelPerfilPadreParent);
					panelPerfilPadreParent.getChildren().add(panelPerfilPaciente);
					botonVerBebes.setVisible(true);
					labelAviso.setVisible(false);
					}
				}
			});
			if(listaPacientesAsociados.getListView().getItems().isEmpty()) {
				labelAviso.setVisible(true);
			}

		}
		else {
			listaBebesAsociados = new ListaGenericaControlador<Bebe> (new Bebe(),(Sanitario)UsuarioLoggeado.getUsuarioLoggeado().getUsuario(), true, true);
			panelPadresAsociadosParent = ge.cargarVistaSegunPanel("ListaGenericaView.fxml", listaBebesAsociados, gridPanePacientes);

			listaBebesAsociados.getListView().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Bebe>() {
				@Override
				public void changed(ObservableValue<? extends Bebe> arg0, Bebe arg1, Bebe arg2) {
					panelPerfilPadreParent.getChildren().clear();
					botonVerBebes.setVisible(false);
					if (arg2 != null) {
					Pane panelPerfilPaciente = ge.cargarVistaSegunPanel("UsuarioPadres.fxml", new UsuarioPadresControlador(gp.getPadreByDni(((Bebe)arg2).getDniPadre()), false), panelPerfilPadreParent);
					panelPerfilPadreParent.getChildren().add(panelPerfilPaciente);
					botonVerBebes.setVisible(true);
					labelAviso.setVisible(false);
					}
				}
			});
			if(listaBebesAsociados.getListView().getItems().isEmpty()) {
				labelAviso.setVisible(true);
			}
		}
		gridPanePacientes.add(panelPadresAsociadosParent, 1, 1, 1, 2);

		listaEnfermerosAsociados = new ListaGenericaControlador<Usuario>((Sanitario)UsuarioLoggeado.getUsuarioLoggeado().getUsuario(), true);
		panelEnfermerosAsociadosParent  = ge.cargarVistaSegunPanel("ListaGenericaView.fxml", listaEnfermerosAsociados, gridPanePacientes);
		gridPanePacientes.add(panelEnfermerosAsociadosParent, 1, 1, 1, 2);
		panelEnfermerosAsociadosParent.setVisible(false);

		panelPerfilPadreParent = new Pane();
		gridPanePacientes.add(panelPerfilPadreParent, 3, 1, 2, 2);

	}

}