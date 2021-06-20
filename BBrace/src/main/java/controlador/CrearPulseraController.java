package controlador;

import java.util.ArrayList;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import modelo.Bebe;
import modelo.Padre;
import modelo.Pulsera;
import modelo.Sanitario;
import modelo.TipoSanitario;
import modelo.UsuarioLoggeado;
import servicios.GestorDePersistencia;

public class CrearPulseraController {

    @FXML
    private GridPane gridPane;

    @FXML
    private Button aniadirBebeButton;

    @FXML
    private Label labelTitulo;

    @FXML
    private Label mensajeAuxiliarLabel;

    @FXML
    private JFXTextField nombreTextField;

    @FXML
    private JFXTextField semanasGestacionTextField;

    @FXML
    private JFXTextArea observacionesTextField;

    @FXML
    private JFXDatePicker fechaNacimientoDatePicker;

    @FXML
    private JFXTextField idPulseraTextField;

    @FXML
    private HBox hboxSup;

    @FXML
    private HBox hboxInf;

    private ObservableList<Bebe> listaObservable;
    
    public CrearPulseraController(ObservableList<Bebe> listaObservable) {
		super();
		this.listaObservable = listaObservable;
	}
    
    @FXML
    void aniadirBebe(ActionEvent event) {
    	UsuarioLoggeado ul = UsuarioLoggeado.getUsuarioLoggeado();//Pillamos el usuario logueado
		Padre padre = (Padre) ul.getUsuario();
		
    	boolean isCamposRellenos = comprobarCamposRellenos();
    	boolean isCodigoDePulseraExiste = comprobarCodigoDePulseraExiste();
    	boolean isNombreNoExistente = comprobarNombreBebeNoExiste(padre);
    	//Comprobamos si los campos está rellenos y el nombre no esta repetido
    	if(isCamposRellenos && isNombreNoExistente) {		
			//Comprobamos si el codigo de la pulsera es valido
    		if(isCodigoDePulseraExiste) {
    			Pulsera p = new Pulsera();
    	    	p.setNombreBebe(nombreTextField.getText());
    	    	p.setId(idPulseraTextField.getText());
    	    	p.setDniPadre(padre.getDni());
    	    	
    	    	Bebe b = new Bebe(nombreTextField.getText(), fechaNacimientoDatePicker.getValue(), p, padre.getDni());
    	    	b.setObservaciones(observacionesTextField.getText());
    	    	b.setSemanasGestacion(semanasGestacionTextField.getText());
    	    	b.setListaEnfermerosDni(asociarEnfermerosABebe(padre));
    	    	listaObservable.add(b);
    	    	Stage stage = (Stage) aniadirBebeButton.getScene().getWindow();
    	    	stage.close();
    		}
    		else {
    			mensajeAuxiliarLabel.setText("Código de pulsera incorrecto.");
    		}
    	}
    	else if(!isCamposRellenos){
    		mensajeAuxiliarLabel.setText("Campos incompletos.");
    	}
    	else {
    		mensajeAuxiliarLabel.setText("El nombre ya existe.");
    	}
    }
    //Comprueba si los campos tienen valores y si la pulser es correcta
    private boolean comprobarCamposRellenos() {
    	boolean isCamposRellenos = false;
    	if(
    		!nombreTextField.getText().isEmpty() &&
    		fechaNacimientoDatePicker.getValue() != null &&
    		!semanasGestacionTextField.getText().isEmpty() &&
    		!idPulseraTextField.getText().isEmpty()
    	) {
    		isCamposRellenos = true;
    	}
    	return isCamposRellenos;
    }
    //Comprueba si la pulsera está aun disponible en el JSON de sin asignar y devuelve un bool
    private boolean comprobarCodigoDePulseraExiste() {
    	GestorDePersistencia gp = new GestorDePersistencia();
    	return gp.comprobarSiPulseraSinAsociarExiste(idPulseraTextField.getText());
    }
    //Comprueba si hay un nombre de bebe repetido, devolviendo true si no existe
    private boolean comprobarNombreBebeNoExiste(Padre p){
    	boolean isNombreNoExistente = true;
    	for(Bebe bebe : p.getListaBebes()) {
    		if(bebe.getNombre().equals(nombreTextField.getText())) {
    			isNombreNoExistente = false;
    		}
    	}
    	return isNombreNoExistente;
    }
	
    private ArrayList<String> asociarEnfermerosABebe(Padre p) {
    	String dniMedicoAsociadoAPadre = p.getMedicoAsociadoFamiliaDni();
    	
    	GestorDePersistencia gp = new GestorDePersistencia();
    	Sanitario medicoAsociado = gp.getSanitarioByDni(dniMedicoAsociadoAPadre);
    	
    	ArrayList<Sanitario> listaEnfermeros = gp.leerSanitariosEnfermeros();
    	//FILTRADO DE ENF ASOCIADOS AL MEDICO
    	ArrayList<Sanitario> enfermerosAsociadosAMedico = new ArrayList<>();
    	for(Sanitario enfermero : listaEnfermeros) {
    		for(String s : medicoAsociado.getListaEnfermerosAsociadosDni()) {
    			if(enfermero.getDni().equals(s)) {
    				enfermerosAsociadosAMedico.add(enfermero);
    			}
    		}	
    	}
    	//FILTRADO DE LOS 2 ENFERMEROS QUE MENOS PACIENTES TENGAN (forma aleatoria)
    	ArrayList<String> enfermerosAAsociar= new ArrayList<>();
    	for(int i = 0; i<2 ; i++) {
    		double operacion =Math.random()*enfermerosAsociadosAMedico.size();
    		int numeroRandom = (int) operacion;
    		Sanitario san = enfermerosAsociadosAMedico.get(numeroRandom);
    		boolean isRepetido = false;
    		for(String sanRepetido : enfermerosAAsociar) {
    			if(sanRepetido.equals(san.getDni())) isRepetido =true;
    		}
    		if(!isRepetido)enfermerosAAsociar.add(san.getDni());
    	}
    	return enfermerosAAsociar;
    	
    }

    public void asociarEnfermeroAMedicos (Sanitario enfermero) {
		ArrayList<String> dniMedico=new ArrayList<>();

		//Solo si es medico tiene una lista de Enfermeros asociados
		if (enfermero.getTipoSanitario().equals(TipoSanitario.ENFERMERO)) {
			GestorDePersistencia gp= new GestorDePersistencia();
			
			//lista con médicos
			ArrayList<Sanitario> listaMedicos=gp.leerSanitariosMedico();

			if(listaMedicos.size()!=0) {
				int busqueda=-1;
				int menorValorFlag=9999;
				for(int i=0;i<listaMedicos.size();i++) {
					int valorActual=listaMedicos.get(i).getListaEnfermerosAsociadosDni().size();
					if( valorActual< menorValorFlag) {
						menorValorFlag=valorActual;
						busqueda=i;
					}
				}
				String medico=listaMedicos.get(busqueda).getDni().toString();

				dniMedico.add(medico);
				Sanitario miMedico=gp.getSanitarioByDni(medico);
				miMedico.getListaEnfermerosAsociadosDni().add(enfermero.getDni().toString());
				gp.escribirSanitario(miMedico);

				
				
				enfermero.setEnfermeroYaAsociadoAMedico(true);
				enfermero.setMedicoAsociadoAEnfermero(dniMedico);
				gp.escribirSanitario(enfermero);
				
			}

		}
	}

}
