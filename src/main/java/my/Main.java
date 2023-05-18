package my;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        try (JiraRestClient jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(URI.create("http://localhost:9090"), "", "")) {
            Iterable<IssueType> issueTypes = jiraRestClient.getMetadataClient().getIssueTypes().get(60, TimeUnit.SECONDS);
            issueTypes.forEach(System.out::println);

            IssueRestClient issueClient = jiraRestClient.getIssueClient();

            Issue issue = issueClient.getIssue("AE-1").claim();
            System.out.println("issue = " + issue);

            final var issueBuilder = new IssueInputBuilder("AE", 10003L, "Issue created by REST API Client");
            issueBuilder.setProjectKey("Project1");
            issueBuilder.setDescription(">> Test Description");
            issueBuilder.setSummary("Test summary");
            Map<String, Object> parentValueMap = new HashMap<String, Object>();
            parentValueMap.put("key", "SOMEISSUE-234");
            FieldInput parentField = new FieldInput("parent", new ComplexIssueInputFieldValue(parentValueMap));
            issueBuilder.setFieldInput(parentField);

            Map<String, Object> customField = new HashMap<String, Object>();
            customField.put("value", "someValue");//This is some custom field value on the subtask
            customField.put("id", "12345");//This is the id of the custom field. You can know this by calling REST GET for a manually created sub-task
            issueBuilder.setFieldValue("customfield_12345",  new ComplexIssueInputFieldValue(customField));//here again you have to query an existing subtask to know the "customfield_*" value

            IssueInput newIssue = issueBuilder. build();

            String issueKey = issueClient.createIssue(newIssue).claim().getKey();
            System.out.println("Created issue key: " + issueKey);
        }
    }
}