package team_10.nourriture_android.bean;

import java.io.Serializable;

/**
 * Created by ping on 2014/12/21.
 */
public class ProblemBean implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;

    private String _id;
    private String name;
    private String type; /* 'ethic', 'health', 'religion' S*/
    private String description;
    private String picture;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
