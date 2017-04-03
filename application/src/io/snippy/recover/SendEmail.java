package io.snippy.recover;

/**
 * Created by Jake on 4/3/2017.
 */

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {


    public String send(String username) {
        String myUsername = "snippycodemanager@gmail.com"; //email to to sent from
        String password = "Software171!"; //pass of email

        Properties props = new Properties(); //set the email's properties
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(myUsername, password);
                    }
                });

        String code = genCode();

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("No-Reply@snippy.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username));
            message.setSubject("Password Recovery");
            message.setText("" +
                    "Hello, you have indicated that you would like to recover your snippy account.\n" +
                    "\n" +
                    "\n" +
                    "Your code is: " + code + "\n" +
                    "\n" +
                    "\n" +
                    "If this was not you, please disregard this email!\n" +
                    "\n" +
                    "------------------------------------------------------------\n" +
                    "***DO NOT RESPOND TO THIS EMAIL***\n" +
                    "\n");

            Transport.send(message);

            println("Done Sending");

            return code;
        } catch (MessagingException e) {
            printErr(e);
        }
        return code;
    }

    private String genCode() {
        int length = 8;
        String code = "";

        for (int i = 0; i < length; i++) {
            Random rand = new Random();
            int val = rand.nextInt(4);

            if (val == 0) {//special char
                code = code + (char) (33 + rand.nextInt(15));
            } else if (val == 1) {//number
                code = code + (char) (48 + rand.nextInt(10));
            } else if (val == 2) {//lowercase
                code = code + (char) (97 + rand.nextInt(26));
            } else {//uppercase
                code = code + (char) (65 + rand.nextInt(26));
            }

            //replace possible confusing characters
            code = code.replace("\"", "=");
            code = code.replace("'", "@");
            code = code.replace(",", "<");
            code = code.replace(".", ">");
            code = code.replace("/", "?");
        }
        return code;
    }

    //============DEBUG=============================
    private static boolean debug = false;

    private void print(String val) {
        if (debug) {
            System.out.print(val);
        }
    }

    private void println(String val) {
        if (debug) {
            print(val + "\n");
        }
    }

    private void printErr(String err) {
        if (debug) {
            System.err.println(err);
        }
    }

    private void printErr(Exception e) {
        if (debug) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        if (debug) {
            new SendEmail().send("email@example.com");
            for (int i = 0; i < 100; i++) {
                System.out.println(new SendEmail().genCode());
            }
        }
    }
}