package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NewsAdapter.ListItemClickListener{

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private NewsAdapter mAdapter;
    private EditText mCategory;
    private String query = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler);
        mProgressBar = findViewById(R.id.pb_loading);
        mCategory = findViewById(R.id.category);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        loadData();
        mCategory.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    query = mCategory.getText().toString();
                    mCategory.clearFocus();
                    hideKeyboard(view);
                    loadData();
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void startAdapter(ArrayList<NewsData> newsData) {
        mAdapter = new NewsAdapter(newsData,this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://gnews.io/api/v4/top-headlines?token=c07dd78adb45e0626e3a8534c82be00e&lang=en&country=in&q="+query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        getStringFromRaw(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getStringFromRaw(String raw) {
        ArrayList<NewsData> res= new ArrayList<>();
        try {
            JSONObject response = new JSONObject(raw);
            JSONArray jsonArray = response.getJSONArray("articles");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String image = obj.getString("image");
                String title = obj.getString("title");
                JSONObject srcobj = obj.getJSONObject("source");
                String source = srcobj.getString("name");
                String url = obj.getString("url");
                res.add(i,new NewsData(image,title,source,url));
            }
            startAdapter(res);
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, ""+e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
}