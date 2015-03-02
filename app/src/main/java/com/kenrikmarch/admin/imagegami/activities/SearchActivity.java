package com.kenrikmarch.admin.imagegami.activities;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.widget.AdapterView;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.view.MenuItem;
import android.widget.Toast;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.util.Log;

import com.kenrikmarch.admin.imagegami.R;
import com.etsy.android.grid.StaggeredGridView;
import com.kenrikmarch.admin.imagegami.support.EndlessScrollListener;
import com.kenrikmarch.admin.imagegami.adapters.ImageResultsAdapter;
import com.kenrikmarch.admin.imagegami.fragments.EditFilterFragment;
import com.kenrikmarch.admin.imagegami.models.FilterSettings;
import com.kenrikmarch.admin.imagegami.models.ImageResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity implements EditFilterFragment.EditFilterDialogListener {

    private ArrayList <ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private StaggeredGridView gvResults;
    private SearchView searchView;
    private ImageView ivBackground;

    private static final String baseURL = "https://ajax.googleapis.com/ajax/services/search/images";
    private static final String version = "?v=1.0";
    private static final String queryParam  = "&q=";
    private static final String resultParam = "&rsz=";
    private static final String startParam  = "&start=";
    private static final int results = 8;
    private FilterSettings filters;
    private String searchTerm;
    private int totalResults;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_search);
        setupViews();
        filters = new FilterSettings();
        imageResults = new ArrayList<ImageResult>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        gvResults.setAdapter(aImageResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult result = imageResults.get(position);
                i.putExtra("image_data",result);
                startActivity(i);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreData(page, totalItemsCount);
            }
        });
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EditFilterFragment editFilterDialog = EditFilterFragment.newInstance("Edit Filters");
        Bundle bundle = new Bundle();
        bundle.putParcelable("filters",filters);
        editFilterDialog.setArguments(bundle);
        editFilterDialog.show(fm, "fragment_edit_filters");
    }

    public void loadMoreData(int page, int totalItemsCount) {
        currentPage = page;
        totalResults = totalItemsCount;
        requestData(searchTerm);
    }

    private void setupViews() {
        gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
        ivBackground = (ImageView) findViewById(R.id.ivBackground);
    }

    private String getURLForQuery(String query, int start, FilterSettings filters) {
        String url = baseURL +version +queryParam +query +resultParam +results +startParam +start;

        if (!filters.size.equals("none")) {
            url = url + "&imgsz=" + filters.size;
        }
        if (!filters.color.equals("none")) {
            url = url + "&imgcolor=" + filters.color;
        }
        if (!filters.type.equals("none")) {
            url = url + "&imgtype=" + filters.type;
        }
        if (!filters.site.equals("")) {
            url = url + "&as_sitesearch=" + filters.site;
        }
        Log.i("URL",url);

        return url;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
                Toast.makeText(getApplicationContext(),"Searching For: " + query, Toast.LENGTH_SHORT).show();
                searchView.clearFocus();
                requestData(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void requestData(final String query) {
        if (isNetworkAvailable()) {
            if (isOnline()) { Log.i("Online","Ping Success"); }
            if (!query.equals(searchTerm)) { aImageResults.clear(); currentPage = 0; totalResults = 0; }
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(getURLForQuery(query,totalResults,filters),new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    ivBackground.setVisibility(View.GONE);
                    super.onSuccess(statusCode, headers, response);
                    updateResults(response,query);
                }
            });
        } else {
            Toast.makeText(this,"Network Offline", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateResults(JSONObject response, String query) {
        JSONArray imageResultsJson = null;
        try {
            imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
            searchTerm = query;
            aImageResults.addAll(ImageResult.fromJsonArray(imageResultsJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null) && activeNetworkInfo.isConnectedOrConnecting();
    }

    public Boolean isOnline() {
        try {
            // Either no Internet connection
            // or Get your guns because if Google is down the Zombie Apocalypse has begun.
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 http://google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filters:
                showEditDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFinishEditDialog(FilterSettings settings) {
        filters = settings;
    }
}
