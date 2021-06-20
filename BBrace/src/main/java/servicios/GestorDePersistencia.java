package servicios;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import modelo.Bebe;
import modelo.Gestor;
import modelo.Notificacion;
import modelo.Padre;
import modelo.Pulsera;
import modelo.Sanitario;
import modelo.TipoSanitario;


public class GestorDePersistencia {
	IPersistenciaSimple gj;
	
	public GestorDePersistencia() {
		this.gj = new GestorSQLiteSimple();
		
	}
	
//GET ARRAYLIST
	//Devuelve un array de notificaciones
	public ArrayList<Notificacion> leerNotificaciones(){
		return gj.leerNotificaciones();
	}
	public ArrayList<Notificacion> leerNotificacionesByDniDestino(String dniDestino){
		return gj.leerNotificacionesByDniDestino(dniDestino);
	}
	//Devuelve un array de padres
	public ArrayList<Padre> leerPadres(){
		ArrayList<Padre> array = gj.leerPadres();
		ArrayList<Padre> arrayDescifrado = new ArrayList<>();
		for(Padre p: array ) {
			p.setContrasenia(descifrarString(p.getContrasenia()));//Desciframos contraseña
			arrayDescifrado.add(p);
		}
		//array.get(array.size()-1).setContrasenia(descifrarString(array.get(array.size()-1).getContrasenia()));
		return arrayDescifrado;
	}
	//FIXME metodo duplicado
	public ArrayList<Padre> leerPadres(boolean cargarDatosBebe){
		GestorSQLiteSimple gs = new GestorSQLiteSimple();
		ArrayList<Padre> array = gs.leerPadres(cargarDatosBebe);
		ArrayList<Padre> arrayDescifrado = new ArrayList<>();
		for(Padre p: array ) {
			p.setContrasenia(descifrarString(p.getContrasenia()));//Desciframos contraseña
			arrayDescifrado.add(p);
		}
		return arrayDescifrado;
	}
	//Devuelve un array de sanitarios
	public ArrayList<Sanitario> leerSanitarios(){
		ArrayList<Sanitario> array = gj.leerSanitarios();
		for(Sanitario s: array ) {
			s.setContrasenia(descifrarString(s.getContrasenia()));//Desciframos contraseña
		}
		return array;
	}
	//Lee el array de Sanitarios y devuelve otro array pero solo de Sanitarios de tipo Enfermero
	public ArrayList<Sanitario> leerSanitariosEnfermeros(){
		return gj.leerSanitarios(TipoSanitario.ENFERMERO);
	}
	
	//Lee el array de Sanitarios y devuelve otro array pero solo de Sanitarios de tipo Médico
	public ArrayList<Sanitario> leerSanitariosMedico(){
		return gj.leerSanitarios(TipoSanitario.MEDICO);
	}
	
	//Devuelve un array de gestores
	public ArrayList<Gestor> leerGestores(){
		ArrayList<Gestor> array = gj.leerGestores();
		for(Gestor g: array ) {
			g.setContrasenia(descifrarString(g.getContrasenia()));//Desciframos contraseña
		}
		return array;
	}
	/**
	 * Lee el archivo de padres y busca qué bebes tienen el dni del sanitario que se busca. 
	 * Devuelve un array de padres (este si que funciona)
	 * @param dniSanitario
	 * @return
	 */
	public ArrayList<Bebe> getBebesAsociadosSanitario(String dniSanitario){
		return gj.getBebesAsociadosSanitario(dniSanitario);
	}
	//FIXME METODO DUPLICADO
	public ArrayList<Bebe> getBebesAsociadosSanitario(String dniSanitario, boolean cargarDatosBebe){
		GestorSQLiteSimple gs = new GestorSQLiteSimple();
		return gs.getBebesAsociadosSanitario(dniSanitario, cargarDatosBebe);
	}
		 
//GET POJOS SEGUN LO ESPECIFICADO (dni,mail)
	//Devuelve un Padre segun un dni
	public Padre getPadreByDni(String dni) {
		Padre p = gj.getPadreByDni(dni);
		p.setContrasenia(descifrarString(p.getContrasenia()));//Desciframos contraseña
		return p;
	}
	//metodo duplicado para no cambiar firma
	//FIXME 
	public Padre getPadreByDni(String dni, boolean leerBebes) {
		GestorSQLiteSimple gs = new GestorSQLiteSimple();
		Padre p = gs.getPadreByDni(dni, leerBebes);
		p.setContrasenia(descifrarString(p.getContrasenia()));//Desciframos contraseña
		return p;
	}
	//Devuelve un Padre segun un mail
	public Padre getPadreByMail(String mail) {
		Padre p = gj.getPadreByMail(mail);
		p.setContrasenia(descifrarString(p.getContrasenia()));//Desciframos contraseña
		return p;
	}
	
	//FIXME Pasar a json y crear metodo generico para json y SQL
	//busca si existe un mail en Padres
	public boolean buscarMailExistePadre(String mail) {
		return gj.buscarMailExistePadre(mail);
	}
	
	//busca si existe un mail en Sanitario
	public boolean buscarMailExisteSanitario(String mail) {
		return gj.buscarMailExisteSanitario(mail);
	}
	
