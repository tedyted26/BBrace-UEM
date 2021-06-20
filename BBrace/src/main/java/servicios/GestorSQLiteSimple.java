package servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

/**
 * 
 * @author deivo
 *
 */
public class GestorSQLiteSimple implements IPersistenciaSimple {

	private GestorSQLiteGenerico sq;

	public GestorSQLiteSimple(){
		sq = new GestorSQLiteGenerico();

	}





	@Override
	public ArrayList<Notificacion> leerNotificaciones() {
		//TODO FUTURO MAYBE
		return null;
	}

	public int leerIdUnionMaximo() {
		int idUnionObtenido = sq.selectMaxIdUnionFromNotificaciones();
		return idUnionObtenido;

	}
	public int leerIdOrdenMaximoByIdUnion(int idUnion) {
		return sq.selectMaxIdOrdenFromNotifiaciones(idUnion);
	}

	public ArrayList<Notificacion> leerNotificacionesByIdUnion(int idUnion) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("idUnion"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(idUnion));

		ArrayList<Map<String,Object>> arrayMapaNotificaciones = sq.selectFromTable("notificaciones", columnas, valores);

		ArrayList<Notificacion> arrayNotificaciones = new ArrayList<>();

		for(Map<String,Object> mapaNotificaciones : arrayMapaNotificaciones) {
			Notificacion notificacion = new Notificacion();

			notificacion.setRemitente((String) mapaNotificaciones.get("remitente"));
			notificacion.setDestino((String) mapaNotificaciones.get("destino") );
			notificacion.setDniRemitente((String) mapaNotificaciones.get("dniRemitente"));
			notificacion.setDniDestino((String) mapaNotificaciones.get("dniDestino"));
			notificacion.setFecha((LocalDate)mapaNotificaciones.get("fecha"));
			notificacion.setAsunto((String) mapaNotificaciones.get("asunto"));
			notificacion.setIdUnion((int) mapaNotificaciones.get("idUnion"));
			notificacion.setIdOrden((int) mapaNotificaciones.get("idOrden"));
			notificacion.setMensaje((String) mapaNotificaciones.get("mensaje"));
			notificacion.setTipoDeMensaje((int) mapaNotificaciones.get("tipoDeMensaje"));


			arrayNotificaciones.add(notificacion);

		}


