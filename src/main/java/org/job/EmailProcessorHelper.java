package org.job;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import org.apache.commons.codec.binary.Base64;

import static javax.mail.Message.RecipientType.TO;

public class EmailProcessorHelper {

    public static Gmail getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, jsonFactory, getCredentials(HTTP_TRANSPORT, jsonFactory))
                .setApplicationName("Gmail Scanner")
                .build();
        ListLabelsResponse listResponse = null;
        try {
            listResponse = service.users().labels().list("me").execute();
        } catch (Exception e) {
            if (e.getMessage().contains("400")) {
                System.out.println("Tokens expired");
                String folderPath = getProjectDirectory()+"\\tokens";
                Path folderToDelete = Paths.get(folderPath);
                Files.walk(folderToDelete)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
        service = new Gmail.Builder(HTTP_TRANSPORT, jsonFactory, getCredentials(HTTP_TRANSPORT, jsonFactory))
                .setApplicationName("Gmail Scanner")
                .build();
        return service;
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, GsonFactory jsonFactory)
            throws IOException {
        // Load client secrets.
        InputStream in = AbstractJobClass.class.getResourceAsStream("/client_secret.json");
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + "E:\\client_secret.json");
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));


        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_MODIFY))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    public static void saveKeysToTextFile(Set<String> keys, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(LocalDate.now() + "\n");
            for (String key : keys) {
                writer.write(key + "\n");
            }
            System.out.println("Remaining keys appended to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTextFromFile(String filePath) {
        List<String> strList=new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
//            System.out.println("Contents of the file:");
            while ((line = reader.readLine()) != null) {
                strList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strList;
    }


    public static void addTextToFile(String filePath, String textToAppend) {
        System.out.println("inside the add text");
        if (textToAppend.equals(LocalDate.now()+"\n")){
            System.out.println("No interesting textToAppend = " + textToAppend);
            return;
        }
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {

            writer.write(textToAppend);
//            System.out.println("String appended to the file: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
                                                          String query) throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();
        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        return messages;
    }

    public static Message getMessage(Gmail service, String userId, List<Message> messages, int index)
            throws IOException {
        Message message = service.users().messages().get(userId, messages.get(index).getId()).execute();
        ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(Collections.singletonList("UNREAD"));
//        System.out.println(userId+" "+messages.get(index).getId());
        service.users().messages().modify(userId, messages.get(index).getId(), mods).execute();
        return message;
    }


    public static boolean check(List<String> storedData, String addition) {
        //                        System.out.println(jobPost[0]+" hello");
        return (!storedData.isEmpty() && storedData.contains(addition)) ||
                addition.toLowerCase().contains("lead") ||
                addition.toLowerCase().contains("principal") ||
                addition.toLowerCase().contains("staff") ||
                addition.toLowerCase().contains("stack") ||
                addition.toLowerCase().contains("devops") ||
                addition.toLowerCase().contains("principle") ||
                addition.toLowerCase().contains("front") ||
                addition.toLowerCase().contains("servicenow") ||
                addition.toLowerCase().contains("director") ||
                addition.toLowerCase().contains("automation") ||
                addition.toLowerCase().contains("manager") ||
                addition.toLowerCase().contains("snowflake") ||
                addition.toLowerCase().contains("architect") ||
                addition.toLowerCase().contains("trainer") ||
                ((!addition.toLowerCase().contains("java") && !addition.toLowerCase().contains("spring")) &&
                        (addition.toLowerCase().contains("node") ||
                                addition.toLowerCase().contains("angular") ||
                                addition.toLowerCase().contains("android") ||
                                addition.toLowerCase().contains("python") ||
                                addition.toLowerCase().contains(".net") ||
                                addition.toLowerCase().contains("dot net") ||
                                addition.toLowerCase().contains("c++") ||
                                addition.toLowerCase().contains("dotnet") ||
                                addition.toLowerCase().contains("abap") ||
                                addition.toLowerCase().contains("golang") ||
                                addition.toLowerCase().contains("go lang") ||
                                addition.toLowerCase().contains("react")));
    }


    public static void sendMail(String sub, String message1) throws GeneralSecurityException, IOException, MessagingException {


        // Encode as MIME msg
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress("koushalvibhav@gmail.com"));
        email.addRecipient(TO, new InternetAddress("koushalvibhav@gmail.com"));
        email.setSubject(sub);
        email.setText(message1);

        // Encode and wrap the MIME msg into a gmail msg
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message msg = new Message();
        msg.setRaw(encodedEmail);

        try {
            // Create send msg
            msg = EmailProcessorHelper.getService().users().messages().send("me", msg).execute();
            System.out.println("Message id: " + msg.getId());
            System.out.println(msg.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send msg: " + e.getDetails());
            } else {
                throw e;
            }
        }
    }

    public static String getProjectDirectory() {
        return System.getProperty("user.dir");
    }


}
