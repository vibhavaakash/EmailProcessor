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
public class MonsterSeviceImpl extends AbstractJobClass {


    public void extraction(String body, List<String> storedData, StringBuilder textToAppend, List<String> urls) {
//        System.out.println(body);
        Document document = Jsoup.parse(body);
        Elements table=document.select("table:has(td:matchesOwn(yrs))");
        Elements rows = table.select("tr:has(td a)");
        for (Element row:rows) {


            String addition=row.text().split("\\|")[0];
            if (!row.text().contains("yrs") || addition.contains("ARE THESE JOBS RELEVANT") || check(storedData,addition)) {
//                        System.out.println(jobPost[0]+" hello");
                continue;
            }
//            System.out.println("Row content: " + row.text()+" hello");

            String link=row.getElementsByAttribute("href").attr("href");
            if (link.contains("careerservice")) {
                System.out.println(link);
            } else {
                urls.add(link);

            }
//
            textToAppend.append(addition+"\n");
            storedData.add(addition);
        }
    }

    @Override
    public JobBoard getType() {
        return JobBoard.MONSTER;
    }

    @Override
    public List<String> getQueries() {
        return List.of("monster after:$PREVDATE -successful");
    }

    @Override
    public List<String> getFileNameAndAddition() {
        List<String> resp=new ArrayList<>();
        resp.add("/monster.txt");
        resp.add(".parts[0].parts[0]");
        return resp;
    }
}
