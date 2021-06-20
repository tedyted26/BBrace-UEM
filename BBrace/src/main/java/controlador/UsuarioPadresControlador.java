package controlador;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import modelo.Padre;
import modelo.Sanitario;
import modelo.TipoSanitario;
import modelo.Usuario;
import servicios.GestorDeEscenas;
import servicios.GestorDePersistencia;

public class UsuarioPadresControlador implements Initializable {

	 @FXML
	    private GridPane gridPaneDatosPadre;

	    @FXML
	    private JFXButton botonCambiarFotoPerfil;

	    @FXML
	    private Circle circuloImagen;

	    @FXML
	    private Label lbInfo;

	    @FXML
	    private JFXTextField textFieldEmail;

	    @FXML
	    private JFXTextField textFieldNombre;

	    @FXML
	    private JFXButton botonModificar;

	    @FXML
	    private JFXButton botonGuardar;

	    @FXML
	    private JFXTextField textFieldDni;

	    @FXML
	    private JFXTextField textFieldMedicoDeFamilia;

	    
	
	@FXML
    private JFXButton botonLista;


	private Usuario u;
	private boolean mostrarBoton;

	public UsuarioPadresControlador (Usuario u, boolean mostrarBoton) {
		this.u = u;
		this.mostrarBoton = mostrarBoton;
	}

	private FileChooser fc;
	private File ficheroElegido;
	private Image im;
	private Image imDefautl;
	private Image imagenSubida;
	private String fotoPropia;

	private GestorDePersistencia gp;
	private File file;
	private GestorDeEscenas ge;
	
    @FXML
    void verLista(ActionEvent event) {
    	ge.cargarPopUp("ListaEnPopUp.fxml", new ListaEnPopUpControlador(u), event);
    }
    
    
	@FXML
	void cambiarFotoPerfil(ActionEvent event) {
		try {
			fc = new FileChooser();
			fc.setTitle("Seleccione la imagen");
			fc.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("Image Files",
							"*.bmp", "*.png", "*.jpg", "*.jpeg", "*.gif"));

			ficheroElegido=fc.showOpenDialog(null);

			if ( ficheroElegido!= null) {
				imagenSubida= new Image(ficheroElegido.toURI().toString());
				circuloImagen.setFill(new ImagePattern(imagenSubida));
				lbInfo.setText("");

			}else lbInfo.setText("Compruebe que el formato de la imagen es el correcto.");


			if (u instanceof Padre) {
				((Padre) u).setRutaFoto(ficheroElegido.toString());
				gp.escribirPadre((Padre)u);
			}
			else {
				((Sanitario) u).setRutaFoto(ficheroElegido.toString());
				gp.escribirSanitario((Sanitario)u);
			}
		}catch(Exception e) {
			System.err.print("Ha habido algun problema en con el explorador de archivos al seleccionar imagen o este se ha cerrado abruptamente.\n");
		}		
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gp = new GestorDePersistencia();
		ge = new GestorDeEscenas();
		botonCambiarFotoPerfil.setVisible(mostrarBoton);
		botonGuardar.setVisible(mostrarBoton);
		botonModificar.setVisible(mostrarBoton);
		textFieldDni.setText(u.getDni());
		textFieldEmail.setText(u.getMail());
		textFieldNombre.setText(u.getNombre());
		textFieldMedicoDeFamilia.setText("");
		botonLista.setVisible(false);

		textFieldDni.setEditable(false);
		textFieldEmail.setEditable(false);
		textFieldNombre.setEditable(false);
		textFieldMedicoDeFamilia.setEditable(false);
		lbInfo.setText("");

