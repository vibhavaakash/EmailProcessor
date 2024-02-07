package org.job.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.job.AbstractJobClass;
import org.job.JobBoard;
import org.springframework.stereotype.Service;
import static org.job.EmailProcessorHelper.check;
@Service
public class LinkedinSeviceImpl extends AbstractJobClass {


    public void extraction(String body, List<String> storedData, StringBuilder textToAppend, List<String> urls) {
        String[] jobPosts=body.split("---------------------------------------------------------");
//        System.out.println(String.join("break", jobPosts));
        for (int i = 0; i < jobPosts.length-1; i++) {

            String[] jobPost = jobPosts[i].split("\n");
            System.out.println(Arrays.toString(jobPost));
            if(jobPost.length<6){
                System.out.println("The jobpost not read:"+ Arrays.toString(jobPost));
                continue;
            }
            int start = 3;
            int end = 4;
            if(i==0){
                start=4;
                end=5;
            }
            String star=jobPost[start].replaceAll("\\r\\n|\\r|\\n", "");
            String endd=jobPost[end].replaceAll("\\r\\n|\\r|\\n", "");
            String link=jobPost[jobPost.length - 2].split("View job: ")[1];

            // Create a Matcher object
            Matcher matcher = Pattern.compile("/view/(\\d+)/").matcher(link);

            // Check if the pattern is found
            if (matcher.find()) {
                // Extract the number from the matched group
                String extractedNumber = matcher.group(1);
//                LINKEDIN_IDS.add(extractedNumber);//TODO Add support for linkedin
                // Print the extracted number
                System.out.println("Extracted Number: " + extractedNumber);
            }

            if (jobPost.length<7 || check(storedData,star + " " + endd)) {
                continue;
            }
            System.out.println(star+" "+endd);
//            System.out.println(Arrays.toString(jobPost));
//            EmailProcessorHelper.mlModel(star);

            if(jobPost[jobPost.length - 2].split("View job: ").length<=1){
                continue;
            };

//            System.out.println(link.substring(0,link.length()-1).replace("%28","(").replace("%29",")"));
            urls.add(link.substring(0,link.length()-1).replace("%28","(").replace("%29",")"));


            textToAppend.append(star).append(" ").append(endd).append("\n");
            storedData.add(star+" "+endd);
        }
    }

    @Override
    public JobBoard getType() {
        return JobBoard.LINKEDIN;
    }

    @Override
    public List<String> getQueries() {
        List<String> queries = new java.util.ArrayList<>();
        queries.add("linkedin from:jobs-noreply@linkedin.com after:$PREVDATE subject:new");
        queries.add("linkedin from:jobalerts-noreply@linkedin.com after:$PREVDATE");
        queries.add("linkedin from:jobs-listings@linkedin.com after:$PREVDATE");
        return queries;
    }

    @Override
    public List<String> getFileNameAndAddition() {
        List<String> resp=new ArrayList<>();
        resp.add("/linkedin.txt");
        resp.add(".parts[0]");
        return resp;
    }
}
