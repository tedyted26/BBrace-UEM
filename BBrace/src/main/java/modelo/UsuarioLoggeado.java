package modelo;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UsuarioLoggeado {
	
	private static UsuarioLoggeado usuarioLoggeado;
	
	private Padre padre;
	private Sanitario sanitario;
	private Gestor gestor;
	private Type tipo;
	
	//patron singleton 
	//para llamar la clase en cualquier sitio : UsuarioLoggeado u = UsuarioLoggeado.getUsuarioLoggeado();
	//si está vacío teneis que llamar  u.rellenarDatos(p), donde p es un usuario (padre, sanitario o gestor)
	public static UsuarioLoggeado getUsuarioLoggeado() {

		if (usuarioLoggeado==null) usuarioLoggeado=new UsuarioLoggeado();
		return usuarioLoggeado;
	}

	private UsuarioLoggeado (){
	}

	//rellena los datos del usuario que ha iniciado sesion
	public void rellenarDatos(Usuario objetoUsuarioConDatos) {
		if(padre==null&&sanitario==null&&gestor==null) {
			if (objetoUsuarioConDatos.getClass().equals(Padre.class)){
				tipo = Padre.class;
				padre = (Padre) objetoUsuarioConDatos;	
			}
			if (objetoUsuarioConDatos.getClass().equals(Sanitario.class)) {
				tipo = Sanitario.class;
				sanitario = (Sanitario) objetoUsuarioConDatos;
			}
			if (objetoUsuarioConDatos.getClass().equals(Gestor.class)) {
				tipo = Gestor.class;
				gestor = (Gestor) objetoUsuarioConDatos;
			}
		}
	}

	//devuelve el tipo de Usuario con el que se ha inicializado la clase
	public Type getTipo() {
		return tipo;
	}
	
	//para añadir el tipo de usuario en las ventanas, si se utiliza la de arriba devuelve un modelo.class
	public String getTipoEnString() {
		if (tipo.equals(Padre.class)) return "Padre";
		if (tipo.equals(Sanitario.class)) return "Sanitario";
		if (tipo.equals(Gestor.class)) return "Gestor";
		else return null;
	}
	
	//borra los datos del usuario
	public void borrarDatos() {
		usuarioLoggeado = null;
		tipo = null;
		padre = null;
		sanitario = null;
		gestor = null;
	}
	
	//devuelve el usuario
	public Usuario getUsuario() {
		if (tipo.equals(Padre.class)) return padre;
		if (tipo.equals(Sanitario.class)) return sanitario;
		if (tipo.equals(Gestor.class)) return gestor;
		else return null;		
	}
	
	public TipoSanitario getTipoSanitario() {
		if (tipo.equals(Sanitario.class)) return sanitario.getTipoSanitario();
		else return null;
	}
	
	//devuelve los datos del Usuario dependiendo de qué tipo se ha loggeado
	public String getDni() {
		if (tipo.equals(Padre.class)) return padre.getDni();
		if (tipo.equals(Sanitario.class)) return sanitario.getDni();
		if (tipo.equals(Gestor.class)) return gestor.getDni();
		else return null;
	}

	public String getNombre() {
		if (tipo.equals(Padre.class)) return padre.getNombre();
		if (tipo.equals(Sanitario.class)) return sanitario.getNombre();
		if (tipo.equals(Gestor.class)) return gestor.getNombre();
		else return null;
	}
	
	public String getContrasenia() {
		if (tipo.equals(Padre.class)) return padre.getContrasenia();
		if (tipo.equals(Sanitario.class)) return sanitario.getContrasenia();
		if (tipo.equals(Gestor.class)) return gestor.getContrasenia();
		else return null;
	}
	
	public String getMail() {
		if (tipo.equals(Padre.class)) return padre.getMail();
		if (tipo.equals(Sanitario.class)) return sanitario.getMail();
		if (tipo.equals(Gestor.class)) return gestor.getMail();
		else return null;
	}
	
	public ArrayList<String> getMedicoAsociadoAEnfermero() {
		if (tipo.equals(Sanitario.class) && sanitario.getTipoSanitario().equals(TipoSanitario.ENFERMERO)) return sanitario.getMedicoAsociadoAEnfermeroDni();
		if (tipo.equals(Sanitario.class) && sanitario.getTipoSanitario().equals(TipoSanitario.MEDICO)) return sanitario.getListaEnfermerosAsociadosDni();

		else return null;
	}
	
	public String getRutaFoto() {
		if (tipo.equals(Padre.class)) return padre.getRutaFoto();
		if (tipo.equals(Sanitario.class)) return sanitario.getRutaFoto();
		else return null;
	}
	
	public ArrayList<Bebe> getListaBebes() {
		if (tipo.equals(Padre.class)) return padre.getListaBebes();
		else return null;
	}

	
	public ArrayList<String> getDniPadresAsociados() {
		if (tipo.equals(Sanitario.class)) return sanitario.getDniPadresAsociados();
		else return null;
	}
}
