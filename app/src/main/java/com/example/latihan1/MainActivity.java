package com.example.latihan1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.latihan1.adapter.UserAdapter;
import com.example.latihan1.api.HttpHandler;
import com.example.latihan1.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, SearchView.OnCloseListener, View.OnClickListener {

    private SearchView svSearchUser;
    private ListView lvUsers;
    private ArrayList<User> userList;
    private ProgressDialog pDialog;
    private Button btnToRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        new GetUsers("").execute();
    }

    private void init(){
        userList = new ArrayList<>();

        lvUsers = findViewById(R.id.lvUserList);
        lvUsers.setEmptyView(findViewById(R.id.rlEmpty));
        lvUsers.setOnItemClickListener(this);

        svSearchUser = findViewById(R.id.svSearchUser);
        svSearchUser.setIconified(false);
        svSearchUser.setOnQueryTextListener(this);
        svSearchUser.setOnCloseListener(this);

        btnToRecyclerView = findViewById(R.id.btnToRecyclerView);
        Log.d("TAG", String.valueOf("init: "+ btnToRecyclerView == null));
        btnToRecyclerView.setOnClickListener(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view == lvUsers) {
            Toast.makeText(MainActivity.this, userList.get(position).getLogin(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onClose() {
        new GetUsers("").execute();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        new GetUsers(query).execute();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v == btnToRecyclerView){
            Intent intent = new Intent(this, RecyclerViewActivity.class);
            startActivity(intent);
        }
    }

    private class GetUsers extends AsyncTask<Void, Void, Void> {

        private String TAG = "test";

        String key;

        public GetUsers(String key) {
            if(key.equals("") || key.isEmpty()){
                this.key = "type:user";
            }else{
                this.key = key;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            fetchUsers();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(pDialog.isShowing()){
                pDialog.dismiss();
            }

            UserAdapter userAdapter = new UserAdapter(MainActivity.this, R.layout.user_row, userList);

            lvUsers.setAdapter(userAdapter);
        }

        private void fetchUsers(){
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("https://api.github.com/search/users?q="+key);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray users = jsonObj.getJSONArray("items");
                    userList.clear();

                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        String login = c.getString("login");
                        String avatarUrl = c.getString("avatar_url");

                        userList.add(new User(login, avatarUrl));
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    }
}


