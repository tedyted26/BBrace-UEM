package modelo;

import java.util.ArrayList;

import modelo.Bebe;

public class Padre extends Usuario{

	private String medicoAsociadoFamiliaDni;
	private ArrayList<Bebe> listaBebes = new ArrayList<Bebe>();	
	private String rutaFoto = null;

	public Padre() {
		super();

	}

	public Padre(String dni, String contrasenia, String nombre, String mail) {
		super();
		setDni(dni);
		setContrasenia(contrasenia);
		setNombre(nombre);
		setMail(mail);

	}

	public Padre(String dni, String contrasenia, String nombre, String mail, ArrayList<Bebe> listaBebes, String medicoAsociadoFamiliaDni) {
		super();
		setDni(dni);
		setContrasenia(contrasenia);
		setNombre(nombre);
		setMail(mail);
		this.listaBebes = listaBebes;
		setMedicoAsociadoFamiliaDni(medicoAsociadoFamiliaDni);

	}
	//Para JSON, constructor completo
	public Padre(String dni, String contrasenia, String nombre, String mail, ArrayList<Bebe> listaBebes, String medicoAsociadoFamiliaDni, String rutaFoto) {
		super();
		setDni(dni);
		setContrasenia(contrasenia);
		setNombre(nombre);
		setMail(mail);
		this.rutaFoto = rutaFoto;
		this.listaBebes = listaBebes;
		setMedicoAsociadoFamiliaDni(medicoAsociadoFamiliaDni);
	}

	public Padre(String dni, String contrasenia, String nombre) {
		super();
		setDni(dni);
		setContrasenia(contrasenia);
		setNombre(nombre);

	}


	public Padre(String dni, String contrasenia) {
		super();
		setDni(dni);
		setContrasenia(contrasenia);

	}

	public ArrayList<Bebe> getListaBebes() {
		return listaBebes;
	}

	public void setListaBebes(ArrayList<Bebe> listaBebes) {
		this.listaBebes = listaBebes;
	}
	
	public String getMedicoAsociadoFamiliaDni() {
		return medicoAsociadoFamiliaDni;
	}


	public void setMedicoAsociadoFamiliaDni(String medicoAsociadoFamiliaDni) {
		this.medicoAsociadoFamiliaDni = medicoAsociadoFamiliaDni;
	}
	
	public String getRutaFoto() {
		return rutaFoto;
	}

	public void setRutaFoto(String rutaFoto) {
		this.rutaFoto = rutaFoto;
	}

	@Override
	public String toString() {
		return "Padre [dni=" + getDni() + ", contrasenia=" + getContrasenia() + "]";
	}

	//Crea una copia y la devuelve (copia profunda)
	public Padre getCopy() {
		Padre p = new Padre(this.getDni(),this.getContrasenia(),this.getNombre(),this.getMail());
		p.setListaBebes(this.listaBebes);
		p.setRutaFoto(this.rutaFoto);
		p.setMedicoAsociadoFamiliaDni(medicoAsociadoFamiliaDni);

		return p;
	}

}
