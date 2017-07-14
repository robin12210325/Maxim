package lemon.pear.maxim.module.maxim.entity;

import cn.bmob.v3.BmobObject;
import lemon.pear.maxim.module.user.entity.MaximUser;

/**
 * 收藏实体类
 */

public class CommentModel extends BmobObject {
    private MaximUser maximUser;
    private MaximModel maximModel;

    private String comment;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
