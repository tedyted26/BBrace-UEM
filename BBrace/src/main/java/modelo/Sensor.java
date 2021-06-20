package modelo;

import java.util.ArrayList;

public class Sensor {

	private TipoSensor nombre; //tipo
	private double limite_anormal_inferior;
	private double limite_anormal_superior;
	private ArrayList<Unidad> datos = new ArrayList<Unidad>();

	public Sensor() {

	}
	public Sensor(TipoSensor nombre) {

		this.nombre=nombre;
		switch(this.nombre) {
			case TEMPINTERNA: this.limite_anormal_inferior = 36.5; this.limite_anormal_superior = 37.5; break;
			case TEMPEXTERNA: this.limite_anormal_inferior = 35.5; this.limite_anormal_superior = 37.0; break;
			case PULSOMETRO: this.limite_anormal_inferior = 85.0; this.limite_anormal_superior = 180.0; break;
			case OXIMETRO: this.limite_anormal_inferior = 90.0; this.limite_anormal_superior = 100.0; break;
		}
	}	
	public Sensor(TipoSensor nombre, double limite_anormal_inferior, double limite_anormal_superior, ArrayList<Unidad> datos) {
		this.nombre = nombre;
		this.limite_anormal_inferior = limite_anormal_inferior;
		this.limite_anormal_superior = limite_anormal_superior;
		this.datos = datos;
	}
	public boolean comprobarDatosAnormales() {
		return limite_anormal_inferior > datos.get(datos.size()-1).getValor();
	}
	public TipoSensor getNombre() {
		return nombre;
	}
	public void setNombre(TipoSensor nombre) {
		this.nombre = nombre;
	}
	public double getLimite_anormal() {
		return limite_anormal_inferior;
	}
	public double getLimite_anormal_superior() {
		return limite_anormal_superior;
	}
	public void setLimite_anormal_inferior(double limite_anormal_inferior) {
		this.limite_anormal_inferior = limite_anormal_inferior;
	}
	public void setLimite_anormal_superior(double limite_anormal_superior) {
		this.limite_anormal_superior = limite_anormal_superior;
	}
	public ArrayList<Unidad> getDatos() {
		return datos;
	}
	public void setDatos(ArrayList<Unidad> datos) {
		this.datos = datos;
	}
	//Devuelve la ultima unidad del sensor, si no puede, devuelve 0

	public Unidad getUltimaUnidad() {
		try {
			return datos.get(datos.size()-1);
		}catch (Exception e) {
			System.err.println("Fallo recogiendo la ultima unidad del sensor"
					+ "\nDetalles:\n"+ e.getLocalizedMessage());
			return null;
		}
	}



}
