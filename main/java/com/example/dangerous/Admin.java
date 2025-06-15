package com.example.dangerous;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Admin extends AppCompatActivity {

    private ListView listView;
    private ArrayList<CallLog> callLogs;
    private CallLogAdapter adapter;
    private SearchView searchVieww;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listView = findViewById(R.id.adminListView);
        searchVieww = findViewById(R.id.searchVieww);

        callLogs = new ArrayList<>();
        adapter = new CallLogAdapter(this, callLogs);
        listView.setAdapter(adapter);

        new FetchAdminDataTask().execute();

        searchVieww.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    // Adapter class
    private class CallLogAdapter extends BaseAdapter implements Filterable {

        private Context context;
        private ArrayList<CallLog> originalData;
        private ArrayList<CallLog> filteredData;
        private ItemFilter filter = new ItemFilter();

        public CallLogAdapter(Context context, ArrayList<CallLog> data) {
            this.context = context;
            this.originalData = data;
            this.filteredData = data;
        }

        @Override
        public int getCount() {
            return filteredData.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return filteredData.get(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.call_log_item, parent, false);
                holder = new ViewHolder();
                holder.companyText = convertView.findViewById(R.id.callerNameText);
                holder.numberText = convertView.findViewById(R.id.phoneNumberText);
                holder.callTimeText = convertView.findViewById(R.id.callTimeText);
                holder.updateButton = convertView.findViewById(R.id.updateButton);
                holder.deleteButton = convertView.findViewById(R.id.deleteButton);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CallLog log = filteredData.get(position);

            holder.companyText.setText(log.companyName);
            holder.numberText.setText(log.number);
            holder.callTimeText.setText(log.callTime);

            holder.updateButton.setOnClickListener(v -> showUpdateDialog(log));
            holder.deleteButton.setOnClickListener(v -> confirmDelete(log));

            return convertView;
        }

        class ViewHolder {
            TextView companyText, numberText, callTimeText;
            Button updateButton, deleteButton;
        }

        // Filter implementation for search
        @Override
        public Filter getFilter() {
            return filter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();
                final ArrayList<CallLog> list = originalData;
                final ArrayList<CallLog> nlist = new ArrayList<>();

                for (CallLog log : list) {
                    if (log.companyName.toLowerCase().contains(filterString) ||
                            log.number.toLowerCase().contains(filterString) ||
                            log.callTime.toLowerCase().contains(filterString)) {
                        nlist.add(log);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<CallLog>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    private void showUpdateDialog(CallLog log) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Record");

        EditText inputCompany = new EditText(this);
        inputCompany.setHint("Company Name");
        inputCompany.setText(log.companyName);

        EditText inputNumber = new EditText(this);
        inputNumber.setHint("Number");
        inputNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        inputNumber.setText(log.number);

        EditText inputCallTime = new EditText(this);
        inputCallTime.setHint("Call Time");
        inputCallTime.setText(log.callTime);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(inputCompany);
        layout.addView(inputNumber);
        layout.addView(inputCallTime);

        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newCompany = inputCompany.getText().toString().trim();
            String newNumber = inputNumber.getText().toString().trim();
            String newCallTime = inputCallTime.getText().toString().trim();

            new UpdateAdminDataTask(log.id, newCompany, newNumber, newCallTime).execute();
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void confirmDelete(CallLog log) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this record?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new DeleteAdminDataTask(log.id).execute();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Fetch task
    private class FetchAdminDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.137.27/Dangerous/get_admin_data.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(Admin.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject json = new JSONObject(result);
                boolean success = json.getBoolean("success");

                if (!success) {
                    Toast.makeText(Admin.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONArray data = json.getJSONArray("data");
                callLogs.clear();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    callLogs.add(new CallLog(
                            item.getInt("id"),
                            item.getString("companyName"),
                            item.getString("number"),
                            item.getString("call_time")
                    ));
                }

                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Admin.this, "Error parsing data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateAdminDataTask extends AsyncTask<Void, Void, String> {
        private int id;
        private String companyName, number, callTime;

        public UpdateAdminDataTask(int id, String companyName, String number, String callTime) {
            this.id = id;
            this.companyName = companyName;
            this.number = number;
            this.callTime = callTime;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.137.27/Dangerous/update_admin_data.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "id=" + id +
                        "&companyName=" + URLEncoder.encode(companyName, "UTF-8") +
                        "&number=" + URLEncoder.encode(number, "UTF-8") +
                        "&call_time=" + URLEncoder.encode(callTime, "UTF-8");

                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "{\"success\":false,\"message\":\"" + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                boolean success = json.getBoolean("success");
                String message = json.getString("message");
                Toast.makeText(Admin.this, message, Toast.LENGTH_SHORT).show();

                if (success) {
                    new FetchAdminDataTask().execute(); // Refresh list
                }
            } catch (Exception e) {
                Toast.makeText(Admin.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeleteAdminDataTask extends AsyncTask<Void, Void, String> {
        private int id;

        public DeleteAdminDataTask(int id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.137.27/Dangerous/delete_admin_data.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "id=" + id;

                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "{\"success\":false,\"message\":\"" + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                boolean success = json.getBoolean("success");
                String message = json.getString("message");
                Toast.makeText(Admin.this, message, Toast.LENGTH_SHORT).show();

                if (success) {
                    new FetchAdminDataTask().execute(); // Refresh list
                }
            } catch (Exception e) {
                Toast.makeText(Admin.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
