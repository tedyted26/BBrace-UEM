package servicios;
import java.util.ArrayList;

import javafx.scene.chart.XYChart;
import modelo.Padre;
import modelo.Sensor;
import modelo.TipoSensor;
import modelo.Unidad;
import modelo.UsuarioLoggeado;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GestorDeGraficas {

	private int contador=0, limiteGrafica;

	private XYChart.Data data;

	private XYChart.Series series;

	private ArrayList<XYChart.Data> listaDatos;

	private ArrayList<Sensor> listaSensores;

	private TipoSensor tipo;

	private Padre usuario;

	private double valorMinimo, valorMaximo;


	//INTERFAZ PÚBLICA

	/**
	 * Constructor segun la grafica que se quiera representar y el usuario al que pertenecen los datos
	 * @param nombreTabla --tabla que se quiere representar
	 * @param padre --puede ser null si las graficas pertenecen al usuarioLoggeado
	 */
	public GestorDeGraficas(String nombreTabla, Padre padre) {
		listaSensores = new ArrayList<Sensor>();
		if (padre == null) usuario = (Padre) UsuarioLoggeado.getUsuarioLoggeado().getUsuario();
		else usuario = padre;
		tipo = tipoSegunNombre(nombreTabla);
		limiteGrafica = 20;

	}

	/**
	 * Guarda la lista de sensores para un bebé en específico
	 * @param babyindex --Índice del bebé en el array de bebes del usuario loggeado.
	 */
	public void setBebeSeleccionado(int babyindex) {
		listaSensores = usuario.getListaBebes().get(babyindex).getPulsera().getAllSensores();
	}


	/**
	 * Genera una serie de datos que necesita la tabla para representarse.
	 * Depende del parámetro pasado por el constructor, o sea, el nombre de la tabla.
	 * Segun la tabla que sea se genera una gráfica u otra (temperatura, pulso o oxígeno).
	 * @return javafx.scene.chart.XYChart.Series --Serie de datos que se introducen en el Chart
	 */
	public XYChart.Series generarDatosDeJson() {
		series = new XYChart.Series();

		//vector con los XYChart.Data 
		listaDatos = transformarVector();

		//este contador es para saber el num de elementos que hay, se utiliza en la generacion de datos constantes
		contador=listaDatos.size();

		//añade cada uno de los elementos a la serie de datos
		for(int i=0; i<contador; i++) {
			if (series.getData().size()>limiteGrafica) series.getData().remove(0);
			series.getData().add(listaDatos.get(i));
		}
		return series;
	}

	/**
	 * Genera un dato constante dependiendo de la tabla que representa, y de si se necesita un valor minimo o uno maximo
	 * @return javafx.scene.chart.XYChart.Series --Serie de datos que se introducen en el Chart
	 */
	public XYChart.Series generarDatosConstantes(String minOmax){
		series = new XYChart.Series();
		double limiteAnormal = 0;

		for (int i = 0; i<listaSensores.size(); i++){

			if (listaSensores.get(i).getNombre().equals(tipo)) {

				if (minOmax.equals("min")) limiteAnormal = listaSensores.get(i).getLimite_anormal();
				else if (minOmax.equals("max")) limiteAnormal = listaSensores.get(i).getLimite_anormal_superior();

				for(int j=0; j<contador; j++) { 
					String n= listaSensores.get(i).getDatos().get(j).getStringHora();
					data= new XYChart.Data(n,limiteAnormal);
					if (series.getData().size()>limiteGrafica) series.getData().remove(0);
					series.getData().add(data);
				}
				i = listaSensores.size();
			}
		}		
		return series;

	}

	/**
	 * Encuentra el valor minimo presente en los sensores para la gráfica especificada en el constructor.
	 * Puede ser tanto de los valores anormales como de los datos obtenidos del propio sensor.
	 * @return double 
	 */
	public double getValorMinimo() {
		double unidadmin = 300;
		double anormalmin = 300;

		for (Sensor s: listaSensores) {
			if (s.getNombre().equals(tipo)) {	
				if (anormalmin > s.getLimite_anormal()) anormalmin = s.getLimite_anormal();
				
				int diferencia = 0;
				if (s.getDatos().size()>limiteGrafica) {
					diferencia = s.getDatos().size()-limiteGrafica;
					for (int i=diferencia; i<s.getDatos().size(); i++) {
						Unidad u = s.getDatos().get(i);
						if (unidadmin > u.getValor()) unidadmin = u.getValor();
					}
				}
				else {
					for (Unidad u : s.getDatos()) {
						if (unidadmin > u.getValor()) unidadmin = u.getValor();
					}
				}
				
			}
		}
		if (unidadmin < anormalmin) valorMinimo = unidadmin;
		else valorMinimo = anormalmin; 
		return valorMinimo;
	}

	/**
	 * Encuentra el valor minimo presente en los sensores ara la gráfica especificada en el constructor.
	 * Puede ser tanto de los valores anormales como de los datos obtenidos del propio sensor.
	 * @return double
	 */
	public double getValorMaximo() {
		double unidadmax = 0;
		double anormalmax = 0;

		for (Sensor s: listaSensores) {
			if (s.getNombre().equals(tipo)) {
				if (anormalmax < s.getLimite_anormal_superior()) anormalmax = s.getLimite_anormal_superior();
				
				int diferencia = 0;
				if (s.getDatos().size()>limiteGrafica) {
					diferencia = s.getDatos().size()-limiteGrafica;
					for (int i=diferencia; i<s.getDatos().size(); i++) {
						Unidad u = s.getDatos().get(i);
						if (unidadmax < u.getValor()) unidadmax = u.getValor();
					}
				}
				else {
					for (Unidad u : s.getDatos()) {
						
						if (unidadmax < u.getValor()) unidadmax = u.getValor();
					}
				}
				
			}
		}
		if (unidadmax > anormalmax) valorMaximo = unidadmax;
		else valorMaximo = anormalmax;
		return valorMaximo;
	}


	//INTERFAZ PRIVADA
	/**
	 * Metodo que recoge los datos del vector que viene de json y forma 
	 * el vector de datos correspondiente a la tabla especificada en el constructor
	 * @return ArrayList XYChart.Data --Array de puntos de un Chart
	 */
	private ArrayList<XYChart.Data> transformarVector(){

		listaDatos = new ArrayList<XYChart.Data>();

		for (Sensor s:listaSensores){
			if (s.getNombre().equals(tipo)) {
				for (Unidad u : s.getDatos()) {
					data = new XYChart.Data(u.getStringHora(), u.getValor());
					listaDatos.add(data);
				}
			}
		}
		return listaDatos;
	}

	/**
	 * Método que se utiliza para especificar el tipo de sensor con el que se esta trabajando 
	 * dependiendo del nombre de la gráfica
	 * @param nombreTabla --nombre de la tabla pasada por el constructor
	 * @return TipoSensor
	 */
	private TipoSensor tipoSegunNombre(String nombreTabla) {

		switch(nombreTabla) {
		case "Temperatura": tipo = TipoSensor.TEMPINTERNA; break;
		case "Pulso": tipo = TipoSensor.PULSOMETRO; break;
		case "Oxigeno": tipo = TipoSensor.OXIMETRO; break;
		}

		return tipo;
	}

}
