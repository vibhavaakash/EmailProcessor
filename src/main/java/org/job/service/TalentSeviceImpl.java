package org.job.service;

import java.util.ArrayList;
import java.util.List;

import org.job.AbstractJobClass;
import org.job.JobBoard;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class TalentSeviceImpl extends AbstractJobClass {


    public void extraction(String body, List<String> storedData, StringBuilder textToAppend, List<String> urls) {
        Document doc = Jsoup.parse(body);
        Elements links = doc.select("td a[href*=initiator]").select("table[valign=top]");
//        System.out.println(links);

        // Extract and print href attribute values
        for (Element link : links) {
//
            Elements jobPost= link.select("tr");

            String addition="";
            String hrefValue ="";
            if(jobPost.get(0).select("font.job__link").isEmpty()){
                addition=jobPost.get(2).text()+" "+jobPost.get(4).text();
                hrefValue=jobPost.get(2).toString().split("\"")[3];
            }
            else{
                addition=jobPost.get(0).text()+" "+jobPost.get(2).text();
                hrefValue=jobPost.get(0).toString().split("\"")[3];
            }

            if (storedData.contains(addition) ||
                    addition.toLowerCase().contains("lead") ||
                    addition.toLowerCase().contains("principal") ||
                    addition.toLowerCase().contains("staff") ||
                    addition.toLowerCase().contains("stack") ||
                    addition.toLowerCase().contains("devops") ||
                    addition.toLowerCase().contains("principle") ||
                    addition.toLowerCase().contains("front") ||
                    addition.toLowerCase().contains("servicenow") ||
                    addition.toLowerCase().contains("angular") ||
                    addition.toLowerCase().contains("director") ||
                    addition.toLowerCase().contains("automation") ||
                    addition.toLowerCase().contains("manager") ||
                    addition.toLowerCase().contains("accountant") ||
                    addition.toLowerCase().contains("president") ||
                    addition.toLowerCase().contains("databricks") ||
                    addition.toLowerCase().contains("snowflake") ||
                    addition.toLowerCase().contains("analyst") ||
                    addition.toLowerCase().contains("murex") ||
                    addition.toLowerCase().contains("tableau") ||
                    !(
                            addition.toLowerCase().contains("java") || addition.toLowerCase().contains("spring") || addition.toLowerCase().contains("j2ee") || addition.toLowerCase().contains("backend") || addition.toLowerCase().contains("engineer") || addition.toLowerCase().contains("developer")
                    )) {

                continue;
            }
            System.out.println(addition+" hello");
            System.out.println(hrefValue);
//            System.out.println("Matching href value: " + hrefValue + cou);
            urls.add(hrefValue);

            textToAppend.append(addition+"\n");
            storedData.add(addition);
        }
    }

    @Override
    public JobBoard getType() {
        return JobBoard.TALENT;
    }

    @Override
    public List<String> getQueries() {
        return List.of("Talent.com after:$PREVDATE");
    }

    @Override
    public List<String> getFileNameAndAddition() {
        List<String> resp=new ArrayList<>();
        resp.add("/talent.txt");
        resp.add("");
        return resp;
    }
}
