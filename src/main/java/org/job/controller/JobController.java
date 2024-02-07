package org.job.controller;

import java.util.ArrayList;
import java.util.List;

import org.job.AbstractJobService;
import org.job.JobBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class JobController {

    @Autowired
    private List<AbstractJobService> jobServices;


    @PostMapping("/getcardstatus")
    public @ResponseBody ResponseEntity<?> getCardStatus(@RequestBody List<JobBoard> jobBoards, @RequestParam Long minusDays) {
        List<String> resp=new ArrayList<>();
        try {

            for(AbstractJobService jobService:jobServices){
                if (!jobBoards.contains(jobService.getType()))continue;
                jobService.getPagesForJobs(jobBoards,minusDays,resp);
                if (resp.size()>30){
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(resp, HttpStatus.ACCEPTED);
    }

    @GetMapping("/monitor")
    public @ResponseBody ResponseEntity<?> monitor() {
        return new ResponseEntity<>(List.of("This is good"), HttpStatus.ACCEPTED);
    }

}
