package edu.uga.cs.simplegroupmessaging;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;
    Button createGroupButton;

    private ArrayList<String> chatItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        recyclerView = findViewById(R.id.recyclerView);
        createGroupButton = findViewById(R.id.createGroup);

        // use a linear layout manager for the recycler view
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new ChatsRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatsActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        // Create a NewsSource instance
        //newsSource = new NewsSource(this);

        // Execute the retrieval of requested news articles in an asynchronous way,
        // without blocking the UI thread.
        //new GetNewsTask().execute( searchTerms );

    }

    /*public class GetNewsTask extends AsyncTask<String, Void, List<Chat>> {

        // This method will run as a background process to read news articles from the news source.
        @Override
        protected List<Chat> doInBackground(String... params) {

            //Log.d(DEBUG_TAG, "ShowNewsActivity: searching for news on: " + params[0] );

            //chatItems = newsSource.retrieveNews( params[0] );

            if( chatItems != null )
                Log.d(DEBUG_TAG, "ShowNewsActivity: News items retrieved: " + chatItems.size());
            else
                Log.d(DEBUG_TAG, "ShowNewsActivity: newsItems is null" );

            return chatItems;
        }

        // This method will be automatically called by Android, once the news items reading
        // background process is finished.  It will then create and set an adapter to provide
        // values for the RecyclerView.
        @Override
        protected void onPostExecute(List<Chat> chatList) {
            super.onPostExecute( chatList );
            recyclerAdapter = new ChatsRecyclerAdapter( chatList );
            recyclerView.setAdapter( recyclerAdapter );
        }
    }*/
}
