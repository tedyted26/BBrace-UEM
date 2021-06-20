package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import modelo.Bebe;
import modelo.Padre;
import modelo.Sanitario;
import modelo.TipoSanitario;
import modelo.Usuario;
import modelo.UsuarioLoggeado;
import servicios.GestorDeEscenas;
import servicios.GestorDePersistencia;

public class MenuGestorController implements Initializable{

	private String nombreUsuario, tipoUsuario;
	
	private GestorDeEscenas ge;	
	private GestorDePersistencia gp;

    @FXML
    private BorderPane borderPaneParent;

    @FXML
    private HBox menuHSuperior;

    @FXML
    private Label labelNombreUsuario;

    @FXML
    private BorderPane borderPaneGestor;

    @FXML
    private HBox menuHPrincipalPadres;

    @FXML
    private HBox hBoxLogo;

    @FXML
    private ImageView imageViewLogo;

    @FXML
    private HBox hBoxBotones;

    @FXML
    private JFXButton botonDatos;

    @FXML
    private JFXButton botonRegistrarSanitario;

    @FXML
    private JFXButton botonLogOut;

    @FXML
    private StackPane stackPanePadre;

    @FXML
    private BorderPane borderPaneGestion;

    @FXML
    private StackPane stackPaneDatos;

    @FXML
    private GridPane gridPaneDatos;

    @FXML
    private VBox vBoxBotonesInicio;

    @FXML
    private JFXButton botonDatosSanitario;

    @FXML
    private JFXButton botonDatosPaciente;

    @FXML
    private GridPane gridPaneDatosPaciente;

    @FXML
    private JFXButton botonAtrasPaciente;

    @FXML
    private JFXButton botonVerBebes;

    @FXML
    private GridPane gridPaneDatosSanitario;

    @FXML
    private JFXButton botonAtrasSanitario;

    @FXML
    private JFXButton botonCambiarListas;

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
    private ListaGenericaControlador<Padre> listaPadres;
    private ListaGenericaControlador<Usuario> listaSanitarios;
    private ListaGenericaControlador<Usuario> listaPacientesDeSanitario;
    private ListaGenericaControlador<Bebe> listaBebesDeEnfermero;
    private ListaGenericaControlador<Usuario> listaEnfermerosDeMedico;
    private Pane panelPadresAsociadosParent;
    private Pane panelEnfermerosAsociadosParent;
    

    @FXML
    void cambiarListas(ActionEvent event) {
    	if (botonCambiarListas.getText().equals("VER ENFERMEROS ASOCIADOS")) {
    		
    		panelPadresAsociadosParent.setVisible(false);
    		panelEnfermerosAsociadosParent.setVisible(true);
    		
    		botonCambiarListas.setText("VER PACIENTES ASOCIADOS");
    		botonCambiarListas.setPadding(new Insets(0, 18, 0, 18));
    	}
    	else if (botonCambiarListas.getText().equals("VER PACIENTES ASOCIADOS")) {
    		
    		panelPadresAsociadosParent.setVisible(true);
    		panelEnfermerosAsociadosParent.setVisible(false);
    		
    		botonCambiarListas.setText("VER ENFERMEROS ASOCIADOS");
    		botonCambiarListas.setPadding(new Insets(0, 10, 0, 10));
    	}
    }

    @FXML
    void irAtras(ActionEvent event) {
    	if (event.getSource().equals(botonAtrasPaciente)) { 
    		gridPaneDatos.setVisible(true);
    		gridPaneDatosPaciente.setVisible(false);
    	}
    	else if (event.getSource().equals(botonAtrasSanitario)) {
    		gridPaneDatos.setVisible(true);
    		gridPaneDatosSanitario.setVisible(false);
    	}
    	else if (event.getSource().equals(botonAtrasBebe)) {
    		gridPaneDatosBebes.setVisible(false);
    		gridPaneDatosPaciente.setVisible(true);
    	}
    }
    
