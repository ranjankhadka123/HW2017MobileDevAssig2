package com.example.rkhadka.itunessearch;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        final EditText mEditText = new EditText(this);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Search for tracks")
                .setView(mEditText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String query = mEditText.getText().toString();
                        FragmentManager fm = getSupportFragmentManager();
                        Fragment itunesSongListFragment = new ItunesSongListFragment();
                        ItunesSongSource.setQuery(query);
                        fm.beginTransaction().replace(R.id.container, itunesSongListFragment).commit();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).create();

        alertDialog.show();
        return true;
    }


}
