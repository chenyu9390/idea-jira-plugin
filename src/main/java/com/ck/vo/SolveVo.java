package com.ck.vo;

import org.mozilla.javascript.annotations.JSGetter;

import java.util.List;

public class SolveVo {

    private String issueKey;

    /**
     * 过渡id
     */
    private String transitionId;

    /**
     * 解决方案
     */
    private String customfield_12249;

    /**
     * bug解决人
     */
    private String customfield_12304;

    /**
     * Remark（备注）
     */
    private String customfield_12251;

    /**
     * 影响范围
     */
    private String customfield_13209;

    /**
     * 版本vo列表
     */
    private List<VersionVo> versionVoList;

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getCustomfield_12249() {
        return customfield_12249;
    }

    public void setCustomfield_12249(String customfield_12249) {
        this.customfield_12249 = customfield_12249;
    }

    public String getCustomfield_12304() {
        return customfield_12304;
    }

    public void setCustomfield_12304(String customfield_12304) {
        this.customfield_12304 = customfield_12304;
    }

    public String getCustomfield_12251() {
        return customfield_12251;
    }

    public void setCustomfield_12251(String customfield_12251) {
        this.customfield_12251 = customfield_12251;
    }

    public String getCustomfield_13209() {
        return customfield_13209;
    }

    public void setCustomfield_13209(String customfield_13209) {
        this.customfield_13209 = customfield_13209;
    }

    public String getTransitionId() {
        return transitionId;
    }

    public void setTransitionId(String transitionId) {
        this.transitionId = transitionId;
    }

    public List<VersionVo> getVersionVoList() {
        return versionVoList;
    }

    public void setVersionVoList(List<VersionVo> versionVoList) {
        this.versionVoList = versionVoList;
    }
}
