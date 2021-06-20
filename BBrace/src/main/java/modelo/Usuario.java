package modelo;

import javafx.scene.image.Image;
import servicios.GestorDeEscenas;

public class Usuario {

	private String dni;
	private String contrasenia;
	private String nombre;
	private String mail;
	private Image fotoPerfil;


	public Usuario() {
		setFotoPredeterminada();

	}

	public Usuario(String dni, String contrasenia, String nombre, String mail) {
		this.dni = dni;
		this.contrasenia = contrasenia;
		this.nombre = nombre;
		this.mail = mail;
		setFotoPredeterminada();

	}

	public Usuario(String dni, String contrasenia, String nombre, String mail, Image foto) {
		this.dni = dni;
		this.contrasenia = contrasenia;
		this.nombre = nombre;
		this.mail = mail;

		setFotoPredeterminada();

	}
	
	public void setFotoPredeterminada() {
		try {
			
			fotoPerfil= new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"usuarioImagenDefecto.png");
			
		}catch(Exception e){
		
		}
	}

	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni =dni;

	}
	public String getContrasenia() {
		return contrasenia;
	}
	public void setContrasenia(String contrasenia) {	
		this.contrasenia = contrasenia;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}

	public Image getFotoPerfil() {
		return fotoPerfil;
	}


}
