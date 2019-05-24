package com.pu.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pu.facebook.database.DatabaseHandler;

public class RegistrationActivity extends AppCompatActivity {

    EditText etFirst, etLast, etEmail, etPassword, etConfirm;
    String FirstName, LastName, Email, Password, ConfirmPassword;
    Button btnSignUp;
    TextView tvAlready;
    private RadioGroup radioSexGroup;
    RadioButton radioSexButton;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        databaseHandler = new DatabaseHandler(this);
        etFirst = (EditText) findViewById(R.id.etFirstName);
        etLast = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirm = (EditText) findViewById(R.id.etConfirmPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        tvAlready = (TextView) findViewById(R.id.tvAlready);
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioSexButton = (RadioButton) findViewById(selectedId);

                FirstName = etFirst.getText().toString();
                LastName = etLast.getText().toString();
                Email = etEmail.getText().toString();
                Password = etPassword.getText().toString();
                ConfirmPassword = etConfirm.getText().toString();

                if (TextUtils.isEmpty(FirstName)) {
                    etFirst.setError("Invalid!");
                    return;
                }

                if (TextUtils.isEmpty(LastName)) {
                    etLast.setError("Invalid!");
                    return;
                }


                if (TextUtils.isEmpty(Email)) {
                    etEmail.setError("Invalid!");
                    return;
                }


                if (TextUtils.isEmpty(Password)) {
                    etPassword.setError("Invalid!");
                    return;
                }
                if (!TextUtils.equals(Password, ConfirmPassword)) {
                    etConfirm.setError("Invalid!");
                    return;
                }
                databaseHandler.addUser(FirstName, LastName, Email, Password, radioSexButton.getText().toString());
                Toast.makeText(RegistrationActivity.this, "Successfully Registered!!", Toast.LENGTH_LONG).show();
                Intent login = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(login);
                finish();

            }
        });
        tvAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

    }
}
