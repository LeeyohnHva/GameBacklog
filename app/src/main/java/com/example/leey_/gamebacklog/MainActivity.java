package com.example.leey_.gamebacklog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_GAME = "Game";

    public static final int ADD_REQUESTCODE = 1234;
    private static final int EDIT_REQUESTCODE = 7331;


    public static AppDatabase db;
    private RecyclerView mRecyclerView;
    private GameAdapter mAdapter;

    private List<Game> mGames;


    public final static int TASK_GET_ALL_GAME = 0;
    public final static int TASK_DELETE_GAME = 1;
    public final static int TASK_UPDATE_GAME = 2;
    public final static int TASK_INSERT_GAME = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = AppDatabase.getInstance(this);

        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        mAdapter = new GameAdapter(mGames, new GameAdapter.GameClickListener() {
            @Override
            public void gameOnClick(int i) {
                Intent intent = new Intent(MainActivity.this, ManageGameActivity.class);
                intent.putExtra(EXTRA_GAME, mGames.get(i));
                startActivityForResult(intent,EDIT_REQUESTCODE);
            }
        }, getResources());

        mRecyclerView.setAdapter(mAdapter);

        new GameAsyncTask(TASK_GET_ALL_GAME).execute();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }

                    //Called when a user swipes left or right on a ViewHolder
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //Get the index corresponding to the selected position
                        int position = (viewHolder.getAdapterPosition());
                        Game game = mGames.get(position);
                        new GameAsyncTask(TASK_DELETE_GAME).execute(game);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,ManageGameActivity.class),ADD_REQUESTCODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                Game game = data.getParcelableExtra(MainActivity.EXTRA_GAME);
                new GameAsyncTask(TASK_INSERT_GAME).execute(game);
            }
        }else if(requestCode == EDIT_REQUESTCODE){
            if (resultCode == RESULT_OK) {
                Game game = data.getParcelableExtra(MainActivity.EXTRA_GAME);
                new GameAsyncTask(TASK_UPDATE_GAME).execute(game);
            }
        }
    }

    public void onGameDbUpdated(List list) {
        mGames = list;
        updateUI();
    }

    public void updateUI(){
        mAdapter.swapList(mGames);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class GameAsyncTask extends AsyncTask<Game, Void, List> {


        private int taskCode;


        public GameAsyncTask(int taskCode) {
            this.taskCode = taskCode;
        }


        @Override
        protected List doInBackground(Game... games) {
            switch (taskCode){
                case TASK_DELETE_GAME:
                    db.gameDAO().deleteGames(games[0]);
                    break;
                case TASK_UPDATE_GAME:
                    db.gameDAO().updateGames(games[0]);
                    break;
                case TASK_INSERT_GAME:
                    db.gameDAO().insertGames(games[0]);
                    break;
            }

            //To return a new list with the updated data, we get all the data from the database again.
            return db.gameDAO().getAllGames();
        }


        @Override
        protected void onPostExecute(List list) {

            super.onPostExecute(list);

            onGameDbUpdated(list);

        }

    }
}
