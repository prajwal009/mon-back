

package com.api.apimonitoring;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.api.apimonitoring.GmailSenderApplication;

import kong.unirest.HttpResponse;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;


@RestController
public class GmailSenderApplication 
{
	@Value("${gmail.username}")
	private String username;
	@Value("${gmail.password}")
	private String password;



	@Autowired
	IMonitoringRepository monitoringRepository;
	

	public void sendmail(String id) throws AddressException, MessagingException, IOException {
		System.out.println("email entered");
		Monitors monitorMail = monitoringRepository.findOneMonitor(id);
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				  });
		
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(username, false));

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(monitorMail.geteMail()));
		msg.setSubject("Status Code of api "+monitorMail.getUrl());
		msg.setContent("MOnitor Name "+monitorMail.getMonitorName()+" Monitor Type "+monitorMail.getMethodType(), "text/html");
		msg.setSentDate(new Date());
		
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent("Present status code of api "+monitorMail.getUrl()+" is "+monitorMail.getStatusCode(), "text/html");
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		Transport.send(msg);

	
	}
}
