package com.example.woody.kiddymov;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;




public class KidMenu extends ActionBarActivity {

    private static final String LOG_TAG = "AudioplayerKidsMenuTest";

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private AsyncTask find_task;
    private ArrayList<Document> list_of_docs;
    private Integer size_of_list_of_docs;
    private ProgressDialog barProgressDialog;
    private Handler updateBarHandler;
    private Document temp_doc;
    private boolean done_download = false;
    private MediaPlayer mPlayer = null;
    private String speech_to_string = "none";
    private Integer vid_index = 0;
    ImageButton start_S2T;
    TextView text_view_to_display_S2T;
    MongoHandler mdb_handler = new MongoHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids_menu);
        text_view_to_display_S2T = (TextView) findViewById(R.id.kids_menu_s2t_text_view);
        start_S2T = (ImageButton) findViewById(R.id.kids_menu_start_voice_to_text);

        DocumetsBuilder find_doc_builder = new DocumetsBuilder(this);
        Document find_doc = find_doc_builder.getUserDocOnly();
//        FindIterable<Document> user_vids_list = mdb_handler.findDoc(find_doc);
        list_of_docs = mdb_handler.findDoc(find_doc);
        size_of_list_of_docs = list_of_docs.size();
        String temp_str = "none";
        if (list_of_docs.size() <= 0)
        {
            text_view_to_display_S2T.setText(temp_str);
        }
        else {
            start_S2T.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promptSpeechInput();
                }
            });


            suggestVid();
        }
    }

    private void playVid()
    {
        Document vid_doc = list_of_docs.get(vid_index);
        Intent temp_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vid_doc.getString("vid_url")));
//            temp_intent.putExtra(SearchManager.QUERY, vid_doc.getString("vid_url"));
        int count = (int)vid_doc.get("count");
        count++;
        Document new_doc =new Document("count", count);
        mdb_handler.updateDoc(vid_doc, new_doc);
        startActivity(temp_intent);

    }

    private void suggestVid()
    {
        if (vid_index < size_of_list_of_docs) {
            vid_index = vid_index + 1;
        }else
        {
            vid_index = 0;
        }
        String temp_str;
        Document first_doc_temp = list_of_docs.get(vid_index);
        temp_str = first_doc_temp.getString("vid_url");
        text_view_to_display_S2T.setText(temp_str);
        Bitmap temp_bit = getYoutubThumbnail(temp_str);
        if (temp_bit != null) {
            start_S2T.setImageBitmap(temp_bit);
        }
        startPlaying(first_doc_temp.getString("record_file_path"));
    }

    private void startPlaying(String mFileName) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getYoutubThumbnail(String youtube_url)
    {
        Uri youtube_uri = Uri.parse(youtube_url);
        String temp_vid_query = youtube_uri.getQuery();
        String vid_ID = null;
        if (temp_vid_query != null) {
            vid_ID = temp_vid_query.split("&")[0].split("=")[1];
        }
        String pic_url = "http://img.youtube.com/vi/"+vid_ID+"/1.jpg";
        Bitmap temp_pic;
        try {
            temp_pic = getBitmapFromURL(pic_url);
        } catch ( Exception e) {
            temp_pic = null;
        }
        return temp_pic ;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {

            DownloadImageTask temp_download_task = new DownloadImageTask();
            Bitmap myBitmap = temp_download_task.execute(src).get();

//            URL url = new URL(src);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
////            Bitmap myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            return myBitmap;
        }
//        catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
        catch (Exception e) {
            return null;
        }
    }

    public void doSpeechCommand(String command)
    {
        if (command.equals("next"))
        {
            suggestVid();
        }
        if (command.equals("play"))
//        else  //dbg
        {
            playVid();
//            Document vid_doc = list_of_docs.get(vid_index);
//            Intent temp_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vid_doc.getString("vid_url")));
////            temp_intent.putExtra(SearchManager.QUERY, vid_doc.getString("vid_url"));
//            startActivity(temp_intent);
        }
        else
        {
            suggestVid();
        }
    }


    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speech_to_string = result.get(0);
                    text_view_to_display_S2T.setText(speech_to_string);
                    doSpeechCommand(speech_to_string);
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kids_menu, menu);
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


class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}



//    public static String getYoutubeThumbnailUrl(String youtubeUrl)
//    {
//        String thumbImageUrl = "http://img.youtube.com/vi/noimagefound/default.jpg";
//        if( youtubeUrl!=null && youtubeUrl.trim().length()>0 && youtubeUrl.startsWith("http") && youtubeUrl.contains("youtube"))
//        {
//            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
//            try
//            {
//                youtubeUrl = URLDecoder.decode(youtubeUrl, "UTF-8");
//                if(youtubeUrl.indexOf('?')>0)
//                {
//                    String array[] = youtubeUrl.split("\\?");
//                    int equalsFilterIndex = array.length - 1;
//                    String equalsString = array[equalsFilterIndex];
//                    if(equalsString.indexOf('&')>0)
//                    {
//                        String ampersandArray[] = equalsString.split("&");
//                        for (String parameter : ampersandArray)
//                        {
//                            String keyvaluePair[] = parameter.split("=");
//                            params.put(URLDecoder.decode(keyvaluePair[0],"UTF-8"),URLDecoder.decode(keyvaluePair[1],"UTF-8"));
//                        }
//                    }
//                    else
//                    {
//                        String v[] = equalsString.split("=");
//                        params.put(URLDecoder.decode(v[0],"UTF-8"),URLDecoder.decode(v[1],"UTF-8"));
//                    }
//                }
//                int size = params.size();
//                if(size==0 || !params.containsKey("v"))
//                {
//                    if(size>0)
//                        youtubeUrl = youtubeUrl.substring(0, youtubeUrl.indexOf("?",0));
//                    String vtoSplit = "/v/";
//                    int index = youtubeUrl.indexOf(vtoSplit,0);
//                    int fromIndex = index + vtoSplit.length();
//                    int lastIndex = youtubeUrl.indexOf("?", 0);
//                    if(lastIndex==-1)
//                        lastIndex = youtubeUrl.length();
//                    String v = youtubeUrl.substring(fromIndex,lastIndex);
//                    thumbImageUrl = "http://img.youtube.com/vi/" + v + "/default.jpg";
//                }
//                else
//                {
//                    String v = params.get("v");
//                    thumbImageUrl = "http://img.youtube.com/vi/" + v + "/default.jpg";
//                }
//            }
//            catch(Exception e)
//            {
//                if(e!=null)
//                    e.printStackTrace();
//            }
//        }
//        return thumbImageUrl;
//    }
