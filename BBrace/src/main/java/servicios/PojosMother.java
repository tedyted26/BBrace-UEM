package servicios;

import java.time.LocalDate;

import modelo.Bebe;
import modelo.Notificacion;
import modelo.Padre;

//Es una clase de utilidad
//Permite obtener POJOS o listas de ellos sin tener que estar llamando a sus constructores
public class PojosMother<T>{
	//LISTAS DE POJOS
	private final static Padre listaPadres[] = {
			new Padre("56895689E", "contra1", "Paco perez dam"),
			new Padre("868767667", "contra2", "David San José"),
			new Padre("6786786786", "contra3", "Pepe Viyuela"),
			new Padre("568945275689E", "contra4", "Hidetaka Miyazaki"),
			new Padre("5689567878689E", "contra5", "Boris Izaguirre"),
			new Padre("7878789789", "contra6", "Aaron Vizcaya Reino"),
			new Padre("798546456", "contra7", "Alejandro Sans")
	};
	
	private final static Notificacion listaNotificaciones[] = {
			new Notificacion("Doctor", "David San Jose", LocalDate.of(2020, 5, 24), "Cita1", "Esto es un mensaje pregenerado"),
			new Notificacion("Doctor", "David San Jose",  LocalDate.of(2010, 6, 2), "Cita2", "Esto es un mensaje pregenerado"),
			new Notificacion("Pulsera", "David San Jose",  LocalDate.of(2007, 7, 27), "Alerta1", "Esto es un mensaje pregenerado3"),
			new Notificacion("Pulsera", "David San Jose",  LocalDate.of(2008, 12, 30), "Cita3", "Esto es un mensaje pregenerado"),
			new Notificacion("Sanitario", "David San Jose",  LocalDate.of(2020, 1, 5), "Alerta2", "Esto es un mensaje pregenerado"),
			new Notificacion("Sanitario", "David San Jose",  LocalDate.of(2019, 2, 2), "Informacion", "Esto es un mensaje pregenerado")
	};
	private final static Bebe listaBebes[] = {
			new Bebe("Paco", LocalDate.of(2019, 5, 24),"12345678S"),
			new Bebe("Jose Antonio", LocalDate.of(2019, 8, 20),"45678912A"),
			new Bebe("Francisco", LocalDate.of(2019, 11, 5),"98765432P"),
			new Bebe("Otro nombre feo de bebe", LocalDate.of(1500, 1, 1),"64597831C"),
	};
	//METODOS
	
	public Object[] generateListByObjectType(T tipo) { //Devuelve una lista segun el POJO que se le pase
		switch (tipo.getClass().getSimpleName()){
		case "Padre":
			//return generatePadreArray();
		case "CentroSanitario":
			break;
		case "Bebe":
			break;
		case "Notificacion":
			return generateNotificacionArray();
		}
		return null;
	}
	public static Notificacion generateNotificacion() {
		return listaNotificaciones[(int) (Math.random()*6)];
	}
	public static Notificacion[] generateNotificacionArray(){
		return listaNotificaciones;
	}
	public static Padre generatePadre(){
		return listaPadres[(int) (Math.random()*6)];
	}
	public static Padre[] generatePadreArray(){
		return listaPadres;
	}
	public static Bebe generateBebe() {
		return listaBebes[(int) (Math.random()*(listaBebes.length-1))];
	}
	public static Bebe[] generateBebeArray(){
		return listaBebes;
	}
}
