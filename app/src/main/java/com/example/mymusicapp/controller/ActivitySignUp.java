package com.example.mymusicapp.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymusicapp.R;
import com.example.mymusicapp.repository.DBAccountHelper;

public class ActivitySignUp extends AppCompatActivity {
    EditText etNameSignUp,etPass1,etPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etNameSignUp = findViewById(R.id.etNameSignUp);
        etPass1 = findViewById(R.id.etPass1);
        etPass2 = findViewById(R.id.etPass2);
    }
    public void btnAddAccount(View v)
    {
        String name = etNameSignUp.getText().toString().trim();
        String pass1 = etPass1.getText().toString().trim();
        String pass2 = etPass2.getText().toString().trim();
        if(etPass1.getText().toString().isEmpty()||etPass2.getText().toString().isEmpty()||etNameSignUp.getText().toString().isEmpty())
        {
            Toast.makeText(this, R.string.enter_full, Toast.LENGTH_SHORT).show();
        }
        else if(pass1.equals(pass2))
        {
            try
            {
                DBAccountHelper db = new DBAccountHelper(this);
                db.open();
                Boolean check = db.CheckSignUp(name);
                if(check){
                    db.createEntry(name, pass1);
                    db.close();
                    Toast.makeText(this, R.string.add_account_success, Toast.LENGTH_SHORT).show();
                    etNameSignUp.setText("");
                    etPass1.setText("");
                    etPass2.setText("");
                    Intent intent = new Intent( ActivitySignUp.this,
                            com.example.mymusicapp.controller.ActivityMain.class); // paste first line
                    SharedPreferences.Editor editor = getSharedPreferences(ActivityLogin.MY_PREFS_FILENAME, ActivityLogin.MODE_PRIVATE).edit();
                    editor.putString(ActivityLogin.NAME, name);
                    editor.putString(ActivityLogin.CHECK,"0");
                    editor.apply();
                    startActivity(intent);
                    ActivitySignUp.this.finish();
                }
                else
                {
                    Toast.makeText(this, R.string.account_existed, Toast.LENGTH_SHORT).show();
                }

            }
            catch (SQLException e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        else
        {
            Toast.makeText(this, R.string.wrong_again_pass, Toast.LENGTH_SHORT).show();
            etPass2.setText("");
        }

    }
}