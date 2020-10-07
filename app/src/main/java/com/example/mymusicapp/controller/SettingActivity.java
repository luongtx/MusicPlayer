package com.example.mymusicapp.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mymusicapp.R;

import static com.example.mymusicapp.util.Constants.KEY_THEME;
import static com.example.mymusicapp.util.Constants.MY_PREFS_FILENAME;

public class SettingActivity extends AppCompatActivity {

    TextView btnReset;
    Button btnClose;

    Spinner spin_themes;

    View layout_setting;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTheme();
        setContentView(R.layout.activity_setting);
        layout_setting = findViewById(R.id.layout_setting);
        spin_themes = findViewById(R.id.spinner_theme);

        btnReset = findViewById(R.id.tvReset);
        btnReset.setOnClickListener(v -> {
            resetDefault();
        });

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            closeSetting();
        });

        int[] styles = {R.style.AppTheme_NoActionBar, R.style.AppTheme_NoActionBar_Red, R.style.AppTheme_NoActionBar_Green, R.style.AppTheme_NoActionBar_Blue};
        int[] colors = {getColor(R.color.colorPrimary), Color.RED, Color.GREEN, Color.BLUE};
        spin_themes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveTheme(styles[position]);
                applyTheme(colors[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                saveTheme(R.style.AppTheme_NoActionBar);
                applyTheme(colors[0]);
            }
        });
    }

    private void applyTheme(int color) {
        layout_setting.setBackgroundColor(color);
    }

    private void refresh() {
        try {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeSetting() {
        finish();
    }

    private void resetDefault() {
        saveTheme(R.style.AppTheme_NoActionBar);
    }


    private void saveTheme(int theme) {
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_FILENAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_THEME, theme);
        editor.apply();
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_FILENAME, MODE_PRIVATE);
        int colorTheme = sharedPreferences.getInt(KEY_THEME, R.style.AppTheme_NoActionBar);
        setTheme(colorTheme);
    }
}
