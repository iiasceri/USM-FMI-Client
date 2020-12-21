package iiasceri.me.View.Marks;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import iiasceri.me.R;
import iiasceri.me.Utilities.Utilities;
import iiasceri.me.View.SettingsActivity;
import iiasceri.me.View.ToolbarActivity;

public class MarksActivity extends ToolbarActivity {

    Toolbar toolbar;
    private RequestQueue mQueue;
    CatLoadingView mView;

    // Titles of the individual pages (displayed in tabs)
    private final String[] PAGE_TITLES = new String[] {
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "Medii"
    };

    // The fragments that are used as the individual pages
    private final Fragment[] PAGES = new Fragment[] {
            new S1Fragment(),
            new S2Fragment(),
            new S3Fragment(),
            new S4Fragment(),
            new S5Fragment(),
            new S6Fragment(),
            new GPAFragment()
    };

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipe_refresh_marks);
        ViewPager mViewPager = findViewById(R.id.view_pager_marks);


        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!mPrefs.contains("ID")) {
            final Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }
        else {
            // Set the Toolbar as the activity's app bar (instead of the default ActionBar)
            //[1]vert + Toolbar
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

            // add back arrow to toolbar
            if (getSupportActionBar() != null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            // Connect the ViewPager to our custom PagerAdapter. The PagerAdapter supplies the pages
            // (fragments) to the ViewPager, which the ViewPager needs to display.
            // The ViewPager is responsible for sliding pages (fragments) in and out upon user input
            mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
            mViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            pullToRefresh.setEnabled(false);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            pullToRefresh.setEnabled(true);
                            break;
                    }
                    return false;
                }
            });

            // Connect the tabs with the ViewPager (the setupWithViewPager method does this for us in
            // both directions, i.e. when a new tab is selected, the ViewPager switches to this page,
            // and when the ViewPager switches to a new page, the corresponding tab is selected)
            TabLayout tabLayout = findViewById(R.id.tab_layout_marks);
            tabLayout.setupWithViewPager(mViewPager);
            Objects.requireNonNull(tabLayout.getTabAt(6)).select();

            if (!mPrefs.contains("Marks")) {
                getMarksData();
            }
        }
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMarksData();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void getMarksData() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
        //start anim
        mView = new CatLoadingView();
        mView.show(getSupportFragmentManager(), "");
        mView.setCanceledOnTouchOutside(false);
        jsonGetMarks(mPrefs.getString("ID", ""));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    /* PagerAdapter for supplying the ViewPager with the pages (fragments) to display. */
    public class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return PAGES[position];
        }

        @Override
        public int getCount() {
            return PAGES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PAGE_TITLES[position];
        }

    }

    private void jsonGetMarks(String idnp) {

        String url = Utilities.getServerURL(getApplicationContext()) +
                "get_marks?" +
                "id=" + idnp;

        Log.i("URL", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();

                            if (response.has("semestre")) {
                                String json = response.getString("semestre");

                                prefsEditor.putString("Marks", json);
                                prefsEditor.apply();

                                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                final TabLayout tabLayout = findViewById(R.id.tab_layout_marks);
                                Fragment currentFragment = PAGES[tabLayout.getSelectedTabPosition()];
                                if (tabLayout.getSelectedTabPosition() > 0) {
                                    Fragment currentFragmentPrev = PAGES[tabLayout.getSelectedTabPosition() - 1];
                                    ft.detach(currentFragmentPrev).attach(currentFragmentPrev);
                                }
                                ft.detach(currentFragment).attach(currentFragment).commit();
                                mView.dismiss();
                            }
                            else {
                                showAlert();
                            }

                        } catch (JSONException e) {
                            showAlert();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showAlert();
                error.printStackTrace();
            }

        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 100,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    void showAlert() {
        mView.dismiss();
        AlertDialog alertDialog;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MarksActivity.this);
        builder.setMessage("A intervenit o eroare. \nVerificati IDNP din setari si conexiunea la internet");
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
