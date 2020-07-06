package com.example.mymusicapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.SQLException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mymusicapp.R;
import com.example.mymusicapp.repository.DBAccountHelper;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class ActivityAccount extends AppCompatActivity {
    TextView tvName,tv1,tv2,tv3;
    Button btnLogOut,btnChangePass, btnChangePass2;
    EditText etOldPass, etNewPass, etNewPassAgain;
    private LoginButton loginButton;
    private String name , check ="";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tvName = findViewById(R.id.tvName);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnChangePass = findViewById(R.id.btnChangePass);
        btnChangePass2 = findViewById(R.id.btnChangePass2);
        etOldPass = findViewById(R.id.etOldPass);
        etNewPass = findViewById(R.id.etNewPass);
        etNewPassAgain = findViewById(R.id.etNewPassAgain);
        loginButton = findViewById(R.id.login_button);

        SharedPreferences prefs = getSharedPreferences(ActivityLogin.MY_PREFS_FILENAME, MODE_PRIVATE);
        setLocale(prefs.getString("prefer_lang", "en"));
        name = prefs.getString(ActivityLogin.NAME, "");
        check = prefs.getString(ActivityLogin.CHECK, "");

        if(name.equals(""))
        {
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.VISIBLE);
            etNewPassAgain.setVisibility(View.GONE);
            etNewPass.setVisibility(View.GONE);
            etOldPass.setVisibility(View.GONE);
            btnChangePass.setVisibility(View.GONE);
            btnChangePass2.setVisibility(View.GONE);
            tvName.setVisibility(View.GONE);
            btnLogOut.setVisibility(View.GONE);
        }
        else
        {
            tvName.setText(getString(R.string.welcome) + name);
            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            tv3.setVisibility(View.GONE);
            etNewPassAgain.setVisibility(View.VISIBLE);
            etNewPass.setVisibility(View.VISIBLE);
            etOldPass.setVisibility(View.VISIBLE);
            btnChangePass.setVisibility(View.VISIBLE);
            btnChangePass2.setVisibility(View.GONE);
            tvName.setVisibility(View.VISIBLE);
            btnLogOut.setVisibility(View.VISIBLE);
        }

        if(check.equals("API"))
        {
            etNewPassAgain.setVisibility(View.GONE);
            etNewPass.setVisibility(View.GONE);
            etOldPass.setVisibility(View.GONE);
            btnChangePass.setVisibility(View.GONE);
            btnChangePass2.setVisibility(View.GONE);
        }
        else if (check.equals("0"))
        {
            etNewPassAgain.setVisibility(View.GONE);
            etNewPass.setVisibility(View.GONE);
            etOldPass.setVisibility(View.GONE);
            btnChangePass2.setVisibility(View.GONE);
        }
        else
        {
            etNewPassAgain.setVisibility(View.GONE);
            etNewPass.setVisibility(View.GONE);
            etOldPass.setVisibility(View.GONE);
            btnChangePass.setVisibility(View.GONE);
            btnChangePass2.setVisibility(View.GONE);
            btnLogOut.setVisibility(View.GONE);
        }

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNewPassAgain.setVisibility(View.VISIBLE);
                etNewPass.setVisibility(View.VISIBLE);
                etOldPass.setVisibility(View.VISIBLE);
                btnChangePass.setVisibility(View.GONE);
                btnChangePass2.setVisibility(View.VISIBLE);
            }
        });


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityAccount.this, ActivityLogin.class);
                intent.putExtra("tvn", name);
                tvName.setText("");
                name ="";
                SharedPreferences.Editor editor = getSharedPreferences(ActivityLogin.MY_PREFS_FILENAME, ActivityLogin.MODE_PRIVATE).edit();
                editor.putString(ActivityLogin.NAME, "");
                editor.putString(ActivityLogin.CHECK,"");
                editor.apply();
//                setResult(RESULT_OK, intent);
                startActivity(intent);
                ActivityAccount.this.finish();

            }
        });


    }

    public void UpdateAcc(View v) {
        if (etOldPass.getText().toString().isEmpty() || etNewPass.getText().toString().isEmpty() || etNewPassAgain.getText().toString().isEmpty()) {
            Toast.makeText(ActivityAccount.this, R.string.enter_full, Toast.LENGTH_SHORT).show();
        } else if (etNewPass.getText().toString().equals(etNewPassAgain.getText().toString())) {
            try {
                String pass = etOldPass.getText().toString().trim();
                String pass1 = etNewPass.getText().toString().trim();
                DBAccountHelper db = new DBAccountHelper(this);
                db.open();
                Boolean login = db.Login(name, pass);

                if (login) {
                    db.updateEntry(name, pass1);
                    Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
                    etNewPassAgain.setVisibility(View.GONE);
                    etNewPass.setVisibility(View.GONE);
                    etOldPass.setVisibility(View.GONE);
                    btnChangePass.setVisibility(View.VISIBLE);
                    btnChangePass2.setVisibility(View.GONE);
                    etNewPass.setText("");
                    etOldPass.setText("");
                    etNewPassAgain.setText("");
                } else {
                    Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
                    etOldPass.setText("");
                }
                db.close();
            } catch (SQLException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.wrong_new_password, Toast.LENGTH_SHORT).show();
        }

    }
    public void DN(View v)
    {
        ActivityAccount.this.finish();
    }
    public void DK(View v)
    {
        startActivity(new Intent(ActivityAccount.this,ActivitySignUp.class));
        ActivityAccount.this.finish();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        if (!config.locale.getLanguage().equals(myLocale.getLanguage())) {
            config.locale = myLocale;
            res.updateConfiguration(config, dm);
            Intent refresh = new Intent(this, ActivityAccount.class);
            startActivity(refresh);
        }
    }

}