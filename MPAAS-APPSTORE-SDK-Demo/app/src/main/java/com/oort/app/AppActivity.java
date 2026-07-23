package com.oort.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.oort.core.constant.Constant;
import com.oortcloud.appstore.AppStoreActivity;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2026/2/6-15:50.
 * Version 1.0
 * Description:
 */
public class AppActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app);

        //启动应用市场
        findViewById(R.id.app_store).setOnClickListener(view -> {
            Constant.openActivity = 2;
            startActivity(new Intent(this, AppStoreActivity.class));
            finish();
        });

    }
}
