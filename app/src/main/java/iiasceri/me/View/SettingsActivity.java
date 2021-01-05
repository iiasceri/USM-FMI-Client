package iiasceri.me.View;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.github.florent37.materialtextfield.MaterialTextField;
import com.marozzi.roundbutton.RoundButton;

import java.util.Objects;

import iiasceri.me.R;
import iiasceri.me.View.Marks.MarksActivity;

public class SettingsActivity extends ToolbarActivity {

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


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
        toolbarTitle.setText("Setări");

        MaterialTextField idnp = findViewById(R.id.IDMaterialTextFieldSettings);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!mPrefs.contains("ID")) {
            AlertDialog alertDialog;
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setMessage("Introduceți IDNP dvs pentru a putea vizualiza notele");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MaterialTextField idnp = findViewById(R.id.IDMaterialTextFieldSettings);
                    idnp.setHasFocus(true);
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }
        else {
            String idnpString = mPrefs.getString("ID", "");
            assert idnpString != null;
            String fragmentString =
                    String.valueOf(idnpString.charAt(0)) +
                            idnpString.charAt(1) +
                            idnpString.charAt(2) +
                            "∙∙∙∙∙∙∙" +
                            idnpString.charAt(idnpString.length() - 3) +
                            idnpString.charAt(idnpString.length() - 2) +
                            idnpString.charAt(idnpString.length() - 1);

            idnp.getEditText().setText(fragmentString);
        }
        idnp.setHasFocus(true);
        final EditText idnpEditText = idnp.findViewById(R.id.IDEditTextSettings);

        idnpEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    idnpEditText.setText("");
                    MaterialTextField idnp = findViewById(R.id.IDMaterialTextFieldSettings);
                    idnp.expand();
                    idnp.setHasFocus(true);
                }

                return true;
            }
        });

        RoundButton save = findViewById(R.id.saveRoundButtonSettings);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTextField idnpTMP = findViewById(R.id.IDMaterialTextFieldSettings);
                String idnpStr = idnpTMP.getEditText().getText().toString();
                if (!idnpStr.matches("[0-9]+") || idnpStr.length() != 13) {
                    AlertDialog alertDialog;
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setMessage("Introduceți IDNP de 13 cifre.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            idnpEditText.setText("");
                            MaterialTextField idnp = findViewById(R.id.IDMaterialTextFieldSettings);
                            idnp.expand();
                            idnp.setHasFocus(true);
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    String idnpStringToPrefs = idnpTMP.getEditText().getText().toString();

                    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString("ID", idnpStringToPrefs).apply();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });

        RoundButton changeServer = findViewById(R.id.RBSettingsNewServer);

        changeServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                final View editDialogView = li.inflate(R.layout.edit_dialogs, null);
                AlertDialog alertDialog;
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setView(editDialogView);
                builder.setMessage("Introduceți IP Adresa");
                builder.setPositiveButton("Salvează", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText serverEditText = editDialogView.findViewById(R.id.serverEditTextSettings);

                        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();

                        prefsEditor.putString("Server", serverEditText.getText().toString());

                        prefsEditor.apply();
                    }
                });
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (item.getItemId() == android.R.id.home) {
            if (Objects.equals(mPrefs.getString("LastActivity", ""), "MarksActivity")) {
                final Intent intent = new Intent(getApplicationContext(), MarksActivity.class);
                startActivity(intent);
            }
            else if (Objects.equals(mPrefs.getString("LastActivity", ""), "AnotherActivity")) {
                //Replace MainActivity.class with AnotherActivity
                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else {
                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
