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
import static org.job.EmailProcessorHelper.check;
@Service
public class NaukriSeviceImpl extends AbstractJobClass {


    public void extraction(String body, List<String> storedData, StringBuilder textToAppend, List<String> urls) {
        Document doc = Jsoup.parse(body);
        Elements links = doc.select("a[href*=www.naukri.com/jd/]");

        // Extract and print href attribute values
        for (Element link : links) {
            String hrefValue = link.attr("href");
            String addition=hrefValue.split("/")[4].split("\\?")[0];
            if (storedData.contains(addition) || check(storedData, addition)) {

                continue;
            }
            System.out.println(addition+" hello");
//            System.out.println("Matching href value: " + hrefValue + cou);
            urls.add(hrefValue.replaceAll("\\^","%255E"));

            textToAppend.append(addition+"\n");
            storedData.add(addition);
        }
    }

    @Override
    public JobBoard getType() {
        return JobBoard.NAUKRI;
    }

    @Override
    public List<String> getQueries() {
        return List.of("naukri from:naukrialerts@naukri.com after:$PREVDATE");
    }

    @Override
    public List<String> getFileNameAndAddition() {
        List<String> resp=new ArrayList<>();
        resp.add("/naukri.txt");
        resp.add(".parts[0].parts[1]");
        return resp;
    }
}
