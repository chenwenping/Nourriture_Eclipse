package team_10.nourriture_android.bean;

import java.io.Serializable;

/**
 * Created by ping on 2014/12/28.
 */
public class FriendBean implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;

    private String _id;
    private String content;
    private String from;
    private String to;
    private String status; /* 'accept', 'request' */

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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
