package iiasceri.me.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.material.snackbar.Snackbar;
import com.marozzi.roundbutton.RoundButton;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import iiasceri.me.R;
import iiasceri.me.Utilities.Utilities;

//import com.github.florent37.materialtextfield.MaterialTextField;

public class LoginActivity extends ToolbarActivity {

    String username = "";
    String password = "";

    int LONG_ANIMATION_DURATION = 1200;
    int MEDIUM_ANIMATION_DURATION = 800;
    int SHORT_ANIMATION_DURATION = 220;
    boolean flag = false;

    private RequestQueue mQueue;

    CatLoadingView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Primim height, width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        //UI
        final ImageView imageView = findViewById(R.id.fmi_logo_view_login);
        final RoundButton loginButton = findViewById(R.id.login2Button);
        final MaterialTextField materialTextField = findViewById(R.id.usernameMaterialTextFieldLogin);
        final MaterialTextField materialTextField2 = findViewById(R.id.passwordMaterialTextFieldLogin);

        materialTextField.setAlpha(0f);
        materialTextField2.setAlpha(0f);
        loginButton.setAlpha(0f);

        /*[1]vert + Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/

        imageView.animate().scaleX(0.5f).scaleY(0.5f).setDuration(LONG_ANIMATION_DURATION);
        imageView.animate().translationXBy(-((float)width/3)).setDuration(LONG_ANIMATION_DURATION);
        imageView.animate().translationYBy(-((float)height/10) - 15).setDuration(LONG_ANIMATION_DURATION);

        //Animatia txt & buton
        materialTextField.animate().alpha(1f).setDuration(LONG_ANIMATION_DURATION);
        loginButton.animate().alpha(1f).setDuration(LONG_ANIMATION_DURATION);

        materialTextField.animate().translationYBy((float)-height/2 + 200).setDuration(LONG_ANIMATION_DURATION);
        materialTextField2.animate().translationYBy((float)-height/2 + 200).setDuration(LONG_ANIMATION_DURATION);
        materialTextField2.animate().translationXBy(1000).setDuration(MEDIUM_ANIMATION_DURATION);
        loginButton.animate().translationYBy((float)-height/2 + 190).setDuration(LONG_ANIMATION_DURATION);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                materialTextField.setHasFocus(true);
            }
        }, LONG_ANIMATION_DURATION);


        mQueue = Volley.newRequestQueue(this);
        mQueue.start();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Primim height, width
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                MaterialTextField usernameMTF = findViewById(R.id.usernameMaterialTextFieldLogin);
                MaterialTextField passwordMTF = findViewById(R.id.passwordMaterialTextFieldLogin);

                username = usernameMTF.getEditText().getText().toString();
                password = passwordMTF.getEditText().getText().toString();

                if (username.isEmpty()) {
                    errorToasts("Introduceți Numele de Utilizator");
                    animateMailField(view);
                }
                else {


                    if (flag) {
                        if (password.isEmpty()) {
                            errorToasts("Introduceți Parola");
                            animatePasswordField(view);
                        } else {
                            mView = new CatLoadingView();
                            mView.show(getSupportFragmentManager(), "");
                            jsonLoginUser(username, password);
                        }
                    } else {
                        //Animatia: bring password field and get rid of mail field
                        passwordMTF.expand();
                        passwordMTF.setHasFocus(true);
                        usernameMTF.animate().translationXBy(-1000).setDuration(MEDIUM_ANIMATION_DURATION);
                        usernameMTF.animate().alpha(0f).setDuration(MEDIUM_ANIMATION_DURATION);
                        passwordMTF.animate().translationXBy(-1000).setDuration(MEDIUM_ANIMATION_DURATION);
                        passwordMTF.animate().alpha(1f).setDuration(MEDIUM_ANIMATION_DURATION);
                        flag = true;
                    }
                }
            }
        });

        if (Utilities.checkConnection(getApplicationContext())) {
        } else {
            Snackbar.make(findViewById(R.id.layoutLogin), "Verificati Conexiunea la Internet!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void jsonLoginUser(String username,
                               String password) {

        String url = Utilities.getServerURL(getApplicationContext()) +
                "login?" +
                "username=" + username +
                "&password=" + password;

        Log.i("URL", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();

                            if (response.getString("status").equals("success")) {

                                Log.i("user", response.getString("user"));
                                String json = response.getString("user");

                                prefsEditor.putString("User", json);
                                prefsEditor.putString("LoginSuccess", "yes");
                                prefsEditor.apply();

                                mView.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                shoAlertDialog("Utilizator sau parolă greșită");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.toString();
                if (errorMsg.contains("com.android.volley.NoConnectionError")) {
                    shoAlertDialog("Pentru a continua aveti nevoie de Internet");
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    public void errorToasts(String hintAsError) {
        Toast toast= Toast.makeText(getApplicationContext(),
                hintAsError, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public void animateMailField(View view) {
        final MaterialTextField materialTextField = findViewById(R.id.usernameMaterialTextFieldLogin);
        materialTextField.animate().translationXBy(70).setDuration(SHORT_ANIMATION_DURATION);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                materialTextField.animate().translationXBy(-140).setDuration(SHORT_ANIMATION_DURATION);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialTextField.animate().translationXBy(70).setDuration(SHORT_ANIMATION_DURATION);

                    }
                }, SHORT_ANIMATION_DURATION + 90);

            }
        }, SHORT_ANIMATION_DURATION + 90);
    }

    public void animatePasswordField(View view) {
        final MaterialTextField materialTextField = findViewById(R.id.passwordMaterialTextFieldLogin);
        materialTextField.animate().translationXBy(70).setDuration(SHORT_ANIMATION_DURATION);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                materialTextField.animate().translationXBy(-140).setDuration(SHORT_ANIMATION_DURATION);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialTextField.animate().translationXBy(70).setDuration(SHORT_ANIMATION_DURATION);

                    }
                }, SHORT_ANIMATION_DURATION);

            }
        }, SHORT_ANIMATION_DURATION);
    }

    public void shoAlertDialog(String message) {
        mView.dismiss();
        MaterialTextField materialTextField2 = findViewById(R.id.passwordMaterialTextFieldLogin);
        materialTextField2.getEditText().setText("");
        AlertDialog alertDialog;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MaterialTextField usernameMTF = findViewById(R.id.usernameMaterialTextFieldLogin);
                MaterialTextField passwordMTF = findViewById(R.id.passwordMaterialTextFieldLogin);

                usernameMTF.animate().translationXBy(1000).setDuration(MEDIUM_ANIMATION_DURATION);
                usernameMTF.animate().alpha(1f).setDuration(MEDIUM_ANIMATION_DURATION);
                passwordMTF.animate().translationXBy(1000).setDuration(MEDIUM_ANIMATION_DURATION);
                passwordMTF.animate().alpha(0f).setDuration(MEDIUM_ANIMATION_DURATION);

                usernameMTF.setHasFocus(true);
                flag = false;
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }
}
