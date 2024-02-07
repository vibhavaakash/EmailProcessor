package org.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobStatusApplication {
    public static void main(String[] args) {

        SpringApplication.run(JobStatusApplication.class, args);
    }
}
