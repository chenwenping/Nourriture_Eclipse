package team_10.nourriture_android.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ping on 2014/12/21.
 */
public class UserBean implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;

    private String _id;
    private String username;
    private String password;
    private boolean admin;
    private String firstName;
    private String lastName;
    private String gender; /* 'male', 'female' */
    private String email;
    private String status; /* 'online', 'away', 'offline' */
    private String phoneNumber;
    private String introduction;
    private Date registrationDate;
    private String role; /* 'normal', 'foodSupplier', 'gastronomist' */
    private String picture;
    private String[] problems;
    private String[] friends;

    public void initUserBean(UserBean userBean) {
        this._id = userBean._id;
        this.username = userBean.username;
        this.password = userBean.password;
        this.admin = userBean.admin;
        this.firstName = userBean.firstName;
        this.lastName = userBean.lastName;
        this.gender = userBean.gender;
        this.email = userBean.email;
        this.status = userBean.status;
        this.phoneNumber = userBean.phoneNumber;
        this.introduction = userBean.introduction;
        this.registrationDate = userBean.registrationDate;
        this.role = userBean.role;
        this.picture = userBean.picture;
        this.problems = userBean.problems;
        this.friends = userBean.friends;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String[] getProblems() {
        return problems;
    }

    public void setProblems(String[] problems) {
        this.problems = problems;
    }

    public String[] getFriends() {
        return friends;
    }

    public void setFriends(String[] friends) {
        this.friends = friends;
    }
}
