package modelo;

import java.util.ArrayList;

public class Pulsera {
	private String id;
	private String dniPadre;
	private String nombreBebe;
	private Sensor temperaturaExt = new Sensor(TipoSensor.TEMPEXTERNA);
	private Sensor temperaturaInt = new Sensor(TipoSensor.TEMPINTERNA);
	private Sensor oximetro = new Sensor(TipoSensor.OXIMETRO);
	private Sensor pulsometro = new Sensor(TipoSensor.PULSOMETRO);
	
	public Pulsera(String dniPadre, String nombreBebe, Sensor temperaturaExt, Sensor temperaturaInt, Sensor oximetro, Sensor pulsometro) {
		this.dniPadre = dniPadre;
		this.nombreBebe = nombreBebe;
		this.temperaturaExt = temperaturaExt;
		this.temperaturaInt = temperaturaInt;
		this.oximetro = oximetro;
		this.pulsometro = pulsometro;
	}
	
	public Pulsera() {
		
	}
	//GETTERS Y SETTERS
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDniPadre() {
		return dniPadre;
	}
	public void setDniPadre(String dniPadre) {
		this.dniPadre = dniPadre;
	}
	public String getNombreBebe() {
		return nombreBebe;
	}
	public void setNombreBebe(String nombreBebe) {
		this.nombreBebe = nombreBebe;
	}
	
	public Sensor getTemperaturaExt() {
		return temperaturaExt;
	}
	
	public void setTemperaturaExt(Sensor temperaturaExt) {
		this.temperaturaExt = temperaturaExt;
	}
	public Sensor getTemperaturaInt() {
		return temperaturaInt;
	}
	public void setTemperaturaInt(Sensor temperaturaInt) {
		this.temperaturaInt = temperaturaInt;
	}
	public Sensor getOximetro() {
		return oximetro;
	}
	public void setOximetro(Sensor oximetro) {
		this.oximetro = oximetro;
	}
	public Sensor getPulsometro() {
		return pulsometro;
	}
	public void setPulsometro(Sensor pulsometro) {
		this.pulsometro = pulsometro;
	}
	public ArrayList<Sensor> getAllSensores() {
		ArrayList<Sensor> sensores = new ArrayList<>();
		sensores.add(temperaturaExt);
		sensores.add(temperaturaInt);
		sensores.add(oximetro);
		sensores.add(pulsometro);
		return sensores;
	}
	
}
