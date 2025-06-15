package com.example.dangerous;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {

    ImageButton cameraButton, searchButton, homeButton, menuButton, phoneButton;
    private String username;  // Store username here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home); // Your layout file

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get username passed from Login activity
        username = getIntent().getStringExtra("username");



        // Link buttons
        cameraButton = findViewById(R.id.imageButtonCamera);
        searchButton = findViewById(R.id.imageButtonSearch);
        homeButton = findViewById(R.id.imageButtonHome);
        menuButton = findViewById(R.id.imageButtonMenu);
        phoneButton = findViewById(R.id.imageButtonPhone);

        // Set click listeners to navigate
        cameraButton.setOnClickListener(view ->
                startActivity(new Intent(Home.this, MainActivity.class)));

        searchButton.setOnClickListener(view ->
                startActivity(new Intent(Home.this, Search.class)));

        homeButton.setOnClickListener(view ->
                startActivity(new Intent(Home.this, Home.class)));

        menuButton.setOnClickListener(view ->
                startActivity(new Intent(Home.this, Menu.class)));

        phoneButton.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, Phone.class); // or Admin.this
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("companyName", getIntent().getStringExtra("companyName"));
            startActivity(intent);


        });
    }
}
