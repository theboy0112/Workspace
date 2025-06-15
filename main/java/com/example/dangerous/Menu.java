package com.example.dangerous;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {

    LinearLayout menuAbout, menuHome, menuCamera, menuSearch, menuLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize the menu items
        menuAbout = findViewById(R.id.menuAbout);
        menuHome = findViewById(R.id.menuHome);
        menuCamera = findViewById(R.id.menuCamera);
        menuSearch = findViewById(R.id.menuSearch);
        menuLogout = findViewById(R.id.menuLogout);

        menuAbout.setOnClickListener(view -> {
            // Create the AlertDialog
            new AlertDialog.Builder(Menu.this)
                    .setTitle("About App")
                    .setMessage("In an ever-evolving world where safety and security are paramount, our Dangerous Weapons Detection App is designed to contribute to peace and order. By leveraging cutting-edge technology, this app detects and identifies dangerous weapons—such as firearms and other weapons—in real-time through a live camera feed. Whether it's in public spaces, schools, or sensitive areas, the app serves as an early-warning system, alerting authorities and security personnel to potential threats before they escalate.\n" +
                            "\n" +
                            "Our mission is to empower communities and law enforcement agencies with tools that help maintain safety and order in a proactive and non-invasive manner. By combining advanced machine learning and image recognition, the app helps ensure that harmful situations are addressed swiftly, ensuring peace of mind for all.")
                    .setPositiveButton("OK", null) // Button to close the dialog
                    .show(); // Ensure the dialog shows up
        });

        // Home -> Main2Activity
        menuHome.setOnClickListener(view -> {
            Intent intent = new Intent(Menu.this, Home.class);
            startActivity(intent);
        });

        // Camera -> MainActivity
        menuCamera.setOnClickListener(view -> {
            Intent intent = new Intent(Menu.this, MainActivity.class);
            startActivity(intent);
        });

        // Search (Still placeholder)
        menuSearch.setOnClickListener(view -> {
            // You can implement a SearchActivity here
            new AlertDialog.Builder(Menu.this)
                    .setTitle("Policía Nacional de Pilipinas")
                    .setMessage("1.CALAPE PNP - 0991\n" +
                                "2.LOON PNP - 3212\n" +
                                "3.TAGBILARAN PNP-7261\n" +
                            "4.TUBIGON PNP - 0951\n" +
                            "5.LOON PNP - 1222\n" +
                            "5.TAGBILARAN PNP-7561\n")

                    .setPositiveButton("OK", null) // Button to close the dialog
                    .show(); // Ensure the dialog shows up
        });


        // Logout -> Confirmation dialog
        menuLogout.setOnClickListener(view -> {
            new AlertDialog.Builder(Menu.this)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit the app?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        finishAffinity(); // Exits the app
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
