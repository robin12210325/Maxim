package lemon.pear.maxim.module.maxim.entity;

import cn.bmob.v3.BmobObject;

/**
 * 箴言实体
 */

public class MaximModel extends BmobObject {
    private String url;
    private String title;
    private String content;
    private String detail;
    private Integer type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
