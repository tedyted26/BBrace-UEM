package controlador;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
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

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class GenerarNotificacion implements Initializable {

	@FXML
	private GridPane myGridPane;

	@FXML
	private JFXToggleButton tgEmergencia;

	@FXML
	private JFXTextField etAsunto;

	@FXML
	private JFXButton btnAtras;

	@FXML
	private JFXButton btnEnviar;

	@FXML
	private JFXComboBox<Usuario> jcbDestinatarios;

	@FXML
	private Label labelPara;

	@FXML
	private Label labelAsunto;

	@FXML
	private Label labelAtencion;

	@FXML
	private Label labelTitulo;

	@FXML
	private JFXTextArea etMensaje;

	@FXML
	private HBox hboxSup;

	@FXML
	private HBox hboxInf;

	@FXML
	private Label tvInfo;


	private String flagProcedencia;
	private boolean isEmergencia;
	private Notificacion notificacionAnterior;
	private boolean isRespuesta = false;
	
	private int idUnion;
	private int idOrden;
	
	private String tipoUsuarioConectado;
	private String tipoUsuarioReceptor ;
	private String tipoSanitarioReceptor;
	private int tipoDeMensaje;
	
	public GenerarNotificacion(boolean isEmergencia, String flagProcedencia) {
		this.isEmergencia=isEmergencia;
		this.flagProcedencia=flagProcedencia;
	}
	public GenerarNotificacion(boolean isEmergencia, String flagProcedencia, Notificacion n, boolean isRespuesta) {
		this.isEmergencia=isEmergencia;
		this.flagProcedencia=flagProcedencia;
		this.notificacionAnterior = n;
		this.isRespuesta = isRespuesta;
	}
	/*public GenerarNotificacion(boolean isEmergencia, String flagProcedencia, boolean isRespuesta) {
		this.isEmergencia = isEmergencia;
		this.flagProcedencia = flagProcedencia;
		this.isRespuesta = isRespuesta;
	}*/
	
	
	

	private int hora, min, seg;



	public void initialize(URL location, ResourceBundle resources) {
		//Establecemos la forma personalizada en la que se nos muestran los Usuarios 
		Callback<ListView<Usuario>, ListCell<Usuario>> factory = lv -> new ListCell<Usuario>() {
		    @Override
		    protected void updateItem(Usuario item, boolean empty) {
		        super.updateItem(item, empty);
		        //Cambia el mensaje segun el tipo de usuario
		        if(item instanceof Padre) 
		        	setText(empty ? "" : ("[P] "+item.getNombre()));
		        else if(item instanceof Sanitario) 
		        	setText(empty ? "" : ("[S] "+item.getNombre()));
		        
		    }

		};
		//Anexamos las celdas personalizadas al combobox
		
		jcbDestinatarios.setCellFactory(factory);
		jcbDestinatarios.setButtonCell(factory.call(null));
		
		//Determina si mostrar el boton de volver atras
		if(flagProcedencia.equals("sensor")) {
			btnAtras.setVisible(false);
		}

		tgEmergencia.setSelected(isEmergencia);
		if (tgEmergencia.isSelected()) {
			labelAtencion.setVisible(true);
		}
		else labelAtencion.setVisible(false);
		//Recogemos el usuario remitente loggueado para usar sus datos
		UsuarioLoggeado ul = UsuarioLoggeado.getUsuarioLoggeado();	
		GestorDePersistencia gp = new GestorDePersistencia(); //Cargamos un gestor de persistencia
		
		//Dependiendo del remitente, los datos cargados cambian
		/*
		 * PADRE: muestra medico y enfermeros asociados a bebes
		 * MEDICO: muestra padres y enfermeros a su cargo
		 * ENFERMERO: muestra padres segun bebe y medico
		 * 
		 * */
		if(ul.getTipo() == Padre.class) { //TODO OBTENGO EL TIPO DE USUARIO
			Padre p = (Padre) ul.getUsuario();
			//Obtenemos el medico asociado al padre
			String medicoAsociadoDni = p.getMedicoAsociadoFamiliaDni();
			Sanitario medico = gp.getSanitarioByDni(medicoAsociadoDni);
			ArrayList<Sanitario> listaEnfermerosAsociados = getListaEnfermerosAsociadosABebesDePadre(p);
			
//			if(isRespuesta) {
			jcbDestinatarios.setEditable(false);
//			}else {
			jcbDestinatarios.getItems().add(medico);
			jcbDestinatarios.getItems().addAll(listaEnfermerosAsociados);
//			}
			tipoUsuarioConectado = "padre";
			
		}else if (ul.getTipo() == Sanitario.class) {
			Sanitario sanitarioLogeado = (Sanitario) ul.getUsuario();
			if(sanitarioLogeado.getTipoSanitario() == TipoSanitario.MEDICO) {
				//Creamos la lista de usuarios que almacenaremos
				ArrayList<Usuario> listaUsuariosAMostrar = new ArrayList<>();
				//Tomamos ambas listas del medico logueado
				ArrayList<String> listaEnfermerosAsociadosDni = sanitarioLogeado.getListaEnfermerosAsociadosDni();
				ArrayList<String> listaPadresAsociadosDni = sanitarioLogeado.getDniPadresAsociados();
				//Cogemos la lista de padres y enfermeros de la BBDD
				ArrayList<Padre> listaPadresCompleta = gp.leerPadres();
				ArrayList<Sanitario> listaEnfermerosCompleta = gp.leerSanitariosEnfermeros();
				
				//Añadimos todos los padres que coincidan
				for(Padre padreTmp : listaPadresCompleta) {
					for(String dniTmp : listaPadresAsociadosDni) {
						if(padreTmp.getDni().equals(dniTmp)) 
							listaUsuariosAMostrar.add(padreTmp);
					}
				}
				//Añadimos todos los Enfermeros que coincidan
				for(Sanitario enfermeroTmp : listaEnfermerosCompleta) {
					for(String dniTmp : listaEnfermerosAsociadosDni) {
						if(enfermeroTmp.getDni().equals(dniTmp)) listaUsuariosAMostrar.add(enfermeroTmp);
					}
				}
				jcbDestinatarios.getItems().addAll(listaUsuariosAMostrar);
				
				tipoUsuarioConectado = "medico";
				
			}
			else if(sanitarioLogeado.getTipoSanitario() == TipoSanitario.ENFERMERO) {
				//Creamos la lista de usuarios que almacenaremos
				ArrayList<Usuario> listaUsuariosAMostrar = new ArrayList<>();
				ArrayList<Padre> listaPadresConBebesAsociados = new ArrayList<>();
				ArrayList<Padre> listaPadresCompleta = gp.leerPadres();
				//Cogemos las listas del enfermero logueado
				String medicoAsociadoDni = sanitarioLogeado.getMedicoAsociadoAEnfermeroDni().get(0);
				
				
				Sanitario medicoAsociado = gp.getSanitarioByDni(medicoAsociadoDni);
				ArrayList<String> listaPadresAsociadosAMedicoDni = medicoAsociado.getDniPadresAsociados();
				//Filtramos los padres, obteniendo los asociados al medico
				for(Padre padreTmp : listaPadresCompleta) {
					for(String dniTmp : listaPadresAsociadosAMedicoDni) {
						if(padreTmp.getDni().equals(dniTmp)) {
							listaPadresConBebesAsociados.add(padreTmp);
						}
					}
				}
				//Filtramos los padres, obteniendo aquellos de los que el sanitario tiene hijos asociados
				for(Padre padreTmp : listaPadresConBebesAsociados) {
					boolean isPadreRepetido = false;
					for(Bebe bebeTmp : padreTmp.getListaBebes()) {
						for(String dniTmp : bebeTmp.getListaEnfermerosDni()) {
							if(sanitarioLogeado.getDni().equals(dniTmp) && !isPadreRepetido) {
								listaUsuariosAMostrar.add(padreTmp);
								isPadreRepetido = true;
							}
						}
					}
				}
				
				jcbDestinatarios.getItems().addAll(listaUsuariosAMostrar);
				jcbDestinatarios.getItems().add(medicoAsociado);
				
				tipoUsuarioConectado = "enfermero";
			}
		}
		
		//Si es una respuesta a mensaje, seleccionamos campos por defecto
		if(notificacionAnterior != null) {
			for(Usuario usuarioAResponder : jcbDestinatarios.getItems()) {
				//Si el dni del user coincide con el del remitente entra
				if(usuarioAResponder.getDni().equals(notificacionAnterior.getDniRemitente())) {
					//Inicializamos los valores del mensaje segun los del anterior mensaje
					jcbDestinatarios.setValue(usuarioAResponder);
					tgEmergencia.setSelected(notificacionAnterior.getIsEmergencia());
					etAsunto.setText(notificacionAnterior.getAsunto());
					
				}
			}
		}
			
	}
	
	private ArrayList<Sanitario> getListaEnfermerosAsociadosABebesDePadre(Padre p){
		GestorDePersistencia gp = new GestorDePersistencia();
		//Creamos listas para almacenar Dnis de enfermeros y la lista completa de los mismos
		ArrayList<Sanitario> listaEnfermerosCompleta = gp.leerSanitariosEnfermeros();
		ArrayList<String> listaDnisEnfermerosAsociados = new ArrayList<>();
		ArrayList<Sanitario> listaEnfermerosAsociados = new ArrayList<>();//La lista a devolver
		
		for(Bebe bebeTmp : p.getListaBebes()) {
			//bebeTmp.setListaEnfermerosDni(new ArrayList<>(Arrays.asList("666666666","12345678Z")));
			for(String dniTmp : bebeTmp.getListaEnfermerosDni()) {
				listaDnisEnfermerosAsociados.add(dniTmp);
			}
		}
		//Eliminamos dnis repetidos de la lista
		listaDnisEnfermerosAsociados = (ArrayList<String>) listaDnisEnfermerosAsociados.stream().distinct().collect(Collectors.toList());
		//Comparamos la lista de dnis y de sanitarios, extrayendo los enfermeros asocaidos a los bebes
		for(Sanitario sanitTmp : listaEnfermerosCompleta) {
			for(String dniEnfTmp : listaDnisEnfermerosAsociados) {
				if(sanitTmp.getDni().equals(dniEnfTmp)) {
					listaEnfermerosAsociados.add(sanitTmp);
				}
			}
			
		}
		return listaEnfermerosAsociados;
	}
	//Launches another window.
	private void launchStage(String filePath, String stageTitle) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(filePath));
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setTitle(stageTitle);
		stage.setScene(scene);
		stage.show();
	}
	


	@FXML
	void botonEmergencia(ActionEvent event) {
		if (tgEmergencia.isSelected()) {
			labelAtencion.setVisible(true);
		}
		else labelAtencion.setVisible(false);
	}

	@FXML
	void atras(ActionEvent event) {
		//Volvemos a la ventana de notificaciones
		
		if(flagProcedencia.equals("menu")) {
			volverANotificaciones(event);
		}else if(flagProcedencia.equals("sensor")) {
			
		}
	

	}

	@FXML
	void enviarMensaje(ActionEvent event) throws Exception {//TODO AQUI TOCO (DANI)
		if(idOrden == 0 || idUnion == 0) {
			
		}
		Calendar calendario = new GregorianCalendar();
		LocalDate fecha = LocalDate.now();

		hora = calendario.get(Calendar.HOUR_OF_DAY);
		min = calendario.get(Calendar.MINUTE);
		seg = calendario.get(Calendar.SECOND);

		String horaTotal = "a las "+hora+":"+min+":"+seg;
		//Inicializamos las variables del mensaje
		String asunto = etAsunto.getText().toString().trim();
		String mensaje = etMensaje.getText().toString().trim();
		boolean emergencia = tgEmergencia.isSelected();
		Usuario usuarioSeleccionado = jcbDestinatarios.getSelectionModel().getSelectedItem();
		
		if(usuarioSeleccionado instanceof Padre) {
			tipoUsuarioReceptor = "padre";
		}else if (usuarioSeleccionado instanceof Sanitario){
			
			//tipoUsuarioReceptor = ((Sanitario) usuarioSeleccionado).getTipoSanitario().toString();
			
			tipoSanitarioReceptor = ((Sanitario) usuarioSeleccionado).getTipoSanitario().toString();
			if(tipoSanitarioReceptor.equals("ENFERMERO")) {
				tipoUsuarioReceptor = "enfermero";
			}else if (tipoSanitarioReceptor.equals("MEDICO")){				
				tipoUsuarioReceptor = "medico";
			}
			/*else {
				tipoUsuarioReceptor = "NO HE COGIDO USUARIO RECEPTOR";//TODO NOS QUEDAMOS AQUI!!!!!!!!!
			}*/
			
			
			
		}
		if(isRespuesta) {
			GestorDePersistencia gdp = new GestorDePersistencia();
			//TODO consulta para recoger el mismo IdUnion del mensaje al cual está respondiendo
			//Select 
			idUnion = notificacionAnterior.getIdUnion();
			//Select asunto, mensaje, idUnion, idOrden from notificaciones where idUnion = idUnion
			
			//TODO sacar el idOrden y sumarle 1 (+1)
			idOrden =  gdp.selectMaxIdOrdenFromNotificacionesByIdUnion(idUnion);
			//TODO RECOGER DE LA BBDD EL IDORDEN DE LA BBDD DONDE IDUNION(que tengo) = IDUNION(que saco de la bbdd)
			idOrden++;
			
			
			
			
			//Es imposible que no exista, porque no se puede responder a un mensaje que no existe.
			
		}else {
			GestorDePersistencia gdp = new GestorDePersistencia();
			idOrden = 1;
			idUnion = gdp.selectMaxIdUnionFromNotificaciones();
			idUnion++;
			
			
			//TODO si el mensaje no es respuesta, el idUnion debe autoincrementar al último idUnion registrado en la BBDD y el idOrden debe ponerse a 1
			//select idUnion from notificaciones where idUnion = ?
			//
		}
		
		
		String destino = usuarioSeleccionado.getNombre();
		String dniDestino = usuarioSeleccionado.getDni();
		String email = usuarioSeleccionado.getMail();
		UsuarioLoggeado ul = UsuarioLoggeado.getUsuarioLoggeado();
		String remitente = ul.getNombre();
		String dniRemitente = ul.getDni();
		ArrayList<String> registroMensajes = new ArrayList<>();
		//Si es una respuesta a otro mensaje, inicializamos el registro de mensajes
		
		/*if(notificacionAnterior != null) {
			registroMensajes = notificacionAnterior.getRegistroMensajes();
			//Añadimos el ultimo mensaje al registro
			registroMensajes.add("["+notificacionAnterior.getFecha()+"] "+//TODO ECHAR VISTAZO
					notificacionAnterior.getRemitente()+":\n "+
					notificacionAnterior.getMensaje());
		}*/

		//TODO CONSEGUIR EL DESTINATARIO PARA ENVIARLO POR EL POPUP

		Stage stage = (Stage) myGridPane.getScene().getWindow();
		Alert.AlertType type = Alert.AlertType.CONFIRMATION;
		Alert alert = new Alert(type, "");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(stage);

		alert.getDialogPane().setContentText("Pulse Aceptar para continuar");
		alert.getDialogPane().setHeaderText("¿Seguro que quiere enviar el mensaje?");

		Optional<ButtonType> result = alert.showAndWait();
		if(result.get()== ButtonType.OK) {	
			fecha = LocalDate.now();
			//Creamos la notificacion con todos sus campos y se registra en json
			System.out.println("Emisor: "+tipoUsuarioConectado);
			System.out.println("Receptor: "+tipoUsuarioReceptor);
			
			if(tipoUsuarioConectado.equals("padre") && tipoUsuarioReceptor.equals("enfermero")) {
				tipoDeMensaje = 1;
			}else if(tipoUsuarioConectado.equals("padre") && tipoUsuarioReceptor.equals("medico")) {
				tipoDeMensaje = 2;
			}else if(tipoUsuarioConectado.equals("enfermero") && tipoUsuarioReceptor.equals("medico")) {
				tipoDeMensaje = 3;
			}else if(tipoUsuarioConectado.equals("enfermero") && tipoUsuarioReceptor.equals("padre")) {
				tipoDeMensaje = 4;
			}else if(tipoUsuarioConectado.equals("medico") && tipoUsuarioReceptor.equals("padre")) {
				tipoDeMensaje = 5;
			}else if(tipoUsuarioConectado.equals("medico") && tipoUsuarioReceptor.equals("enfermero")) {
				tipoDeMensaje = 6;
			}
			
			Notificacion n = new Notificacion(remitente, destino, dniRemitente, dniDestino, fecha, asunto, idUnion, idOrden, mensaje, tipoDeMensaje, isEmergencia );
			n.setDniDestino(dniDestino);
			n.setDniRemitente(dniRemitente);
			n.setRegistroMensajes(registroMensajes);
			registrarNotificación(n);
			if(emergencia) {
				EmailNotificacion.enviarMail(email, remitente, horaTotal, fecha.toString(), asunto );
			}



			//Volvemos a la ventana de notificaciones       
			volverANotificaciones(event);
		}else if(result.get() == ButtonType.CANCEL) {
			tvInfo.setText("Operación cancelada, Compruebe su conexión a Internet");
		}

			
	}

	public void volverANotificaciones(ActionEvent event) {

		GestorDeEscenas ge = new GestorDeEscenas();
		ge.cargarEscena("ListaGenericaView.fxml",
				new ListaGenericaControlador<Notificacion>(PojosMother.generateNotificacion()),
				event, false);
	}

	private void registrarNotificación(Notificacion not) {
		GestorDePersistencia gdp = new GestorDePersistencia();
		gdp.escribirNotificacion(not);
	}

}






