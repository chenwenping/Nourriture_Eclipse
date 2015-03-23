package team_10.nourriture_android.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;

import team_10.nourriture_android.R;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2014/12/21.
 */
@SuppressLint("NewApi")
public class DishAddActivity extends ActionBarActivity implements View.OnClickListener {

    public static int KEY_ADD_DISH = 2;
    private EditText dish_name_et;
    private EditText dish_description_et;
    private Button dish_add_btn;
    private ImageView dish_picture;
    private Button back_btn;
    private String dish_name;
    private String dish_description;
    private SharedPreferences sp;
    private ProgressDialog progress;
    private DishBean dishBean;
    private String picturePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_add);

        progress = new ProgressDialog(this);
        sp = getApplicationContext().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

        initView();
    }

    public void initView() {
        dish_name_et = (EditText) this.findViewById(R.id.et_dish_name);
        dish_description_et = (EditText) this.findViewById(R.id.et_dish_description);
        dish_add_btn = (Button) this.findViewById(R.id.btn_dish_add);
        dish_picture = (ImageView) this.findViewById(R.id.img_dish_picture);
        back_btn = (Button) this.findViewById(R.id.btn_back);

        dish_add_btn.setOnClickListener(this);
        dish_picture.setOnClickListener(this);
        back_btn.setOnClickListener(this);
    }

    /*public String encodeBase64File(String path){
        try {
            File file = new File(path);
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }*/

    public void addDish(String dish_name, String dish_description) {
        RequestParams params = new RequestParams();
        params.add("name", dish_name);
        params.add("description", dish_description);

        /*if(picturePath != null && "".equals(picturePath.trim())){
            picturePath = encodeBase64File("nn");
            try {
                params.add("picture", URLEncoder.encode(picturePath, "UTF-8"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }*/

        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        NourritureRestClient.postWithLogin("dishes", params, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("add dish", response.toString());
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (statusCode == 201) {
                    try {
                        dishBean = JsonTobean.getBean(DishBean.class, response.toString());
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dishBean", dishBean);
                        intent.putExtras(bundle);
                        setResult(KEY_ADD_DISH, intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Adding dish is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Adding dish is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dish_add:
                dish_name = dish_name_et.getText().toString().trim();
                dish_description = dish_description_et.getText().toString().trim();
                if (dish_name == null || "".equals(dish_name)) {
                    dish_name_et.requestFocus();
                    Toast.makeText(this, "Please enter the dish name.", Toast.LENGTH_SHORT).show();
                } else if (dish_description == null || "".equals(dish_description)) {
                    dish_description_et.requestFocus();
                    Toast.makeText(this, "Please enter the dish description.", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dish_name_et.getWindowToken(), 0); // 闅愯棌杞敭鐩�
                    imm.hideSoftInputFromWindow(dish_description_et.getWindowToken(), 0); // 闅愯棌杞敭鐩�
                    progress.setMessage("Add dish...");
                    progress.show();
                    addDish(dish_name, dish_description);
                }
                break;
            case R.id.img_dish_picture:
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
}
