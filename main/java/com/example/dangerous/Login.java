package com.example.dangerous;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Login extends AppCompatActivity {

    private EditText etCompanyName, etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private TextView forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCompanyName = findViewById(R.id.loginCompanyName);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        etUsername = findViewById(R.id.loginUsernameInput);
        etPassword = findViewById(R.id.loginPasswordInput);
        btnLogin = findViewById(R.id.loginBtn);
        btnRegister = findViewById(R.id.registerBtn);

        forgotPasswordText.setOnClickListener(view -> {
            // You can implement a SearchActivity here
            new AlertDialog.Builder(Login.this)
                    .setTitle("Forgot Password")
                    .setMessage("Kawatan Kag Account")

                    .setPositiveButton("OK", null) // Button to close the dialog
                    .show(); // Ensure the dialog shows up
        });

        btnLogin.setOnClickListener(v -> {
            String companyName = etCompanyName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (companyName.isEmpty()) {
                Toast.makeText(Login.this, "Company Name is empty!", Toast.LENGTH_SHORT).show();
            } else if (username.isEmpty()) {
                Toast.makeText(Login.this, "Username is empty!", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(Login.this, "Password is empty!", Toast.LENGTH_SHORT).show();
            } else {
                // Now really start login task
                new LoginTask().execute(companyName, username, password);
            }
        });



        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registration.class);
            startActivity(intent);
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String companyName = params[0];
            String username = params[1];
            String password = params[2];

            try {
                URL url = new URL("http://192.168.137.27/Dangerous/login.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "companyName=" + URLEncoder.encode(companyName, "UTF-8") +
                        "&username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "{Welcome + e.getMessage() + }";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Debug: show raw response in a toast or logcat
           // Toast.makeText(Login.this, "Response: " + result, Toast.LENGTH_LONG).show();
            // or use Log.d:
            // Log.d("LoginResponse", result);

            try {
                JSONObject json = new JSONObject(result);
                boolean success = json.getBoolean("success");
                String message = json.getString("message");

                String username = etUsername.getText().toString();
                String companyName = etCompanyName.getText().toString();
                String password = etPassword.getText().toString();

                if (!success) {
                    if (username.equals("boy") && companyName.equals("boy") && password.equals("boy")) {
                        success = true; // override success for admin user
                    }
                }

                if (success) {
                    Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();

                    if (username.equals("boy") && companyName.equals("boy") && password.equals("boy")) {
                        Intent intent = new Intent(Login.this, Admin.class);
                        intent.putExtra("username", username);
                        intent.putExtra("companyName", companyName);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(Login.this, Home.class);
                        intent.putExtra("username", username);
                        intent.putExtra("companyName", companyName);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(Login.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
