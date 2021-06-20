package controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import modelo.Notificacion;
import servicios.GestorDeEscenas;
import servicios.GestorDePersistencia;

public class TarjetaNotificacionControlador <T> extends ListCell<T> {

	@FXML
    private AnchorPane card_pane;

    @FXML
    private Label asuntoLabel;

    @FXML
    private Label remitenteLabel;

    @FXML
    private Label fechaLabel;
    
    @FXML
    private TextArea mensajeTArea;
    
    @FXML
    private JFXButton botonResponder;
    
    @FXML
    private JFXButton botonEliminar;
    
    FXMLLoader mLLoader;
    
    private boolean isRespuesta = false;

    @FXML
    void responderMensaje(ActionEvent event) {
    	isRespuesta = true;
    	GestorDeEscenas ge = new GestorDeEscenas();
    	Notificacion n = (Notificacion) getItem();
    	ge.cargarEscena("GenerarNotificacion.fxml", new GenerarNotificacion(false, "menu", n, isRespuesta), event, false);
    	
    	 //TODO RESPONDER DANI.    	
    }
    
    @FXML
    void eliminarMensaje(ActionEvent event) {

    }
    
    
    //Cada vez que el item es actualizado salta esta funcion
    @Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		
		if(empty || item == null) {
			setText(null);
			setGraphic(null);
		}else {
			if(mLLoader == null) {
				//Carga la vista y el controlador (this)
				mLLoader = new FXMLLoader(getClass().
						getResource(GestorDeEscenas.getRutaRelativaDeVistasFXML()
								+"TarjetaNotificacion.fxml"));
				mLLoader.setController(this);
				
				
				try {
					mLLoader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			//Inicializamos todos los valores, transformando el tipo T a Notificacion	
			Notificacion notificacion =(Notificacion) item;
			asuntoLabel.setText(String.valueOf(notificacion.getAsunto()));
			remitenteLabel.setText(String.valueOf(notificacion.getRemitente()));
			
			
			LocalDate date = notificacion.getFecha();
			//fechaLabel.setText(date.getYear()+"-"+date.getDayOfMonth()+"-"+date.getMonthValue());
			
			botonResponder.setGraphic(new ImageView(new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"flechita.png")));
			botonEliminar.setGraphic(new ImageView(new Image(GestorDeEscenas.getRutaRelativaDeVistasFXML()+"papelera.png")));
			
			botonResponder.setTooltip(new Tooltip("Responder"));
			botonEliminar.setTooltip(new Tooltip("Eliminar mensaje"));
			//Si es emergencia cambia el color del texto
			if(notificacion.getIsEmergencia())asuntoLabel.getStyleClass().add("text-labels-alerta-mismo-tamanio");;
			//Inicializamos el mensaje a mostrar
			String mensaje = String.valueOf(notificacion.getMensaje());
			String registroMensajes = "";
			String mensajeCompleto = "";
			for(String tmp : notificacion.getRegistroMensajes()) {
				registroMensajes += (tmp+"\n");
			}
			if(!registroMensajes.isEmpty()) {
				mensajeCompleto = registroMensajes 
						+ "---------------------------------\n\n";
			}
			mensajeCompleto += mensaje;
			
			mensajeTArea.setText(mensajeCompleto);
			
			setText(null);
			setGraphic(card_pane);
		}
		
	}
}

