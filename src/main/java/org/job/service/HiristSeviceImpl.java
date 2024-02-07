package org.job.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.job.AbstractJobClass;
import org.job.JobBoard;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class HiristSeviceImpl extends AbstractJobClass {


    public void extraction(String body, List<String> storedData, StringBuilder textToAppend, List<String> urls) {
        Document doc = Jsoup.parse(body);
        Elements links = doc.select("li");

        // Extract and print href attribute values
        for (Element link : links) {
            String hrefValue = link.select("a").attr("href");
            String addition= Arrays.toString(Arrays.copyOfRange(link.text().split(" - "),1,link.text().split(" - ").length));
            int min=Integer.parseInt(addition.split("\\(")[1].split("yrs")[0].split("-")[0]);
            System.out.println(min);
            if (storedData.contains(addition) ||
                    min>=4 ||
                    addition.toLowerCase().contains("principal") ||
                    addition.toLowerCase().contains("staff") ||
                    addition.toLowerCase().contains("principle") ||
                    addition.toLowerCase().contains("front") ||
                    addition.toLowerCase().contains("servicenow") ||
                    addition.toLowerCase().contains("angular") ||
                    addition.toLowerCase().contains("director") ||
                    addition.toLowerCase().contains("automation") ||
                    addition.toLowerCase().contains("test") ||
                    addition.toLowerCase().contains("analyst") ||
                    addition.toLowerCase().contains("flutter") ||
                    addition.toLowerCase().contains("snowflake") ||
                    addition.toLowerCase().contains("manager") ||
                    addition.toLowerCase().contains("murex") ||
                    addition.toLowerCase().contains("president") ||
                    !(
                            addition.toLowerCase().contains("java") || addition.toLowerCase().contains("spring") || addition.toLowerCase().contains("j2ee") || addition.toLowerCase().contains("backend") || addition.toLowerCase().contains("engineer") || addition.toLowerCase().contains("developer")
                    )) {

                continue;
            }
            System.out.println(addition+" hello");
//            System.out.println("Matching href value: " + hrefValue + cou);
            urls.add(hrefValue);

            textToAppend.append(addition+"\n");
            storedData.add(addition);
        }
    }

    @Override
    public JobBoard getType() {
        return JobBoard.HIRIST;
    }

    @Override
    public List<String> getQueries() {
        return List.of("hirist.com after:$PREVDATE");
    }

    @Override
    public List<String> getFileNameAndAddition() {
        List<String> resp=new ArrayList<>();
        resp.add("/hirist.txt");
        resp.add(".parts[0]");
        return resp;
    }
}
