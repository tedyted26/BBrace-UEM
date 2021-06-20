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

public class EmailNotificacion {
	

	public static void enviarMail(String destino,
			String remitente,
			String horaTotal,
			String fecha,
			String asunto) throws Exception {
		
			
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
		
		Message message = prepareMessage(session, myAccountEmail, destino, remitente, horaTotal, fecha, asunto);
		
		Transport.send(message);
	
		
	}

	private static Message prepareMessage(Session session, String myAccountEmail, String destino, String remitente, String horaTotal, String fecha, String codigoRecuperacion) {
		
			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(myAccountEmail));
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(destino));
				message.setSubject("¡¡Tiene una notificación de EMERGENCIA sin Leer!!");
				message.setContent("<html><p style='font-size:20px'>Tiene un mensaje de " +remitente+ " enviado el "+fecha+" "+horaTotal+"&#10071</p>","text/html");
				return message;
			} catch (AddressException e) {
				e.printStackTrace();

			} catch (MessagingException e) {
				
				
			}
			return null;
		
	}

}
