package controlador;

import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import modelo.Bebe;
import modelo.Notificacion;
import modelo.Padre;
import modelo.Sanitario;
import modelo.Sensor;
import modelo.TipoSanitario;
import modelo.Usuario;
import modelo.UsuarioLoggeado;
import servicios.GestorDeEscenas;
import servicios.GestorDePersistencia;

//Esta lista permite mostrar cualquier tipo de datos, vinculando los mismos a tarjetas prediseñada (ej: TarjetaPadre.fxml)

public class ListaGenericaControlador<T> implements Initializable {

	@FXML
	private BorderPane mainListaBorderPane;
	@FXML
	private ListView<T> listView;

	@FXML
	private Label titulo;

	@FXML
	private JFXButton botonAccionDeLaLista;

	@FXML
	private JFXTextField textfieldBuscar;

	private String flagProcedencia = "menu";

	//Lista que se actualiza sola
	@SuppressWarnings("rawtypes")
	private ObservableList listaObservable;
	@SuppressWarnings("rawtypes")
	private ObservableList listaFija;

	//Objeto que almacena el tipo de lista (Padre, Notificacion, etc)
	private T tipo;

	//flag para crear las listas de enfermeros asociados
	private boolean listaEnfermeros;
	private boolean bebeAsociado;
	private Sanitario sanitario;
	private boolean cargarDatosBebe;


	public ListaGenericaControlador(T t) {
		this.tipo = t;
		listaObservable = FXCollections.observableArrayList();
		this.listaEnfermeros = false;
		this.bebeAsociado = false;

	}
	//sorry sanjo pero no veia otra manera
	public ListaGenericaControlador(T t, boolean listaEnfermeros) {
		this.tipo = t;
		listaObservable = FXCollections.observableArrayList();
		this.listaEnfermeros = listaEnfermeros;
		this.bebeAsociado = false;
	}
	//lo siento por esto tambien pero tampoco veia otra manera y me esta implosionando la existencia
	public ListaGenericaControlador(T t, Sanitario s, boolean bebeAsociado, boolean cargarDatosBebe) {
		this.tipo = t;
		this.sanitario = s;
		listaObservable = FXCollections.observableArrayList();
		this.bebeAsociado = bebeAsociado;
		this.listaEnfermeros = false;
		this.cargarDatosBebe = cargarDatosBebe;		
	}

	public ListView<T> getListView(){
		return listView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		GestorDePersistencia gp = new GestorDePersistencia();
		//Añadimos el observable al Nodo de lista
		listView.setItems(listaObservable);
		listaObservable.addListener(new ListChangeListener<T>() {

			//Cuando la lista cambia, realiza una acción
			@Override
			public void onChanged(Change<? extends T> c) {
				switch (tipo.getClass().getSimpleName()){
				case "Padre":
					break;
				case "Sanitario":
					break;
				case "Bebe": //Actualiza el padre al completo y lo mete en el json
					c.next();
					if(!bebeAsociado) {
						UsuarioLoggeado ul = UsuarioLoggeado.getUsuarioLoggeado();//Pillamos el usuario logueado
						Padre p = (Padre) ul.getUsuario();

						ArrayList<Bebe> listaBebes = new ArrayList<>();//Actualizamos la lista del bebe del padre
						for(Object o : c.getList().toArray()) {	
							Bebe b = (Bebe)o;
							listaBebes.add(b);

							if(c.wasAdded()) {
								c.getAddedSubList().forEach(obj -> gp.escribirDatosPulsera(
										((Bebe)obj).getPulsera()));;
							}
							if(c.wasRemoved()) {
								c.getRemoved().forEach(obj -> gp.eliminarPulseraCompleta(
										((Bebe)obj).getPulsera().getId()));
								c.getRemoved().forEach(obj -> gp.desactivarBebeById(
										((Bebe)obj).getId()));
							}
						}
						p.setListaBebes(listaBebes);
						//Metemos el padre en la base de datos
						gp.escribirPadre(p);						
					}
					break;
				case "Notificacion":	
					break;
				}
			}

		});
		configurarListaSegunElTipo();

		//Esta parte se encarga de ordenar previamente la lista de padres/sanitarios
		if (tipo instanceof Usuario) {
			Collator en_US = Collator.getInstance(Locale.ENGLISH);
			en_US.setStrength(Collator.PRIMARY);
			listaObservable.sort((e1,e2) -> en_US.compare(((Usuario)e1).getNombre(), ((Usuario)e2).getNombre()));
		}
		listaFija = FXCollections.observableArrayList(listaObservable);
	}
	/**
	 * Compara 2 strings alfabeticamente, tomando el mismo numero de caracteres en
	 * ambas, de esta forma se puede comparar si empiezan igual.
	 * 
	 * Devuelve 0 si son iguales, 1 si esta por delante lexicograficamente, -1 si
	 * está detras
	 * @param a
	 * @param b
	 * @return
	 */
	private int compararStringsEmpiezanIgual(String a, String b) {
		int sSize = (a.length() < b.length())?a.length():b.length();
		Collator collator = Collator.getInstance(Locale.ENGLISH);
		collator.setStrength(Collator.PRIMARY);
		return collator.compare(a.substring(0,sSize), b.substring(0,sSize));
	}

