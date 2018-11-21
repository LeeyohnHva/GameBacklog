package com.example.leey_.gamebacklog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DateFormat;
import java.util.Date;

public class ManageGameActivity extends AppCompatActivity {

    private EditText mEditTitle;
    private EditText mEditPlatform;
    private EditText mEditNotes;
    private Spinner mSpinnerStatus;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditTitle = findViewById(R.id.editView_title);
        mEditPlatform = findViewById(R.id.editView_platform);
        mEditNotes = findViewById(R.id.editView_notes);
        mSpinnerStatus = findViewById(R.id.spinner_status);

        Game gameUpdate =  getIntent().getParcelableExtra(MainActivity.EXTRA_GAME);
        //If editing a game set the text fields to the game details.
        if(gameUpdate != null){
            mEditTitle.setText(gameUpdate.getTitle());
            mEditPlatform.setText(gameUpdate.getPlatform());
            mEditNotes.setText(gameUpdate.getNotes());
            mSpinnerStatus.setSelection(gameUpdate.getStatus());
        }else{
            gameUpdate = new Game("","","",-1,"");
        }

        id = gameUpdate.getId();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleText = mEditTitle.getText().toString();
                String platformText = mEditPlatform.getText().toString();
                String notesText = mEditNotes.getText().toString();
                int statusPos = mSpinnerStatus.getSelectedItemPosition();

                Intent resultIntent = new Intent();
                DateFormat dateF = DateFormat.getDateInstance();
                Date now = new Date(System.currentTimeMillis());

                Game game = new Game(titleText,platformText,notesText,statusPos, dateF.format(now));
                //Set the id for the game so that it updates the correct one
                game.setId(id);
                resultIntent.putExtra(MainActivity.EXTRA_GAME, game);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

}
