package com.github.cuter44.nyafx.mail;

import java.util.Properties;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Message.RecipientType;

/**
 * @require javamail-1.6+
 */
public class Mailer
{
    protected Properties config;

    protected String personal;
    protected String address;

    protected Session session;

  // CONSTRUCT
    public Mailer()
    {
        try
        {
            InputStreamReader is = new InputStreamReader(
                this.getClass()
                    .getResourceAsStream("/javamail.properties"),
                "utf-8"
            );

            this.config = new Properties();
            this.config.load(is);
            is.close();
        }
        catch (UnsupportedEncodingException ex)
        {
            throw(new RuntimeException(ex));
        }
        catch (IOException ex)
        {
            throw(new RuntimeException(ex));
        }

        final String u = this.config.getProperty("mail.smtp.username");
        final String p = this.config.getProperty("mail.smtp.password");

        Authenticator auth = new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return(
                    new PasswordAuthentication(u, p)
                );
            }
        };

        this.session = Session.getDefaultInstance(this.config, auth);

        this.personal = this.config.getProperty("mail.smtp.personal");
        this.address = this.config.getProperty("mail.smtp.address");

        return;
    }

    public Mailer(Properties config)
    {

        this.config = config;

        final String u = this.config.getProperty("mail.smtp.username");
        final String p = this.config.getProperty("mail.smtp.password");

        Authenticator auth = new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return(
                    new PasswordAuthentication(u, p)
                );
            }
        };

        this.session = Session.getDefaultInstance(this.config, auth);

        this.personal = this.config.getProperty("mail.smtp.personal");
        this.address = this.config.getProperty("mail.smtp.address");

        return;
    }

  // SINGLETON
    private static class Singleton
    {
        public static final Mailer INSTANCE = new Mailer();
    }

    public static Mailer getInstance()
    {
        return(Singleton.INSTANCE);
    }

  // MESSAGE
    public MimeMessage createMimeMessage()
        throws MessagingException, AddressException
    {
        try
        {
            MimeMessage msg = new MimeMessage(this.session);

            msg.setFrom(
                new InternetAddress(
                    this.address,
                    MimeUtility.encodeText(this.personal, "UTF-8", "B")
                )
            );

            return(msg);
        }
        catch (UnsupportedEncodingException ex)
        {
            // never occured
            return(null);
        }
    }

    public MimeMessage createMimeMessage(String to)
        throws MessagingException, AddressException
    {
        MimeMessage msg = this.createMimeMessage();

        msg.addRecipient(
            RecipientType.TO,
            new InternetAddress(to)
        );

        return(msg);
    }

    public void send(MimeMessage msg)
        throws MessagingException
    {
        Transport.send(msg);
    }

  // INSTANT SEND
    public void sendHTMLMail(String to, String subject, String content)
        throws MessagingException, AddressException
    {
        // CREATE
        MimeMessage msg = this.createMimeMessage(to);

        // SUBJECT
        msg.setSubject(subject);

        // BODY
        MimeMultipart multiPart = new MimeMultipart("alternative");

        MimeBodyPart part1 = new MimeBodyPart();
        part1.setText(content, "UTF-8");
        part1.setHeader("Content-Type","text/html; charset=\"utf-8\"");

        multiPart.addBodyPart(part1);

        msg.setContent(multiPart);

        // SEND
        this.send(msg);

        return;
    }
}