	/**
	 * Devuelve un ArrayList<Usuario> dados una lista ordenada por el nombre de los usuarios y un filtro. Devuelve todos
	 * aquellos usuarios cuyos nombres empiezan por el filtro (String)
	 * @param listaAFiltrar
	 * @param filtro
	 * @return
	 */
	private ArrayList<Usuario> filtrarPorNombre(List<Usuario> listaAFiltrar, String filtro) {
		if(listaAFiltrar.isEmpty()) return new ArrayList<Usuario>();
		else return filtrarPorNombreRecursivo(listaAFiltrar, filtro.toLowerCase(), 0, listaAFiltrar.size()-1);

	}
	/**
	 * Metodo recursivo para encontrar aquellos usuarios cuyo inicio del nombre coincide con el
	 * filtro.
	 * 
	 * Para cada llamada recursiva toma 2 ints (ini y fin), entre 0 y el tamaño de la lista, 
	 * con los que va cercando donde buscar, dividiendo las sublistas a la mitad.
	 * 
	 * Al estar ordenada, solo necesita de 1 llamada recursiva por cada llamada.
	 * @param listaAFiltrar
	 * @param filtro
	 * @param ini
	 * @param fin
	 * @return
	 */
	private ArrayList<Usuario> filtrarPorNombreRecursivo(List<Usuario> listaAFiltrar, String filtro,int ini, int fin) {
		if(ini == fin) {
			Usuario u = listaAFiltrar.get(ini);
			ArrayList<Usuario> listaADevolver = new ArrayList<Usuario>();
			if(compararStringsEmpiezanIgual(u.getNombre(), filtro) == 0) {
				listaADevolver.add(u);
			}
			return listaADevolver;
		}
		else {
			int mitad = (ini + fin)/2;
			String nombre = listaAFiltrar.get(mitad).getNombre();
			int localizadorRegionDeLista = compararStringsEmpiezanIgual(nombre, filtro);
			if(localizadorRegionDeLista == 0) {
				//Si encuentra uno que coincida, busca aquellos que puedan agruparse en torno a él
				int iniFiltro = getIniDelFiltro(listaAFiltrar, filtro, ini, mitad);
				int finFiltro = getFinDelFiltro(listaAFiltrar, filtro, mitad, fin);
				ArrayList<Usuario> listaADevolver = new ArrayList<Usuario>();

				listaADevolver.addAll(listaAFiltrar.subList(iniFiltro, finFiltro));
				return listaADevolver;

			}
			else if(localizadorRegionDeLista == 1)
				return filtrarPorNombreRecursivo(listaAFiltrar, filtro, ini, mitad);
			else if(localizadorRegionDeLista == -1)
				return filtrarPorNombreRecursivo(listaAFiltrar, filtro, mitad+1, fin);		
		}
		return null;
	}
	/**
	 * Devuelve la primera posición donde coincide el filtro
	 * Se llama de forma recursiva hasta que lo localiza
	 * @param listaAFiltrar
	 * @param filtro
	 * @param ini
	 * @param fin
	 * @return
	 */
	private int getIniDelFiltro(List<Usuario> listaAFiltrar, String filtro, int ini, int fin) {
		if(ini == fin) return ini;
		else {
			int mitad = (ini + fin)/2;
			String nombre = listaAFiltrar.get(mitad).getNombre();
			if(compararStringsEmpiezanIgual(nombre, filtro) == 0) 
				return getIniDelFiltro(listaAFiltrar, filtro, ini, mitad);
			else return getIniDelFiltro(listaAFiltrar, filtro, mitad+1, fin);
		}
	}
	/**
	 * Devuelve una posición despues de la última que coincide con el filtro
	 * Se llama de forma recursiva hasta que la localiza
	 * @param listaAFiltrar
	 * @param filtro
	 * @param ini
	 * @param fin
	 * @return
	 */
	private int getFinDelFiltro(List<Usuario> listaAFiltrar, String filtro, int ini, int fin) {
		if(ini == fin) return ini;
		else {
			int mitad = (ini + fin)/2;
			String nombre = listaAFiltrar.get(mitad).getNombre();
			if(compararStringsEmpiezanIgual(nombre, filtro) == 0) 
				return getFinDelFiltro(listaAFiltrar, filtro, mitad+1, fin);
			else return getFinDelFiltro(listaAFiltrar, filtro, ini, mitad-1);
		}
	}

