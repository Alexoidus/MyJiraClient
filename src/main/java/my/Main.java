package my;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.*;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String EPIC_LINK_FIELD = "customfield_10102";

    public static void main(String[] args) throws Exception {
        try (JiraRestClient jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(URI.create("http://localhost:9090"), "", "")) {
            Iterable<IssueType> issueTypes = jiraRestClient.getMetadataClient().getIssueTypes().get(60, TimeUnit.SECONDS);
            issueTypes.forEach(System.out::println);

            SearchRestClient searchClient = jiraRestClient.getSearchClient();
            SearchResult searchResult = searchClient.searchJql("project = AE").claim();
            for (Issue foundIssue : searchResult.getIssues()) {
                System.out.println("found issue = " + foundIssue);
            }

            IssueCreator issueCreator = new IssueCreator();
            issueCreator.create("./src/main/resources/issues.csv");

            IssueRestClient issueClient = jiraRestClient.getIssueClient();

            Issue issue = issueClient.getIssue("AE-1").claim();
            System.out.println("issue = " + issue);

            final var issueBuilder = new IssueInputBuilder();
            issueBuilder.setProjectKey("AE");
            issueBuilder.setIssueTypeId(10001L);
            issueBuilder.setSummary("Test summary");
            issueBuilder.setDescription("Test Description");
            //issueBuilder.setFieldInput(new FieldInput("parent", ComplexIssueInputFieldValue.with("key", "AE-1")));
            issueBuilder.setFieldInput(new FieldInput(EPIC_LINK_FIELD, "AE-5"));
            //issueBuilder.setFieldInput(new FieldInput("timetracking", "1d"));
            //issueBuilder.setComponentsNames(List.of("Backend"));

            Map<String, Object> fieldsMap = new HashMap<>();
            fieldsMap.put("originalEstimate", "1d");
            fieldsMap.put("remainingEstimate", "1d");
            issueBuilder.setFieldValue("timetracking", new ComplexIssueInputFieldValue(fieldsMap));

            IssueInput newIssue = issueBuilder.build();

            String createdIssueKey = issueClient.createIssue(newIssue).claim().getKey();

            System.out.println("Created issue key: " + createdIssueKey);

            LinkIssuesInput linkIssuesInput = new LinkIssuesInput("AE-1", createdIssueKey, "Проблема, разделенная");
            issueClient.linkIssue(linkIssuesInput).claim();
            System.out.println("Created link " + linkIssuesInput);
        }
    }
}