package com.example.woody.kiddymov;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import org.bson.Document;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ariel on 30/08/2015.
 */
public class DocumetsBuilder {

    private Context context;

    public DocumetsBuilder(Context myContext){
        this.context = myContext;
    }

    public Document getDoc(String vid_url, int answer1, int count, String record_path){
        String user_name = getUsername();
        Document vid_doc = new Document();
        vid_doc.append("vid_url", vid_url)
                .append("q1", answer1)
                .append("count", count)
                .append("user_name", user_name)
                .append("record_file_path", record_path);

        return vid_doc;
    }

    public Document getUserDocOnly(){
        String user_name = getUsername();
        user_name  = "temp_user";
        Document vid_doc = new Document();
        return vid_doc.append("user_name", user_name);
    }

    public String getUsername() {
        AccountManager manager = AccountManager.get(context);
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
}