	@SuppressWarnings("unchecked")
	@FXML
	void accionSegunTipoLista(ActionEvent event) {
		GestorDeEscenas ge = new GestorDeEscenas();
		String filtro;
		ArrayList<Usuario> listaFiltrada;
		switch (tipo.getClass().getSimpleName()){
		case "Padre":
			filtro = textfieldBuscar.getText();
			listaObservable.clear();
			if (!filtro.equals("")) {
				listaFiltrada = filtrarPorNombre(listaFija, filtro);		 		
				listaObservable.addAll(listaFiltrada);
			}
			else listaObservable.addAll(listaFija);
			break;
		case "Sanitario":
			filtro = textfieldBuscar.getText();
			listaObservable.clear();
			if (!filtro.equals("")) {
				listaFiltrada = filtrarPorNombre(listaFija, filtro);
				listaObservable.addAll(listaFiltrada);
			}
			else listaObservable.addAll(listaFija);
			break;
		case "Bebe":
			if (bebeAsociado) {
				//TODO boton de buscar bebes asociados
			}
			ge.cargarEscenaEnNuevaVentana("CrearPulsera.fxml", new CrearPulseraController(listaObservable),false);
			break;
		case "Notificacion":	
			ge.cargarEscena("GenerarNotificacion.fxml", new GenerarNotificacion(false, flagProcedencia), event, false);
			break;
		}


	}

