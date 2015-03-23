package team_10.nourriture_android.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ping on 2014/12/21.
 */
public class DishBean implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;

    private String _id;
    private String name;
    private String description;
    private Date date;
    private String picture;
    private String[] recipe; /*  default: 'No recipeBean' */
    private String[] ingredients;
    private String[] problems;
    private String user;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String[] getRecipe() {
        return recipe;
    }

    public void setRecipe(String[] recipe) {
        this.recipe = recipe;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String[] getProblems() {
        return problems;
    }

    public void setProblems(String[] problems) {
        this.problems = problems;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