		if (u instanceof Padre) {
			fotoPropia = ((Padre) u).getRutaFoto();
			try {
				Sanitario s = gp.getSanitarioByDni(((Padre) u).getMedicoAsociadoFamiliaDni());
				textFieldMedicoDeFamilia.setText(s.getNombre());
			}catch (Exception e) {
				System.err.print("El usuario no tiene medico de familia asociado.\n");
				textFieldMedicoDeFamilia.setText("");
			}


		}
		else {
			textFieldMedicoDeFamilia.setVisible(false);
			fotoPropia = ((Sanitario) u).getRutaFoto();
			if (((Sanitario) u).getTipoSanitario().equals(TipoSanitario.ENFERMERO)) {
				textFieldMedicoDeFamilia.setVisible(false);
			}
			else if (((Sanitario) u).getTipoSanitario().equals(TipoSanitario.MEDICO)&&mostrarBoton) {
				textFieldMedicoDeFamilia.setVisible(false);
				botonLista.setVisible(true);
			}

		}

		//CONFIGURACION DE LA FOTO
		if(fotoPropia==null || fotoPropia.equals("")) {

			imDefautl = new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"usuarioImagenDefecto.png");
			circuloImagen.setFill(new ImagePattern(imDefautl));
		}
		else {
			try {
				file = new File(fotoPropia);
				if (file.isFile()) {
					fotoPropia = "file:///"+fotoPropia;
					im = new Image(fotoPropia);
					circuloImagen.setFill(new ImagePattern(im));

				}else {
					imDefautl = new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"usuarioImagenDefecto.png");
					circuloImagen.setFill(new ImagePattern(imDefautl));
					lbInfo.setText("La imagen cambió de ubicación, nombre o extensión.");

				}

			}catch(Exception e) {
				lbInfo.setText("Se ha producido un error cargando la imagen.");
				System.err.println("Se ha producido un error cargando la imagen.");
			}
		}

	}

	@FXML
    void guardarDatos(ActionEvent event) {
		//Comprueba si el nombre tiene 2 caracteres al menos
		String nombreNuevo = textFieldNombre.getText();
		String emailNuevo = textFieldEmail.getText();
		if(nombreNuevo.length() > 2) {
			//Comprobamos si tiene 10 caracteres el correo
			if(isEmailValido(emailNuevo))
			{
				textFieldEmail.setEditable(false);
				textFieldNombre.setEditable(false);
				botonGuardar.setDisable(true);
				botonModificar.setDisable(false);
				
				GestorDePersistencia gp = new GestorDePersistencia();
				textFieldNombre.getText();
				
				u.setNombre(nombreNuevo);
				u.setMail(emailNuevo);

				if(u instanceof Padre) {
					Padre p = (Padre) u;
					gp.escribirPadre(p);
					textFieldMedicoDeFamilia.setDisable(false);
                    textFieldDni.setDisable(false);
				}
				else if(u instanceof Sanitario) {
					Sanitario s = (Sanitario) u;
					gp.escribirSanitario(s);
					textFieldDni.setDisable(false);
					//textFieldMedicoDeFamilia.setVisible(false);
				}
			}
			
		}
		else {
			lbInfo.setText("El campo nombre está vacio");

		}
		
	}
    
	private boolean isEmailValido(String mail) {
		final String singleValidExpression = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w-]+\\.)*\\w+[\\w-]*\\.([a-z]{1,4}|\\d+)$";
		final String validExpression = "^" + singleValidExpression + "(\\s*;\\s*" + singleValidExpression + ")*$";

		boolean isMailValido  = false;
		Pattern compare = Pattern.compile(validExpression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = compare.matcher(mail);
		if(matcher.matches()==false) {

			lbInfo.setText("Mail no válido");
			isMailValido = false;
		}else {
			isMailValido = true;
		}
		return isMailValido;
	}

    @FXML
    void modificarDatos(ActionEvent event) {
    	textFieldEmail.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused-editable"), true);
    	textFieldEmail.setEditable(true);
		textFieldNombre.setEditable(true);
		
		botonModificar.setDisable(true);
		botonGuardar.setDisable(false);
		
		textFieldMedicoDeFamilia.setDisable(true);
        textFieldDni.setDisable(true);

    }

}