		return arrayNotificaciones;
	}


	@Override
	public ArrayList<Notificacion> leerNotificacionesByDniDestino(String dniDestino) {

		//TODO TOQUETEAR FIJAR LINEA 50	
		//TODO RECOJA EL MISMO ID UNION LOS UNA EN UN STRING SEPARADO POR EL ASUNTO

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dniDestino"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dniDestino));
		ArrayList<String> columnas2 = new ArrayList<String>(Arrays.asList("dniRemitente"));

		ArrayList<Map<String,Object>> arrayMapaNotificacionesDniDestino = sq.selectFromTable("notificaciones", columnas, valores);
		ArrayList<Map<String,Object>> arrayMapaNotificacionesDniRemitente = sq.selectFromTable("notificaciones", columnas2, valores);//HASTA AQUI LO SACA BIEN
		//TODO 

		ArrayList<Notificacion> arrayNotificaciones = new ArrayList<>();
		ArrayList<Map<String,Object>> arrayFinal = new ArrayList<>();
		arrayFinal.addAll(arrayMapaNotificacionesDniDestino);
		arrayFinal.addAll(arrayMapaNotificacionesDniRemitente);
		for(Map<String,Object> mapaNotificaciones : arrayFinal) {
			Notificacion notificacion = new Notificacion();
			String fechaAdaptadaALocalDate = (mapaNotificaciones.get("fecha")).toString().replace(" ", "T");

			notificacion.setRemitente((String) mapaNotificaciones.get("remitente"));
			notificacion.setDestino((String) mapaNotificaciones.get("destino") );
			notificacion.setDniRemitente((String) mapaNotificaciones.get("dniRemitente"));
			notificacion.setDniDestino((String) mapaNotificaciones.get("dniDestino"));
			notificacion.setFecha(LocalDate.parse(fechaAdaptadaALocalDate));
			notificacion.setAsunto((String) mapaNotificaciones.get("asunto"));
			notificacion.setIdUnion((int) mapaNotificaciones.get("idUnion"));
			notificacion.setIdOrden((int) mapaNotificaciones.get("idOrden"));
			notificacion.setMensaje((String) mapaNotificaciones.get("mensaje"));
			notificacion.setTipoDeMensaje((int) mapaNotificaciones.get("tipoDeMensaje"));


			arrayNotificaciones.add(notificacion);

		}
		arrayNotificaciones.sort(Comparator.comparing(Notificacion::getIdUnion));
		ArrayList<Notificacion> arrayParaEnviar = new ArrayList<Notificacion>();//HATA AQUI BIEN

		if(arrayNotificaciones.size() != 0) {

			int ini = 0;
			int idUnionLista = arrayNotificaciones.get(0).getIdUnion();

			int arrayNotSize = arrayNotificaciones.size();
			for (int i = 0; i <= arrayNotSize;i++) {
				boolean maestro = false;
				if(i == arrayNotSize) {
					List<Notificacion> sublista = arrayNotificaciones.subList(ini, i);
					sublista.sort(Comparator.comparing(Notificacion::getIdOrden));//HASTA AQUI BIEN
					Notificacion notifiacionMaestra = new Notificacion();
					for(int j = sublista.size()-1;j >= 0; j--) {

						if(maestro) {
							Notificacion notificacionTemporal = sublista.get(j);

							notifiacionMaestra.getRegistroMensajes().add("["+notificacionTemporal.getFecha()+"] "+
									notificacionTemporal.getRemitente()+":\n "+
									notificacionTemporal.getMensaje());
						}
						if(sublista.get(j).getDniDestino().equals(dniDestino) && !maestro) {
							maestro = true;
							notifiacionMaestra = sublista.get(j);

						}	

					}
					if(maestro) {
						Collections.reverse(notifiacionMaestra.getRegistroMensajes());
						arrayParaEnviar.add(notifiacionMaestra);
					}


					ini = i;

					if(i < arrayNotSize)
						idUnionLista = arrayNotificaciones.get(i).getIdUnion();
				}else {
					if(arrayNotificaciones.get(i).getIdUnion()!=idUnionLista || i == arrayNotSize-1) {
						//					if(i == arrayNotSize-1 && ) i++;
						List<Notificacion> sublista = arrayNotificaciones.subList(ini, i);
						sublista.sort(Comparator.comparing(Notificacion::getIdOrden));//HASTA AQUI BIEN
						Notificacion notifiacionMaestra = new Notificacion();
						for(int j = sublista.size()-1;j >= 0; j--) {

							if(maestro) {
								Notificacion notificacionTemporal = sublista.get(j);

								notifiacionMaestra.getRegistroMensajes().add("["+notificacionTemporal.getFecha()+"] "+
										notificacionTemporal.getRemitente()+":\n "+
										notificacionTemporal.getMensaje());
							}
							if(sublista.get(j).getDniDestino().equals(dniDestino) && !maestro) {
								maestro = true;
								notifiacionMaestra = sublista.get(j);

							}	

						}
						if(maestro) {
							Collections.reverse(notifiacionMaestra.getRegistroMensajes());
							arrayParaEnviar.add(notifiacionMaestra);
						}


						ini = i;

						if(i < arrayNotSize)
							idUnionLista = arrayNotificaciones.get(i).getIdUnion();

					}
				}


			}
		}

		return arrayParaEnviar;//TODO AQUI PIERDE LOS DATOS

	}

	@Override
	/**
	 * Devuelve todos los padres de la BBDD con sus respectivos hijos
	 * @return 
	 */
	public ArrayList<Padre> leerPadres() {
		ArrayList<String> columnas = new ArrayList<String>();
		ArrayList<Object> valores = new ArrayList<Object>();
		ArrayList<Map<String,Object>> arrayMapasPadre = sq.selectFromTable("padre", columnas, valores);

		ArrayList<Padre> arrayPadres = new ArrayList<>();
		for(Map<String,Object> mapaPadre : arrayMapasPadre) {
			Padre padre = new Padre();

			String dniPadre = (String)mapaPadre.get("dni");
			padre.setDni(dniPadre);
			padre.setNombre((String)mapaPadre.get("nombre"));
			padre.setContrasenia((String)mapaPadre.get("contrasenia"));
			padre.setMail((String)mapaPadre.get("mail"));
			padre.setRutaFoto((String)mapaPadre.get("rutafoto"));			
			padre.setMedicoAsociadoFamiliaDni((String)mapaPadre.get("dniMedicoAsociado"));

			ArrayList<String> columnasBB = new ArrayList<String>(Arrays.asList("dniPadre"));
			ArrayList<Object> valoresBB = new ArrayList<Object>(Arrays.asList(dniPadre));
			padre.setListaBebes(leerBebesByFields(columnasBB, valoresBB));

			arrayPadres.add(padre);
		}

		return arrayPadres;
	}
	//FIXME
	//para no cambiar la firma del metodo
	public ArrayList<Padre> leerPadres(boolean cargarDatosBebes) {
		ArrayList<String> columnas = new ArrayList<String>();
		ArrayList<Object> valores = new ArrayList<Object>();

		return leerPadreByFields(columnas, valores, cargarDatosBebes);
	}

	@Override
	public ArrayList<Sanitario> leerSanitarios() {
		ArrayList<Sanitario> arraySanitarios = new ArrayList<Sanitario>();

		ArrayList<String> columnas = new ArrayList<String>();
		ArrayList<Object> valores = new ArrayList<Object>();

		//consulta de medicos
		arraySanitarios.addAll(leerSanitarioByFields(TipoSanitario.MEDICO, columnas, valores));

		//consulta de enfermeros
		arraySanitarios.addAll(leerSanitarioByFields(TipoSanitario.ENFERMERO, columnas, valores));

		return arraySanitarios;
	}

	private ArrayList<String> leerListaPadresAsociadosAMedicoPorDniMedico(String dniMedico) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dniMedicoAsociado"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dniMedico));

		ArrayList<Padre> listaPadresAsociados = leerPadreByFields(columnas, valores, false);
		ArrayList<String> listaPadresAsociadosDni = new ArrayList<String>();

		for (Padre p : listaPadresAsociados) {
			listaPadresAsociadosDni.add(p.getDni());
		}
		return listaPadresAsociadosDni;
	}

	private ArrayList<String> leerListaEnfermerosPorDniMedico(String dniMedico) {
		ArrayList<String> listaEnfermerosDni = new ArrayList<String>();

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dniMedicoAsociado"));//TODO FIJARSE AQUI DANI
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dniMedico));

		ArrayList<Map<String, Object>> listaEnfermeros = sq.selectFromTable("enfermero", columnas, valores);

		for (Map<String, Object> mapaEnfermero : listaEnfermeros) {
			String dni = (String)mapaEnfermero.get("dni");
			listaEnfermerosDni.add(dni);
		}
		return listaEnfermerosDni;
	}

	//FIXME este devuelve todos los sanitarios de un tipo
	@Override
	public ArrayList<Sanitario> leerSanitarios(TipoSanitario tipoSanitario) {
		ArrayList<String> columnas = new ArrayList<String>();
		ArrayList<Object> valores = new ArrayList<Object>();
		return leerSanitarioByFields(tipoSanitario, columnas, valores);
	}

	//FIXME no le queria cambiar la firma en la interfaz, asi que he hecho otro con lo que me hacia falta
	private ArrayList<Sanitario> leerSanitarioByFields(TipoSanitario tipoSanitario, ArrayList<String> columnas, ArrayList<Object> valores) {
		ArrayList<Sanitario> sanitarios = new ArrayList<Sanitario>();

		ArrayList<Map<String,Object>> arrayMapas;

		if (tipoSanitario.equals(TipoSanitario.MEDICO)) arrayMapas = sq.selectFromTable("medico", columnas, valores);

		else arrayMapas = sq.selectFromTable("enfermero", columnas, valores);

		for(Map<String,Object> mapa : arrayMapas) {
			Sanitario sanitario = new Sanitario();

			String dni = (String)mapa.get("dni");
			sanitario.setDni(dni);
			sanitario.setNombre((String)mapa.get("nombre"));
			sanitario.setContrasenia((String)mapa.get("contrasenia"));
			sanitario.setMail((String)mapa.get("mail"));
			sanitario.setRutaFoto((String)mapa.get("rutafoto"));

			if (tipoSanitario.equals(TipoSanitario.ENFERMERO)) {	
				sanitario.setTipoSanitario(TipoSanitario.ENFERMERO);
				sanitario.setEnfermeroYaAsociadoAMedico(true);

				sanitario.setListaEnfermerosAsociados(null);
				sanitario.setDniPadresAsociados(null);

			}
			else {
				sanitario.setTipoSanitario(TipoSanitario.MEDICO);
				sanitario.setEnfermeroYaAsociadoAMedico(false);

				sanitario.setMedicoAsociadoAEnfermero(null);
				sanitario.setListaEnfermerosAsociados(leerListaEnfermerosPorDniMedico(dni));
				sanitario.setDniPadresAsociados(leerListaPadresAsociadosAMedicoPorDniMedico(dni));

				String dniMedico = (String)mapa.get("dniMedicoAsociado");
				sanitario.setMedicoAsociadoAEnfermero(new ArrayList<String>(Arrays.asList(dniMedico)));		

			}

			sanitarios.add(sanitario);
		}
		return sanitarios;
	}

	//DE MOMENTO NO
	@Override
	public ArrayList<Gestor> leerGestores() {
		ArrayList<String> columnas = new ArrayList<String>();
		ArrayList<Object> valores = new ArrayList<Object>();

		return getGestorByFields(columnas, valores);	
	}
	//SI HAY QUE HACERLO
	@Override
	public ArrayList<Bebe> getBebesAsociadosSanitario(String dniSanitario) {
		ArrayList<Bebe> listaBebes = new ArrayList<Bebe>();

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni_enfermero"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dniSanitario));
		ArrayList<Map<String,Object>> arrayTcBebes = sq.selectFromTable("tcEnfermeroBebe", columnas, valores);

		for(Map<String,Object> mapaTcBebe : arrayTcBebes) {
			int idBebe = (int)mapaTcBebe.get("id_bebe");

			ArrayList<String> columnasBB = new ArrayList<String>(Arrays.asList("id"));
			ArrayList<Object> valoresBB = new ArrayList<Object>(Arrays.asList(idBebe));
			ArrayList<Map<String,Object>> arrayBebes = sq.selectFromTable("bebe", columnasBB, valoresBB);

			Map<String,Object> mapabebe = arrayBebes.get(0);

			Bebe bebe = new Bebe();

			String dniPadre = (String)mapabebe.get("dniPadre");
			bebe.setDniPadre(dniPadre);
			String nombreBebe = (String)mapabebe.get("nombre"); 
			bebe.setNombre(nombreBebe);

			bebe.setFechaNacimiento(LocalDate.parse((String)mapabebe.get("fnacimiento"))); //FIXME comprobar que va bien
			bebe.setObservaciones((String)mapabebe.get("observaciones"));
			bebe.setSemanasGestacion((String)mapabebe.get("sgestacion")); //FIXME semanas en string?? aparece como int en la bbdd y string en los pojos			

			String idPulsera = (String)mapabebe.get("idPulsera");
			bebe.setPulsera(leerPulseraById(idPulsera, dniPadre, nombreBebe));

			bebe.setListaEnfermerosDni(leerListaEnfermerosPorIdBebe(idBebe));

			listaBebes.add(bebe);
		}

		return listaBebes;
	}
	//FIXME
	//metodo para no cambiar la firma del original
	public ArrayList<Bebe> getBebesAsociadosSanitario(String dniSanitario, boolean cargarDatosBebe) {
		ArrayList<Bebe> listaBebes = new ArrayList<Bebe>();

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni_enfermero"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dniSanitario));
		ArrayList<Map<String,Object>> arrayTcBebes = sq.selectFromTable("tcEnfermeroBebe", columnas, valores);

		for(Map<String,Object> mapaTcBebe : arrayTcBebes) {
			int idBebe = (int)mapaTcBebe.get("id_bebe");

			ArrayList<String> columnasBB = new ArrayList<String>(Arrays.asList("id"));
			ArrayList<Object> valoresBB = new ArrayList<Object>(Arrays.asList(idBebe));
			ArrayList<Map<String,Object>> arrayBebes = sq.selectFromTable("bebe", columnasBB, valoresBB);

			Map<String,Object> mapabebe = arrayBebes.get(0);

			Bebe bebe = new Bebe();

			String dniPadre = (String)mapabebe.get("dniPadre");
			bebe.setDniPadre(dniPadre);
			String nombreBebe = (String)mapabebe.get("nombre"); 
			bebe.setNombre(nombreBebe);

			bebe.setFechaNacimiento(LocalDate.parse((String)mapabebe.get("fnacimiento"))); //FIXME comprobar que va bien
			bebe.setObservaciones((String)mapabebe.get("observaciones"));
			bebe.setSemanasGestacion((String)mapabebe.get("sgestacion")); //FIXME semanas en string?? aparece como int en la bbdd y string en los pojos			

			if (cargarDatosBebe) {
				String idPulsera = (String)mapabebe.get("idPulsera");
				bebe.setPulsera(leerPulseraById(idPulsera, dniPadre, nombreBebe));
			}

			bebe.setListaEnfermerosDni(leerListaEnfermerosPorIdBebe(idBebe));

			listaBebes.add(bebe);
		}

		return listaBebes;
	}

	@Override
	public Padre getPadreByDni(String dni) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dni));

		return leerPadreByFields( columnas, valores, true).get(0);
	}
	//FIXME
	//metodo duplicado para no cambiar la firma
	public Padre getPadreByDni(String dni, boolean leerBebes) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dni));

		return leerPadreByFields( columnas, valores, leerBebes).get(0);
	}

	@Override
	public Padre getPadreByMail(String mail) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("mail"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(mail));

		return leerPadreByFields( columnas, valores, true).get(0);
	}

	//SI HAY QUE HACERLO
	@Override 
	public Sanitario getSanitarioByDni(String dni) {
		ArrayList<Sanitario> listaSanitarios = new ArrayList<Sanitario>();

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dni));

		listaSanitarios.addAll(leerSanitarioByFields(TipoSanitario.MEDICO, columnas, valores)); //FIXME mirar a ver si hay algun error con arrays vacios
		listaSanitarios.addAll(leerSanitarioByFields(TipoSanitario.ENFERMERO, columnas, valores));

		return listaSanitarios.get(0);
	}

	//SI HAY QUE HACERLO
	@Override
	public Sanitario getSanitarioByMail(String mail) {
		ArrayList<Sanitario> listaSanitarios = new ArrayList<Sanitario>();

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("mail"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(mail));

		listaSanitarios.addAll(leerSanitarioByFields(TipoSanitario.MEDICO, columnas, valores)); //FIXME mirar a ver si hay algun error con arrays vacios
		listaSanitarios.addAll(leerSanitarioByFields(TipoSanitario.ENFERMERO, columnas, valores));

		return listaSanitarios.get(0);
	}
	//SI HAY QUE HACERLO
	private ArrayList<Gestor> getGestorByFields(ArrayList<String> columnas, ArrayList<Object> valores) {
		ArrayList<Gestor> listaGestores = new ArrayList<Gestor>();
		ArrayList<Map<String,Object>> arrayMapasGestor = sq.selectFromTable("gestor", columnas, valores);

		for(Map<String,Object> mapaGestor : arrayMapasGestor) {
			Gestor gestor = new Gestor();

			gestor.setDni((String)mapaGestor.get("dni"));
			gestor.setNombre((String)mapaGestor.get("nombre"));
			gestor.setContrasenia((String)mapaGestor.get("contrasenia"));
			gestor.setMail((String)mapaGestor.get("mail"));

			listaGestores.add(gestor);
		}

		return listaGestores;

	}
	@Override
	public Gestor getGestorByDni(String dni) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dni));

		return getGestorByFields(columnas, valores).get(0);	
	}
	//SI HAY QUE HACERLO
	@Override
	public Gestor getGestorByMail(String mail) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("mail"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(mail));

		return getGestorByFields(columnas, valores).get(0);
	}

	private boolean isExistente (String tabla, ArrayList<String> columnas, ArrayList<Object> valores) {
		ArrayList<Map<String, Object>> lista = sq.selectFromTable(tabla, columnas, valores);
		if (lista.isEmpty()) return false;
		else return true;
	}

	public boolean comprobarDniContrasenia(String tabla, String dni, String contrasenia) {
		return isExistente(tabla,
				new ArrayList<String>(Arrays.asList("dni","contrasenia")),
				new ArrayList<Object>(Arrays.asList(dni,contrasenia)));
	}

	@Override
	public void escribirNotificacion(Notificacion n) {//TODO escribir notificacion
		int isEmergenciaValue = 0;
		ArrayList<String> columnas = new ArrayList<String>();
		ArrayList<Object> valores = new ArrayList<Object>();
		columnas = new ArrayList<String>(Arrays.asList("remitente", "destino", "dniRemitente", "dniDestino", "fecha", "asunto", "idUnion", "idOrden", "mensaje", "tipoDeMensaje", "isEmergencia"));
		if(n.getIsEmergencia()==true) {
			isEmergenciaValue = 1;
		}else {
			isEmergenciaValue = 0; 
		}
		valores = new ArrayList<Object>(Arrays.asList(n.getRemitente(), n.getDestino(), n.getDniRemitente(), n.getDniDestino(), n.getFecha(), n.getAsunto(), n.getIdUnion(), n.getIdOrden(), n.getMensaje(), n.getTipoDeMensaje(), isEmergenciaValue));

		sq.insertValuesIntoTable("notificaciones",columnas, valores);




	}

	@Override
	public void escribirPadre(Padre p) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(p.getDni()));
		//si existe modificar
		if (isExistente ("padre", columnas, valores)) {
			columnas = new ArrayList<String>(Arrays.asList("nombre", "contrasenia", "mail", "rutafoto", "dniMedicoAsociado"));
			valores = new ArrayList<Object>(Arrays.asList(p.getNombre(), p.getContrasenia(), p.getMail(), p.getRutaFoto(), p.getMedicoAsociadoFamiliaDni()));

			sq.updateTableField("padre", p.getDni(), columnas, valores);			
		}
		//si no existe crear
		else {
			columnas = new ArrayList<String>(Arrays.asList("dni","nombre", "contrasenia", "mail", "rutafoto", "dniMedicoAsociado"));
			valores = new ArrayList<Object>(Arrays.asList(p.getDni(),p.getNombre(), p.getContrasenia(), p.getMail(), p.getRutaFoto(), p.getMedicoAsociadoFamiliaDni()));
			sq.insertValuesIntoTable("padre", columnas, valores);
		}
		//for que se encarga de los bebes
		ArrayList<Bebe> listaBebes = p.getListaBebes();
		for (Bebe b : listaBebes) {
			escribirBebe(b);
		}

	}
	/**
	 * Segun un bebe pasado por parametro, comprueba si ya se ha creado, si no, genera uno nuevo en la base,
	 * asignandole enfermeros.
	 * Si existe, updatea los campos.
	 * @param b
	 */
	private void escribirBebe(Bebe b) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("nombre", "dniPadre"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(b.getNombre(), b.getDniPadre()));

		//si existe modificar
		if (isExistente ("bebe", columnas, valores)) {

			ArrayList<Map<String, Object>> arrayBebes = sq.selectFromTable("bebe", columnas, valores);
			int id = (int)arrayBebes.get(0).get("id");

			columnas = new ArrayList<String>(Arrays.asList("nombre", "sgestacion", "observaciones", "fnacimiento", "dniPadre", "idPulsera"));
			valores = new ArrayList<Object>(Arrays.asList(b.getNombre(), b.getSemanasGestacion(), b.getObservaciones(), b.getFechaNacimiento().toString(), b.getDniPadre(), b.getPulsera().getId()));


			sq.updateTableField("bebe", id, columnas, valores);	
		}
		//si no existe crear
		else {
			columnas = new ArrayList<String>(Arrays.asList("nombre", "sgestacion", "observaciones", "fnacimiento", "dniPadre", "idPulsera"));
			valores = new ArrayList<Object>(Arrays.asList(b.getNombre(), b.getSemanasGestacion(), b.getObservaciones(), b.getFechaNacimiento().toString(), b.getDniPadre(), b.getPulsera().getId()));

			sq.insertValuesIntoTable("bebe", columnas, valores);

			//ASIGNACION DE ENFERMERO
			//Toma el id del bebe de la base
			ArrayList<Map<String, Object>> arrayBebes = sq.selectFromTable("bebe", columnas, valores);
			int id = (int)arrayBebes.get(0).get("id");

			//Asignacion de enfermeros
			ArrayList<String> columnasEnfBebe = new ArrayList<String>(Arrays.asList("dni_enfermero","id_bebe"));
			ArrayList<String> dnisEnfermeros = b.getListaEnfermerosDni();	
			@SuppressWarnings("unchecked")
			ArrayList<Object>[] valoresEnfBebes = (ArrayList<Object>[])new ArrayList[dnisEnfermeros.size()];

			for(int i=0; i < dnisEnfermeros.size();i++) {
				valoresEnfBebes[i] = new ArrayList<Object>(Arrays.asList(dnisEnfermeros.get(i), id));
			}
			sq.insertValuesIntoTable("tcenfermerobebe", columnasEnfBebe, valoresEnfBebes);		
		}
		//nos encargamos de la pulsera
		escribirDatosPulsera(b.getPulsera());		
	}

	@Override
	public void escribirSanitario(Sanitario s) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(s.getDni()));

		String tabla = "";

		if (s.getTipoSanitario().equals(TipoSanitario.MEDICO)) tabla = "medico"; 
		else tabla = "enfermero";

		//si existe modificar
		if (isExistente (tabla, columnas, valores)) {
			columnas = new ArrayList<String>(Arrays.asList("nombre", "contrasenia", "mail", "rutafoto"));
			valores = new ArrayList<Object>(Arrays.asList(s.getNombre(), s.getContrasenia(), s.getMail(), s.getRutaFoto()));

			if (tabla.equals("enfermero")) {
				columnas.add("dniMedicoAsociado");
				valores.add(s.getMedicoAsociadoAEnfermeroDni().get(0));
			}
			sq.updateTableField(tabla, s.getDni(), columnas, valores);			
		}
		//si no existe crear
		else {
			columnas = new ArrayList<String>(Arrays.asList("dni", "nombre", "contrasenia", "mail", "rutafoto"));
			valores = new ArrayList<Object>(Arrays.asList(s.getDni(),s.getNombre(), s.getContrasenia(), s.getMail(), s.getRutaFoto()));

			if (tabla.equals("enfermero")) {
				columnas.add("dniMedicoAsociado");
				valores.add(s.getMedicoAsociadoAEnfermeroDni().get(0));
			}
			sq.insertValuesIntoTable(tabla, columnas, valores);
		}			
	}

	@Override
	public void escribirGestor(Gestor g) {
		// TODO Auto-generated method stub

	}
	//FIXME desde la app no deberian poderse modificar datos de las pulseras
	//		Dejo el metodo, aunque solo deberia hacer la primera parte del if
	@Override
	public void escribirDatosPulsera(Pulsera pulsera) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("isActivada"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(true));

		//activar la pulsera si no esta activada

		sq.updateTableField("pulsera", pulsera.getId(), columnas, valores);	
	}

	@Override
	public void escribirPulseraSinAsignar(String pulsera) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("isActivada"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(false));
		sq.updateTableField("pulsera", pulsera, columnas, valores);	
	}
	@Override
	public void desactivarBebeById(int id) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("isActivado"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(false));
		sq.updateTableField("bebe", id, columnas, valores);

		sq.deleteValuesFromTable("tcenfermerobebe", id);
	}
	@Override
	public void desactivarDatosPulsera(String pulsera) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("isActivada"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(false));
		sq.updateTableField("pulsera", pulsera, columnas, valores);
	}

	@Override
	public void desactivarPulseraSinAsignar(String pulsera) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("isActivada"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(true));
		sq.updateTableField("pulsera", pulsera, columnas, valores);

	}

	@Override
	public boolean buscarMailExistePadre(String mail) {	
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("mail"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(mail));
		boolean arrayPadre = isExistente("padre", columnas, valores);

		return arrayPadre;
	}

	@Override
	public boolean buscarMailExisteSanitario(String mail) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("mail"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(mail));
		boolean arrayMedicos = isExistente("medico", columnas, valores);
		boolean arrayEnfermeros = isExistente("enfermero", columnas, valores);



		return arrayMedicos && arrayEnfermeros;
	}

	@Override
	public boolean comprobarSiPadreExiste(String dni) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("dni"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(dni));
		ArrayList<Map<String,Object>> arrayPadre = sq.selectFromTable("padre", columnas, valores);

		return !(arrayPadre.get(0)== null);
	}

	@Override
	public boolean comprobarSiPulseraSinAsociarExiste(String idPulsera) {
		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("id","isActivada"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(idPulsera, false));
		ArrayList<Map<String,Object>> arrayPulseras = sq.selectFromTable("pulsera", columnas, valores);

		return !(arrayPulseras.get(0) == null);
	}

	/**
	 * Devuelve tras transformar de mapa a Padre, y llama en cascada a los metodos para transformar los POJOS dependientes(Bebe, Pulsera, etc)
	 * Columnas y valores han de estar ordenados
	 * @param columnas (ArrayList<String>) nombres de las columnas para el filtro de SQL
	 * @param valores (ArrayList<String>) valores de las columnas para el filtro de SQL
	 * @return (Padre) el padre tras el parseo
	 */
	private ArrayList<Padre> leerPadreByFields(ArrayList<String> columnas, ArrayList<Object> valores, boolean leerBebes) {
		ArrayList<Padre> listaPadres = new ArrayList<Padre>();

		ArrayList<Map<String,Object>> arrayPadre = sq.selectFromTable("padre", columnas, valores);

		for (Map<String,Object> mapaPadre : arrayPadre) {
			Padre padre = new Padre();

			String dniPadre = (String)mapaPadre.get("dni");
			padre.setDni(dniPadre);
			padre.setNombre((String)mapaPadre.get("nombre"));
			padre.setContrasenia((String)mapaPadre.get("contrasenia"));
			padre.setMail((String)mapaPadre.get("mail"));
			padre.setRutaFoto((String)mapaPadre.get("rutafoto"));			
			padre.setMedicoAsociadoFamiliaDni((String)mapaPadre.get("dniMedicoAsociado"));

			if (leerBebes) {
				ArrayList<String> columnasBB = new ArrayList<String>(Arrays.asList("dniPadre","isActivado"));
				ArrayList<Object> valoresBB = new ArrayList<Object>(Arrays.asList(dniPadre, true));
				padre.setListaBebes(leerBebesByFields(columnasBB, valoresBB));						
			}
			listaPadres.add(padre);

		}

		return listaPadres;
	}

	/**
	 * Devuelve tras transformar de mapa a BEBE, y llama en cascada a los metodos para transformar los POJOS dependientes
	 * @param columnas (ArrayList<String>) nombres de las columnas para el filtro de SQL
	 * @param valores (ArrayList<String>) valores de las columnas para el filtro de SQL
	 * @return (ArrayList<Bebe>) el array con los bebes
	 */
	private ArrayList<Bebe> leerBebesByFields(ArrayList<String> columnas, ArrayList<Object> valores) {
		ArrayList<Bebe> listaBebes = new ArrayList<Bebe>();


		ArrayList<Map<String,Object>> arrayBebes = sq.selectFromTable("bebe", columnas, valores);

		for(Map<String,Object> mapaBebe : arrayBebes) {
			Bebe bebe = new Bebe();

			String dniPadre = (String)mapaBebe.get("dniPadre");
			bebe.setDniPadre(dniPadre);
			String nombreBebe = (String)mapaBebe.get("nombre"); 
			bebe.setNombre(nombreBebe);

			bebe.setFechaNacimiento(LocalDate.parse((String)mapaBebe.get("fnacimiento"))); //FIXME comprobar que va bien
			String observaciones = (String)mapaBebe.get("observaciones");
			if(observaciones == null) bebe.setObservaciones("");
			else bebe.setObservaciones(observaciones);
			bebe.setSemanasGestacion((String)mapaBebe.get("sgestacion")); //FIXME semanas en string?? aparece como int en la bbdd y string en los pojos			

			String idPulsera = (String)mapaBebe.get("idPulsera");
			bebe.setPulsera(leerPulseraById(idPulsera, dniPadre, nombreBebe));

			int idBebe = (int)mapaBebe.get("id");
			bebe.setId(idBebe);
			bebe.setListaEnfermerosDni(leerListaEnfermerosPorIdBebe(idBebe));

			listaBebes.add(bebe);
		}

		return listaBebes;
	}

	private ArrayList<String> leerListaEnfermerosPorIdBebe(int idBebe) {
		ArrayList<String> listaEnfermeros = new ArrayList<String>();

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("id_bebe"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(idBebe));
		ArrayList<Map<String,Object>> arrayEnfermeros = sq.selectFromTable("tcEnfermeroBebe", columnas, valores);

		for(Map<String,Object> mapaTcEnfermero : arrayEnfermeros) {
			String dni = (String)mapaTcEnfermero.get("dni_enfermero");
			listaEnfermeros.add(dni);
		}
		return listaEnfermeros;
	}
	/**
	 * Devuelve tras transformar de mapa a Pulsera dado el id, dni del padre y el nombre del bebe
	 * Llama al metodo de parseo de sensores
	 * @param idPulsera 
	 * @param dniPadre 
	 * @param nombreBebe
	 * @return la pulsera tras el parseo
	 */
	private Pulsera leerPulseraById(String idPulsera, String dniPadre, String nombreBebe) {
		Pulsera pulsera = new Pulsera();

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("id"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(idPulsera));
		ArrayList<Map<String,Object>> arrayPulsera = sq.selectFromTable("pulsera", columnas, valores);

		Map<String,Object> mapaPulsera = arrayPulsera.get(0);

		pulsera.setId(idPulsera);
		pulsera.setDniPadre(dniPadre);
		pulsera.setNombreBebe(nombreBebe);

		int idOximetro = (int)mapaPulsera.get("idOximetro");
		pulsera.setOximetro(leerSensorById(idOximetro));

		int idPulsometro = (int)mapaPulsera.get("idPulsometro");
		pulsera.setPulsometro(leerSensorById(idPulsometro));

		int idTemperatura = (int)mapaPulsera.get("idTemperatura");
		pulsera.setTemperaturaInt(leerSensorById(idTemperatura)); 

		pulsera.setTemperaturaExt(new Sensor(TipoSensor.TEMPEXTERNA)); //FIXME vamos a quitar el de externa(?)

		//TODO añadirla a pojos y FIXME cuando viene de sql es un boolean o un int? 
		//pulsera.isActivada((Boolean)mapaPulsera.get("isActivada")); 

		return pulsera;
	}
	/**
	 * Devuelve el sensor tras transformarlo de mapa a Sensor, llamando despues al metodo de transformacion
	 * de datos
	 * @param id
	 * @return
	 */
	private Sensor leerSensorById(int id) {
		Sensor sensor = new Sensor();

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("id"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(id));
		ArrayList<Map<String,Object>> arraySensor = sq.selectFromTable("sensor", columnas, valores);

		Map<String,Object> mapaSensor = arraySensor.get(0);

		//FIXME meter setters en el pojo y TODO añadir el is activo (mirar lo de boolean)
		sensor.setLimite_anormal_inferior((double)mapaSensor.get("limAnormalInf"));
		sensor.setLimite_anormal_superior((double)mapaSensor.get("limAnormalSup"));

		String nombreSensor = (String)mapaSensor.get("tipoSensor");

		switch(nombreSensor) {
		case "TEMPEXTERNA": sensor.setNombre(TipoSensor.TEMPEXTERNA);
		break;
		case "TEMPINTERNA": sensor.setNombre(TipoSensor.TEMPINTERNA);
		break;
		case "OXIMETRO": sensor.setNombre(TipoSensor.OXIMETRO);
		break;
		case "PULSOMETRO": sensor.setNombre(TipoSensor.PULSOMETRO);
		break;
		}

		sensor.setDatos(leerDatosPorIdSensor(id));


		return sensor;
	}
	/**
	 * Devuelve todos los datos, tras transformar de mapa a Array de Unidades
	 * @param id
	 * @return
	 */
	private ArrayList<Unidad> leerDatosPorIdSensor(int id) {
		ArrayList<Unidad> datos = new ArrayList<Unidad>();

		ArrayList<String> columnas = new ArrayList<String>(Arrays.asList("idSensor"));
		ArrayList<Object> valores = new ArrayList<Object>(Arrays.asList(id));
		ArrayList<Map<String,Object>> arrayDatos = sq.selectFromTable("datos", columnas, valores);

		for(Map<String,Object> mapaDatos : arrayDatos) {
			Unidad unidad = new Unidad();

			String fechaAdaptadaALocalDate = (mapaDatos.get("fecha")).toString().replace(" ", "T");
			LocalDateTime time = LocalDateTime.parse(fechaAdaptadaALocalDate); //FIXME mirar a ver si funciona con LocalDateTime
			unidad.setStringTime(time);

			unidad.setValor((double)mapaDatos.get("valor"));

			datos.add(unidad);
		}

		return datos;
	}
}
