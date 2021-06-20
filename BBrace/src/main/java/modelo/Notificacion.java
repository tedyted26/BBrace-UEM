package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Notificacion {
	private int idNotificacion;
	private String remitente;
	private String destino;
	private String dniRemitente;
	private String dniDestino;
	private LocalDate fecha;
	private String asunto;
	ArrayList<String> registroMensajes = new ArrayList<>();
	private int idUnion;
	private int idOrden;
	private String mensaje;
	private int tipoDeMensaje;
	/*
	 *Tipo de Mensaje indica quien es el EMISOR y el RECEPTOR del mensaje con la siguiente tabla:
	 *Padre -> Enfermero ->-> 1
	 *Padre -> Medico ->-> 2
	 *Enfermero -> Medico ->-> 3
	 *Enfermero -> Padre ->-> 4
	 *Medico -> Padre ->-> 5
	 *Medico -> Enfermero ->-> 6
	 */
	
	private boolean isEmergencia = false;
	public Notificacion() {
		
	}
	
	public Notificacion(String remitente, String destino, LocalDate fecha, String asunto, String mensaje) {
		super();
		this.remitente = remitente;
		this.destino = destino;
		this.fecha = fecha;
		this.asunto = asunto;
		this.mensaje = mensaje;
	}
	
	public Notificacion(String remitente, String destino, String dniRemitente, String dniDestino, LocalDate fecha,
			String asunto, int idUnion, int idOrden, String mensaje, int tipoDeMensaje, boolean isEmergencia) {
		super();
		this.remitente = remitente;
		this.destino = destino;
		this.dniRemitente = dniRemitente;
		this.dniDestino = dniDestino;
		this.fecha = fecha;
		this.asunto = asunto;
		this.idUnion = idUnion;
		this.idOrden = idOrden;
		this.mensaje = mensaje;
		this.tipoDeMensaje = tipoDeMensaje;
		this.isEmergencia = isEmergencia;
	}



	public Notificacion(String remitente, String destino, LocalDate fecha, String asunto, String mensaje,
			boolean isEmergencia, int tipoDeMensaje) {
		super();
		this.remitente = remitente;
		this.destino = destino;
		this.fecha = fecha;
		this.asunto = asunto;
		this.mensaje = mensaje;
		this.isEmergencia = isEmergencia;
		this.tipoDeMensaje = tipoDeMensaje;
	}


	//GETTERS Y SETTERS
	public int getIdNotificacion() {
		return idNotificacion;
	}

	public void setIdNotificacion(int idNotificacion) {
		this.idNotificacion = idNotificacion;
	}

	public int getIdUnion() {
		return idUnion;
	}

	public void setIdUnion(int idUnion) {
		this.idUnion = idUnion;
	}

	public int getIdOrden() {
		return idOrden;
	}

	public void setIdOrden(int idOrden) {
		this.idOrden = idOrden;
	}

	public int getTipoDeMensaje() {
		return tipoDeMensaje;
	}

	public void setTipoDeMensaje(int tipoDeMensaje) {
		this.tipoDeMensaje = tipoDeMensaje;
	}

	public void setEmergencia(boolean isEmergencia) {
		this.isEmergencia = isEmergencia;
	}

	public String getRemitente() {
		return remitente;
	}
	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	public void setIsEmergencia(boolean b) {
		isEmergencia = b;
	}
	public boolean getIsEmergencia() {
		return isEmergencia;
	}
	public String getDniRemitente() {
		return dniRemitente;
	}
	public void setDniRemitente(String dniRemitente) {
		this.dniRemitente = dniRemitente;
	}
	public String getDniDestino() {
		return dniDestino;
	}
	public void setDniDestino(String dniDestino) {
		this.dniDestino = dniDestino;
	}
	public ArrayList<String> getRegistroMensajes() {
		return registroMensajes;
	}
	public void setRegistroMensajes(ArrayList<String> registroMensajes) {
		this.registroMensajes = registroMensajes;
	}
	
	
	
	
}
