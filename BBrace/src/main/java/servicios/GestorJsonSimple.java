package servicios;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import modelo.Bebe;
import modelo.Gestor;
import modelo.Notificacion;
import modelo.Padre;
import modelo.Pulsera;
import modelo.Sanitario;
import modelo.Sensor;
import modelo.TipoSanitario;
import modelo.TipoSensor;
import modelo.Unidad;

//Se encarga de la extraccion y guardado de datos de JSON.
//Esta clase solo es instanciable a traves de las clases del paquete Servicios,
//por lo que no puede ser llamada desde los controladores. Esto tiene diversos factores en cuenta:
//
//	-Evitar la alteracion del JSON por varios miembros del grupo a la vez, ya que puede llevar a errores
//	-Evitar tener que hacer grandes cambios en el momento que cambiamos a BBDD, ya que todas las clases haran
//		uso de GestorDePersistencia, usando la misma como un inyector, de esta forma, solo es necesario
//		cambiar una clase para hacer los cambios efectivos.

public class GestorJsonSimple implements IPersistenciaSimple{
	//Tipos de POJO, utilizados para especificar la etiqueta de cabecera de los JSONArray principales
	protected enum TipoPOJO{
		PADRE("padre"),
		SANITARIO("sanitario"),
		GESTOR("gestor"),
		NOTIFICACION("notificacion"),
		PULSERA("pulsera"),
		IDPULSERA("idPulsera");
		
		public final String label;
		
		private TipoPOJO(String label) {
			this.label = label;
		}
	}
	//Campos principales de busqueda, usado para filtrar POJOS 
	//(Evitamos asi el uso de Strings, para que no se le pueda pasar cualquier cosa)
	protected enum TipoCampoABuscar{
		DNI("dni"),
		MAIL("mail"),
		IDPULSERA("idPulsera");
		
		public final String label;
		
		private TipoCampoABuscar(String label) {
			this.label = label;
		}
	}
	//Rutas a los Json
	private final String rutaJsonPadres = "FicheroPadres.json";
	private final String rutaJsonNotificaciones = "FicheroNotificaciones.json";
	private final String rutaJsonSanitarios = "FicheroSanitarios.json";
	private final String rutaJsonGestor = "FicheroGestor.json";
	private final String rutaJsonDatosPulseras = "FicheroDatosPulseras.json";
	private final String rutaJsonPulserasSinAsignar = "FicheroPulserasSinAsignar.json";
	
//GETTERS DE RUTAS
	public String getRutaJsonPadres() {
		return rutaJsonPadres;
	}
	public String getRutaJsonNotificaciones() {
		return rutaJsonNotificaciones;
	}
	public String getRutaJsonSanitarios() {
		return rutaJsonSanitarios;
	}
	public String getRutaJsonGestor() {
		return rutaJsonGestor;
	}
	public String getRutaJsonDatosPulseras() {
		return rutaJsonDatosPulseras;
	}
	public String getRutaJsonPulserasSinAsignar() {
		return rutaJsonPulserasSinAsignar;
	}
	//CONSTRUCTOR
	//Declaro el constructor protected para que solo pueda ser llamado desde
	//el gestor de persistencia
	protected GestorJsonSimple() {
	}
	
//GET ARRAYLIST DE POJOS
	//Devuelve una lista con todas las notificaciones
	public ArrayList<Notificacion> leerNotificaciones() {
		 ArrayList<Notificacion> notificacionArray = new ArrayList<Notificacion>();
		 JSONArray listaNotificaciones = getJSONArrayByPath(rutaJsonNotificaciones);
		 
		 listaNotificaciones.forEach(emp ->notificacionArray.add(parseNotificacionObject( (JSONObject) emp )));
		 return notificacionArray;
	}
	public ArrayList<Notificacion> leerNotificacionesByDniDestino(String dniDestino) {
		 ArrayList<Notificacion> notificacionArray = new ArrayList<>();
		 for(Notificacion n :  leerNotificaciones()) {
			 if(n.getDniDestino().equals(dniDestino)) {
				 notificacionArray.add(n);
			 }
		 }
		 return notificacionArray;
	}
	//Devuelve una lista con todos los padres
	public ArrayList<Padre> leerPadres() {
		 ArrayList<Padre> padreArray = new ArrayList<Padre>();
		 JSONArray listaPadres = getJSONArrayByPath(rutaJsonPadres);

		 listaPadres.forEach(emp ->padreArray.add(parsePadreObject( (JSONObject) emp )));
		 return padreArray;
	}
	//Devuelve una lista con todos los sanitarios
	public ArrayList<Sanitario> leerSanitarios() {
		 ArrayList<Sanitario> sanitarioArray = new ArrayList<Sanitario>();
		 JSONArray listaSanitarios = getJSONArrayByPath(rutaJsonSanitarios);

		 listaSanitarios.forEach(san ->sanitarioArray.add(parseSanitarioObject( (JSONObject) san )));
		 return sanitarioArray;
	}
	