	//Esto configura tanto la lista como el controlador de la tarjeta, segun el tipo con el que trabajemos
	@SuppressWarnings("unchecked")
	private void configurarListaSegunElTipo() {
		GestorDePersistencia gj = new GestorDePersistencia();
		UsuarioLoggeado usuario = UsuarioLoggeado.getUsuarioLoggeado();

		switch (tipo.getClass().getSimpleName()){
		case "Padre":
			listaObservable.addAll(gj.leerPadres(false));
			listView.setCellFactory(listaObservable -> new TarjetaPadreControlador<T>());
			titulo.setText("Lista de padres");
			botonAccionDeLaLista.setText(null);
			botonAccionDeLaLista.setGraphic(new ImageView(new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"lupaBlanca.png")));
			botonAccionDeLaLista.setTooltip(new Tooltip("Buscar"));
			botonAccionDeLaLista.setPrefWidth(30);
			botonAccionDeLaLista.setVisible(true);
			textfieldBuscar.setVisible(true);
			break;
		case "Sanitario":
			Sanitario sa = (Sanitario) tipo;
			//si el sanitario pasado por parametro está vacío, carga los datos de todos los sanitarios
			if (sa.getDni()==null) {
				listaObservable.addAll(gj.leerSanitarios());
				listView.setCellFactory(listaObservable -> new TarjetaSanitarioControlador<T>());
				titulo.setText("Lista de sanitarios");
				botonAccionDeLaLista.setText(null);
				botonAccionDeLaLista.setGraphic(new ImageView(new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"lupaBlanca.png")));
				botonAccionDeLaLista.setTooltip(new Tooltip("Buscar"));
				botonAccionDeLaLista.setPrefWidth(30);
				botonAccionDeLaLista.setVisible(true);
				textfieldBuscar.setVisible(true);
			}
			//si no, en vez de una lista de sanitarios, se representa la lista de padres asociados a ese sanitario
			else {
				if (sa.getTipoSanitario()==TipoSanitario.MEDICO) {
					if (listaEnfermeros) {
						ArrayList<Sanitario> listaEnfermerosAsociados = new ArrayList<>();
						for (String dni : sa.getListaEnfermerosAsociadosDni()) {
							listaEnfermerosAsociados.add(gj.getSanitarioByDni(dni));
						}
						listaObservable.addAll(listaEnfermerosAsociados);
						listView.setCellFactory(listaObservable -> new TarjetaSanitarioControlador<T>());
						titulo.setText("Enfermeros asociados");
						botonAccionDeLaLista.setText(null);
						botonAccionDeLaLista.setGraphic(new ImageView(new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"lupaBlanca.png")));
						botonAccionDeLaLista.setTooltip(new Tooltip("Buscar"));
						botonAccionDeLaLista.setPrefWidth(30);
						botonAccionDeLaLista.setVisible(true);
						textfieldBuscar.setVisible(true);
					}
					else {
						ArrayList<Padre> listaPadresAsociados = new ArrayList<>();
						for (String dni : sa.getDniPadresAsociados()) listaPadresAsociados.add(gj.getPadreByDni(dni, false));		
						listaObservable.addAll(listaPadresAsociados);
						listView.setCellFactory(listaObservable -> new TarjetaPadreControlador<T>());
						titulo.setText("Padres asociados");
						botonAccionDeLaLista.setText(null);
						botonAccionDeLaLista.setGraphic(new ImageView(new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"lupaBlanca.png")));
						botonAccionDeLaLista.setTooltip(new Tooltip("Buscar"));
						botonAccionDeLaLista.setPrefWidth(30);
						botonAccionDeLaLista.setVisible(true);
						textfieldBuscar.setVisible(true);
					}
				}
			}

			break;
		case "Bebe":
			if (bebeAsociado) {
				if (sanitario.getTipoSanitario()==TipoSanitario.ENFERMERO) {
					ArrayList<Bebe> listaBebesAsociados = gj.getBebesAsociadosSanitario(sanitario.getDni(),cargarDatosBebe);
					listaObservable.addAll(listaBebesAsociados);
					listView.setCellFactory(listaObservable -> new TarjetaBebesAsociadosControlador<T>()); 
					titulo.setText("Bebes asociados");
					//					botonAccionDeLaLista.setText(null);
					//					botonAccionDeLaLista.setGraphic(new ImageView(new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"lupaBlanca.png")));
					//					botonAccionDeLaLista.setTooltip(new Tooltip("Buscar"));
					//					botonAccionDeLaLista.setPrefWidth(30);
					//					botonAccionDeLaLista.setVisible(true);
					//					textfieldBuscar.setVisible(true);
				}
			}else {
				listaObservable.addAll(usuario.getListaBebes());
				listView.setOrientation(Orientation.HORIZONTAL);
				listView.setCellFactory(listaObservable -> new TarjetaBebeControlador<T>());
				titulo.setText("Lista de Pulseras");
				botonAccionDeLaLista.setText("AÑADIR PULSERA");
				botonAccionDeLaLista.setVisible(true);
				listView.getStyleClass().clear();
				listView.getStyleClass().add("lista-pulseras");
			}
			break;
		case "Notificacion"://TODO AQUI DANI	
			ArrayList<Notificacion> array =gj.leerNotificacionesByDniDestino(usuario.getDni());//TODO AQUI COGE TODAS DEL USUARIO
			Collections.reverse(array);//TODO SANJO
			listaObservable.addAll(array);
			//listaObservable.sort(Comparator.comparing(Notificacion::getFecha).reversed());
			listView.setCellFactory(listaObservable -> new TarjetaNotificacionControlador<T>());//TODO CELDA POR CADA NOTIFICACION
			titulo.setText("Lista de Notificaciones");
			botonAccionDeLaLista.setText("ENVIAR MENSAJE");
			botonAccionDeLaLista.setVisible(true);
			break;
		case "Sensor":
			Sensor s = (Sensor) tipo;
			listaObservable.addAll(s.getDatos());
			listView.setCellFactory(listaObservable -> new TarjetaHistorialController<T>(s));
			if(s.getNombre().toString().equals("TEMPINTERNA"))titulo.setText("Temperatura");
			if(s.getNombre().toString().equals("PULSOMETRO"))titulo.setText("Pulso");
			if(s.getNombre().toString().equals("OXIMETRO"))titulo.setText("Oxígeno en Sangre");
			titulo.setAlignment(Pos.CENTER);
			botonAccionDeLaLista.setVisible(false);
		}
	}

}


