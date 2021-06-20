package controlador;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import javafx.scene.input.KeyEvent;
import modelo.Sanitario;
import modelo.TipoSanitario;
import servicios.GestorDePersistencia;


public class RegistroSanitariosControlador implements Initializable {

	private boolean mailValido=false;
	private boolean dniValido=false;

	@FXML
	private Label labelUsuarioRegistradoCorrectamente;

	@FXML
	private JFXTextField etNombre;

	@FXML
	private JFXTextField etMail;

	@FXML
	private JFXTextField etDni;

	@FXML
	private JFXPasswordField etContrasenia;

	@FXML
	private JFXPasswordField etContraseniaConfirmar;

	@FXML
	private JFXComboBox<String> comboBoxTipoSanitario;

	@FXML
	private Label labelMailNoValido;

	@FXML
	private Label labelDniNoValido;

	@FXML
	private JFXButton botonRegistrarse;


	@FXML
	void registroNuevoSanitario(ActionEvent event) {
		GestorDePersistencia gp= new GestorDePersistencia();

		String tipoSanitario = comboBoxTipoSanitario.getSelectionModel().getSelectedItem();
		boolean sanitarioSeleccionado=comboBoxTipoSanitario.getSelectionModel().isEmpty();


		//Comprobamos que todos los campos esten completos
		//Comprobamos si el mail y dni introducidos son validos
		if(etDni.getText().isEmpty()==false 
				&& etContrasenia.getText().isEmpty()==false 
				&& etContraseniaConfirmar.getText().isEmpty()==false 
				&& etNombre.getText().isEmpty()==false
				&& etMail.getText().isEmpty()==false 
				&& mailValido==true 
				&& dniValido==true
				&& sanitarioSeleccionado==false){

			if(gp.buscarMailExisteSanitario(etMail.getText().toString())==false) {
				//Si la contraseña coincide con confirmas contraseña
				if(etContrasenia.getText().equals(etContraseniaConfirmar.getText())) {

					//Busco si el usuario está ya creado

					//ArrayList<Padre> listaPadres=g.padresFromJson("FicheroPadres.json");

					GestorDePersistencia persistencia=new GestorDePersistencia();            		

					Sanitario s = new Sanitario();
					s.setDni(etDni.getText().toString().trim());//.trim() sirve para recoger los datos sin espaciado al principio y al final
					s.setMail(etMail.getText().toString().trim()); 
					s.setNombre(etNombre.getText().toString().trim());
					s.setContrasenia(etContrasenia.getText().toString().trim());

					if(tipoSanitario.equals("Enfermero")) {
						s.setTipoSanitario(TipoSanitario.ENFERMERO);
						asociarEnfermeroAMedicos(s);
					}else if(tipoSanitario.equals("Médico")) {
						s.setTipoSanitario(TipoSanitario.MEDICO);
						s.setListaEnfermerosAsociados(asociarMedicoAEnfermeros(s));
					}

					persistencia.escribirSanitario(s);
					etNombre.setText("");
					etMail.setText("");
					etContraseniaConfirmar.setText("");
					etContrasenia.setText("");
					etDni.setText("");
					labelDniNoValido.setText("");
					labelMailNoValido.setText("");
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
		}else if (mailValido==false) {
			labelUsuarioRegistradoCorrectamente.setText("Debe introducir un mail válido");

		}else if(sanitarioSeleccionado==true){

			labelUsuarioRegistradoCorrectamente.setText("Debe seleccionar el tipo de sanitario");

		}else {
			labelUsuarioRegistradoCorrectamente.setText("Debe rellenar todos los campos de texto");
		}


	}

	/*
	 * Función que valida si los campos de texto introducidos en el Registro son correctos
	 */
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

			final String singleValidExpression = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
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

	public ArrayList<String> asociarMedicoAEnfermeros (Sanitario medico) {

		//Solo si es medico tiene una lista de Enfermeros asociados
		if (medico.getTipoSanitario().equals(TipoSanitario.MEDICO)) {
			GestorDePersistencia gp= new GestorDePersistencia();
			ArrayList<Sanitario> listaEnfermeros=gp.leerSanitariosEnfermeros();
			ArrayList<String> enfermerosAsociadosAMedicoDni= new ArrayList<String>();


			// Recorro el array de enfermeros y le asocio al médico como máximo 2 de los enfermeros que no tienen médico asociado
			int f=0;
			for(int i=0; i<listaEnfermeros.size();i++) {
				boolean enfermeroOcupado=listaEnfermeros.get(i).getEnfermeroYaAsociadoAMedico();

				//Si el enfermero no tiene un médico ya asignado entonces le asigno ese
				if (enfermeroOcupado==false && f<2) {
					enfermerosAsociadosAMedicoDni.add(listaEnfermeros.get(i).getDni().toString());

					listaEnfermeros.get(i).setEnfermeroYaAsociadoAMedico(true);
					ArrayList<String> medicoAsociadoEnfermero= new ArrayList<String>();
					String dniMedico= medico.getDni().toString();
					medicoAsociadoEnfermero.add(dniMedico);
					listaEnfermeros.get(i).setMedicoAsociadoAEnfermero(medicoAsociadoEnfermero);


					gp.escribirSanitario(listaEnfermeros.get(i));
					f++;

				}
			}
			return enfermerosAsociadosAMedicoDni;

		}else {
			return null;
		}

	}

	public void asociarEnfermeroAMedicos (Sanitario enfermero) {
		ArrayList<String> dniMedico=new ArrayList<>();

		//Solo si es medico tiene una lista de Enfermeros asociados
		if (enfermero.getTipoSanitario().equals(TipoSanitario.ENFERMERO)) {
			GestorDePersistencia gp= new GestorDePersistencia();

			//lista con médicos
			ArrayList<Sanitario> listaMedicos=gp.leerSanitariosMedico();

			if(listaMedicos.size()!=0) {
				int busqueda=-1;
				int menorValorFlag=9999;
				for(int i=0;i<listaMedicos.size();i++) {
					int valorActual=listaMedicos.get(i).getListaEnfermerosAsociadosDni().size();
					if( valorActual< menorValorFlag) {
						menorValorFlag=valorActual;
						busqueda=i;
					}
				}
				String medico=listaMedicos.get(busqueda).getDni().toString();

				dniMedico.add(medico);
				Sanitario miMedico=gp.getSanitarioByDni(medico);
				miMedico.getListaEnfermerosAsociadosDni().add(enfermero.getDni().toString());
				gp.escribirSanitario(miMedico);



				enfermero.setEnfermeroYaAsociadoAMedico(true);
				enfermero.setMedicoAsociadoAEnfermero(dniMedico);
				gp.escribirSanitario(enfermero);

			}

		}
	}


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		comboBoxTipoSanitario.getItems().add("Enfermero");
		comboBoxTipoSanitario.getItems().add("Médico");


	}

}