	public ArrayList<Sanitario> leerSanitarios(TipoSanitario tipoSanitario){
		ArrayList<Sanitario> array = leerSanitarios();
		ArrayList<Sanitario> arrayEnfermeros = new ArrayList<>();

		for(Sanitario s: array ) {
			if(s.getTipoSanitario()==tipoSanitario) {
				arrayEnfermeros.add(s);
			}			
		}
		return arrayEnfermeros;
	}
	//Devuelve una lista con todos los gestores
	public ArrayList<Gestor> leerGestores() {
		 ArrayList<Gestor> gestorArray = new ArrayList<Gestor>();
		 JSONArray listaGestores = getJSONArrayByPath(rutaJsonGestor);

		 listaGestores.forEach(san ->gestorArray.add(parseGestorObject( (JSONObject) san )));
		 return gestorArray;
	}
		
//GET POJOS SEGUN ESPECIFICACION
	//Devuelve un Padre segun el dni pasado
	public Padre getPadreByDni(String dni) {
		 JSONArray listaPadres = getJSONArrayByPath(rutaJsonPadres);
		 Padre padre;

		int index = getIndexIfExist(dni,TipoPOJO.PADRE,TipoCampoABuscar.DNI, listaPadres);
        padre = parsePadreObject((JSONObject)listaPadres.get(index));
        
		return padre;
		
	}
	
	//Devuelve un Padre segun el mail pasado
	public Padre getPadreByMail(String mail) {
		JSONArray listaPadres = getJSONArrayByPath(rutaJsonPadres);
		Padre padre;
		
		int index = getIndexIfExist(mail,TipoPOJO.PADRE,TipoCampoABuscar.MAIL, listaPadres);
		padre = parsePadreObject((JSONObject)listaPadres.get(index));
		
		return padre;
	
	}	
	//Devuelve un Sanitario segun el dni pasado
	public Sanitario getSanitarioByDni(String dni) {
		JSONArray listaSanitarios = getJSONArrayByPath(rutaJsonSanitarios);
		Sanitario sanitario;

		int index = getIndexIfExist(dni,TipoPOJO.SANITARIO,TipoCampoABuscar.DNI, listaSanitarios);
		sanitario = parseSanitarioObject((JSONObject)listaSanitarios.get(index));
        
		return sanitario;
		
	}
	//Devuelve un Sanitario segun el mail pasado
	public Sanitario getSanitarioByMail(String mail) {
		JSONArray listaSanitarios = getJSONArrayByPath(rutaJsonSanitarios);
		Sanitario sanitario;

		int index = getIndexIfExist(mail,TipoPOJO.SANITARIO,TipoCampoABuscar.MAIL, listaSanitarios);
		sanitario = parseSanitarioObject((JSONObject)listaSanitarios.get(index));
        
		return sanitario;
		
	}
	/**
	 * Devuelve un arrayList con todos los sanitarios asociados a un padre 
	 * @param dniPadre dni por el que se busca en el sanitario
	 * @return listaSanitarios
	 */
	public ArrayList<Sanitario> getSanitariosByDniPadre (String dniPadre) {
		ArrayList<Sanitario> listaSanitarios = new ArrayList<>();
		JSONArray listaJsonSanitarios = getJSONArrayByPath(rutaJsonSanitarios);
		if(!listaJsonSanitarios.isEmpty()) {
			for(Object p : listaJsonSanitarios){
				boolean isContenido = false;
				JSONObject pObject= (JSONObject)p;
				JSONObject p1 = (JSONObject) pObject.get(TipoPOJO.SANITARIO.label);
				
				JSONArray arrayDniPadresAsociados = (JSONArray) p1.get("dniPadresAsociados");
				
				for (Object o : arrayDniPadresAsociados) {
					String dniTemporal = (String)o;
					if(dniTemporal.equals(dniPadre)) {
						isContenido = true;
					}
				}
				if (isContenido) {
					Sanitario devuelto = parseSanitarioObject(pObject);
					listaSanitarios.add(devuelto);
				}
			}
		}
		
		return listaSanitarios;
	}
	//Devuelve un Gestor segun el dni pasado
	public Gestor getGestorByDni(String dni) {
		JSONArray listaGestores = getJSONArrayByPath(rutaJsonGestor);
		Gestor gestor;

		int index = getIndexIfExist(dni,TipoPOJO.GESTOR,TipoCampoABuscar.DNI, listaGestores);
		gestor = parseGestorObject((JSONObject)listaGestores.get(index));
       
		return gestor;
		
	}
	//Devuelve un Gestor segun el dni pasado
	public Gestor getGestorByMail(String mail) {
			JSONArray listaGestores = getJSONArrayByPath(rutaJsonGestor);
			Gestor gestor;

			int index = getIndexIfExist(mail,TipoPOJO.GESTOR,TipoCampoABuscar.MAIL, listaGestores);
			gestor = parseGestorObject((JSONObject)listaGestores.get(index));
	       
			return gestor;
			
		}

