package org.tuanit;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class GmailSenderApp {
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String ACCOUNTS_FILE_PATH = "accounts.ser";

    private static final java.util.List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private HashMap<String, AccountInfo> credentialMap = new HashMap<>();

    private AccountInfo getCredentials(String user) throws IOException, GeneralSecurityException {
        NetHttpTransport.Builder httpTransportBuilder = new NetHttpTransport.Builder();
        httpTransportBuilder.doNotValidateCertificate();
        InputStream in = GmailSenderApp.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransportBuilder.build(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AccountInfo(new AuthorizationCodeInstalledApp(flow, receiver).authorize(user));
    }

    private void loadAccounts() {
        try (FileInputStream fileIn = new FileInputStream(ACCOUNTS_FILE_PATH);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Map<String, AccountInfo> loadedCredentials = (Map<String, AccountInfo>) in.readObject();
            for (Map.Entry<String, AccountInfo> entry : loadedCredentials.entrySet()) {
                String emailAddress = entry.getKey();
                AccountInfo credential = entry.getValue();
                credentialMap.put(emailAddress, credential);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveAccounts() {
        try (FileOutputStream fileOut = new FileOutputStream(ACCOUNTS_FILE_PATH);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            // Save only the email addresses and corresponding credentials
            HashMap<String, AccountInfo> emailCredentials = new HashMap<>();
            for (HashMap.Entry<String, AccountInfo> entry : credentialMap.entrySet()) {
                String emailAddress = entry.getKey();
                AccountInfo credential = entry.getValue();
                emailCredentials.put(emailAddress, credential);
            }
            out.writeObject(emailCredentials);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        loadAccounts();
    }

    public void shutdown() {
        saveAccounts();
    }

    public void addAccount(String user) throws GeneralSecurityException, IOException {
        AccountInfo credential = getCredentials( user);
        credentialMap.put(user, credential);
        saveAccounts();
    }

    public void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("To:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField toText = new JTextField(20);
        toText.setBounds(100, 20, 165, 25);
        panel.add(toText);

        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setBounds(10, 50, 80, 25);
        panel.add(subjectLabel);

        JTextField subjectText = new JTextField(20);
        subjectText.setBounds(100, 50, 165, 25);
        panel.add(subjectText);

        JLabel bodyLabel = new JLabel("Body:");
        bodyLabel.setBounds(10, 80, 80, 25);
        panel.add(bodyLabel);

        JTextArea bodyText = new JTextArea();
        bodyText.setBounds(100, 80, 165, 100);
        panel.add(bodyText);

        JLabel accountLabel = new JLabel("Account:");
        accountLabel.setBounds(10, 190, 80, 25);
        panel.add(accountLabel);

        JComboBox<String> accountComboBox = new JComboBox<>();
        accountComboBox.setBounds(100, 190, 165, 25);
        panel.add(accountComboBox);

        JButton addButton = new JButton("Add Account");
        addButton.setBounds(10, 230, 150, 25);
        panel.add(addButton);

        JButton sendButton = new JButton("Send");
        sendButton.setBounds(170, 230, 80, 25);
        panel.add(sendButton);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = JOptionPane.showInputDialog("Enter user identifier:");
                if (user != null && !user.isEmpty()) {
                    try {
                        addAccount(user);
                        accountComboBox.addItem(user);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedUser = (String) accountComboBox.getSelectedItem();
                    sendMessage(selectedUser, toText.getText(), subjectText.getText(), bodyText.getText());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @SneakyThrows
    private void sendMessage(String user, String to, String subject, String bodyText) throws IOException, GeneralSecurityException {
        Credential credential = credentialMap.get(user).getCredential();
        if (credential == null) {
            throw new RuntimeException("No credentials found for user: " + user);
        }

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        MimeMessage email = createEmail(to, "me", subject, bodyText);
        sendMessage(service, "me", email);
    }

    public MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));

        String[] toAddresses = to.split(",");
        for (String toAddress : toAddresses) {
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toAddress.trim()));
        }

        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public Message sendMessage(Gmail service, String userId, MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = com.google.api.client.util.Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }
}

