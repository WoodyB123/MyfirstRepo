package com.example.woody.kiddymov;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class ParentMenu extends ActionBarActivity {

    private EditText search_key_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_menu);
        search_key_text = (EditText) findViewById(R.id.key_to_search);

    }

    public void searchVidOnLine(View view){
        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
        String query = search_key_text.getText().toString();
        search.putExtra(SearchManager.QUERY, query);

        startActivity(search);
    }

    public void addNewVid(View view){
        Intent intent = new Intent(this,AddNewVid.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parent_menu, menu);
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
}
