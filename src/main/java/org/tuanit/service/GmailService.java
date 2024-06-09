package org.tuanit.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tuanit.model.EmailProfile;
import org.tuanit.model.MailSenderModel;
import org.tuanit.model.swing.BaseJListCell;
import org.tuanit.util.Helper;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

@Component
public class GmailService {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Autowired
    AccountService accountService;
    @Autowired
    ExecutingService executingService;
    @Autowired
    HttpTransport httpTransport;


    public void sendMail(JFrame jFrame, List<EmailProfile> mailList, List<String> to, MailSenderModel mailSenderModel) {
        List<List<String>> toSplitedList = Helper.splitList(to, mailList.size());

        for (EmailProfile emailProfile : mailList) {
            Credential credential = accountService.getCredentials(emailProfile.getProfile());
            executingService.execute(jFrame, emailProfile.getProfile(), jFrame.getTitle(), executingStatus -> {
                Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
//                        .setApplicationName(APPLICATION_NAME)
                        .build();
                List<String> list = toSplitedList.get(mailList.indexOf(emailProfile));
                Helper.forEachWrapper(list, executingStatus, (emailReceiver, indexInfo) -> {
                    List<String> files = mailSenderModel.getFiles();
                    try {
                        MimeMessage message = createEmailWithMultipleAttachments(emailReceiver, emailProfile.getEmail(),
                                emailProfile.getNickname(), mailSenderModel.getSubject(), mailSenderModel.getContent(), mailSenderModel.getIsHtml(), mailSenderModel.getFiles());
                        sendMessage(service, message);
                        executingStatus.logs.addElement("Đã gửi: " + emailReceiver);
                        Helper.sleepExecute(mailSenderModel.getSleep(), executingStatus, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        executingStatus.logs.addElement("Lỗi: " + e.getMessage());
                    }
                });
            });
        }
    }


    private MimeMessage createEmail(String to, String from, String nickname, String subject, String bodyText) throws MessagingException, UnsupportedEncodingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(StringUtils.isNotBlank(nickname) ? new InternetAddress(from, nickname) : new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    private MimeMessage createEmailWithMultipleAttachments(String to, String from, String nickname, String subject, String bodyText, Boolean isHtml, List<String> filePaths) throws MessagingException, IOException {
//        if (filePaths.isEmpty()) {
//            return createEmail(to, from, nickname, subject, bodyText);
//        }
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(StringUtils.isNotBlank(nickname) ? new InternetAddress(from, nickname) : new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        if (isHtml)
            mimeBodyPart.setContent(bodyText, "text/html");
        else
            mimeBodyPart.setContent(bodyText, "text/plain");

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        for (String filePath : filePaths) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(new File(filePath));
            multipart.addBodyPart(attachmentBodyPart);
        }

        email.setContent(multipart);
        return email;
    }

    private Message sendMessage(Gmail service, MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        message = service.users().messages().send("me", message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

}
