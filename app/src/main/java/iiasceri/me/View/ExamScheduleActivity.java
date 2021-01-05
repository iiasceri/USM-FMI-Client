package iiasceri.me.View;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import iiasceri.me.Model.Pojo;
import iiasceri.me.R;
import iiasceri.me.Utilities.Utilities;
import iiasceri.me.View.Schedule.OfflineSchedule;

public class ExamScheduleActivity extends ToolbarActivity {

    private List<Object> mRecyclerViewItems = new ArrayList<>();
    private RequestQueue mQueue;
    CatLoadingView mView;
    private Boolean offlineMode = true;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_schedule);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipe_refresh_exam_schedule);

        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
        final SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        JSONObject jo;
        try {
            jo = new JSONObject(mPrefs.getString("User", ""));

            if (!mPrefs.contains("ExamSchedule")) {
                if (!offlineMode) {
                    mView = new CatLoadingView();
                    mView.show(getSupportFragmentManager(), "");
                }
                jsonGetExamSchedule(jo.getString("groupName"), jo.getString("subGroup"));
            }
            else
                addMenuItemsFromJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //[1]vert + Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Have fun during exams!");

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    JSONObject jo = new JSONObject(mPrefs.getString("User", ""));
                    mView = new CatLoadingView();
                    mView.show(getSupportFragmentManager(), "");
                    jsonGetExamSchedule(jo.getString("groupName"), jo.getString("subGroup"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void addMenuItemsFromJson() {

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new RecyclerViewAdapterExamSchedule(getApplicationContext(), mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        try {

            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String jsonDataString = mPrefs.getString("ExamSchedule", "");
            JSONArray menuItemsJsonArray = new JSONArray(jsonDataString);

            if (!mRecyclerViewItems.isEmpty())
                mRecyclerViewItems.clear();

            for (int i = 0; i < menuItemsJsonArray.length(); ++i) {

                JSONObject menuItemObject = menuItemsJsonArray.getJSONObject(i);

                String menuItemName = "(" + menuItemObject.getString("ora") + ")  " +  menuItemObject.getString("ziExamen") + " " + menuItemObject.getString("dataExamen");
                String menuItemDescription = menuItemObject.getString("disciplina");
                String menuItemPrice =menuItemObject.getString("cabinet");
                String menuItemCategory = menuItemObject.getString("asistent");
                String menuItemImageName = "menu_item_image";

                Pojo pojo = new Pojo(menuItemName, menuItemDescription, menuItemPrice,
                        menuItemCategory, menuItemImageName);

                mRecyclerViewItems.add(pojo);
            }
        } catch (Exception exception) {
            Log.e(ExamScheduleActivity.class.getName(), "Unable to parse JSON file.", exception);
        }
    }


    private void jsonGetExamSchedule(String groupName,
                                     String subGroup) throws JSONException {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if (offlineMode) {
            String json = new JSONObject(new OfflineSchedule().getMySchedule(subGroup, "exam", this)).getString("orar");
            prefsEditor.putString("ExamSchedule", json);
            prefsEditor.apply();
            return;
        }

        String url = Utilities.getServerURL(getApplicationContext()) +
                "get_schedule?" +
                "groupName=" + groupName +
                "&subGroup=" + subGroup +
                "&scheduleType=exam";

        Log.i("URL", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("orar")) {
                            Log.i("exam schedule", response.getString("orar"));
                            String json = response.getString("orar");
                            prefsEditor.putString("ExamSchedule", json);
                            prefsEditor.apply();

                            addMenuItemsFromJson();
                            mView.dismiss();
                        }
                        else {
                            showAlert();
                        }

                    } catch (JSONException e) {
                        showAlert();
                        e.printStackTrace();
                    }
                }, error -> {
                    showAlert();
                    error.printStackTrace();
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);

    }

    void showAlert() {
        mView.dismiss();
        AlertDialog alertDialog;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(ExamScheduleActivity.this);
        builder.setMessage("La moment nu este orarul sesiunii pentru grupa dvs");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

}