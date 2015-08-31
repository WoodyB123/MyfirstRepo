package com.example.woody.kiddymov;

import android.accounts.Account;
import android.accounts.AccountManager;
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

import java.util.LinkedList;
import java.util.List;

public class AddNewVid extends ActionBarActivity {
    private String new_vid_str;
    private String record_path = "";
    private Integer answer1 = 0;
    private MongoHandler mongoDBHandler = new MongoHandler();
    private AsyncTask insertTask;
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
                String user_name = getUsername();
// TODO: Earse the next line.
                user_name = "temp_user";
                DocumetsBuilder doc_builder =  new DocumetsBuilder(AddNewVid.this);
                Document vid_doc = doc_builder.getDoc(new_vid_str,answer1,0,record_path);
//                Document vid_doc = new Document();
//                vid_doc.append("vid_url", new_vid_str)
//                        .append("q1", answer1)
//                        .append("count", 0)
//                        .append("user_name", user_name)
//                        .append("record_file_path", record_path);
                insertTask = mongoDBHandler.insertDoc(vid_doc);
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

    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
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
                        AsyncTask.Status stat = insertTask.getStatus();
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

