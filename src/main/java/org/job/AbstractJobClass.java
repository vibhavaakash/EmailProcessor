package org.job;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import io.restassured.path.json.JsonPath;
import org.apache.commons.codec.binary.Base64;

public abstract class AbstractJobClass implements AbstractJobService{

    private static final String USER_ID = "me";

    public String prevDate(Long dayMinus){
        LocalDate date=LocalDate.now().minusDays(dayMinus);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return date.format(formatter);
    }

    @Override
    public void getPagesForJobs(List<JobBoard> jobBoards, Long minusDays, List<String> urls) throws GeneralSecurityException, IOException {

        Gmail service = EmailProcessorHelper.getService();
        List<String> queries=getQueries();
        for(int k=0;k< queries.size();k++) {
            List<Message> messages = EmailProcessorHelper.listMessagesMatchingQuery(service, USER_ID, queries.get(k).replace("$PREVDATE",prevDate(minusDays)));
            System.out.println(messages.size());
            for (int cou=0;cou<messages.size();cou++) {
                if(urls.size()>30){System.out.println("Opened 30 links");break;}
                try {
                    Message message = EmailProcessorHelper.getMessage(service, USER_ID, messages, cou);
                    LocalDateTime msgTime= LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getInternalDate()), ZoneId.systemDefault());
                    JsonPath jp = new JsonPath(message.toString());
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("payload", jp.getString("payload"));
//                    System.out.println(jsonObject.toString());

                    List<String> fileNameandAdd=getFileNameAndAddition();
                    String extra = fileNameandAdd.get(1);
                    String filePath = EmailProcessorHelper.getProjectDirectory()+"\\files"+fileNameandAdd.get(0);

                    List<String> storedData = EmailProcessorHelper.getTextFromFile(filePath);
                    String bytes=jp.getString("payload"+extra+".body.data");
                    if (bytes==null || bytes.isEmpty()){
                        continue;
                    }

                    String dateToAdd="";
                    if (!storedData.get(storedData.size()-1).equals(LocalDate.now().toString())){
                        dateToAdd=LocalDate.now() +"\n";
                    }

                    System.out.println("Mail Time: "+msgTime);
                    String body = new String(Base64.decodeBase64(bytes));

//                    System.out.println(body);

                    StringBuilder textToAppend = new StringBuilder(dateToAdd);



                    extraction(body,storedData,textToAppend, urls);
//                        EmailProcessorHelper.saveKeysToTextFile(LINKEDIN_IDS,"/linkedinweb.txt");//TODO add this in linkedin section

//                        textToAppend = new StringBuilder("");


                    EmailProcessorHelper.addTextToFile(filePath, textToAppend.toString());

//                    cou++;
//                    break;

//                System.out.println(body.split("\n")[7]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                }
//                if(cou>6) break;
            }
            if(urls.size()>30)break;
//            if(cou>6) break;
        }
    }


}
