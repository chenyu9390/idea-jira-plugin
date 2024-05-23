package com.ck.jira;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.intellij.openapi.ui.Messages;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.ck.config.JiraConfig;
import com.ck.vo.JiraVo;

import javax.swing.*;
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
                voList.add(vo);
            }
        }catch (Exception e){
            Messages.showMessageDialog(ERROR_MSG,TITLE, Messages.getErrorIcon());
        }
        return voList;
    }
}
