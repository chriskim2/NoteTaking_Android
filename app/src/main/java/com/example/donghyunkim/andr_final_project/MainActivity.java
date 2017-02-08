package com.example.donghyunkim.andr_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_ADD = 1;
    static final int REQUEST_EDIT = 2;

    DatabaseHelper myDB = new DatabaseHelper(this);
    ListViewAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Memo List");
    }

    @Override
    protected void onResume() {
        super.onResume();

        listView = (ListView) findViewById(R.id.listView);
        List<Memo> mlist = myDB.getAllMemos();

        adapter = new ListViewAdapter(this, android.R.layout.simple_list_item_1, mlist);
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(mItemClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDB.close();
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Memo e = (Memo)parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("method", "edit");
            intent.putExtra("id", e.getId());
            startActivity(intent);
        }
    };

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
        if (id == R.id.action_add) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("method", "add");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
