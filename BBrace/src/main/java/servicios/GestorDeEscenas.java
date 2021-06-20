package servicios;

import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GestorDeEscenas {
	
	private final static String rutaRelativaDeVistasFXML =  "/org/openjfx1/BBrace/vista/";
	private final static Rectangle2D screen = Screen.getPrimary().getVisualBounds();
	
	private void configuraLaEscena(String nombreDeLaVistaFxml,Object controladorDeLaVentana, Stage window, boolean escalarVentanaAPantalla) {
		FXMLLoader loader = new FXMLLoader(getClass().
    			getResource(rutaRelativaDeVistasFXML + nombreDeLaVistaFxml));
		loader.setController(controladorDeLaVentana);
		//para añadir el icono a cada ventana
		try {
			window.getIcons().add(new Image(rutaRelativaDeVistasFXML + "canguro suelto 64.png"));
		}catch (Exception e) {
			e.printStackTrace();
		}
		//para añadir el titulo de la ventana
		window.setTitle("Bbrace");
		if (escalarVentanaAPantalla) escalarVentana(window);
		
		Parent root;
		
		try {
			root = loader.load();
			
			window.setScene(new Scene(root));
	        window.show(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Permite cargar la escena en la ventana actual
	public void cargarEscena(String nombreDeLaVistaFxml,Object controladorDeLaVentana, ActionEvent event, boolean escalarVentanaAPantalla){
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		configuraLaEscena(nombreDeLaVistaFxml, controladorDeLaVentana, window, escalarVentanaAPantalla);
	}
	public void cargarEscena(String nombreDeLaVistaFxml,Object controladorDeLaVentana, Stage stage, boolean escalarVentanaAPantalla){
		configuraLaEscena(nombreDeLaVistaFxml, controladorDeLaVentana, stage, escalarVentanaAPantalla);
	}
	//Carga la escena en una nueva ventana
	public void cargarEscenaEnNuevaVentana(String nombreDeLaVistaFxml,Object controladorDeLaVentana, boolean escalarVentanaAPantalla) {
		Stage window = new Stage();
		configuraLaEscena(nombreDeLaVistaFxml, controladorDeLaVentana, window, escalarVentanaAPantalla);
	}
	//Metodo de Teo
	/**
	 * Carga una escena en una ventana nueva sin redimensionarse y sin dejar que se pueda interactuar con la ventana
	 * principal hasta que se haya cerrado el popup
	 * @param nombreDeLaVistaFxml
	 * @param controladorDeLaVentana
	 * @param event 
	 */
	public void cargarPopUp(String nombreDeLaVistaFxml,Object controladorDeLaVentana, ActionEvent event) {
		Stage window = new Stage();
		window.initModality(Modality.WINDOW_MODAL);
		window.initOwner(((Node)event.getSource()).getScene().getWindow());
		configuraLaEscena(nombreDeLaVistaFxml, controladorDeLaVentana, window, false);
	}
	//Carga un FXML y su controlador en el Pane que se le pase (nodo)
	public Pane cargarVistaEnPane(String nombreDeLaVistaFxml, Object controladorDeLaVentana, Pane panelACargarLaVista) {
		FXMLLoader loader = new FXMLLoader(getClass().
    			getResource(rutaRelativaDeVistasFXML + nombreDeLaVistaFxml));
		loader.setController(controladorDeLaVentana);
		Pane newLoadedPane = null;
		
		try {
			newLoadedPane = loader.load();
			List<Node> parentChildren = ((Pane)panelACargarLaVista.getParent()).getChildren();
			Node n = parentChildren.set(parentChildren.indexOf(panelACargarLaVista), newLoadedPane);
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		return newLoadedPane;
	}
	
	//Método de teo (copiao y pegao de sanjo)
	/**
	 * Crea un panel (panel hijo) cargado con una vista fxml y lo redimensiona segun el panel en el que se quiera cargar (panel padre).
	 * @param nombreDeLaVistaFxml --Nombre del archivo, no hace falta ruta relativa.
	 * @param controladorDeLaVentana
	 * @param panelACargarLaVista --Panel padre al que se le va a añadir la vista.
	 * @return javafx.scene.layout.Pane --Panel hijo. Puede ser cargado dentro del padre. Se puede adaptar a cualquier panel mediante casting.
	 */
	public Pane cargarVistaSegunPanel(String nombreDeLaVistaFxml, Object controladorDeLaVentana, Pane panelACargarLaVista) {
		FXMLLoader loader = new FXMLLoader(getClass().
    			getResource(rutaRelativaDeVistasFXML + nombreDeLaVistaFxml));
		loader.setController(controladorDeLaVentana);
		Pane newLoadedPane = null;
		try {
			newLoadedPane = loader.load();
			newLoadedPane.prefWidthProperty().bind(panelACargarLaVista.widthProperty());
			newLoadedPane.prefHeightProperty().bind(panelACargarLaVista.heightProperty());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newLoadedPane;
	}
	//Metodo de teo
	/**
	 * Método utilizado para detectar la resolución de pantalla y escalar el stage (o escenario) dependiendo de esa resolución. 
	 * Además, inicializa el escenerio en ventana completa y centrada en la pantalla.
	 * @param stage
	 */
	private void escalarVentana(Stage stage){
		stage.setResizable(true);
		stage.setMaximized(true);
		
        stage.setX(screen.getMinX());
        stage.setY(screen.getMinY());
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
        
        //si da problemas se puede quitar, esta solo de prueba
        //stage.centerOnScreen();

	}
	
	
	public static String getRutaRelativaDeVistasFXML() {
		return rutaRelativaDeVistasFXML;
	}

	public static Rectangle2D getScreen() {
		return screen;
	}
}
