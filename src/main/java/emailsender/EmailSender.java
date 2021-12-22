package emailsender;

import emailgateway.EmailGateway;
import emailgateway.EmailGatewayInterface;

import javax.mail.MessagingException;
import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;

public class EmailSender {
    private EmailGatewayInterface emailSender;

    public EmailSender() {
        emailSender = new EmailGateway();
    }

    public EmailGatewayInterface getEmailSender() {
        return emailSender;
    }

    public void setEmailSender(EmailGatewayInterface emailSender) {
        this.emailSender = emailSender;
    }

    public void sendVerification(String email, String token, boolean isANewUser) throws NamingException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, MessagingException {
        emailSender.sendVerification(email, token, isANewUser);
    }
}
