package controlador;

import javafx.scene.control.Label;
import modelo.Gestor;
import modelo.Padre;
import modelo.Sanitario;
import servicios.GestorDePersistencia;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;




public class CambioContrasenia{

	private String email;
	private String tipoUsuario;


    @FXML
    private GridPane gridPaneParent;

    @FXML
    private Label labelTitulo;

    @FXML
    private Label lbFallos;

    @FXML
    private JFXButton btnCambiarContrasenia;

    @FXML
    private JFXPasswordField etPrimeraContrasenia;

    @FXML
    private JFXPasswordField etSegundaContrasenia;

    @FXML
    private HBox hboxSup;

    @FXML
    private HBox hBoxInf;

    @FXML
    private Label labelNuevaContrasenia;

    @FXML
    private Label labelRepetirContrasenia;


	private String contrasenia, confirmacionContrasenia;



	public CambioContrasenia(String email, String tipoUsuario) {
		this.email = email;
		this.tipoUsuario = tipoUsuario;

	}

	@FXML
	void cambiarContrasenia(ActionEvent event) {


		contrasenia = etPrimeraContrasenia.getText().toString().trim();
		confirmacionContrasenia = etSegundaContrasenia.getText().toString().trim();

		if(!contrasenia.equals(confirmacionContrasenia)) {
			lbFallos.setText("Contraseñas no coincidentes.");
		}else {
			GestorDePersistencia gdp = new GestorDePersistencia();

			if(tipoUsuario.equals("Padre")) {

				Padre p = gdp.getPadreByMail(email);
				if(p==null) {
					lbFallos.setText("El correo introducido no existe.");
				}else {
					p.setContrasenia(confirmacionContrasenia);

					gdp.escribirPadre(p);

					Stage stage = (Stage) btnCambiarContrasenia.getScene().getWindow();
					stage.close();
				}

			}else if(tipoUsuario.equals("Sanitario")) {

				Sanitario s = gdp.getSanitarioByMail(email);
				if(s==null) {
					lbFallos.setText("El correo introducido no existe.");
				}else {
					s.setContrasenia(confirmacionContrasenia);
					gdp.escribirSanitario(s);
					
				
					
					Stage stage = (Stage) btnCambiarContrasenia.getScene().getWindow();
					stage.close();
					
				}

			}else if(tipoUsuario.equals("Gestor")) {
				
				Gestor g = gdp.getGestorByMail(email);
				if(g==null) {
					lbFallos.setText("El correo introducido no existe.");
				}else {
					g.setContrasenia(confirmacionContrasenia);
					gdp.escribirGestor(g);
					
					
					Stage stage = (Stage) btnCambiarContrasenia.getScene().getWindow();
					stage.close();
				}

			}

		}

	}

}
