package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Bebe {
	
	// enfermero asociado a una lista de bebés. A su vez ese enfermero va asociado a un médico.
	private ArrayList<String> listaEnfermerosDni= new ArrayList<String>();
	private String nombre;
	private LocalDate fechaNacimiento;
	private String semanasGestacion = "";
	private String observaciones = "";
	private String dniPadre;
	private Pulsera pulsera = new Pulsera();
	private int id;

	//Para JSON, constructor completo
	public Bebe(String nombre, LocalDate fechaNacimiento, String semanasGestacion, String observaciones, Pulsera pulsera, String dniPadre, ArrayList<String> listaEnfermerosDni) {
		super();
		this.nombre = nombre;
		this.fechaNacimiento = fechaNacimiento;
		this.semanasGestacion = semanasGestacion;
		this.observaciones = observaciones;
		this.pulsera = pulsera;
		this.dniPadre = dniPadre;
		this.listaEnfermerosDni=listaEnfermerosDni;
	}
	public Bebe(String nombre, LocalDate fechaNacimiento, Pulsera pulsera, String dniPadre) {
		super();
		this.nombre = nombre;
		this.fechaNacimiento = fechaNacimiento;;
		this.pulsera = pulsera;
		this.dniPadre = dniPadre;
	}

	public Bebe(String nombre, LocalDate fechaNacimiento, String dniPadre) {
		super();
		this.nombre = nombre;
		this.fechaNacimiento = fechaNacimiento;
		this.dniPadre = dniPadre;
	}
	
	public Bebe(String nombre, LocalDate fechaNacimiento, String semanasGestacion, String observaciones,
			String dniPadre) {
		super();
		this.nombre = nombre;
		this.fechaNacimiento = fechaNacimiento;
		this.semanasGestacion = semanasGestacion;
		this.observaciones = observaciones;
		this.dniPadre = dniPadre;
	}

	public Bebe() {
		
	}
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Pulsera getPulsera() {
		return pulsera;
	}

	public void setPulsera(Pulsera pulsera) {
		this.pulsera = pulsera;
	}
	
	public String getSemanasGestacion() {
		return semanasGestacion;
	}

	public void setSemanasGestacion(String semanasGestacion) {
		this.semanasGestacion = semanasGestacion;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getDniPadre() {
		return dniPadre;
	}

	public ArrayList<String> getListaEnfermerosDni() {
		return listaEnfermerosDni;
	}
	public void setListaEnfermerosDni(ArrayList<String> listaEnfermerosDni) {
		this.listaEnfermerosDni = listaEnfermerosDni;
	}
	public void setDniPadre(String dniPadre) {
		this.dniPadre = dniPadre;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
	
	
	
}
