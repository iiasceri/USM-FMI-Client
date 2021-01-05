package iiasceri.me.View;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import iiasceri.me.R;

public abstract class ToolbarActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //[1]vert + Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    //[1]Trei puncte(verticale): crearea optiunilor "trimite feedback", "Detalii aplicatie".
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //[1]Trei puncte(verticale): executarea codului dorit la selectarea optiunilor alese
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.menu_feedback) {
//            Toast.makeText(getApplicationContext(), "Thanks ;)", Toast.LENGTH_SHORT).show();
//            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.yea_boii);
//            mediaPlayer.start();
//        }
        if (id == R.id.menu_about) {
            AlertDialog alertDialog;
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(ToolbarActivity.this);
            builder.setTitle("App Details");
            builder.setMessage( ""
                                + "\nAuthor: Iascerinschi Ion"
                                + "\nVersion: 2.0 Offline");
            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
