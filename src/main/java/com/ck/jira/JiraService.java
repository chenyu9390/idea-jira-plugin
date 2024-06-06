package com.ck.jira;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ck.config.JiraConfig;
import com.ck.dot.TransitionDto;
import com.ck.vo.IssueVo;
import com.ck.vo.SolveVo;
import com.ck.vo.VersionVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.ui.Messages;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.Priority;

import java.util.*;

public class JiraService {

    private static final String TITLE = "error";
    private static final String ERROR_MSG = "jira数据获取异常";

    private JiraConfig jiraConfig;

    public JiraService(JiraConfig config){
        this.jiraConfig = config;
    }

    public List<IssueVo> getIssueList() {
        String jql = "resolution = Unresolved AND assignee in (\""+jiraConfig.getUserName()+"\") ORDER BY priority DESC, updated DESC";
        Map<String, Object> map = new HashMap<>();
        map.put("jql", jql);
        List<IssueVo> voList = new ArrayList<>();
        try {
            HttpResponse<JsonNode> response = Unirest.post(jiraConfig.getUrl() + "/rest/api/2/search")
                    .basicAuth(jiraConfig.getUserName(), jiraConfig.getPassword())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(map))
                    .asJson();
            JSONObject jsonObject = JSONUtil.parseObj(response.getBody().toString());
            JSONArray issuesArray = jsonObject.getJSONArray("issues");
            if(CollectionUtil.isEmpty(issuesArray)){
                return Lists.newArrayList();
            }
            for (int index = 0,length = issuesArray.size();index < length;index++) {
                JSONObject issuesObj = issuesArray.getJSONObject(index);
                JSONObject fields = issuesObj.getJSONObject("fields");
                IssueVo vo = new IssueVo();
                vo.setUrl(jiraConfig.getUrl()+"/browse/"+issuesObj.getStr("key"));
                vo.setBugId(issuesObj.getStr("key"));
                vo.setSummary(fields.getStr("summary"));
                vo.setDtos(transitions(vo.getBugId()));
                voList.add(vo);
            }
        }catch (Exception e){
            Messages.showMessageDialog(ERROR_MSG,TITLE, Messages.getErrorIcon());
        }
        return voList;
    }

    public List<TransitionDto> transitions(String issueKey){
        try {
            HttpResponse<String> response = Unirest.get(jiraConfig.getUrl() + "/rest/api/2/issue/"+issueKey+"/transitions")
                    .basicAuth(jiraConfig.getUserName(), jiraConfig.getPassword())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .asString();
            if(response.getStatus() != 200){
                Messages.showMessageDialog(response.getBody(),TITLE, Messages.getErrorIcon());
                return Lists.newArrayList();
            }
            JSONArray jsonArray = JSONUtil.parseObj(response.getBody()).getJSONArray("transitions");
            return jsonArray.toList(TransitionDto.class);
        }catch (Exception e){
            Messages.showMessageDialog(e.getMessage(),TITLE, Messages.getErrorIcon());
        }
        return Lists.newArrayList();
    }

    public void doTransition(SolveVo vo){
        try {
            BasicCredentials creds = new BasicCredentials(jiraConfig.getUserName(), jiraConfig.getPassword());
            JiraClient jira = new JiraClient(jiraConfig.getUrl(), creds);
            Issue issue = jira.getIssue(vo.getIssueKey());
            Issue.FluentTransition transition = issue.transition();
            if(CollUtil.isNotEmpty(vo.getVersionVoList())){
                transition.field("fixVersions",vo.getVersionVoList());
            }
            if(StrUtil.isNotEmpty(vo.getCustomfield_12249())){
                transition.field("customfield_12249",vo.getCustomfield_12249());
            }
            if(StrUtil.isNotEmpty(vo.getCustomfield_12251())){
                transition.field("customfield_12251",vo.getCustomfield_12251());
            }
            if(StrUtil.isNotEmpty(vo.getCustomfield_13209())){
                transition.field("customfield_13209",vo.getCustomfield_13209());
            }
            if(StrUtil.isNotEmpty(vo.getCustomfield_12304())){
                transition.field("customfield_12304",vo.getCustomfield_12304());
            }
            transition.execute(vo.getTransitionId());
        }catch (Exception e){
            Messages.showMessageDialog(e.getMessage(),TITLE, Messages.getErrorIcon());
        }
    }

    private Map<String,List<VersionVo>> getVersions(List<String> projectKey) throws UnirestException {
        Map<String,List<VersionVo>> map = Maps.newHashMapWithExpectedSize(projectKey.size());
        for (String key : projectKey) {
            HttpResponse<String> response = Unirest.get(jiraConfig.getUrl() + "/rest/api/2/project/"+key+"/versions")
                    .basicAuth("albert.chen@quectel.com", "CHENyu123")
                    .header("Accept", "application/json")
                    .asString();
            if(response.getStatus() != 200){
                Messages.showMessageDialog(response.getBody(),TITLE, Messages.getErrorIcon());
            }
            JSONArray jsonArray = JSONUtil.parseArray(response.getBody());
            List<VersionVo> versionVos = jsonArray.toList(VersionVo.class);
            CollUtil.sort(versionVos,(o1,o2)->o2.getId().compareTo(o1.getId()));
            CollUtil.sub(versionVos,0,5);
            map.put(key,versionVos);
        }
        return map;
    }

    public static void main(String[] args) throws Exception {
        HttpResponse<String> response = Unirest.get("https://ticket.quectel.com" + "/rest/api/2/issue/SW6PMS-8483")
                .basicAuth("albert.chen@quectel.com", "CHENyu123")
                .header("Accept", "application/json")
                .asString();
        //System.out.println(response.getBody());
        ObjectMapper objectMapper = new ObjectMapper();

        // Create JSON payload
        ObjectNode updatePayload = objectMapper.createObjectNode();
        ObjectNode fields = updatePayload.putObject("fields");
        // Update the fix version
        fields.putArray("fixVersions").addObject().put("id", "21484");;
        HttpResponse<JsonNode> updateFieldsResponse = Unirest.put("https://ticket.quectel.com/" + "rest/api/2/issue/" + "SW6PMS-8483")
                .header("Content-Type", "application/json")
                .body(updatePayload.toString())
                .asJson();
        System.out.println(updateFieldsResponse.getBody());
        System.out.println("--------------------");
        HttpResponse<JsonNode> httpResponse = Unirest.get("https://ticket.quectel.com/" + "rest/api/2/issue/" + "SW6PMS-8483")
                .header("Content-Type", "application/json")
                .asJson();
        //System.out.println(httpResponse.getBody());

        /*
        解决版本
        BasicCredentials creds = new BasicCredentials("albert.chen@quectel.com", "CHENyu123");
        JiraClient jira = new JiraClient("https://ticket.quectel.com", creds);
        Issue issue = jira.getIssue("SW6PMS-8483");
        TransitionDto dto = new TransitionDto("21481","V2.11.0_20240527_bugfix");
        List<TransitionDto> dtos = new ArrayList<>();
        dtos.add(dto);
        issue.transition().field("fixVersions",dtos).execute("解决");*/
        BasicCredentials creds = new BasicCredentials("neyo.chen", "quec910926!");
        JiraClient jira = new JiraClient("http://192.168.29.116:8080", creds);
        Issue issue = jira.getIssue("SW6PMS-8483");
    }
}
