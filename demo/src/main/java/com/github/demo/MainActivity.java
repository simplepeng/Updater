package com.github.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.simplepeng.updater.Updater;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = findViewById(R.id.editText);
        String apkUrl = "https://raw.githubusercontent.com/simplepeng/Updater/master/apk/demo-release-unsigned.apk";
        editText.setText(apkUrl);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editText.getText().toString().trim();
                updateApk(url);
            }
        });
    }

    private void updateApk(String url) {
        new Updater.Builder(this)
                .setDownloadUrl(url)
                .debug()
                .start();
    }
}
