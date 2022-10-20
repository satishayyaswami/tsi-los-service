package in.tsiconsulting.accelerator.solutions.jobs;

import in.tsiconsulting.accelerator.system.core.REST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Proto implements REST {

    private static final String API_PROVIDER = "Proto";

    private static final String API_NAME = "los";

    private static final String FUNCTION = "_func";
    private static final String DATA = "_data";

    private static final String POST_CANDIDATE = "post_candidate";
    private static final String POST_JOB = "post_job";
    private static final String POST_CANDIDATE_SCORE_DEFINITION = "post_candidate_score_def";
    private static final String POST_CANDIDATE_SCORE_DATA = "post_candidate_score_data";


    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public boolean validate(String method, HttpServletRequest req, HttpServletResponse res) {
        return false;
    }
}
