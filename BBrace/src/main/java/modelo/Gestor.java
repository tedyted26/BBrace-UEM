package modelo;

public class Gestor extends Usuario{

	public Gestor() {

	}

	public Gestor(String dni, String contrasenia, String nombre, String mail) {
		super(dni, contrasenia, nombre, mail);
	}

	@Override
	public String toString() {
		return "Gestor [dni=" + getDni() + ", contrasenia=" + getContrasenia() + "]";
	}
	//Crea y devuelve una copia del gestor (copia profunda)
	public Gestor getCopy() {
		Gestor g = new Gestor(this.getDni(),this.getContrasenia(),this.getNombre(),this.getMail());
		return g;
	}
}
