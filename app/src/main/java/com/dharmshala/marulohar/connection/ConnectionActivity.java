package com.dharmshala.marulohar.connection;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.dharmshala.marulohar.R;
import com.dharmshala.marulohar.connection.fragment.LoginFragment;
import com.dharmshala.marulohar.utils.BaseActivity;

import java.lang.reflect.Field;


public class ConnectionActivity extends BaseActivity {

    Context context;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        context = this;
        initToolbar();
        try {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new LoginFragment(), "LoginFragment")
                        .commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_back);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle(context.getText(R.string.login));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            TextView toolbarTitle = (TextView) f.get(toolbar);
            toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold.ttf"));
            toolbarTitle.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_extra_large));
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
