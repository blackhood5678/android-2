package com.pu.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pu.facebook.database.DatabaseHandler;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnRegistration;
    EditText etUsername, etPassword;
    String Username, Password;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseHandler = new DatabaseHandler(this);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegistration = (Button) findViewById(R.id.btnSignUp);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(reg);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Username = etUsername.getText().toString();
                Password = etPassword.getText().toString();

                if (TextUtils.isEmpty(Username)) {
                    etUsername.setError("Invalid!");
                    return;
                }


                if (TextUtils.isEmpty(Password)) {
                    etPassword.setError("Invalid!");
                    return;
                }
                int login = databaseHandler.getLogin(Username, Password);

                if (login > 0) {
                    ProfileActivity.userid=login;
                    AddPostActivity.userid=login;
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password!", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
