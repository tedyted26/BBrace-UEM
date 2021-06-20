package modelo;

import java.time.LocalDateTime;

public class Unidad {
	private double valor;
	private String fecha;
	private String hora;
	private String minutos, mes, dia;
	private LocalDateTime time;
	
	public Unidad() {
		
	}
	
	public Unidad(double valor) {
		this.valor = valor;
		setStringTime(LocalDateTime.now());
	}
	
	public Unidad(double valor, LocalDateTime time) {
		this.valor = valor;
		setStringTime(time);		
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public String getStringFecha() {
		return fecha;
	}

	public void setStringFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getStringHora() {
		return hora;
	}

	public void setStringHora(String hora) {
		this.hora = hora;
	}
	/**
	 * Devuelve un objeto de tipo LocalDateTime
	 * @return
	 */
	public LocalDateTime getLocalTime() {
		return time;
	}
	/**
	 * Cambia solo el atributo LocalDateTime "time" de la clase, no cambia los strings fecha y hora
	 * @param time
	 */
	public void setLocalTime(LocalDateTime time) {
		this.time = time;
		setStringTime(time);
	}
	
	/**
	 * Cambia el atributo LocalDateTime "time" de la clase y los strings fecha y hora segun el parametro
	 * @param time
	 */
	public void setStringTime(LocalDateTime time) {
		this.time = time;
		
		if (time.getMonthValue()<10) mes = "0"+time.getMonthValue();
		else mes = ""+time.getMonthValue();
		
		if (time.getDayOfMonth()<10) dia = "0"+time.getDayOfMonth();
		else dia = ""+time.getDayOfMonth();
		
		fecha = time.getYear()+"-"+mes+"-"+dia;
		
		if (time.getMinute()<10) minutos = "0"+time.getMinute();
		else minutos = ""+time.getMinute();
		
		hora = time.getHour()+":"+minutos;
		
	}

	
}
