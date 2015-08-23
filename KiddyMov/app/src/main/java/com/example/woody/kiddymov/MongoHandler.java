package com.example.woody.kiddymov;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.URL;


public class MongoHandler extends AsyncTask<URL , Void, String> {

    @Override
    protected String doInBackground(URL... urls) {
        MongoClientURI server_uri;
        MongoClient mongoClient;
        DBPathBuilder pathBuilder = new DBPathBuilder();
//        server_uri = new MongoClientURI("mongodb://nick:1234@ds059692.mongolab.com:59692/code101");
        server_uri = new MongoClientURI(pathBuilder.getCollectionUrl());
        mongoClient = new MongoClient(server_uri);
        MongoDatabase database = mongoClient.getDatabase("code101");
        MongoCollection<Document> collection = database.getCollection("doc101");

        Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new Document("x", 207).append("y", 110));

        collection.insertOne(doc);
        return "done";
    }

}


