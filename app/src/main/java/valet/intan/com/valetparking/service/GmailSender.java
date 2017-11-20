package valet.intan.com.valetparking.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import valet.intan.com.valetparking.util.PrefManager;

/**
 * Created by fadlymunandar on 10/4/17.
 */

public class GmailSender {

    private static final String sender = "intanmvslog@gmail.com";
    private static final String password = "puimvs1234";
    private static final String recipient = "mvsintan@gmail.com";


    static void send(Context context) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender,password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(recipient));
            message.setSubject(getSubject(context));

            Multipart multipart = getLogFiles();

            if (multipart == null) {
                broadcastSendReportStatus("Log report empty. Sending report aborted.",context);
                return;
            }

            multipart.addBodyPart(getBodyPart(context));
            message.setContent(multipart );

            Transport.send(message);

            broadcastSendReportStatus("Report sent", context);
            LoggingUtils.getInstance(context).removeLogFile();

        }catch (MessagingException e) {
            e.printStackTrace();
            broadcastSendReportStatus("Send report failed", context);
        }

    }

    private static String getSubject(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String currentDate = sdf.format(new Date());
        String lobby = PrefManager.getInstance(context).getDefaultDropPointName();
        String site = PrefManager.getInstance(context).getSiteName();

        return  "Log file " + currentDate + " " + lobby + "-" + site;
    }

    private static Multipart getLogFiles() throws MessagingException {
        Multipart multipart = new MimeMultipart();

        String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folderPath = diskPath + File.separatorChar + "logger";

        File folder = new File(folderPath);

        if (folder.exists()) {
            File[] files = folder.listFiles();

            if (files == null || files.length ==0){
                return null;
            }

            for (File file : files) {
                MimeBodyPart bodyPart = new MimeBodyPart();

                String fileName = file.getName();
                String filePath = file.getAbsolutePath();
                DataSource dataSource = new FileDataSource(filePath);
                bodyPart.setDataHandler(new DataHandler(dataSource));
                bodyPart.setFileName(fileName);
                multipart.addBodyPart(bodyPart);
            }
        }else {
            return null;
        }

        return multipart;
    }

    private static BodyPart getBodyPart(Context context) throws MessagingException {
        BodyPart bodyPart = new MimeBodyPart();
        StringBuilder sb = new StringBuilder();

        String userName = PrefManager.getInstance(context).getUserName();
        String lobby = PrefManager.getInstance(context).getDefaultDropPointName();
        String site = PrefManager.getInstance(context).getSiteName();
        String deviceId = PrefManager.getInstance(context).getRemoteDeviceId();
        String imei = PrefManager.getInstance(context).getDeviceId();
        String expToken = PrefManager.getInstance(context).getExpiredToken();
        String lastLogin = PrefManager.getInstance(context).getLastLoginDate();
        String versionApp = null;
        try {
            versionApp = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sb.append("Username: ").append(userName).append("\n")
                .append("Lobby: ").append(lobby).append("\n")
                .append("Site: ").append(site).append("\n")
                .append("Device id: ").append(deviceId).append("\n")
                .append("Imei: ").append(imei).append("\n")
                .append("Token exp. date: ").append(expToken).append("\n")
                .append("Last login: ").append(lastLogin).append("\n")
                .append("App version: ").append(versionApp).append("\n");

        bodyPart.setText(sb.toString());

        return bodyPart;
    }

    private static void broadcastSendReportStatus(String status, Context context) {
        Intent intent = new Intent();
        intent.setAction(LoggingUtils.ACTION_STATUS_SEND_REPORT);
        intent.putExtra(LoggingUtils.EXTRA_STATUS_SEND_REPORT, status);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
