package team_10.nourriture_android.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import team_10.nourriture_android.R;

public class RestaurantDetailActivity extends ActionBarActivity implements View.OnClickListener {

    private Button back_btn, location_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        location_btn = (Button) findViewById(R.id.btn_location);
        back_btn = (Button) findViewById(R.id.btn_back);
        location_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_location:
                Intent intent = new Intent(RestaurantDetailActivity.this, RestaurantLocationActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
