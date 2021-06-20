package controlador;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import modelo.Gestor;
import modelo.Padre;
import modelo.Sanitario;
import servicios.GestorDeEscenas;
import servicios.GestorDePersistencia;

public class EnviarEmailRecuperacion implements Initializable {

    @FXML
    private GridPane gridPane;

    @FXML
    private JFXTextField etMailyCodigo;

    @FXML
    private Label labelTitulo;

    @FXML
    private Label labelEnunciado;

    @FXML
    private HBox hboxBotones;

    @FXML
    private JFXButton btnAdelante;

    @FXML
    private JFXButton btnComprobarCodigo;

    @FXML
    private HBox hBoxSup;

    @FXML
    private HBox hBoxInf;

    @FXML
    private Label labelError;

	private static char[] conjunto = new char[10];
	static char[] elementos={'0','1','2','3','4','5','6','7','8','9' ,'a',
			'b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t',
			'u','v','w','x','y','z'};
	static String codRecuperarContrasenia;
	private String email;
	private String codigoGenerado;

	private String tipoUsuario;

	public EnviarEmailRecuperacion(String tipoUsuario) {
		super();
		this.tipoUsuario = tipoUsuario;
	}

	public void initialize(URL location, ResourceBundle resources) {


	}
	@FXML
	void comprobar(ActionEvent event) {


		String codigoInsertado = etMailyCodigo.getText().toString().toUpperCase();

		if(codigoGenerado.equals(codigoInsertado)) {
			GestorDeEscenas ge = new GestorDeEscenas();

			ge = new GestorDeEscenas();
			ge.cargarEscena("CambioContrasenia.fxml", new CambioContrasenia(email, tipoUsuario), event, false); 

		}else {
			labelError.setText("Código incorrecto.");
		}

	}

	@FXML
	void adelante(ActionEvent event) throws Exception {
		//IndicacionDeProgreso.launch(IndicacionDeProgreso.class);	//TODO poner barra de indicacion de progreso...

		//si no introduce un mail
		if(etMailyCodigo.getText().isEmpty()) {
			labelError.setText("Debe introducir un correo.");
			// si introduce el mail
		}else {
			email = etMailyCodigo.getText().toLowerCase().toString();
			GestorDePersistencia gdp = new GestorDePersistencia();
			//si no existe
			if(tipoUsuario.equals("Sanitario")) {

				if(existeSanitarioByMail(gdp.leerSanitarios(), email)==false) {
					labelError.setText("El correo electrónico introducido no existe, compruebe el campo.");
				}else {
					codigoGenerado = generaCodigo6char().toUpperCase();
					
					RecuperarContrasenia.enviarMail(email,
							"Código de recuperación de contraseña.", 
							"Use este codigo para validar que es usted el que quiere cambiar la contraseña.",
							codigoGenerado);
					etMailyCodigo.setText("");
					labelEnunciado.setText("Se le ha enviado un correo. Introduzca el código que se le ha proporcionado para validar el cambio de contraseña.");
					labelError.setText("");
					
					btnAdelante.setDisable(true);
					btnComprobarCodigo.setDisable(false);
				}

			}else if(tipoUsuario.equals("Gestor")) {

				if(existeGestorByMail(gdp.leerGestores(), email)==false) {
					
					labelError.setText("El correo electrónico introducido no existe, compruebe el campo.");
					
				}else {
					
					codigoGenerado = generaCodigo6char().toUpperCase();
					
					RecuperarContrasenia.enviarMail(email,
							"Código de recuperación de contraseña.", 
							"Use este código para validar que es usted el que quiere cambiar la contraseña.",
							codigoGenerado);
					etMailyCodigo.setText("");
					labelEnunciado.setText("Se le ha enviado un correo. Introduzca el código que se le ha proporcionado para validar el cambio de contraseña.");
					labelError.setText("");
					
					btnAdelante.setDisable(true);
					btnComprobarCodigo.setDisable(false);
				}
				

			}else if(tipoUsuario.equals("Padre")) {

				if (existePadreByMail(gdp.leerPadres(), email)==false ) {
					labelError.setText("El correo electrónico introducido no existe, compruebe el campo.");


				}else {

					codigoGenerado = generaCodigo6char().toUpperCase();
					//TODO METODO DE RECOGER EL CORREO DEL USUARIO CONECTADO
					RecuperarContrasenia.enviarMail(email,
							"Código de recuperación de contraseña.", 
							"Use este código para validar que es usted el que quiere cambiar la contraseña.",
							codigoGenerado);
					etMailyCodigo.setText("");
					labelError.setText("Se le ha enviado un correo. Introduzca el código que se le ha proporcionado para validar el cambio de contraseña.");
					labelError.setText("");
					
					btnAdelante.setDisable(true);
					btnComprobarCodigo.setDisable(false);
				}
			}
		}
	}
	public static boolean existePadreByMail(ArrayList<Padre> lista, String mail){
		boolean existe = false;
		int i = 0;

		while(!existe && i<lista.size()) {
			//comparar los datos de dni y contraseña con los de la lista
			if (mail.equals(lista.get(i).getMail())) { // compara 'busqueda' con elemento actual
				existe = true;
			}
			i++;

		}

		return existe;		
	}

	public static boolean existeSanitarioByMail(ArrayList<Sanitario> lista, String mail){
		boolean existe = false;
		int i = 0;

		while(!existe && i<lista.size()) {
			//comparar los datos de dni y contraseña con los de la lista
			if (mail.equals(lista.get(i).getMail())) { // compara 'busqueda' con elemento actual
				existe = true;
			}
			i++;

		}

		return existe;		
	}

	public static boolean existeGestorByMail(ArrayList<Gestor> lista, String mail){
		boolean existe = false;
		int i = 0;

		while(!existe && i<lista.size()) {
			//comparar los datos de dni y contraseña con los de la lista
			if (mail.equals(lista.get(i).getMail())) { // compara 'busqueda' con elemento actual
				existe = true;
			}
			i++;

		}

		return existe;		
	}



	//Se puede retocar el numero de caracteres que genera
	public static String generaCodigo6char(){

		for(int i=0;i<10;i++){
			int el = (int)(Math.random()*36);
			conjunto[i] = (char)elementos[el];
		}
		return codRecuperarContrasenia = new String(conjunto);
	}

}
