package team_10.nourriture_android.application;

import android.app.Application;

import com.baidu.frontia.FrontiaApplication;
//import com.baidu.mapapi.SDKInitializer;

import team_10.nourriture_android.activity.LoginActivity;
import team_10.nourriture_android.bean.UserBean;
import team_10.nourriture_android.utils.ObjectPersistence;

public class MyApplication extends Application {

    public static String token;

    private static MyApplication instance;
    public boolean islogin = false;
    private UserBean userBean = new UserBean();
    ;

    public static MyApplication getInstance() {
        return instance;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean.initUserBean(userBean);
    }

    public boolean isLogin() {
        return islogin;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //SDKInitializer.initialize(this);
    }

    public void updateOrSaveUserBean(UserBean userBean) {
        this.userBean.initUserBean(userBean);
        ObjectPersistence.writeObjectToFile(getBaseContext(), this.userBean, LoginActivity.TAG_USER_ACCOUNT);
    }

    public UserBean getUserBeanFromFile() {
        return (UserBean) ObjectPersistence.readObjectFromFile(getBaseContext(), LoginActivity.TAG_USER_ACCOUNT);
    }

    public void clearUserBean() {
        ObjectPersistence.writeObjectToFile(getBaseContext(), null, LoginActivity.TAG_USER_ACCOUNT);
    }
}
