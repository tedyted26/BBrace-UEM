package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import modelo.Bebe;
import modelo.Padre;
import modelo.UsuarioLoggeado;
import servicios.GestorDeEscenas;
import servicios.GestorDeGraficas;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class VistaSensoresController implements Initializable{

	private Padre padre = null;
	
	private boolean mostrarBotones=true;

	//variables que se usan en los metodos
	private XYChart.Series seriesTemp, seriesPul, seriesOxg;

	private XYChart.Series seriesConstTempMin, seriesConstPulMin, seriesConstOxgMin;

	private XYChart.Series seriesConstTempMax, seriesConstPulMax, seriesConstOxgMax;

	private double lowerBound, upperBound;

	private String flagProcedencia = "sensor";

	//para que no haga cosas raras
	private GestorDeGraficas ggTemp,ggPul,ggOxg; 

	private GestorDeEscenas ge;

	private LineChart<?, ?> lineChartTemperatura;

	private LineChart<?, ?> lineChartPulso;

	private LineChart<?, ?> lineChartOxigeno;
	
    @FXML
    private StackPane stackPanePadre;

    @FXML
    private GridPane gridVisualizarSensores;

    @FXML
    private VBox vBoxTemp;

    @FXML
    private Label labelTituloTemp;

    @FXML
    private VBox vBoxPul;

    @FXML
    private Label labelTituloPul;

    @FXML
    private VBox vBoxOxg;

    @FXML
    private Label labelTituloOxg;

    @FXML
    private JFXButton botonHistorial;

    @FXML
    private HBox hBoxSeleccionar;

    @FXML
    private Label labelSeleccionarBebe;

    @FXML
    private JFXComboBox<String> comboBoxSeleccionarBebe;

    @FXML
    private Label labelAviso;

    @FXML
    private HBox hBoxBotones;

    @FXML
    private JFXButton botonContacto;

    @FXML
    private JFXButton botonFichaCompleta;

    @FXML
    private GridPane gridPaneHistorial;

    @FXML
    private JFXButton botonAtras;
    
	public VistaSensoresController() {
		this.padre = (Padre) UsuarioLoggeado.getUsuarioLoggeado().getUsuario();
	}

	public VistaSensoresController(Padre padre) {
		this.padre = padre;
		this.mostrarBotones = false;
	}

	@FXML
	void irAHistorial(ActionEvent event) {   	
		if (comboBoxSeleccionarBebe.getSelectionModel().getSelectedIndex()!=-1) {
			Bebe bebe = padre.getListaBebes().get(comboBoxSeleccionarBebe.getSelectionModel().getSelectedIndex());
			GridPane panelHistorial = (GridPane)ge.cargarVistaSegunPanel("VistaHistorial.fxml",new VistaHistorialController(bebe), gridPaneHistorial);
			gridPaneHistorial.add(panelHistorial, 0, 0, 2, 2);
			gridPaneHistorial.setVisible(true);
			gridVisualizarSensores.setVisible(false);
		}

	}
	
	@FXML
	void irAtras(ActionEvent event) {
		gridPaneHistorial.setVisible(false);
		gridVisualizarSensores.setVisible(true);
	}

	@FXML
	void contactoEmergencia(ActionEvent event) {   	 
		ge.cargarPopUp("GenerarNotificacion.fxml", new GenerarNotificacion(true, flagProcedencia), event);
	}

	@FXML
	void verFichaBebe(ActionEvent event) {
		if (comboBoxSeleccionarBebe.getSelectionModel().getSelectedIndex()!=-1) {
			int bebeSeleccionado = comboBoxSeleccionarBebe.getSelectionModel().getSelectedIndex();
			ge.cargarEscenaEnNuevaVentana("VisualizarDatosBebe.fxml", 
					new VisualizarDatosBebeControlador(padre.getListaBebes().get(bebeSeleccionado), padre), false);
		}
	}

	@FXML
	void seleccionarBebe(ActionEvent event) {
		eliminar();
		generarGraficas(comboBoxSeleccionarBebe.getSelectionModel().getSelectedIndex()); 
	}

	@SuppressWarnings("static-access")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ge = new GestorDeEscenas();
		labelAviso.setVisible(false);
		gridVisualizarSensores.setVisible(true);
		gridPaneHistorial.setVisible(false);
		if (!mostrarBotones) {
			hBoxBotones.getChildren().remove(botonContacto);
			botonFichaCompleta.setText("VER FICHA COMPLETA DEL BEBÉ");
			hBoxBotones.setMargin(botonFichaCompleta, new Insets(0, 0, 0, 40));
		}

		try {

			//RELLENO DEL COMBOBOX EN LA PARTE DE SENSORES, Y GENERACION DE GRAFICAS DEPENDIENDO DEL BEBE
			for (int i = 0; i<padre.getListaBebes().size(); i++) {
				comboBoxSeleccionarBebe.getItems().add(i, padre.getListaBebes().get(i).getNombre());
			}
			comboBoxSeleccionarBebe.getSelectionModel().clearAndSelect(0);	
			generarGraficas(comboBoxSeleccionarBebe.getSelectionModel().getSelectedIndex());
		}catch (Exception e) {
			System.err.println("Datos de las gráficas no encontrados.");
			labelAviso.setVisible(true);

		}

	}

	//GENERACIÓN DE GRÁFICAS, ES LARGO, PONED VUESTROS METODOS ENCIMA 
	private void generarGraficas(int index) {
		//inicializamos todo de nuevo
		ggTemp = new GestorDeGraficas("Temperatura", padre);
		ggPul = new GestorDeGraficas("Pulso", padre);
		ggOxg = new GestorDeGraficas("Oxigeno", padre);

		ggTemp.setBebeSeleccionado(index);
		ggPul.setBebeSeleccionado(index);
		ggOxg.setBebeSeleccionado(index);

		lowerBound = ggTemp.getValorMinimo()-0.1;
		upperBound = ggTemp.getValorMaximo()+0.1;

		CategoryAxis ejeXtemp = new CategoryAxis();
		NumberAxis ejeYtemp = new NumberAxis("ºC",lowerBound,upperBound,0.1);

		lowerBound = ggPul.getValorMinimo()-1;
		upperBound = ggPul.getValorMaximo()+1;

		CategoryAxis ejeXpul = new CategoryAxis();
		NumberAxis ejeYpul= new NumberAxis("Pulsaciones",lowerBound,upperBound,2.0);

		lowerBound = ggOxg.getValorMinimo()-0.5;
		upperBound = ggOxg.getValorMaximo()+0.5;

		CategoryAxis ejeXoxg = new CategoryAxis();
		NumberAxis ejeYoxg = new NumberAxis("% oxígeno",lowerBound,upperBound,0.5);

		lineChartTemperatura = new LineChart(ejeXtemp, ejeYtemp);
		lineChartPulso = new LineChart(ejeXpul, ejeYpul);
		lineChartOxigeno = new LineChart(ejeXoxg, ejeYoxg);

		lineChartTemperatura.setMaxSize(600, 600);
		lineChartPulso.setMaxSize(600, 600);
		lineChartOxigeno.setMaxSize(600, 600);	

		seriesTemp = new XYChart.Series();
		seriesPul = new XYChart.Series();
		seriesOxg = new XYChart.Series();

		seriesConstTempMin = new XYChart.Series();
		seriesConstPulMin = new XYChart.Series();
		seriesConstOxgMin = new XYChart.Series();

		seriesConstTempMax = new XYChart.Series();
		seriesConstPulMax = new XYChart.Series();
		seriesConstOxgMax = new XYChart.Series();

		lineChartTemperatura = new LineChart<String, Number>(ejeXtemp, ejeYtemp);
		lineChartPulso = new LineChart<String, Number>(ejeXpul, ejeYpul);
		lineChartOxigeno = new LineChart<String, Number>(ejeXoxg, ejeYoxg);

		//INCISO, CONFIGURACION VISUAL EJES
		ejeXtemp.setGapStartAndEnd(false);
		ejeXpul.setGapStartAndEnd(false);
		ejeXoxg.setGapStartAndEnd(false);

		ejeXtemp.setStartMargin(5.0);
		ejeXpul.setStartMargin(5.0);
		ejeXoxg.setStartMargin(5.0);

		ejeXtemp.setEndMargin(5.0);
		ejeXpul.setEndMargin(5.0);
		ejeXoxg.setEndMargin(5.0);
		//TERMINA EL INCISO

		//generamos grafica a partir de los datos del json
		seriesTemp = ggTemp.generarDatosDeJson();	
		seriesPul = ggPul.generarDatosDeJson();
		seriesOxg = ggOxg.generarDatosDeJson();

		//generamos grafica constante para los valores anormales
		seriesConstTempMin = ggTemp.generarDatosConstantes("min"); // 36.2 la temperatura normal de un bebe recien nacido es de entre 36.5 y 37.5, se considera hipotermia cuando estÃ¡ por debajo de 36.5, e hipertermia cuando estÃ¡ por encima de 38
		seriesConstPulMin = ggPul.generarDatosConstantes("min"); // 85 ritmo mÃ¡ximo de 180 (bebe, 170 en niÃ±os de un par de aÃ±os), promedio de 140(115 en niÃ±os), minimo 90 
		seriesConstOxgMin = ggOxg.generarDatosConstantes("min"); // 90 minimo 93%, Los prematuros con edad gestacional â‰¤ 28 semanas que precisen oxÃ­geno en el perÃ­odo posnatal deberÃ¡n mantenerse dentro de un rango de saturaciÃ³n de 90â€“95%.

		//generamos grafica constante para los valores anormales
		seriesConstTempMax = ggTemp.generarDatosConstantes("max");
		seriesConstPulMax = ggPul.generarDatosConstantes("max");
		seriesConstOxgMax = ggOxg.generarDatosConstantes("max");

		lineChartTemperatura.getData().addAll(seriesConstTempMin, seriesConstTempMax, seriesTemp);
		lineChartPulso.getData().addAll(seriesConstPulMin, seriesConstPulMax, seriesPul);
		lineChartOxigeno.getData().addAll(seriesConstOxgMin, seriesConstOxgMax, seriesOxg);

		gridVisualizarSensores.add(lineChartTemperatura, 0, 1);
		gridVisualizarSensores.add(lineChartPulso, 1, 1);
		gridVisualizarSensores.add(lineChartOxigeno, 2, 1);

		//CONFIGURACION VISUAL GRAFICAS
		lineChartTemperatura.setLegendVisible(false);
		lineChartPulso.setLegendVisible(false);
		lineChartOxigeno.setLegendVisible(false);

		lineChartTemperatura.getXAxis().setLabel("Hora");
		lineChartPulso.getXAxis().setLabel("Hora");
		lineChartOxigeno.getXAxis().setLabel("Hora");

		configuracionIgual(lineChartTemperatura.getXAxis(), "X");
		configuracionIgual(lineChartPulso.getXAxis(), "X");
		configuracionIgual(lineChartOxigeno.getXAxis(), "X");

		configuracionIgual(lineChartTemperatura.getYAxis(), "Y");
		configuracionIgual(lineChartPulso.getYAxis(), "Y");
		configuracionIgual(lineChartOxigeno.getYAxis(), "Y");	
	}

	private void eliminar() {
		gridVisualizarSensores.getChildren().removeAll(lineChartTemperatura, lineChartPulso, lineChartOxigeno);
	}

	private void configuracionIgual(Axis eje, String ejeXoY) {

		eje.setTickLabelsVisible(true);
		eje.setTickMarkVisible(true);
		eje.setTickLabelGap(3.0);
		eje.setTickLength(8.0);
		if (ejeXoY.equals("X")) eje.setAutoRanging(true);
		if (ejeXoY.equals("Y")) eje.setAutoRanging(false);
	}
}
