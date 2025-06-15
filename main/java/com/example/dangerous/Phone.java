package com.example.dangerous;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Phone extends AppCompatActivity {

    private EditText dialedNumber;
    private String companyName;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        companyName = getIntent().getStringExtra("companyName");
        username = getIntent().getStringExtra("username");

        Log.d("DEBUG", "Received Company: " + companyName);
        Log.d("DEBUG", "Received Username: " + username);

        dialedNumber = findViewById(R.id.dialedNumber);

        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2,
                R.id.button3, R.id.button4, R.id.button5,
                R.id.button6, R.id.button7, R.id.button8,
                R.id.button9, R.id.buttonStar, R.id.buttonHash
        };

        View.OnClickListener numberClickListener = v -> {
            Button b = (Button) v;
            String current = dialedNumber.getText().toString();
            dialedNumber.setText(current + b.getText().toString());
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        Button deleteButton = findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(v -> {
            String current = dialedNumber.getText().toString();
            if (!current.isEmpty()) {
                dialedNumber.setText(current.substring(0, current.length() - 1));
            }
        });

        Button callButton = findViewById(R.id.buttonCall);
        callButton.setOnClickListener(v -> {
            String number = dialedNumber.getText().toString();

            if (!number.isEmpty()) {
                // Allowed prefixes list
                String[] allowedPrefixes = {"0991", "3212", "7261", "0951", "1222", "7561"};

                boolean valid = false;
                for (String prefix : allowedPrefixes) {
                    if (number.startsWith(prefix)) {
                        valid = true;
                        break;
                    }
                }

                if (valid) {
                    sendCallLogToServer(companyName, number);

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number));
                    startActivity(intent);
                } else {
                    // Show popup message if number is invalid
                    Toast.makeText(Phone.this, "The number you have dialed is enkz", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void sendCallLogToServer(String companyName, String number) {
        new Thread(() -> {
            try {
                Log.d("DEBUG", "Sending Company: " + companyName + ", number: " + number);

                URL url = new URL("http://192.168.137.27/Dangerous/log_call.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "companyName=" + Uri.encode(companyName) + "&number=" + Uri.encode(number);

                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("DEBUG", "Response code: " + responseCode);

                conn.disconnect();
            } catch (Exception e) {
                Log.e("ERROR", "Error sending call log", e);
            }
        }).start();
    }
}
