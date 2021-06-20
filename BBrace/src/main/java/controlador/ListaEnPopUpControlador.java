package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXListView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import modelo.Padre;
import modelo.Sanitario;
import modelo.TipoSanitario;
import modelo.Usuario;
import servicios.GestorDePersistencia;

public class ListaEnPopUpControlador implements Initializable{

    @FXML
    private GridPane borderPane;

    @FXML
    private HBox hboxSup;

    @FXML
    private HBox hboxInf;

    @FXML
    private JFXListView<Usuario> listView;

    @FXML
    private Label labelTitulo;

    @FXML
    private Label labelAviso;
    
    private GestorDePersistencia gp;
    private ObservableList<Usuario> items;
    private Usuario usuario;
    

       
    public ListaEnPopUpControlador(Usuario usuario) {
    	this.usuario = usuario;
    	items = FXCollections.observableArrayList();
    	
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gp = new GestorDePersistencia();
		listView.setItems(items);
		
		//MOSTRAR LOS ENFERMEROS ASOCIADOS A UN PADRE (CADA UNO DE LOS ENFERMEROS DE SU BEBE)
		if (usuario instanceof Padre) {
			labelTitulo.setText("Enfermeros asignados a sus bebés:");
		}
		
		//MOSTRAR LOS ENFERMEROS ASOCIADOS A UN MEDICO
		else if (usuario instanceof Sanitario && ((Sanitario)usuario).getTipoSanitario().equals(TipoSanitario.MEDICO)) {
			for(String dni : ((Sanitario) usuario).getListaEnfermerosAsociadosDni()) 
				items.add(gp.getSanitarioByDni(dni));
			listView.setCellFactory(listaObservable -> new TarjetaSanitarioControlador<Usuario>());
			labelTitulo.setText("Enfermeros asignados:");
		}
		
		//MOSTRAR LOS BEBES ASOCIADOS A UN SANITARIO	
		else if (usuario instanceof Sanitario && ((Sanitario)usuario).getTipoSanitario().equals(TipoSanitario.ENFERMERO)) {
			labelTitulo.setText("Bebés asociados:");
		}
		
		if (items.isEmpty()) {
			labelAviso.setText("No hay datos o no se pueden cargar.");
			labelAviso.setVisible(true);
		}

	}

}