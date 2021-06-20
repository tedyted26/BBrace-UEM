package servicios;

import java.util.ArrayList;

import modelo.Bebe;
import modelo.Gestor;
import modelo.Notificacion;
import modelo.Padre;
import modelo.Pulsera;
import modelo.Sanitario;
import modelo.TipoSanitario;

public interface IPersistenciaSimple {
	public ArrayList<Notificacion> leerNotificaciones();
	public ArrayList<Notificacion> leerNotificacionesByDniDestino(String dniDestino);
	public ArrayList<Padre> leerPadres();
	public ArrayList<Sanitario> leerSanitarios();
	public ArrayList<Sanitario> leerSanitarios(TipoSanitario tipoSanitario);
	public ArrayList<Gestor> leerGestores();
	public ArrayList<Bebe> getBebesAsociadosSanitario(String dniSanitario);
	public Padre getPadreByDni(String dni);
	public Padre getPadreByMail(String mail);
	public Sanitario getSanitarioByDni(String dni);
	public Sanitario getSanitarioByMail(String mail);
	public Gestor getGestorByDni(String dni);
	public Gestor getGestorByMail(String mail);
	public void escribirNotificacion(Notificacion n);
	public void escribirPadre(Padre p);
	public void escribirSanitario(Sanitario s);
	public void escribirGestor(Gestor g);
	public void escribirDatosPulsera(Pulsera pulsera);
	public void escribirPulseraSinAsignar(String pulsera);
	public void desactivarBebeById(int id);
	public void desactivarDatosPulsera(String pulsera);
	public void desactivarPulseraSinAsignar(String pulsera);
	public boolean buscarMailExistePadre(String mail);
	public boolean buscarMailExisteSanitario(String mail);
	public boolean comprobarSiPadreExiste(String dni);
	public boolean comprobarSiPulseraSinAsociarExiste(String idPulsera);
	public boolean comprobarDniContrasenia(String tabla, String dni, String contrasenia);
	
}
