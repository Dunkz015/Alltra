package com.app.alltra.utils;

import android.app.Activity;
import android.content.Context;

import com.app.alltra.R;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI {
    //Need INTERNET permission

    //Variables
    private final Context context;
    private final String email;
    private final String subject;
    private final String message;

    //Constructor
    public JavaMailAPI(Context mContext, String mEmail, String mSubject, String mMessage) {
        this.context = mContext;
        this.email = mEmail;
        this.subject = mSubject;
        this.message = mMessage;
    }

    public void sendEmail() {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        //Authenticating the password
        Session mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Utils.EMAIL, Utils.PASSWORD);
                    }
                });

        // Using an Executor to perform network operations in the background
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                //Creating MimeMessage object
                MimeMessage mm = new MimeMessage(mSession);

                //Setting sender address
                mm.setFrom(new InternetAddress(Utils.EMAIL));
                //Adding receiver
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                //Adding subject
                mm.setSubject(subject);
                //Adding message
                mm.setText(message);
                //Sending email
                Transport.send(mm);

            } catch (MessagingException e) {
                // handle exception
                ((Activity) context).runOnUiThread(() -> Dialogs.showErrorDialog(context,
                        context.getString(R.string.generic_error)));
                e.printStackTrace();
            }
        });
    }

    public static class Utils {
        //This is your from email
        public static final String EMAIL = "alltra.app.sender@gmail.com";

        //This is your from email password
        public static final String PASSWORD = "ihxg hlyk epnj lzry";
    }
}
