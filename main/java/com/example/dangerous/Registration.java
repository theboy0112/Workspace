package com.example.dangerous;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

public class Registration extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etCompanyN;
    private Button btnRegister;
    private TextView loginPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etCompanyN = findViewById(R.id.companyNameInput);
        loginPrompt = findViewById(R.id.loginPrompt);
        etUsername = findViewById(R.id.usernameInput);
        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnRegister = findViewById(R.id.registerBtn);

        loginPrompt.setOnClickListener(view ->
                startActivity(new Intent(Registration.this, Login.class)));

        btnRegister.setOnClickListener(v -> {
            String company = etCompanyN.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (company.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Registration.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else if (!(email.endsWith("@gmail.com") || email.endsWith("@yahoo.com"))) {
                Toast.makeText(Registration.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            } else {
                new RegisterTask().execute(company, username, email, password);
            }
        });
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String companyName = params[0];
            String username = params[1];
            String email = params[2];
            String password = params[3];

            try {
                URL url = new URL("http://192.168.137.27/Dangerous/register.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // URL-encode all POST data
                String data = "company=" + URLEncoder.encode(companyName, "UTF-8") +
                        "&username=" + URLEncoder.encode(username, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();

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
                return "{\"success\":false, \"message\":\"Error: " + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                boolean success = json.getBoolean("success");
                String message = json.getString("message");

                Toast.makeText(Registration.this, message, Toast.LENGTH_LONG).show();

                if (success) {
                    // Redirect to login after successful registration
                    startActivity(new Intent(Registration.this, Login.class));
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(Registration.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
