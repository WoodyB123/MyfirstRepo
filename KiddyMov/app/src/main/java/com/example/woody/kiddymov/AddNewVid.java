package com.example.woody.kiddymov;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;

public class AddNewVid extends ActionBarActivity {
    private String new_vid_str;
    private String record_path = "";
    private Integer answer1 = 0;
    private MongoHandler mongoDBHandler = new MongoHandler();
    private ProgressDialog barProgressDialog;
    private Handler updateBarHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_vid);
        Intent intent = getIntent();
        new_vid_str = intent.getStringExtra(Intent.EXTRA_TEXT);

        TextView url_view = (TextView) findViewById(R.id.display_url);

        url_view.setText(new_vid_str);
        RadioGroup q1 = (RadioGroup) findViewById(R.id.q1);
        q1.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        View radioButton = group.findViewById(checkedId);
                        int idx = group.indexOfChild(radioButton);
                        answer1 = idx;
                        String checkedId_str = String.format("%d", answer1);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "number:" + checkedId_str + "is checked",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        );


        Button send_button = (Button) findViewById(R.id.send_to_db_button);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// TODO: take user details.
                Document vid_doc = new Document();
                vid_doc.append("vid_url", new_vid_str)
                        .append("q1", answer1)
                        .append("count", 0)
                        .append("user_name", "TBD")
                        .append("record_file_path", record_path);
                mongoDBHandler.execute(vid_doc);
                try {
                    launchBarDialog();
                } catch (Exception e) {
                }
                return;
            }
        });

        Button add_record_button = (Button) findViewById(R.id.add_record_button);
        add_record_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewVid.this, AudioRecordActivity.class);
                startActivityForResult(intent,1);
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                record_path = data.getStringExtra("FILE_NAME");
                // Do something with the contact here (bigger example below)
            }
        }
    }

    public void launchBarDialog() {
        updateBarHandler = new Handler();
        barProgressDialog = new ProgressDialog(AddNewVid.this);
        barProgressDialog.setTitle("Uploading ...");
        barProgressDialog.setMessage("...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    while (barProgressDialog.getProgress() <= barProgressDialog.getMax()) {
                        Thread.sleep(1000);
                        AsyncTask.Status stat = mongoDBHandler.getStatus();
                        if (stat == AsyncTask.Status.FINISHED) {
                            barProgressDialog.setProgress(21);
                        }
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.incrementProgressBy(1);
                            }
                        });
                        if (barProgressDialog.getProgress() >= barProgressDialog.getMax()) {
                            if (stat != AsyncTask.Status.FINISHED){
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Can't reach server",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            barProgressDialog.dismiss();
                            finish();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_vid, menu);
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

