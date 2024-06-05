package com.ck.vo;

import com.ck.dot.TransitionDto;

import java.util.List;

public class JiraVo {

    private String bugId;

    private String summary;

    private String url;

    List<TransitionDto> dtos;

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
