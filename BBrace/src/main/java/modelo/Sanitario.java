package modelo;

import java.util.ArrayList;

import servicios.GestorDeEscenas;

public class Sanitario extends Usuario{
	
	private String rutaFoto = GestorDeEscenas.getRutaRelativaDeVistasFXML()+"usuarioImagenDefecto.png";
	private ArrayList<String> dniPadresAsociados = new ArrayList<String>();
	private TipoSanitario tipoSanitario;
	private ArrayList<String> listaEnfermerosAsociadosDni = new ArrayList<>();
	private ArrayList<String> medicoAsociadoAEnfermeroDni=new ArrayList<>();
	private boolean enfermeroYaAsociadoAMedico;
	
	public Sanitario() {
		
	}
	
	//Para JSON, constructor completo
	public Sanitario(String dni, 
			String contrasenia, 
			String nombre, 
			String mail, 
			TipoSanitario tipo, 
			String rutaFoto, 
			ArrayList<String> dniPadresAsociados,
			ArrayList<String> listaEnfermerosAsociadosDni,
			ArrayList<String> medicoAsociadoAEnfermeroDni, 
			boolean enfermeroYaAsociadoAMedico) {	
		
		setDni(dni);
		setContrasenia(contrasenia);
		setNombre(nombre);
		setMail(mail);
		this.tipoSanitario = tipo;
		this.rutaFoto = rutaFoto;
		this.dniPadresAsociados = dniPadresAsociados;
		this.listaEnfermerosAsociadosDni = listaEnfermerosAsociadosDni;
		this.medicoAsociadoAEnfermeroDni = medicoAsociadoAEnfermeroDni;
		this.enfermeroYaAsociadoAMedico = enfermeroYaAsociadoAMedico;		
	}
	
	public ArrayList<String> getDniPadresAsociados() {
		return dniPadresAsociados;
	}

	public void setDniPadresAsociados(ArrayList<String> dniPadresAsociados) {
		this.dniPadresAsociados = dniPadresAsociados;
	}
	

	public TipoSanitario getTipoSanitario() {
		return tipoSanitario;
	}

	public void setTipoSanitario(TipoSanitario tipoSanitario) {
		this.tipoSanitario = tipoSanitario;
	}

	public void setRutaFoto(String rutaFoto) {
		this.rutaFoto = rutaFoto;
	}
	
	public String getRutaFoto() {
		return this.rutaFoto;
	}
		
	public ArrayList<String> getListaEnfermerosAsociadosDni() {
		return listaEnfermerosAsociadosDni;
	}

	public void setListaEnfermerosAsociados(ArrayList<String> listaEnfermerosAsociadosDni) {
		if(tipoSanitario.equals(TipoSanitario.MEDICO)) {
			this.listaEnfermerosAsociadosDni = listaEnfermerosAsociadosDni;
		}
	}

	public boolean getEnfermeroYaAsociadoAMedico() {
		return enfermeroYaAsociadoAMedico;
	}

	public void setEnfermeroYaAsociadoAMedico(boolean enfermeroYaAsociadoAMedico) {
		if(tipoSanitario.equals(TipoSanitario.ENFERMERO)) {
			this.enfermeroYaAsociadoAMedico = enfermeroYaAsociadoAMedico;
		}
	}

	public ArrayList<String> getMedicoAsociadoAEnfermeroDni() {
		return medicoAsociadoAEnfermeroDni;
	}

	public void setMedicoAsociadoAEnfermero(ArrayList<String> medicoAsociadoAEnfermeroDni) {
		this.medicoAsociadoAEnfermeroDni = medicoAsociadoAEnfermeroDni;
	}

	@Override
	public String toString() {
		return "Sanitario [dni=" + getDni() + ", contrasenia=" + getContrasenia() + "]";
	}
	//crea una copia y la devuelve (copia profunda)
	public Sanitario getCopy() {
		Sanitario s = new Sanitario(
				this.getDni(), 
				this.getContrasenia(),
				this.getNombre(),
				this.getMail(), 
				this.tipoSanitario, 
				this.rutaFoto, 
				this.dniPadresAsociados, 
				this.listaEnfermerosAsociadosDni,
				this.medicoAsociadoAEnfermeroDni,
				this.enfermeroYaAsociadoAMedico
				);
		return s;
	}
}
