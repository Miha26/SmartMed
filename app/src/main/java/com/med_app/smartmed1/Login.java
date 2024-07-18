//Log in
package com.med_app.smartmed1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.med_app.smartmed1.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                if(email.equals("") || password.equals(""))
                    Toast.makeText(Login.this,"All fields are mandatory!",Toast.LENGTH_SHORT).show();
                else
                {
                    Boolean checkCredentials = databaseHelper.checkEmailPassword(email, password);

                    if(checkCredentials == true)
                    {
                        // Salvare email în SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.apply();

                        Toast.makeText(Login.this,"Login Successful!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(Login.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        binding.singupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
            }
        });
    }

}