    @FXML
    void verBebes(ActionEvent event) {
    	gridPaneDatosBebes.setVisible(true);
    	gridPaneDatosPaciente.setVisible(false);
    	Padre p = listaPadres.getListView().getSelectionModel().getSelectedItem();
    	StackPane panelBebes = (StackPane) ge.cargarVistaSegunPanel("VistaSensores.fxml", new VistaSensoresController(gp.getPadreByDni(p.getDni())), gridPaneDatosBebes);
    	gridPaneDatosBebes.add(panelBebes, 0, 1, 3, 1);
    	
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
    void cambiarADatos(ActionEvent event) {
		gridPaneDatosPaciente.setVisible(false);
		gridPaneDatos.setVisible(false);
		gridPaneDatosSanitario.setVisible(false);
		if(event.getSource().equals(botonDatosSanitario)) gridPaneDatosSanitario.setVisible(true);
		if(event.getSource().equals(botonDatosPaciente)) gridPaneDatosPaciente.setVisible(true);
    }

    @FXML
    void cambiarVista(ActionEvent event) {
    	borderPaneGestion.setVisible(false);
    	stackPaneDatos.setVisible(false);
    	gridPaneDatosPaciente.setVisible(false);
    	gridPaneDatosSanitario.setVisible(false);
    	gridPaneDatosBebes.setVisible(false);
		if(event.getSource().equals(botonRegistrarSanitario)) borderPaneGestion.setVisible(true);
		if(event.getSource().equals(botonDatos)) {
			stackPaneDatos.setVisible(true);
			gridPaneDatos.setVisible(true);
		}
    }

    @FXML
    void logout(ActionEvent event) {
    	UsuarioLoggeado.getUsuarioLoggeado().borrarDatos();
		ge.cargarEscena("VentanaInicial.fxml", new VentanaInicialControlador(), event, true);
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		terminosCondiciones = "https://download1497.mediafire.com/z6gswxoe1u3g/4nmn02gg3s3ay2i/Aviso+legal+y+t%C3%A9rminos+de+uso+BBraceUEM.pdf";
    	politicaPrivacidad = "https://download1588.mediafire.com/syg9026ytujg/bkqf1nruc96k94j/BBraceUEM2020+Privacy+Policy.pdf";
    	
		ge = new GestorDeEscenas();
		gp = new GestorDePersistencia();
		
		borderPaneGestion.setVisible(false);
		stackPaneDatos.setVisible(true);
		gridPaneDatos.setVisible(true);
		gridPaneDatosPaciente.setVisible(false);
		gridPaneDatosSanitario.setVisible(false);
		gridPaneDatosBebes.setVisible(false);
		botonVerBebes.setVisible(false);
		botonCambiarListas.setText("VER ENFERMEROS ASOCIADOS");
		botonCambiarListas.setVisible(false);
    	
		//BLOQUE DE RELLENAR CAMPOS CON LOS DATOS DEL USUARIO LOGGEADO
		if (!UsuarioLoggeado.getUsuarioLoggeado().getNombre().equals(null)&&!UsuarioLoggeado.getUsuarioLoggeado().getTipoEnString().equals(null)) {
			nombreUsuario = UsuarioLoggeado.getUsuarioLoggeado().getNombre();
			tipoUsuario = UsuarioLoggeado.getUsuarioLoggeado().getTipoEnString();
		}
		else {
			nombreUsuario = "%userName%";
			tipoUsuario = "%user%";
		}
		labelNombreUsuario.setText(nombreUsuario+" ("+tipoUsuario+")");
		
		//PANEL DE GESTIÓN 
		GridPane panelGestion = (GridPane) ge.cargarVistaSegunPanel("VistaRegistroSanitarios.fxml", new RegistroSanitariosControlador(), borderPaneGestion);
		borderPaneGestion.setCenter(panelGestion);		
		
		//PANEL DE DATOS
		//datos de pacientes
		setDatosDePacientes();		
		
		//datos de sanitarios 
		setDatosDeSanitarios();
				
	}
	
	
	

	/**
	 * Carga todo el contenido de la ventana de pacientes. Incluye perfil y boton para ver los datos de los bebes (lleva a sensores e historial)
	 */
	private void setDatosDePacientes() {
		//inicializa la lista de padres general
		listaPadres = new ListaGenericaControlador<Padre> (new Padre());
		
		//panel en el que se va a cargar la lista de padres general
		Pane panelListaPaciente = ge.cargarVistaSegunPanel("ListaGenericaView.fxml", listaPadres, gridPaneDatosPaciente);
		gridPaneDatosPaciente.add(panelListaPaciente, 1, 1, 1, 1);
		
		//panel en el que se va a cargar el perfil del paciente seleccionado. Se inicializa y añade aqui pero se cambia en el changed()
		Pane panelPerfilPadresParent = new Pane();
		gridPaneDatosPaciente.add(panelPerfilPadresParent, 3, 1, 1, 1);
		
		//changed() o onChange de la lista. Detecta cuando se selecciona un elemento y realiza una accion en consecuencia
		listaPadres.getListView().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Padre>() {
			@Override
			public void changed(ObservableValue<? extends Padre> arg0, Padre arg1, Padre arg2) {
				//vacia el panel de su contenido anterior
				panelPerfilPadresParent.getChildren().clear();
				botonVerBebes.setVisible(false);
				if (arg2 != null){
				//carga el panel con su nuevo contenidos
				Pane panelPerfilPaciente = ge.cargarVistaSegunPanel("UsuarioPadres.fxml", new UsuarioPadresControlador(arg2, false), panelPerfilPadresParent);
				panelPerfilPadresParent.getChildren().add(panelPerfilPaciente);
				//configuracion adicional
				botonVerBebes.setVisible(true);
				}
			}
		});
	}
	/**
	 * Carga todo el contenido de la ventana de sanitarios. Incluye perfil, pacientes asociados y botones de gestion de esos pacientes(añadir y eliminar)
	 */
	private void setDatosDeSanitarios() {
		//inicializa la lista de sanitarios general
		listaSanitarios = new ListaGenericaControlador<Usuario>(new Sanitario());
		
		//panel en el que se va a cargar la lista de sanitarios general
		Pane panelListaSanitario = ge.cargarVistaSegunPanel("ListaGenericaView.fxml", listaSanitarios, gridPaneDatosSanitario);
		gridPaneDatosSanitario.add(panelListaSanitario, 1, 1, 1, 3);
		
		//panel en el que se va cargar el perfil del sanitario seleccionado. Se inicializa y añade aqui pero se cambia en el changed()
		BorderPane panelPerfilSanitariosParent = new BorderPane();
		gridPaneDatosSanitario.add(panelPerfilSanitariosParent, 4, 1, 1, 2);
		
		//panel en el que se va a cargar la lista de padres asociados o bebes asociados (depende del tipo de medico)
		//se inicializa aqui pero se cambia en el changed()
		panelPadresAsociadosParent = new Pane();
		gridPaneDatosSanitario.add(panelPadresAsociadosParent, 3, 2, 1, 2);
		
		panelEnfermerosAsociadosParent = new Pane();
		gridPaneDatosSanitario.add(panelEnfermerosAsociadosParent, 3, 2, 1, 2);
		panelEnfermerosAsociadosParent.setVisible(false);
		
		//changed() o onChange de la lista. Detecta cuando se selecciona un elemento y realiza una accion en consecuencia
		listaSanitarios.getListView().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Usuario>() {
			@Override
			public void changed(ObservableValue<? extends Usuario> arg0, Usuario arg1, Usuario arg2) {
				//vacía el panel del perfil mostrado anterior (si ha habido)
				panelPerfilSanitariosParent.getChildren().clear();
				//vacia el panel de la lista de padres o bebes asociados anterior (si ha habido)
				panelPadresAsociadosParent.getChildren().clear();
				//vacia el panel de la lista de enfermeros anterior (si ha habido)
				panelEnfermerosAsociadosParent.getChildren().clear();
				botonCambiarListas.setVisible(false);
				if (arg2 != null) {				
				//carga el panel con el nuevo perfil
				Pane panelPerfilSanitario = ge.cargarVistaSegunPanel("UsuarioPadres.fxml", new UsuarioPadresControlador((Sanitario)arg2, false), panelPerfilSanitariosParent);
				panelPerfilSanitariosParent.setCenter(panelPerfilSanitario);
				//muestra los botones de gestion de pacientes asociados
				if (((Sanitario) arg2).getTipoSanitario().equals(TipoSanitario.MEDICO)) botonCambiarListas.setVisible(true);
				else botonCambiarListas.setVisible(false);
				//creación de las listas asociadas a los sanitarios (de padres y enfermeros)
				crearListaPadresAsociados((Sanitario)arg2);
				crearListaEnfermerosAsociados((Sanitario)arg2);
				//configuracion adicional
				panelEnfermerosAsociadosParent.setVisible(false);
				panelPadresAsociadosParent.setVisible(true);
				botonCambiarListas.setText("VER ENFERMEROS ASOCIADOS");
				}
			}
		});
	}
	
	private void crearListaPadresAsociados(Sanitario s) {		
		Pane panelListaPadresAsociados;
		//crea la lista pasando por parametro el sanitario nuevo que se ha seleccionado (arg2)
		if (s.getTipoSanitario().equals(TipoSanitario.MEDICO)) {
		listaPacientesDeSanitario = new ListaGenericaControlador<Usuario>(s);
		//carga la lista en el panel 
		panelListaPadresAsociados = ge.cargarVistaSegunPanel("ListaGenericaView.fxml", listaPacientesDeSanitario, panelPadresAsociadosParent);
		}else {
			listaBebesDeEnfermero = new ListaGenericaControlador<Bebe>(new Bebe(), s, true, false);
			panelListaPadresAsociados = ge.cargarVistaSegunPanel("ListaGenericaView.fxml", listaBebesDeEnfermero, panelPadresAsociadosParent);
		}
		panelPadresAsociadosParent.getChildren().add(panelListaPadresAsociados);	
		
	}
	
	private void crearListaEnfermerosAsociados(Sanitario s) {
		//crea la lista pasando por parametro el sanitario nuevo que se ha seleccionado (arg2) y una flag
		listaEnfermerosDeMedico = new ListaGenericaControlador<Usuario>(s, true);
		//carga la lista en el panel 
		Pane panelListaEnfermerosAsociados = ge.cargarVistaSegunPanel("ListaGenericaView.fxml", listaEnfermerosDeMedico, panelEnfermerosAsociadosParent);
		panelEnfermerosAsociadosParent.getChildren().add(panelListaEnfermerosAsociados);
	}

}
