/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.logic.email;
 
import java.io.Serializable; 
import java.io.UnsupportedEncodingException;
import java.util.Properties; 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author idali
 */
public class MailHandler implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    private static final int SMTP_HOST_PORT = 587;
    private static final String SMTP_AUTH_USER = "dina@mail.dina-web.net";
    private static final String SMTP_AUTH_PWD = "D-I-N-A"; 
    private final String MAIL_HOST = "mail.smtps.host";
    
    private final static String SMTP_HOST_NAME = "mail.dina-web.net";
    
    private final String MAIL_PROTOCOL = "mail.transport.protocol";
    private final String SMTP = "smtp";
    private final String MAIL_AUTH = "mail.smtps.auth";
    private final String MAIL_PORT = "mail.smtp.port";
    private final String MAIL_ENABLE = "mail.smtp.starttls.enable";
    
    private final String TEXT_HTML = "text/html; charset=ISO-8859-1";
    
    private final Session session;
    private final Message message;
    
    private final String loginPage = "http://localhost:4200/login";
    
    
    private final String START_DIV_TAG_WITH_FONT = "<div style=\"font-size: 1.2em; \">";
    private final String START_DIV_TAG = "<div>";

    private final String END_DIV_TAG = "</div>";
    private final String BR_TAG = "<br />";
    
    public MailHandler() {
        Properties props = new Properties();

        props.put(SMTP_HOST_NAME, SMTP_HOST_NAME);
        props.put(MAIL_PROTOCOL, SMTP);
        props.put(MAIL_HOST, SMTP_HOST_NAME);
        props.put(MAIL_AUTH, String.valueOf(Boolean.TRUE));
        props.put(MAIL_PORT, SMTP_HOST_PORT);
        props.put(MAIL_ENABLE, String.valueOf(Boolean.TRUE));

        session = Session.getInstance(props, null);
        session.setDebug(true);

        message = new MimeMessage(session); 
    }
    
    public void sendMail(String email, String password, String firstName,String lastName) {
        logger.info("sendMail : {}", email);
                
        String emailAddress = "ida.li@nrm.se";  // for test
        try { 
            message.setFrom(new InternetAddress(SMTP_AUTH_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress)); 
             
                //            message.addRecipient(Message.RecipientType.TO, new InternetAddress(testEmail));
            message.setSubject(MimeUtility.encodeText("DINA user management registration"));
            
            message.setContent(appendMailBody(password, firstName, lastName, email), TEXT_HTML);
            
            InternetAddress[] toaddress = new InternetAddress[]{new InternetAddress(emailAddress)};
            
            Transport transport = session.getTransport();
            transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
            transport.sendMessage(message, toaddress);
            transport.close();
        } catch (MessagingException | UnsupportedEncodingException ex) {
            logger.error(ex.getMessage()); 
        }
    }
     
     
    private String appendMailBody(String password, String firstName, String lastName, String userName) {
        StringBuilder sb = new StringBuilder();
        sb.append(START_DIV_TAG_WITH_FONT);
        sb.append(START_DIV_TAG);
        appendMailTitle(sb, firstName, lastName);
        sb.append(BR_TAG);
        sb.append(BR_TAG);

        sb.append("Your DINA User Management account has been created with the following information:  ");
        sb.append(BR_TAG);
        sb.append(BR_TAG);
        sb.append("Username: ");
        sb.append(userName);
        sb.append(BR_TAG);
        sb.append("Password: ");
        sb.append(password);
        sb.append(BR_TAG);
        sb.append(BR_TAG);
        sb.append("Click ");
        sb.append("<a href=\"");
        sb.append(loginPage);
        sb.append("\">User management registration</a>");
        sb.append(" to login and change password");
        sb.append(END_DIV_TAG);
        sb.append(END_DIV_TAG);
        
        return sb.toString();
    }
 
    private void appendMailTitle(final StringBuilder sb, final String firstName, final String lastName) {
        sb.append("<h3>");
        sb.append("Hi ");
        sb.append(firstName);
        sb.append(" ");
        sb.append(lastName);
        sb.append("</h3>");
    }
}
