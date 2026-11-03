package com.example.firstapp_luismunoz.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.firstapp_luismunoz.R;
import com.example.firstapp_luismunoz.ui.ViewModelFactory;
import com.example.firstapp_luismunoz.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginViewModel viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(LoginViewModel.class);

        EditText usernameInput = findViewById(R.id.username_input);
        EditText passwordInput = findViewById(R.id.password_input);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v ->
                viewModel.login(usernameInput.getText().toString(), passwordInput.getText().toString()));
        registerButton.setOnClickListener(v ->
                viewModel.register(usernameInput.getText().toString(), passwordInput.getText().toString()));

        viewModel.isBusy().observe(this, busy -> {
            loginButton.setEnabled(!busy);
            registerButton.setEnabled(!busy);
        });
        viewModel.getMessages().observe(this, event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
        viewModel.getLoginSuccess().observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }
}
