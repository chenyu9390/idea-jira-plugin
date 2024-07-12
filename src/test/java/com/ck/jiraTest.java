package com.ck;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.Var;
import com.google.protobuf.compiler.PluginProtos;
import net.rcarz.jiraclient.*;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class jiraTest {

    private final String STAGE_FIELD = "customfield_13504";
    private final String BUG_FIELD = "customfield_12823";

    private final String user_name = "neyo.chen";
    private final String password = "quec910926!";

    private final String ISSUE_KEY = "PMS-19";

    JiraClient client;

    @Before
    public void init(){
        BasicCredentials creds = new BasicCredentials(user_name, password);
        client = new JiraClient("http://192.168.29.116:8080",creds);
    }

    @Test
    public void getMataData() throws JiraException, RestException, IOException {
        String url = "http://192.168.29.116:8080/rest/api/2/issue/createmeta?projectKeys=PMS";
        JSON json = client.getRestClient().get(URI.create(url));
        System.out.println(json);
    }

    @Test
    public void getIssue() throws JiraException {
        Issue issue = client.getIssue("HWAEC-47");
        System.out.println(JSONUtil.toJsonStr(issue));
        System.out.println(issue.getAssignee().toString());
        System.out.println(issue.getIssueType());
        System.out.println(issue.getStatus());
        System.out.println(issue.getPriority());
        System.out.println(issue.getLabels());
        System.out.println(issue.getVotes());
        for (IssueLink issueLink : issue.getIssueLinks()) {
            System.out.println(issueLink);
        }
        System.out.println(issue.getIssueLinks());
    }

    @Test
    public void getIssueType() throws JiraException {
        List<IssueType> typeList = client.getIssueTypes();
        for (IssueType type : typeList) {
            System.out.println(type);
        }
    }
    @Test
    public void createIssue() throws JiraException {
        Issue issue = client.createIssue("PMS", "故事")
                .field(Field.PROJECT, "PMS")
                .field(Field.DESCRIPTION, "这是一个测试问题")
                .field(Field.SUMMARY, "测试")
                .execute();
        System.out.println(issue);
        issue.addComment("ddddd");
    }

    @Test
    public void createChildIssue() throws JiraException {
        Issue issue = client.createIssue("PMS", "ST-BUG")
                .field(Field.PROJECT, "PMS")
                .field(Field.DESCRIPTION, "这是一个测试问题")
                .field(Field.SUMMARY, "父任务")
                .execute();
        System.out.println(issue);
        Issue subTask = issue.createSubtask()
                .field(Field.SUMMARY, "子任务")
                .execute();
        System.out.println(subTask);

    }

    /**
     * 添加评论
     *
     * @throws JiraException jira异常
     */
    @Test
    public void addComment() throws JiraException {
        Issue issue = client.getIssue(ISSUE_KEY);
        issue.addComment("添加评论");
    }

    /**
     * 添加附件
     *
     * @throws JiraException jira异常
     */
    @Test
    public void addAttachment() throws JiraException {
        Issue issue = client.getIssue(ISSUE_KEY);
        issue.addAttachment(new File("C:\\Users\\albert.chen\\Desktop\\1.xlsx"));
    }

    /**
     * 添加Watcher
     *
     * @throws JiraException jira异常
     */
    @Test
    public void addWatch() throws JiraException {
        Issue issue = client.getIssue(ISSUE_KEY);
        issue.addWatcher("albert.chen");
    }

    /**
     * 处理流程
     *
     * @throws JiraException jira异常
     * @throws RestException rest异常
     * @throws IOException   IOException
     */
    @Test
    public void getTransition() throws JiraException, RestException, IOException {
        String url = "http://192.168.29.116:8080/rest/api/2/issue/"+"SYSTEM-236"+"/transitions";
        JSON json = client.getRestClient().get(URI.create(url));
        System.out.println(json);
    }

    /**
     * 流程扭转
     *
     * @throws JiraException jira异常
     */
    @Test
    public void transition() throws JiraException {
        Issue issue = client.getIssue("SYSTEM-236");
        //当前阶段
        issue.transition().execute("接受");
    }

    /**
     * 客户自定义字段
     *
     * @throws JiraException      吉拉例外
     * @throws URISyntaxException urisyntax异常
     * @throws RestException      休息例外
     * @throws IOException        IOException
     */
    @Test
    public void customerField() throws JiraException, URISyntaxException, RestException, IOException {
        //获取自定义字段
        JSON json = client.getRestClient().get(new URI("http://192.168.29.116:8080/rest/api/2/customFields?projectIds=HWAE-CCMS&maxResults=600"));
        System.out.println(json);
        System.out.println("---------------");
        Issue issue = client.getIssue(ISSUE_KEY);
        //修复的版本
        List<Version> versions = Field.getResourceArray(Version.class, issue.getField(Field.FIX_VERSIONS), client.getRestClient());
        System.out.println(versions);
        //项目阶段
        CustomFieldOption option = Field.getResource(CustomFieldOption.class, issue.getField(STAGE_FIELD), client.getRestClient());
        System.out.println(option);
        //BUG验证等级
        option = Field.getResource(CustomFieldOption.class, issue.getField(BUG_FIELD), client.getRestClient());
        System.out.println(option);
        //类型
        IssueType type = Field.getResource(IssueType.class, issue.getField(Field.ISSUE_TYPE), client.getRestClient());
        System.out.println(type);
        //优先级
        Priority priority = Field.getResource(Priority.class, issue.getField(Field.PRIORITY), client.getRestClient());
        System.out.println(priority);
        System.out.println(issue.getField(Field.VERSIONS));
        List<Attachment> attachments = Field.getResourceArray(Attachment.class, issue.getField(Field.ATTACHMENT), client.getRestClient());
        System.out.println(attachments);
    }

    @Test
    public void customFieldOption() throws Exception {
        JSON json = client.getRestClient().get(new URI("http://192.168.29.116:8080/rest/api/2/field"));
        //System.out.println(json);
        IssueType issueType = client.getIssue("HWAEC-40").getIssueType();
        //System.out.println(issueType);
        Object object = client.getIssue("HWAEC-40").getField("customfield_13400");
        //System.out.println(object.toString());
        json = client.getRestClient().get(new URI("http://192.168.29.116:8080/rest/api/2/customFieldOption/13400"));
        //System.out.println(json);
        json = client.getRestClient().get(new URI("http://192.168.29.116:8080/rest/api/2/configuration"));
        //System.out.println(json);
        //
        Project project = client.getProject("HWAEC");
        json = client.getRestClient().get(new URI("http://192.168.29.116:8080/rest/api/2/project/"+project.getId()+"/properties"));
        System.out.println(json);

        List<CustomFieldOption> customFieldAllowedValues = client.getCustomFieldAllowedValues("customfield_13400", project.getKey(), issueType.getName());
        System.out.println(JSONUtil.toJsonStr(customFieldAllowedValues));
    }
}
