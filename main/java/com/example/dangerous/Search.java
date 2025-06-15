package com.example.dangerous;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class Search extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private ArrayList<String> itemList = new ArrayList<>(Arrays.asList
            ("CALAPE PNP - 0991",  "TAGBILARAN PNP - 7261", "TUBIGON PNP - 09511", "LOON PNP - 1222", "TAGBILARAN PNP - 7561"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // You can handle final search action here
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter results as user types
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
