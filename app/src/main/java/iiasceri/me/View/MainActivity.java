package iiasceri.me.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import iiasceri.me.R;
import iiasceri.me.Utilities.Utilities;
import iiasceri.me.View.Marks.MarksActivity;
import iiasceri.me.View.Schedule.ScheduleActivity;

public class MainActivity extends ToolbarActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        //[1]vert + Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //[2]ori
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        View hView =  navigationView.getHeaderView(0);
        TextView nameTextView = hView.findViewById(R.id.nameTextViewDrawer);
        ImageView genderImageView = hView.findViewById(R.id.genderImageViewDrawer);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        JSONObject obj;
        String name = "Linda Figlind";
        String gender = "";

        try {
            String json = mPrefs.getString("User", "");
            obj = new JSONObject(json);
            name = obj.getString("familyName");
            gender = obj.getString("gender");

            Log.i("name", name);
            Log.i("gender", gender);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        nameTextView.setText(name);
        if (gender.equals("male"))
            genderImageView.setImageDrawable(getResources().getDrawable(R.drawable.male_user));

        TextView t4 = findViewById(R.id.textView4);
        TextView t5 = findViewById(R.id.textView5);

        t4.setAlpha(0f);
        t5.setAlpha(0f);

        //[3]browser
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition,
                                        String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        if (Utilities.checkConnection(getApplicationContext())) {
            webView.loadUrl("http://fmi.usm.md");
//            SharedPreferences.Editor prefsEditor = mPrefs.edit();
//            prefsEditor.putString("WebViewWasLoaded", "yes");
//            prefsEditor.apply();
        } else {

//            if (!mPrefs.contains("WebViewWasLoaded")) {
                t4.setAlpha(1f);
                t5.setAlpha(1f);
//            }

            Snackbar.make(findViewById(R.id.layoutMain), "Verificati Conexiunea La Internet Pentru a Putea Inoi Datele! (Nu e obligatoriu daca deja le-ati descarcat)", Snackbar.LENGTH_LONG).show();
        }

        // Load all contact details:
        Map<String, String> detailsPhone = new LinkedHashMap<>();
        detailsPhone.put("N.Plesca", "(078) 717 525");
        detailsPhone.put("M.Butnaru", "(067) 213 456");
        detailsPhone.put("G.Sturza", "(068) 213 456");
        detailsPhone.put("O.Cerbu", "(069) 444 555");
        detailsPhone.put("V.Carhana", "(060) 677 533");
        detailsPhone.put("T.Pasa", "(069) 46 30 40");
        detailsPhone.put("Gh.Capatana", "(061) 999 822");
        detailsPhone.put("I.Epifanova", "(078) 717 525");
        detailsPhone.put("V.Grigorcea", "(067) 213 456");
        detailsPhone.put("C.Isacova", "(060) 677 533");
        detailsPhone.put("L.Novac", "(060) 677 533");
        detailsPhone.put("M.Croitor", "(060) 677 533");
        detailsPhone.put("T.Capcelea", "(060) 677 533");
        detailsPhone.put("G.Marin", "(060) 677 533");
        JSONObject jsonDetailsPhone = new JSONObject(detailsPhone);

        Map<String, String> detailsMail = new LinkedHashMap<>();
        detailsMail.put("N.Plesca", "natalia.plesca@exemplu.com");
        detailsMail.put("M.Butnaru", "m.butnaru@exemplu.com");
        detailsMail.put("G.Sturza", "greta.sturza@exemplu.com");
        detailsMail.put("O.Cerbu", "olga.cerbu@exemplu.com");
        detailsMail.put("V.Carhana", "v.carhana@exemplu.com");
        detailsMail.put("T.Pasa", "tatiana.pasa@yahoo.com");
        detailsMail.put("Gh.Capatana", "gh.capatana@gmail.com");
        detailsMail.put("I.Epifanova", "irina.epifanova@gmail.com");
        detailsMail.put("V.Grigorcea", "v.grigorcea@usm.md");
        detailsMail.put("C.Isacova", "c.isacova@yahoo.com");
        detailsMail.put("L.Novac", "liudmila.novac@gmail.com");
        detailsMail.put("M.Croitor", "m.croitor@mail.md");
        detailsMail.put("T.Capcelea", "titu.capcelea@gmail.com");
        detailsMail.put("G.Marin", "g.marin@mail.ru");
        JSONObject jsonDetailsMail = new JSONObject(detailsMail);

        List<String> profList = new ArrayList<>();
        profList.add("N.Plesca");
        profList.add("M.Butnaru");
        profList.add("G.Sturza");
        profList.add("O.Cerbu");
        profList.add("V.Carhana");
        profList.add("T.Pasa");
        profList.add("Gh.Capatana");
        profList.add("I.Epifanova");
        profList.add("V.Grigorcea");
        profList.add("C.Isacova");
        profList.add("L.Novac");
        profList.add("M.Croitor");
        profList.add("T.Capcelea");
        profList.add("G.Marin");
        JSONArray proffessorsJsonArray = new JSONArray(profList);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("Professors", proffessorsJsonArray.toString());
        prefsEditor.putString("PhoneByName", jsonDetailsPhone.toString());
        prefsEditor.putString("MailByName", jsonDetailsMail.toString());
        prefsEditor.apply();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //[2]Trei linii(orizontale): executarea codului la select. optiunilor gen "orar" etc.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();


        if (id == R.id.nav_subheader_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.navigation_schedule) {
//            Toast.makeText(getApplicationContext(), "Schedule", Toast.LENGTH_SHORT).show();
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.orarhh);
            mediaPlayer.start();

            Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
            startActivity(intent);
        }
        if (id == R.id.navigation_schedule_exams) {
            Intent intent = new Intent(getApplicationContext(), ExamScheduleActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.navigation_marks) {
            Intent intent = new Intent(getApplicationContext(), MarksActivity.class);
            startActivity(intent);
        }
//        else if (id == R.id.nav_subheader_messages) {
//            Toast.makeText(getApplicationContext(), "Messages", Toast.LENGTH_SHORT).show();
//        }
        else if (id == R.id.nav_subheader_logout) {
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.remove("User");
            prefsEditor.remove("ExamSchedule");
            prefsEditor.remove("Schedule");
            prefsEditor.remove("ID");
            prefsEditor.remove("Marks");
            prefsEditor.apply();

            Intent intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
                startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}
