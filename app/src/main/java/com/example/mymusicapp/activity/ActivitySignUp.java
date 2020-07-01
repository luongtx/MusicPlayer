package com.example.mymusicapp.activity;

import android.content.Intent;
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
            Toast.makeText(this, "Điền đầy đủ", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "Thêm tài khoản thành công", Toast.LENGTH_SHORT).show();
                    etNameSignUp.setText("");
                    etPass1.setText("");
                    etPass2.setText("");
                    Intent intent = new Intent(ActivitySignUp.this,
                            com.example.mymusicapp.activity.ActivityMain.class); // paste first line
                    intent.putExtra("name", name);
                    intent.putExtra("check","0");
                    startActivity(intent);
                    ActivitySignUp.this.finish();
                }
                else
                {
                    Toast.makeText(this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                }

            }
            catch (SQLException e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        else
        {
            Toast.makeText(this, "Pass Nhập lại không đúng", Toast.LENGTH_SHORT).show();
            etPass2.setText("");
        }

    }
}