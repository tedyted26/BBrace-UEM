package controlador;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import modelo.Gestor;
import modelo.Padre;
import modelo.Sanitario;
import modelo.UsuarioLoggeado;
import servicios.GestorDeEscenas;
import servicios.GestorDePersistencia;

public class VistaLogginController implements Initializable{

	private GestorDeEscenas ge = new GestorDeEscenas();

	@FXML
	private GridPane gridPaneParent;

	@FXML
	private GridPane gridPaneChild;

	@FXML
	private JFXTextField dniTextField;

	@FXML
	private JFXPasswordField contraseniaTextField;

	@FXML
	private JFXButton botonAcceder;

	@FXML
	private JFXComboBox<String> comboboxInicioSesion;
	
	@FXML
    private Hyperlink hlRecuperarContrasenia;

	@FXML
	private Label labelIncorrecto;
	private String seleccion;
	
	private String seleccionCambioContrasenia;

    @FXML
    void recuperarContraseña(ActionEvent event) {
    	seleccionCambioContrasenia = comboboxInicioSesion.getValue().toString();
    	ge.cargarPopUp("EnviarEmailRecuperacion.fxml", new EnviarEmailRecuperacion(seleccionCambioContrasenia), event); 
    	

    }

	@FXML
	void inicioDeSesion(ActionEvent event) {
		seleccion = comboboxInicioSesion.getValue().toString();
		String dni = dniTextField.getText();
		String contrasenia = contraseniaTextField.getText();


		GestorDePersistencia gp = new GestorDePersistencia();

		if(seleccion.contentEquals("Padre")) {
			if (gp.comprobarDniContrasenia("padre", dni, contrasenia)) {
				//De teo: se meten los datos del usuario que se ha loggeado en la clase estatica y se carga la ventana de los padres
				UsuarioLoggeado.getUsuarioLoggeado().rellenarDatos(gp.getPadreByDni(dni));
				ge.cargarEscena("MenuPadres.fxml", new MenuPadresController(), event, true);

			}
			else {
				labelIncorrecto.setText("No se puede iniciar sesión, usuario o contraseña incorrectas");
			}
		}

		else if(seleccion.contentEquals("Sanitario")) {
			if (gp.comprobarDniContrasenia("medico", dni, contrasenia) || gp.comprobarDniContrasenia("enfermero", dni, contrasenia)) {
				//CAMBIAR PANTALLA DE INICIO
				UsuarioLoggeado.getUsuarioLoggeado().rellenarDatos(gp.getSanitarioByDni(dni));
				ge.cargarEscena("MenuSanitarios.fxml", new MenuSanitariosController(), event, true);

			}
			else {
				labelIncorrecto.setText("No se puede iniciar sesión, usuario o contraseña incorrectas");
			}
		}
		else if(seleccion.contentEquals("Gestor")) {
			if (gp.comprobarDniContrasenia("gestor", dni, contrasenia)) {
				//CAMBIAR PANTALLA DE INICIO
				UsuarioLoggeado.getUsuarioLoggeado().rellenarDatos(gp.getGestorByDni(dni));
				ge.cargarEscena("MenuGestor.fxml", new MenuGestorController(), event, true);

			}
			else {
				labelIncorrecto.setText("No se puede iniciar sesión, usuario o contraseña incorrectas");
			}
		}
	}

public static boolean existePadre(ArrayList<Padre> lista, String dni, String contrasenia){
	boolean existe = false;
	int i = 0;

	while(!existe && i<lista.size()) {
		//comparar los datos de dni y contraseña con los de la lista
		if (dni.equals(lista.get(i).getDni()) && contrasenia.equals(lista.get(i).getContrasenia())) { // compara 'busqueda' con elemento actual
			existe = true;
		}
		i++;

	}

	return existe;		
}

public static boolean existeSanitario(ArrayList<Sanitario> lista, String dni, String contrasenia){
	boolean existe = false;
	int i = 0;

	while(!existe && i<lista.size()) {
		//comparar los datos de dni y contraseña con los de la lista
		if (dni.equals(lista.get(i).getDni()) && contrasenia.equals(lista.get(i).getContrasenia())) { // compara 'busqueda' con elemento actual
			existe = true;
		}
		i++;

	}

	return existe;		
}

public static boolean existeGestor(ArrayList<Gestor> lista, String dni, String contrasenia){
	boolean existe = false;
	int i = 0;

	while(!existe && i<lista.size()) {
		//comparar los datos de dni y contraseña con los de la lista
		if (dni.equals(lista.get(i).getDni()) && contrasenia.equals(lista.get(i).getContrasenia())) { // compara 'busqueda' con elemento actual
			existe = true;
		}
		i++;

	}

	return existe;		
}

@Override
public void initialize(URL arg0, ResourceBundle arg1) {
	ObservableList<String> listaCombo = FXCollections.observableArrayList("Padre", "Sanitario", "Gestor");
	comboboxInicioSesion.setItems(listaCombo);
	comboboxInicioSesion.getSelectionModel().select(0);		
}

}
