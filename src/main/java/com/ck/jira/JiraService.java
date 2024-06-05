package com.ck.jira;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ck.config.JiraConfig;
import com.ck.dot.TransitionDto;
import com.ck.vo.JiraVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.intellij.openapi.ui.Messages;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraService {

    private static final String TITLE = "error";
    private static final String ERROR_MSG = "jira数据获取异常";

    private JiraConfig jiraConfig;

    public JiraService(JiraConfig config){
        this.jiraConfig = config;
    }

    public List<JiraVo> getIssueList() {
        String jql = "resolution = Unresolved AND assignee in (\""+jiraConfig.getUserName()+"\") ORDER BY priority DESC, updated DESC";
        Map<String, Object> map = new HashMap<>();
        map.put("jql", jql);
        List<JiraVo> voList = new ArrayList<>();
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
                JiraVo vo = new JiraVo();
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

    public void doTransition(String issueKey,String transitionId){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, String> transitionMap = new HashMap<>();
            transitionMap.put("id", transitionId); // 将此处的ID替换为正确的Transition ID
            requestBody.put("transition", transitionMap);
            String jsonRequestBody = objectMapper.writeValueAsString(requestBody);
            HttpResponse<String> response = Unirest.post("https://ticket.quectel.com" + "/rest/api/2/issue/"+issueKey+"/transitions")
                    .basicAuth("albert.chen@quectel.com", "CHENyu123")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(jsonRequestBody)
                    .asString();
            if(response.getStatus() != 200){
                Messages.showMessageDialog(response.getBody(),TITLE, Messages.getErrorIcon());
            }
        }catch (Exception e){
            Messages.showMessageDialog(e.getMessage(),TITLE, Messages.getErrorIcon());
        }
    }

    public static void main(String[] args) throws Exception {
        HttpResponse<String> response = Unirest.get("https://ticket.quectel.com" + "/rest/api/2/issue/SW6PMS-8478/transitions")
                .basicAuth("albert.chen@quectel.com", "CHENyu123")
                .header("Accept", "application/json")
                .asString();
        System.out.println(response.getBody());
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, String> transitionMap = new HashMap<>();
        transitionMap.put("id", "11"); // 将此处的ID替换为正确的Transition ID
        requestBody.put("transition", transitionMap);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);
        response = Unirest.post("https://ticket.quectel.com" + "/rest/api/2/issue/SW6PMS-8478/transitions")
                .basicAuth("albert.chen@quectel.com", "CHENyu123")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(jsonRequestBody)
                .asString();
        System.out.println(response.getBody());


    }
}
