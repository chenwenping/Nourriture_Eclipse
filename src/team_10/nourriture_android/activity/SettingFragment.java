package team_10.nourriture_android.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import team_10.nourriture_android.R;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.service.PollingService;
import team_10.nourriture_android.service.PollingUtils;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;


/**
 * Created by ping on 2014/12/20.
 */
public class SettingFragment extends Fragment {

    private Button exit_btn;
    private SharedPreferences sp;
    private boolean isLogin = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sp = getActivity().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        isLogin = sp.getBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);

        exit_btn = (Button) getActivity().findViewById(R.id.btn_exit);
        if (isLogin) {
            exit_btn.setVisibility(View.VISIBLE);
        } else {
            exit_btn.setVisibility(View.GONE);
        }
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Confirm exit").setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage("Do you really want to exit?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // clear login information
                                MyApplication.getInstance().clearUserBean();
                                MyApplication.getInstance().islogin = false;
                                SharedPreferences sp = getActivity().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);
                                editor.commit();
                                Toast.makeText(getActivity(), "exit successfully", Toast.LENGTH_SHORT).show();
                                exit_btn.setVisibility(View.GONE);
                                NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.cancelAll();
                                PollingUtils.stopPollingService(getActivity(), PollingService.class, PollingService.ACTION);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        });
    }
}
