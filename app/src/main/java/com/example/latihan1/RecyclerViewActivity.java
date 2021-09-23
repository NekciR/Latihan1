package com.example.latihan1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.latihan1.adapter.UserRecyclerViewAdapter;
import com.example.latihan1.api.HttpHandler;
import com.example.latihan1.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerViewActivity extends AppCompatActivity implements UserRecyclerViewAdapter.OnItemClickListener,SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener {

    private SearchView svSearchUser;
    private ArrayList<User> userList;
    private ProgressDialog pDialog;
    private Button btnToListView;

    private RecyclerView mRecyclerView;
    private UserRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        init();

        new GetUsers("").execute();
    }

    private void init(){
        userList = new ArrayList<>();

        svSearchUser = findViewById(R.id.svSearchUser);
        svSearchUser.setIconified(false);
        svSearchUser.setOnQueryTextListener(this);
        svSearchUser.setOnCloseListener(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");

        btnToListView = findViewById(R.id.btnToListView);
        btnToListView.setOnClickListener(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new UserRecyclerViewAdapter(userList);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(RecyclerViewActivity.this, userList.get(position).getLogin(), Toast.LENGTH_SHORT).show();
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
        if(v == btnToListView){
            onBackPressed();
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

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
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