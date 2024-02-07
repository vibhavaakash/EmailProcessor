package org.job;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface AbstractJobService{

    public void getPagesForJobs(List<JobBoard> jobBoards, Long minusDays, List<String> urls) throws GeneralSecurityException, IOException;

    public JobBoard getType();

    public List<String> getQueries();

    public List<String> getFileNameAndAddition();

    public void extraction(String body, List<String> storedData, StringBuilder textToAppend, List<String> urls);

}
