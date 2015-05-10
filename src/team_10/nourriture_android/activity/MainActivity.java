package team_10.nourriture_android.activity;

import android.app.Notification;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import team_10.nourriture_android.R;
import team_10.nourriture_android.push.Utils;
import team_10.nourriture_android.service.PollingService;
import team_10.nourriture_android.service.PollingUtils;

public class MainActivity extends ActionBarActivity {

    private Button btn_dishes;
    private Button btn_myRecipes;
    private Button btn_notification;
    private Button btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start polling service
        //PollingUtils.startPollingService(this, 2, PollingService.class, PollingService.ACTION);

        /*Resources resource = this.getResources();
        String pkgName = this.getPackageName();

        // Push: 浠pikey鐨勬柟寮忕櫥褰曪紝涓�埇鏀惧湪涓籄ctivity鐨刼nCreate涓�
        // 杩欓噷鎶奱pikey瀛樻斁浜巑anifest鏂囦欢涓紝鍙槸涓�瀛樻斁鏂瑰紡锛�
        // 鎮ㄥ彲浠ョ敤鑷畾涔夊父閲忕瓑鍏跺畠鏂瑰紡瀹炵幇锛屾潵鏇挎崲鍙傛暟涓殑Utils.getMetaValue(MainActivity.this,
        // "api_key")
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(MainActivity.this, "api_key"));
        // Push: 濡傛灉鎯冲熀浜庡湴鐞嗕綅缃帹閫侊紝鍙互鎵撳紑鏀寔鍦扮悊浣嶇疆鐨勬帹閫佺殑寮�叧
        // PushManager.enableLbs(getApplicationContext());

        // Push: 璁剧疆鑷畾涔夌殑閫氱煡鏍峰紡锛屽叿浣揂PI浠嬬粛瑙佺敤鎴锋墜鍐岋紝濡傛灉鎯充娇鐢ㄧ郴缁熼粯璁ょ殑鍙互涓嶅姞杩欐浠ｇ爜
        // 璇峰湪閫氱煡鎺ㄩ�鐣岄潰涓紝楂樼骇璁剧疆->閫氱煡鏍忔牱寮�>鑷畾涔夋牱寮忥紝閫変腑骞朵笖濉啓鍊硷細1锛�
        // 涓庝笅鏂逛唬鐮佷腑 PushManager.setNotificationBuilder(this, 1, cBuilder)涓殑绗簩涓弬鏁板搴�
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                getApplicationContext(), resource.getIdentifier(
                "notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName),
                resource.getIdentifier("notification_title", "id", pkgName),
                resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier(
                "ic_launcher", "drawable", pkgName));
        PushManager.setNotificationBuilder(this, 1, cBuilder);*/

        btn_dishes = (Button) findViewById(R.id.dishes_btn);
        btn_myRecipes = (Button) findViewById(R.id.myRecipes_btn);
        btn_notification = (Button) findViewById(R.id.notification_btn);
        btn_setting = (Button) findViewById(R.id.setting_btn);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentFrame, new DishesFragment()).commit();

        btn_dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentFrame, new DishesFragment()).commit();
            }
        });
        btn_myRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentFrame, new RestaurantFragment()).commit();
            }
        });
        btn_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentFrame, new NotificationFragment()).commit();
            }
        });
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentFrame, new SettingFragment()).commit();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stop polling service
        //PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
    }
}
