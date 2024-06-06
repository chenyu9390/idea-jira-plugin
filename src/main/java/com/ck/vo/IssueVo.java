package com.ck.vo;

import com.ck.dot.TransitionDto;
import com.google.common.collect.Lists;

import java.util.List;

public class IssueVo {

    private String bugId;

    private String summary;

    private String url;

    List<TransitionDto> dtos = Lists.newArrayList();

    List<VersionVo> versionVos = Lists.newArrayList();

    public List<VersionVo> getVersionVos() {
        return versionVos;
    }

    public void setVersionVos(List<VersionVo> versionVos) {
        this.versionVos = versionVos;
    }

    public List<TransitionDto> getDtos() {
        return dtos;
    }

    public void setDtos(List<TransitionDto> dtos) {
        this.dtos = dtos;
    }

    public String getBugId() {
        return bugId;
    }

    public void setBugId(String bugId) {
        this.bugId = bugId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
