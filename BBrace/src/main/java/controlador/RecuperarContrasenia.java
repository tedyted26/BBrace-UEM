package controlador;

import java.util.Properties;


import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;




public class RecuperarContrasenia {
	
	
	public static void enviarMail(String recepient,
			String tituloCorreo,
			String mensajeCorreo,
			String codigoRecuperacion) throws Exception {
		
			
		Properties properties = new Properties();
		
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		
		String myAccountEmail = "bbraceuem@gmail.com";
		String password = "bbrace2020";
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(myAccountEmail, password);
			}
		});
		
		Message message = prepareMessage(session, myAccountEmail, recepient, tituloCorreo, mensajeCorreo, codigoRecuperacion);
		
		Transport.send(message);
	
		
	}

	private static Message prepareMessage(Session session, String myAccountEmail, String recepient, String tituloCorreo, String mensajeCorreo, String codigoRecuperacion) {
		
			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(myAccountEmail));
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
				message.setSubject(tituloCorreo);
				message.setContent("<html><p style='font-size:20px'>"+mensajeCorreo+"</p>" + "<p style='font-size:44px';><strong>"+codigoRecuperacion+"</strong></p></html>","text/html");
				return message;
			} catch (AddressException e) {
				e.printStackTrace();
				//Logger.getLogger(RecuperarContrasenia.class.getName()).log(Level.SEVERE, null, e);
			} catch (MessagingException e) {
				
			}
			return null;
		
	}
}
