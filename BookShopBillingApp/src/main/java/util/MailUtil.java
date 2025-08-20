package util;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import service.ConfigurationService;

public class MailUtil {
    public static void sendMailHtml(String to, String subject, String htmlContent) throws jakarta.mail.MessagingException {
        ConfigurationService config = ConfigurationService.getInstance();
        String host = config.getSmtpHost();
        String port = config.getSmtpPort();
        String username = config.getSmtpUsername();
        String password = config.getSmtpPassword();
        String from = config.getSmtpFrom();
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");
        Transport.send(message);
    }
} 