	//Devuelve un Sanitario segun un dni
	public Sanitario getSanitarioByDni(String dni) {
		Sanitario s = gj.getSanitarioByDni(dni);
		s.setContrasenia(descifrarString(s.getContrasenia()));//Desciframos contraseña
		return s;
	}
	//Devuelve un Sanitario segun un mail
	public Sanitario getSanitarioByMail(String mail) {
		Sanitario s = gj.getSanitarioByMail(mail);
		s.setContrasenia(descifrarString(s.getContrasenia()));//Desciframos contraseña
		return s;
	}
	/**
	 * Devuelve una lista de Sanitarios asociados a un padre segun el dni del padre
	 * @param dniPadre 
	 * @return
	 */
//	public ArrayList<Padre> getPadresByDniSanitario(String dniSanitario) {
//		return gj.getSanitariosByDniPadre(dniPadre);
//	}
	//Devuelve un Gestor segun un dni
	public Gestor getGestorByDni(String dni) {
		Gestor g = gj.getGestorByDni(dni);
		g.setContrasenia(descifrarString(g.getContrasenia()));//Desciframos contraseña
		return g;
	}
	//Devuelve un Gestor segun un mail
	public Gestor getGestorByMail(String mail) {
		Gestor g = gj.getGestorByMail(mail);
		g.setContrasenia(descifrarString(g.getContrasenia()));//Desciframos contraseña
		return g;
	}
	
//GUARDAR POJOS EN PERSISTENCIA
	//Guarda una notificacion
	public void escribirNotificacion(Notificacion n) {
		gj.escribirNotificacion(n);
	}
	//Guarda un padre, si existe, lo sobrescribe
	public void escribirPadre(Padre p) {
		Padre padre = p.getCopy();
		padre.setContrasenia(cifrarString(padre.getContrasenia()));//Ciframos contraseña
		gj.escribirPadre(padre);
		
	}
	//Guarda un sanitario, si existe, lo sobrescribe
	public void escribirSanitario(Sanitario s) {
		Sanitario sanitario = s.getCopy();
		sanitario.setContrasenia(cifrarString(sanitario.getContrasenia()));//Ciframos contraseña
		gj.escribirSanitario(sanitario);
	}
	//Guarda un gestor, si existe, lo sobrescribe
	public void escribirGestor(Gestor g) {
		Gestor gestor = g.getCopy();
		gestor.setContrasenia(cifrarString(gestor.getContrasenia()));//Ciframos contraseña
		gj.escribirGestor(gestor);
	}
	//Guarda una pulsera y la elimina del JSON de no asignadas
	public void escribirDatosPulsera(Pulsera p) {
		gj.escribirDatosPulsera(p);
		gj.desactivarPulseraSinAsignar(p.getId());
	}
	
	public void desactivarBebeById(int id){
		gj.desactivarBebeById(id);
	}
//ELIMINAR PULSERAS
	/**
	 * Elimina la pulsera pasada por parametro del archivo de datos de pulsera (con todos los datos incluidos),
	 * añade el id de la pulsera al archivo de pulseras sin asignar, y
	 * @param idPulsera
	 */
	public void eliminarPulseraCompleta(String idPulsera) {
		gj.desactivarDatosPulsera(idPulsera);
		gj.escribirPulseraSinAsignar(idPulsera);
	}

//COMPROBAR SI EXISTE
	//Devuelve True si el padre existe, false si no. (Segun el dni)
	public boolean comprobarSiPadreExiste(String dni) {
		return gj.comprobarSiPadreExiste(dni); //Si el indice es -1, el padre no existe
	}
	//Devuelve True si la pulsera sin asociar existe, false si no (Segun id de Pulsera)
	public boolean comprobarSiPulseraSinAsociarExiste(String idPulsera) {
		return gj.comprobarSiPulseraSinAsociarExiste(idPulsera);//Si el indice es -1, la pulsera no existe
	}
	public boolean comprobarDniContrasenia(String tabla, String dni, String contrasenia) {
		return gj.comprobarDniContrasenia(tabla, dni, cifrarString(contrasenia));
	}
	
	public  ArrayList<Notificacion> leerNotificacionesByIdUnion(int idUnion) {
		GestorSQLiteSimple gs = new GestorSQLiteSimple();
		
		
		return gs.leerNotificacionesByIdUnion(idUnion);
		
	}
	
	public int selectMaxIdUnionFromNotificaciones(){
		GestorSQLiteSimple gs = new GestorSQLiteSimple();
		
		return gs.leerIdUnionMaximo();
	}
	public int selectMaxIdOrdenFromNotificacionesByIdUnion(int idUnion) {
		GestorSQLiteSimple gs = new GestorSQLiteSimple();
		return  gs.leerIdOrdenMaximoByIdUnion(idUnion);
	}
	
	
//CIFRADO Y DESCIFRADO
	//Cifra una string
	private String cifrarString(String contra) {
		String newContrasenia = "NO FUNCIONA";
		try {
			newContrasenia = Encriptacion.encriptar(contra) ;
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return newContrasenia;
	}
	//Descifra una string
	private String descifrarString(String contra){
		String newContrasenia = "NO FUNCIONA";
		try {
			newContrasenia = Encriptacion.desencriptar(contra) ;
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return newContrasenia;
	}
		
}
