package controlador;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import modelo.Padre;
import modelo.Sanitario;
import servicios.GestorDePersistencia;

public class VistaRegistroController {

	private boolean mailValido=false;
	private boolean dniValido=false;

	@FXML
	private GridPane gridPaneParent;

	@FXML
	private Label labelUsuarioRegistradoCorrectamente;

	@FXML
	private GridPane gridPaneChild;

	@FXML
	private JFXTextField etNombre;

	@FXML
	private JFXTextField etMail;

	@FXML
	private JFXTextField etDni;

	@FXML
	private JFXCheckBox checkTerminosCondiciones;

	@FXML
	private JFXButton botonRegistrarse;

	@FXML
	private Label labelMailNoValido;

	@FXML
	private Label labelDniNoValido;

	@FXML
	private JFXPasswordField etContrasenia;

	@FXML
	private JFXPasswordField etContraseniaConfirmar;

	@FXML
	void registroNuevoUsuario(ActionEvent event) {
		GestorDePersistencia gp= new GestorDePersistencia();


		//Comprobamos que todos los campos esten completos
		//Comprobamos si el mail y dni introducidos son validos
		if(etDni.getText().isEmpty()==false 
				&& etContrasenia.getText().isEmpty()==false 
				&& etContraseniaConfirmar.getText().isEmpty()==false 
				&& etNombre.getText().isEmpty()==false
				&& etMail.getText().isEmpty()==false 
				&& checkTerminosCondiciones.isSelected()==true 
				&& mailValido==true 
				&& dniValido==true){


			if(gp.buscarMailExistePadre(etMail.getText().toString())==false) {
				//Si la contraseña coincide con confirmas contraseña
				if(etContrasenia.getText().equals(etContraseniaConfirmar.getText())) {

					//Busco si el usuario está ya creado

					//ArrayList<Padre> listaPadres=g.padresFromJson("FicheroPadres.json");

					GestorDePersistencia persistencia=new GestorDePersistencia();            		

					Padre p = new Padre();
					p.setDni(etDni.getText().toString().trim());//.trim() sirve para recoger los datos sin espaciado al principio y al final
					p.setMail(etMail.getText().toString().trim()); 
					p.setNombre(etNombre.getText().toString().trim());
					p.setContrasenia(etContrasenia.getText().toString().trim());
					asociarMedicoAPadre(p);

					persistencia.escribirPadre(p);
					etNombre.setText("");
					etMail.setText("");
					etContraseniaConfirmar.setText("");
					etContrasenia.setText("");
					etDni.setText("");
					labelDniNoValido.setText("");
					labelMailNoValido.setText("");
					checkTerminosCondiciones.setSelected(false);
					labelUsuarioRegistradoCorrectamente.setText("Su usuario se ha registrado correctamente");
					//  }


					//Si la contraseña no coincide con confirmar contraseña
				}else {
					labelUsuarioRegistradoCorrectamente.setText("Las contraseñas no coinciden");
				}
			}else {
				labelUsuarioRegistradoCorrectamente.setText("La dirección de correo electrónico ya está registrada");

			}

			//No ha seleccionado una opción en el menu de opciones

			//  if principal a partir de aquí
		}else if(checkTerminosCondiciones.isSelected()==false) {

			labelUsuarioRegistradoCorrectamente.setText("Para poder registrarse debe aceptar los Términos y Condiciones de uso");
		}else if (mailValido==false) {
			labelUsuarioRegistradoCorrectamente.setText("Debe introducir un mail válido");

		}else {
			labelUsuarioRegistradoCorrectamente.setText("Debe rellenar todos los campos de texto");
		}
	}

	@FXML
	void validarCamposTexto(KeyEvent event) {
		Object evt =event.getSource();
		
		if(evt.equals(etNombre)) {
			//Condición para solo ingresar datos tipo texto
			if(!Character.isLetter(event.getCharacter().charAt(0)) &&
					!event.getCharacter().equals(" ")){
				event.consume(); //no puede escribirlo
			}

		}else if(evt.equals(etDni)) {

			if(etDni.getText().length()>=9) {
				event.consume();
			}else if(etDni.getText().length()<8) {
				labelDniNoValido.setText("El DNI tiene que tener 9 caracteres");

			}else if(etDni.getText().length()==8) {
				labelDniNoValido.setText(" ");
				setDniValido(true);           
			}
		}else if(evt.equals(etMail)) {

			final String singleValidExpression = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w-]+\\.)*\\w+[\\w-]*\\.([a-z]{1,4}|\\d+)$";
			final String validExpression = "^" + singleValidExpression + "(\\s*;\\s*" + singleValidExpression + ")*$";

			Pattern compare = Pattern.compile(validExpression, Pattern.CASE_INSENSITIVE);
			Matcher matcher = compare.matcher(etMail.getText());
			if(matcher.matches()==false) {

				labelMailNoValido.setText("Mail no válido");
				setMailValido(false);
			}else {
				labelMailNoValido.setText("Mail válido");
				setMailValido(true);
			}
		}
	}

	/*
	 * Comprobación de Mail y Dni
	 */

	public void setDniValido(boolean dniValido) {
		this.dniValido = dniValido;
	}


	public boolean isMailValido() {
		return mailValido;
	}


	public void setMailValido(boolean mailValido) {
		this.mailValido = mailValido;
	}

	// Definimos un rango de valores y nos devuelve uno aleatoriamente
	private int getNumeroRandom(int min, int max){
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	public void asociarMedicoAPadre(Padre padre){

		GestorDePersistencia gp= new GestorDePersistencia();
		ArrayList<Sanitario> listaMedicos=gp.leerSanitariosMedico();

		//Buscamos el médico que menos pacientes tenga
		if(listaMedicos.size()!=0) {
			int busqueda=-1;
			int menorValorFlag=9999;
			for(int i=0;i<listaMedicos.size();i++) {
				int valorActual=listaMedicos.get(i).getDniPadresAsociados().size();
				if( valorActual< menorValorFlag) {
					menorValorFlag=valorActual;
					busqueda=i;
				}
			}
			String medico=listaMedicos.get(busqueda).getDni().toString();
			Sanitario miMedico=gp.getSanitarioByDni(medico);
			//Añado al padre a la lista de Pacientes Asociados del médico
			miMedico.getDniPadresAsociados().add(padre.getDni().toString());
			padre.setMedicoAsociadoFamiliaDni(miMedico.getDni().toString());

			gp.escribirPadre(padre);
			gp.escribirSanitario(miMedico);


		}
	}

}
