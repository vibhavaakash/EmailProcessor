package org.job.service;

import java.util.ArrayList;
import java.util.List;

import org.job.AbstractJobClass;
import org.job.JobBoard;
import org.springframework.stereotype.Service;

import static org.job.EmailProcessorHelper.check;

@Service
public class IndeedSeviceImpl extends AbstractJobClass {

    public void extraction(String body, List<String> storedData, StringBuilder textToAppend, List<String> urls) {
        for (int i = 0; i < body.split("\n\n").length; i++) {
            String[] jobPost = body.split("\n\n")[i].split("\n");

            int start = 0;
            int end = 1;
            if (jobPost[start]!=null && jobPost[start].isEmpty()){
                start=1;
                end=2;
            }


            if (jobPost.length<3 || check(storedData, jobPost[start] + " "+ jobPost[end])) continue;

//            for (int j = 0; j < jobPost.length; j++) {
//                System.out.println(jobPost[j].replaceAll("amp;","")+" hello "+j);
//            }
            System.out.println(jobPost[start] + " "+ jobPost[end]);

//            String[] mlPrediction=CommandExecution.getCommandOutput(jobPost[start]);
//            Double mlPredictionNumber=Double.parseDouble(mlPrediction[mlPrediction.length-1]);
//            if(mlPredictionNumber==0.0){
            urls.add(jobPost[jobPost.length - 1].replaceAll("amp;",""));
//            }else {
//                addTextToFile("D:\\mldropped.txt",jobPost[start]+" - "+jobPost[jobPost.length - 1].replaceAll("amp;",""));
//            }

            textToAppend.append(jobPost[start]).append(" ").append(jobPost[end]).append("\n");
            storedData.add(jobPost[start]+" "+jobPost[end]);
//            textToAppend.append(jobPost[start]).append(" - \n");
//            storedData.add(jobPost[start]);
//                    for(int j=0;j<jobPost.split("\n").length;j++){

//                    }
        }
    }



    @Override
    public JobBoard getType() {
        return JobBoard.INDEED;
    }

    @Override
    public List<String> getQueries() {
        return List.of("indeed after:$PREVDATE -submitted -interest");
    }

    @Override
    public List<String> getFileNameAndAddition() {
        List<String> resp=new ArrayList<>();
        resp.add("/indeed.txt");
        resp.add(".parts[0]");
        return resp;
    }
}
