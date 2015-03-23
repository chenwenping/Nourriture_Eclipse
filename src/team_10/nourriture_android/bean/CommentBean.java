package team_10.nourriture_android.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ping on 2014/12/21.
 */
public class CommentBean implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;

    private String _id;
    private String content;
    private Date date;
    private boolean share;
    private String user;
    private String dish;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }
}