	/**
	 * Devuelve una Pulsera segun el id asociado
	 * @param idPulsera
	 * @return Pulsera
	 */
	public Pulsera getPulseraById(String idPulsera) {
		JSONArray listaPulseras = getJSONArrayByPath(rutaJsonDatosPulseras);
		Pulsera pulsera;

		int index = getIndexIfExist(idPulsera, TipoPOJO.PULSERA, TipoCampoABuscar.IDPULSERA ,listaPulseras);
		pulsera = parseDatosPulseraObject((JSONObject)listaPulseras.get(index));
       
		return pulsera;
	}	
	
	/**
	 * Devuelve la lista de bebes asociados que tiene un sanitario segun su dni (este si funciona)
	 * @param dniSanitario 
	 * @return
	 */
	public ArrayList<Bebe> getBebesAsociadosSanitario(String dniSanitario){
		ArrayList<Bebe> bebes = new ArrayList<>();
		
		JSONArray listaPadres = getJSONArrayByPath(rutaJsonPadres);
		
		for (Object p : listaPadres) {
			JSONObject pObject= (JSONObject)p;
			JSONObject padre = (JSONObject) pObject.get("padre");
			JSONArray listaBebes = (JSONArray) padre.get("listaBebes");
			for (Object b : listaBebes) {
				JSONObject bebe = (JSONObject)b;
				JSONArray listaEnfermeros = (JSONArray) bebe.get("listaEnfermerosDni");
				if (listaEnfermeros.contains(dniSanitario)) bebes.add(parseBebeObject(bebe, (String) padre.get("dni"))); 
			}
				
		}
		
		return bebes;
	}
//COMPROBAR SI EXISTEN
	public boolean comprobarSiPadreExiste(String dni) {
		return (getIndexIfExist(dni,TipoPOJO.PADRE,TipoCampoABuscar.DNI,
				getJSONArrayByPath(getRutaJsonPadres())
			) >= 0);
	}
	public boolean comprobarSiPulseraSinAsociarExiste(String idPulsera) {
		return (getIndexIDPulseraIfExist(idPulsera,TipoCampoABuscar.IDPULSERA,
				getJSONArrayByPath(getRutaJsonPulserasSinAsignar())
			) >= 0);
	}
	public boolean buscarMailExistePadre(String mail) {
		ArrayList<Padre> array=leerPadres();
		boolean existe=false;
		int i=0;
		while(!existe && i<array.size()) {
			if(array.get(i).getMail().contentEquals(mail)) {
				existe=true;
			}
			i++;
		}	
		return existe;
	}
	public boolean buscarMailExisteSanitario(String mail) {
		ArrayList<Sanitario> array=leerSanitarios();
		boolean existe=false;
		int i=0;
		while(!existe && i<array.size()) {
			if(array.get(i).getMail().contentEquals(mail)) {
				existe=true;
			}
			i++;
		}
		return existe;
	}
//GUARDAR POJOS EN EL JSON
	//Añade una nueva notificacion al json
	@SuppressWarnings("unchecked")
	public void escribirNotificacion(Notificacion n) {
		JSONArray listaNotificaciones = getJSONArrayByPath(rutaJsonNotificaciones);
		
			
			JSONObject detailNotificacion = new JSONObject();
			detailNotificacion.put("remitente", n.getRemitente());
			detailNotificacion.put("destino", n.getDestino());
			detailNotificacion.put("dniRemitente", n.getDniRemitente());
			detailNotificacion.put("dniDestino", n.getDniDestino());
			detailNotificacion.put("fecha", ""+n.getFecha());
			detailNotificacion.put("asunto", n.getAsunto());
			detailNotificacion.put("mensaje", n.getMensaje());
			detailNotificacion.put("isEmergencia", n.getIsEmergencia());
			
			JSONArray registroMensajes = new JSONArray();
			for(String mensajeTmp : n.getRegistroMensajes()) {
				registroMensajes.add(mensajeTmp);
			}
			detailNotificacion.put("registroMensajes", registroMensajes);
			
			JSONObject notificacionJson = new JSONObject();
			notificacionJson.put(TipoPOJO.NOTIFICACION.label, detailNotificacion);
			
			
			listaNotificaciones.add(notificacionJson);
		
		
		try (FileWriter file = new FileWriter(rutaJsonNotificaciones)) {
			 
            file.write(listaNotificaciones.toJSONString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	//
	/**
	 * Añade un nuevo padre o modifica uno ya creado si ya existe (segun dni)
	 * @param p
	 */
	@SuppressWarnings("unchecked")
	public void escribirPadre(Padre p) {
		JSONArray listaPadres = getJSONArrayByPath(rutaJsonPadres);

		JSONArray listaBebes = new JSONArray();

		for(Bebe b : p.getListaBebes()) {		
			JSONObject bebe = new JSONObject();
			bebe.put("nombre", b.getNombre());
			bebe.put("fechaNacimiento", ""+b.getFechaNacimiento());
			bebe.put("padreDni", p.getDni());
			bebe.put("observaciones", b.getObservaciones());
			bebe.put("semanasGestacion", b.getSemanasGestacion());
			bebe.put("idPulsera",b.getPulsera().getId());
			bebe.put("listaEnfermerosDni",b.getListaEnfermerosDni());
			listaBebes.add(bebe);
		}

		JSONObject detailPadre = new JSONObject();
		detailPadre.put("dni", p.getDni());
		detailPadre.put("contrasenia", p.getContrasenia());
		detailPadre.put("nombre", p.getNombre());
		detailPadre.put("mail", p.getMail());
		detailPadre.put("listaBebes", listaBebes);
		detailPadre.put("rutaFoto", p.getRutaFoto());
		detailPadre.put("medicoAsociadoFamiliaDni", p.getMedicoAsociadoFamiliaDni());

		JSONObject padreJson = new JSONObject();
		padreJson.put(TipoPOJO.PADRE.label, detailPadre);
		//Comprobamos si el padre ya existe
		int indexPadre = getIndexIfExist(p.getDni(),TipoPOJO.PADRE,
				TipoCampoABuscar.DNI, listaPadres);
		if(indexPadre >= 0) {
			listaPadres.remove(indexPadre);
		}

		listaPadres.add(padreJson);

		try (FileWriter file = new FileWriter(rutaJsonPadres)) {

			file.write(listaPadres.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//añade un nuevo sanitario o modifica uno ya creado si ya existe (segun dni)
	@SuppressWarnings("unchecked")
	public void escribirSanitario(Sanitario s) {
		JSONArray listaSanitarios = getJSONArrayByPath(rutaJsonSanitarios);
		
			
			JSONObject detailSanitario = new JSONObject();
			
			detailSanitario.put("dniPadresAsociados", s.getDniPadresAsociados()); 
			detailSanitario.put("mail", s.getMail());
			detailSanitario.put("contrasenia", s.getContrasenia());
			detailSanitario.put("nombre", s.getNombre());
			detailSanitario.put("dni", s.getDni());
			detailSanitario.put("tipoSanitario",""+ s.getTipoSanitario());
			detailSanitario.put("enfermeroYaAsociadoAMedico",s.getEnfermeroYaAsociadoAMedico());
			detailSanitario.put("medicoAsociadoAEnfermeroDni", s.getMedicoAsociadoAEnfermeroDni());
			detailSanitario.put("listaEnfermerosAsociadosDni", s.getListaEnfermerosAsociadosDni());
			detailSanitario.put("rutaFoto", s.getRutaFoto());
			
			JSONObject sanitarioJson = new JSONObject();
			sanitarioJson.put(TipoPOJO.SANITARIO.label, detailSanitario);
			
			//Comprobamos si el sanitario ya existe
			int indexSanitario = getIndexIfExist(s.getDni(),TipoPOJO.SANITARIO,
					TipoCampoABuscar.DNI, listaSanitarios);
			if(indexSanitario >= 0) {
				listaSanitarios.remove(indexSanitario);
			}
			
			listaSanitarios.add(sanitarioJson);
			
		try (FileWriter file = new FileWriter(rutaJsonSanitarios)) {
			 
            file.write(listaSanitarios.toJSONString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	//añade un nuevo sanitario o modifica uno ya creado si ya existe (segun dni)
	@SuppressWarnings("unchecked")
	public void escribirGestor(Gestor g) {
		JSONArray listaGestor = getJSONArrayByPath(rutaJsonGestor);
		
			
			JSONObject detailGestor = new JSONObject();
			
			detailGestor.put("mail", g.getMail());
			detailGestor.put("contrasenia", g.getContrasenia());
			detailGestor.put("nombre", g.getNombre());
			detailGestor.put("dni", g.getDni());
			
			JSONObject gestorJson = new JSONObject();
			gestorJson.put(TipoPOJO.GESTOR.label, detailGestor);
			//Comprobamos si el gestor ya existe
			int indexGestor = getIndexIfExist(g.getDni(),TipoPOJO.GESTOR,
					TipoCampoABuscar.DNI,listaGestor);
			if(indexGestor >= 0) {
				listaGestor.remove(indexGestor);
			}
			
			listaGestor.add(gestorJson);
			
		try (FileWriter file = new FileWriter(rutaJsonGestor)) {
			 
            file.write(listaGestor.toJSONString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	/**
	 * Añade una nueva pulsera o la sobreescribe si ya existe
	 * @param p
	 */
	@SuppressWarnings("unchecked")
	public void escribirDatosPulsera(Pulsera p) {
		JSONArray listaDatosPulseras = getJSONArrayByPath(rutaJsonDatosPulseras);

		JSONArray jsonArraySensores = new JSONArray();

		for (Sensor s : p.getAllSensores()) {
			JSONObject jsonObjSensor= new JSONObject();
			JSONArray jsonArrayDatos = new JSONArray();

			for (Unidad u : s.getDatos()) {
				JSONObject jsonObjDato = new JSONObject();
				jsonObjDato.put("fecha", u.getStringFecha());
				jsonObjDato.put("hora", u.getStringHora());
				jsonObjDato.put("unidad", u.getValor());
				
				jsonArrayDatos.add(jsonObjDato);
			}
			
			jsonObjSensor.put("nombre", s.getNombre().toString());
			jsonObjSensor.put("limite_anormal", s.getLimite_anormal());
			jsonObjSensor.put("limite_anormal_superior", s.getLimite_anormal_superior());
			jsonObjSensor.put("datos", jsonArrayDatos);
			
			jsonArraySensores.add(jsonObjSensor);
		}
		
		JSONObject detailDatosPulsera = new JSONObject();
		detailDatosPulsera.put("idPulsera", p.getId());
		detailDatosPulsera.put("dniPadre", p.getDniPadre());
		detailDatosPulsera.put("nombreBebe", p.getNombreBebe());
		detailDatosPulsera.put("listaSensores", jsonArraySensores);

		JSONObject datosPulsera = new JSONObject();
		datosPulsera.put(TipoPOJO.PULSERA.label, detailDatosPulsera);

		//Comprobamos si la pulsera ya existe 
		int index = getIndexIfExist(p.getId(), TipoPOJO.PULSERA, TipoCampoABuscar.IDPULSERA, listaDatosPulseras);
		if (index>=0)listaDatosPulseras.remove(index);

		listaDatosPulseras.add(datosPulsera);

		try (FileWriter file = new FileWriter(rutaJsonDatosPulseras)) {

			file.write(listaDatosPulseras.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void escribirPulseraSinAsignar(String pulsera) {
		JSONArray listaIDPulseras = getJSONArrayByPath(rutaJsonPulserasSinAsignar);

		JSONObject datosPulsera = new JSONObject();
		datosPulsera.put("idPulsera", pulsera);
		
		//Comprobamos si el codigo ya existe
		int index = getIndexIDPulseraIfExist(pulsera, TipoCampoABuscar.IDPULSERA, listaIDPulseras);
		if (index>=0)listaIDPulseras.remove(index);

		listaIDPulseras.add(datosPulsera);

		try (FileWriter file = new FileWriter(rutaJsonPulserasSinAsignar)) {

			file.write(listaIDPulseras.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//METODOS PARA ELIMINAR
	/**
	 * Elimina una pulsera y sus datos del json de datos de pulseras
	 * @param pulsera
	 */
	public void desactivarDatosPulsera(String pulsera) {
		JSONArray listaIDPulseras = getJSONArrayByPath(rutaJsonDatosPulseras);		
		
		//recogemos el indice de la pulsera a eliminar
		int index = getIndexIfExist(pulsera, TipoPOJO.PULSERA, TipoCampoABuscar.IDPULSERA, listaIDPulseras);
		if (index>=0)listaIDPulseras.remove(index);

		try (FileWriter file = new FileWriter(rutaJsonDatosPulseras)) {

			file.write(listaIDPulseras.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void desactivarPulseraSinAsignar(String pulsera) {
		JSONArray listaIDPulseras = getJSONArrayByPath(rutaJsonPulserasSinAsignar);		
		
		//recogemos el indice de la pulsera a eliminar
		int index = getIndexIDPulseraIfExist(pulsera, TipoCampoABuscar.IDPULSERA, listaIDPulseras);
		if (index>=0)listaIDPulseras.remove(index);

		try (FileWriter file = new FileWriter(rutaJsonPulserasSinAsignar)) {

			file.write(listaIDPulseras.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
//HERRAMIENTAS PARA GESTIONAR ARRAYS
	//Devuelve el array principal extraido del JSON, del que sacaremos el resto de datos
	protected JSONArray getJSONArrayByPath(String ruta) {
		JSONArray arrayJson = new JSONArray();
		try 
        {
		 	FileReader reader = new FileReader(ruta);
            //Read JSON file
		 	JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(reader);
 
            arrayJson = (JSONArray) obj;

        } catch (FileNotFoundException e) {
            System.err.println("Fichero no encontrado, se crea uno en su lugar");
           
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return arrayJson;
	}
	//Devuelve la posicion de un JSONarray en la que se encuentra el tipo de POJO segun dni
	protected int getIndexIfExist(String dni, TipoPOJO tipoPOJO, TipoCampoABuscar campoABuscar, JSONArray array) {
		String dniBuscado = dni;
		int arrayIndex= 0;
		int realIndex = -1;
		if(!array.isEmpty()) {
			for(Object p : array){
				JSONObject pObject= (JSONObject)p;
				JSONObject p1 = (JSONObject) pObject.get(tipoPOJO.label);
				String dniTemporal = (String) p1.get(campoABuscar.label);
				if(dniTemporal.equals(dniBuscado)) {
					realIndex = arrayIndex;
				}
				arrayIndex++;
			}
		}
		
		return realIndex;
	}
	protected int getIndexPulseraPadreBebeIfExist(String dniPadre, String nombreBB, TipoPOJO tipoPOJO, JSONArray array) {
		int arrayIndex= 0;
		int realIndex = -1;
		if(!array.isEmpty()) {
			for(Object p : array){
				JSONObject pObject= (JSONObject)p;
				JSONObject p1 = (JSONObject) pObject.get(tipoPOJO.label);
				String dniTemporal = (String) p1.get("dniPadre");
				String nombreTemporal = (String) p1.get("nombreBebe");
				if(dniTemporal.equals(dniPadre)&&nombreTemporal.equals(nombreBB)) {
					realIndex = arrayIndex;
				}
				arrayIndex++;
			}
		}
		
		return realIndex;
	}
	/**
	 * Devuelve la posicion en el array de Ids de pulseras del id pasado por parametro
	 * @param id
	 * @param campoABuscar
	 * @param array
	 * @return
	 */
	protected int getIndexIDPulseraIfExist(String id, TipoCampoABuscar campoABuscar, JSONArray array) {
		int arrayIndex= 0;
		int realIndex = -1;
		if(!array.isEmpty()) {
			for(Object p : array){
				JSONObject pObject= (JSONObject)p;
				String idTemporal = (String) pObject.get(campoABuscar.label);
				if(idTemporal.equals(id)) {
					realIndex = arrayIndex;
				}
				arrayIndex++;
			}
		}
		
		return realIndex;
	} 
	
//METODOS DE PARSEO DE JSON A OBJETO:
	//Devuelve una notificación cuando se le pasa una estructura JSON adecuada
	private Notificacion parseNotificacionObject(JSONObject employee) 
    {

        JSONObject employeeObject = (JSONObject) employee.get(TipoPOJO.NOTIFICACION.label);
         
        String remitente = (String) employeeObject.get("remitente");    
        String destino = (String) employeeObject.get("destino"); 
        String dniRemitente = (String) employeeObject.get("dniRemitente");
        String dniDestino = (String) employeeObject.get("dniDestino");
        LocalDate fecha = LocalDate.parse((String) employeeObject.get("fecha"));    
        String asunto = (String) employeeObject.get("asunto");    
        String mensaje = (String) employeeObject.get("mensaje");
        
        ArrayList<String> registroMensajes = new ArrayList<>();
        JSONArray registroMensajesJSON = (JSONArray) employeeObject.get("registroMensajes");
        for(Object obj : registroMensajesJSON) {
        	registroMensajes.add((String)obj);
        }
        
        boolean isEmergencia = (boolean) employeeObject.get("isEmergencia");    

        
        Notificacion notificacion = 
        		new Notificacion(remitente, destino, fecha, asunto, mensaje);
        notificacion.setIsEmergencia(isEmergencia);
        notificacion.setDniDestino(dniDestino);
        notificacion.setDniRemitente(dniRemitente);
        notificacion.setRegistroMensajes(registroMensajes);
        
        return notificacion;
    }
	//Devuelve un Padre cuando se le pasa una estructura JSON adecuada
	private Padre parsePadreObject(JSONObject padreJson) 
    {
		
        JSONObject padre = (JSONObject) padreJson.get(TipoPOJO.PADRE.label);
         
        String mail = (String) padre.get("mail");    
        //Trae la contraseña ya descifrada
        String contrasenia = (String) padre.get("contrasenia");            
        String nombre = (String) padre.get("nombre");            
        String dni = (String) padre.get("dni");    
        String medicoAsociadoFamiliaDni= (String) padre.get("medicoAsociadoFamiliaDni");
        String rutaFoto = (String) padre.get("rutaFoto");        
        
        JSONArray listaBebesJson = (JSONArray) padre.get("listaBebes");
        ArrayList<Bebe>  bebesArray= new ArrayList<Bebe>();
        
        
        for(Object beb : listaBebesJson) {
        	JSONObject bebeJson = (JSONObject) beb;
        	Bebe bebe = parseBebeObject(bebeJson, dni);
        	bebesArray.add(bebe);
        }
        Padre p = new Padre(dni,contrasenia,nombre,mail,bebesArray,medicoAsociadoFamiliaDni,rutaFoto);
        
        return p;
    }
	//Devuelve un Bebe cuando se le pasa una estructura JSON adecuada
	private Bebe parseBebeObject(JSONObject bebe, String dni) 
    {
           
        LocalDate fechaNacimiento = LocalDate.parse((String) bebe.get("fechaNacimiento"));        
        String semanasGestacion = (String) bebe.get("semanasGestacion");        
        String observaciones = (String) bebe.get("observaciones");     
        String nombre = (String) bebe.get("nombre");    
        
        ArrayList<String> listaEnfermerosDni = new ArrayList<String>();
        JSONArray arrayListaEnfermerosDni = (JSONArray) bebe.get("listaEnfermerosDni");
        arrayListaEnfermerosDni.forEach(dniE -> listaEnfermerosDni.add((String)dniE));
        Pulsera pulsera = getPulseraById((String) bebe.get("idPulsera"));
        Bebe b = new Bebe(nombre,fechaNacimiento, semanasGestacion, observaciones, pulsera, dni, listaEnfermerosDni);
        
        return b;
    }
	/**
	 * Devuelve una Pulsera cuando se le pasa una estructura JSON adecuada
	 * @param pulseraJson
	 * @return Pulsera
	 */
	private Pulsera parseDatosPulseraObject(JSONObject pulseraJson) {
		Pulsera p = new Pulsera();
		
		JSONObject pulsera = (JSONObject) pulseraJson.get("pulsera");
		
		p.setDniPadre((String)pulsera.get("dniPadre"));
		p.setNombreBebe((String)pulsera.get("nombreBebe"));
		p.setId((String)pulsera.get("idPulsera"));
		JSONArray listaSensoresJson = (JSONArray) pulsera.get("listaSensores");

		for(Object s : listaSensoresJson) {
			JSONObject sensorJson = (JSONObject) s;

			double limite_anormal_inferior = (double) sensorJson.get("limite_anormal");
			double limite_anormal_superior = (double) sensorJson.get("limite_anormal_superior");

			Sensor sensor = new Sensor();

			JSONArray listaUnidadesJson = (JSONArray) sensorJson.get("datos");
			ArrayList<Unidad> unidadArray= new ArrayList<Unidad>();	

			for(Object u :listaUnidadesJson) {
				Unidad unidadAux = new Unidad();
				JSONObject unidad = (JSONObject) u;
				unidadAux.setValor((double)unidad.get("unidad"));
				unidadAux.setStringFecha((String)unidad.get("fecha"));
				unidadAux.setStringHora((String)unidad.get("hora"));

				unidadArray.add(unidadAux);
			}

			for(TipoSensor tipo : TipoSensor.values()) {
				String tipoString = (String) sensorJson.get("nombre");
				if(tipoString.equals(""+tipo)) {
					sensor = new Sensor(tipo,limite_anormal_inferior,limite_anormal_superior,unidadArray);
				}
			}

			switch(sensor.getNombre()) {
			case TEMPEXTERNA:
				p.setTemperaturaExt(sensor);
				break;
			case TEMPINTERNA:
				p.setTemperaturaInt(sensor);
				break;
			case OXIMETRO:
				p.setOximetro(sensor);
				break;
			case PULSOMETRO:
				p.setPulsometro(sensor);
				break;
			}
		}
		return p;
	}
	
	public void delay() {
		try {
			Thread.sleep(1000);
		}catch (InterruptedException ex) {
		}
	}
	
	//Devuelve un sanitario cuando se le pasa una estructura JSON adecuada
	@SuppressWarnings("unchecked")
	private Sanitario parseSanitarioObject(JSONObject san) 
    {

        JSONObject sanitarioObject = (JSONObject) san.get(TipoPOJO.SANITARIO.label);   
        String mail = (String) sanitarioObject.get("mail");        
        String contrasenia = (String) sanitarioObject.get("contrasenia");    
        String nombre = (String) sanitarioObject.get("nombre");    
        String dni = (String) sanitarioObject.get("dni");
        String rutaFoto = (String) sanitarioObject.get("rutaFoto");
        boolean enfermeroYaAsociadoAMedico = (boolean) sanitarioObject.get("enfermeroYaAsociadoAMedico");
        
        TipoSanitario tipoSanitario = null;      
        for(TipoSanitario tipo : TipoSanitario.values()) {
    		   String tipoString = (String) sanitarioObject.get("tipoSanitario");
    		   if(tipoString.equals(""+tipo)) {
    			   tipoSanitario = tipo;
    		   }
    	}
        
        ArrayList<String> dniPadresAsociados = new ArrayList<String>();
        JSONArray arrayDniPadresAsociados = (JSONArray) sanitarioObject.get("dniPadresAsociados");
        arrayDniPadresAsociados.forEach(dniP -> dniPadresAsociados.add((String)dniP));
        
        ArrayList<String> listaEnfermerosAsociadosDni = new ArrayList<String>();
        JSONArray enfermerosAsociadosDni = (JSONArray) sanitarioObject.get("listaEnfermerosAsociadosDni");
        enfermerosAsociadosDni.forEach(dniP -> listaEnfermerosAsociadosDni.add((String)dniP));
        
        ArrayList<String> medicoAsociadoAEnfermeroDni = new ArrayList<String>();
        JSONArray arrayMedicoAsociadoAEnfermeroDni = (JSONArray) sanitarioObject.get("medicoAsociadoAEnfermeroDni");
        arrayMedicoAsociadoAEnfermeroDni.forEach(dniP -> medicoAsociadoAEnfermeroDni.add((String)dniP));
        
        Sanitario sanitario = new Sanitario(dni, contrasenia, nombre, mail, tipoSanitario, rutaFoto, 
        		arrayDniPadresAsociados, listaEnfermerosAsociadosDni, arrayMedicoAsociadoAEnfermeroDni, enfermeroYaAsociadoAMedico);
        
        
        return sanitario;
    }
	//Devuelve un gestor cuando se le pasa una estructura JSON adecuada
	private Gestor parseGestorObject(JSONObject ges){

        JSONObject gestorObject = (JSONObject) ges.get(TipoPOJO.GESTOR.label);
         
        String mail = (String) gestorObject.get("mail");      	        
        String contrasenia = (String) gestorObject.get("contrasenia");           
        String nombre = (String) gestorObject.get("nombre");    
        String dni = (String) gestorObject.get("dni");    

        Gestor gestor = new Gestor(dni, contrasenia, nombre, mail);
     
        return gestor;
    }
	@Override
	public boolean comprobarDniContrasenia(String tabla, String dni, String contrasenia) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void desactivarBebeById(int id) {
		// TODO Auto-generated method stub
		
	}
	
}
