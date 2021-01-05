package iiasceri.me.View.Schedule;

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
import android.widget.TextView;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import iiasceri.me.R;
import iiasceri.me.Utilities.Utilities;
import iiasceri.me.View.MainActivity;
import iiasceri.me.View.ToolbarActivity;

public class ScheduleActivity extends ToolbarActivity {

    private Boolean offlineMode = true;
    Toolbar toolbar;
    private RequestQueue mQueue;
    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    int hour = calendar.get(Calendar.HOUR);

    CatLoadingView mView;

    // Titles of the individual pages (displayed in tabs)
    private final String[] PAGE_TITLES = new String[]{
            "M",
            "T",
            "W",
            "Th",
            "Fr",
            "~"
    };

    // Încărcarea fragmentelor într-o listă
    private final Fragment[] PAGES = new Fragment[]{
            new MondayFragment(),
            new TuesdayFragment(),
            new WednesdayFragment(),
            new ThursdayFragment(),
            new FridayFragment(),
            new WeekFragment()
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        final TabLayout tabLayout = findViewById(R.id.tab_layout_schedule);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipe_refresh_schedule);
        ViewPager mViewPager = findViewById(R.id.view_pager_schedule);


        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!mPrefs.contains("Schedule"))
            getScheduleData();

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
        tabLayout.setupWithViewPager(mViewPager);

        /*
            Pe parcursul saptamanii arata orarul pentru ziua curenta
            Daca e dupa ora 18, orarul va fi aratat pentru ziua urmatoare

            Daca ne aflam Intre {Vineri:18} si {Duminica:21.59}
            va fi aratat orarul pe toata saptamana la general
        */
        if (!(day == 1 || day == 7)) {
            if (hour >= 18)
                day++;
        }
        if (day == 1)
            day = 7;
        Objects.requireNonNull(tabLayout.getTabAt(day - 2)).select();

        // Set the Toolbar as the activity's app bar (instead of the default ActionBar)

        //[1]vert + Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Utilities.getParitateTitlu());

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getScheduleData();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void getScheduleData() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        JSONObject jo;
        try {
            jo = new JSONObject(mPrefs.getString("User", ""));
            if (offlineMode) {
                jsonGetSchedule(jo.getString("groupName"), "I");
                return;
            }
            mQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
            mQueue.start();
            if (!offlineMode) {
                mView = new CatLoadingView();
                mView.show(getSupportFragmentManager(), "");
            }
            jsonGetSchedule(jo.getString("groupName"), jo.getString("subGroup"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void jsonGetSchedule(String groupName,
                                 String subGroup) throws JSONException {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if (offlineMode) {
            String json = new JSONObject(new OfflineSchedule().getMySchedule(subGroup, "weekly", this)).getString("orar");

            prefsEditor.putString("Schedule", json);
            prefsEditor.apply();
            Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
            finish();
            startActivity(intent);
            return;
        }

        String url = Utilities.getServerURL(getApplicationContext()) +
                "get_schedule?" +
                "groupName=" + groupName +
                "&subGroup=" + subGroup +
                "&scheduleType=weekly";

        Log.i("URL", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("orar")) {
                            String json = response.getString("orar");

                            prefsEditor.putString("Schedule", json);
                            prefsEditor.apply();
                            mView.dismiss();
                            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            final TabLayout tabLayout = findViewById(R.id.tab_layout_schedule);
                            Fragment currentFragment = PAGES[tabLayout.getSelectedTabPosition()];
                            ft.detach(currentFragment).attach(currentFragment).commit();
                        } else {
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
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 100,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    void showAlert() {
        mView.dismiss();
        AlertDialog alertDialog;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(ScheduleActivity.this);
        builder.setMessage("La moment nu este orarul pentru grupa dvs");
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
