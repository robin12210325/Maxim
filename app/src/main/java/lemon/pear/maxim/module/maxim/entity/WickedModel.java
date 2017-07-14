package lemon.pear.maxim.module.maxim.entity;

import cn.bmob.v3.BmobObject;

/**
 * 负能量实体
 */

public class WickedModel extends BmobObject {

    private String front;
    private String behind;

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBehind() {
        return behind;
    }

    public void setBehind(String behind) {
        this.behind = behind;
    }
}
