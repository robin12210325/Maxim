package lemon.pear.maxim.module.maxim.entity;

import cn.bmob.v3.BmobObject;
import lemon.pear.maxim.module.user.entity.MaximUser;

/**
 * 收藏实体类
 */

public class MaximStore extends BmobObject {
    private MaximUser maximUser;
    private MaximModel maximModel;

    private String maximTitle;

    public MaximUser getMaximUser() {
        return maximUser;
    }

    public void setMaximUser(MaximUser maximUser) {
        this.maximUser = maximUser;
    }

    public MaximModel getMaximModel() {
        return maximModel;
    }

    public void setMaximModel(MaximModel maximModel) {
        this.maximModel = maximModel;
    }

    public String getMaximTitle() {
        return maximTitle;
    }

    public void setMaximTitle(String maximTitle) {
        this.maximTitle = maximTitle;
    }
}
