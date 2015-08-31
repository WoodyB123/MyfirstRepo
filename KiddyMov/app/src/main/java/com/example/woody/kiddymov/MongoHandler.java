package com.example.woody.kiddymov;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.URL;
public class MongoHandler {

    public AsyncTask insertDoc(Document doc){
        MongoInsert insertTask = new MongoInsert();
        insertTask.execute(doc);
        return insertTask;
    }

    public AsyncTask findDoc(Document doc) {
        MongoFind findTask = new MongoFind();
        findTask.execute(doc);
        return findTask;
    }
}

class MongoInsert extends AsyncTask<Document , Void, String> {
    @Override
    protected String doInBackground(Document... docs) {
        MongoClientURI server_uri;
        MongoClient mongoClient;
        DBPathBuilder pathBuilder = new DBPathBuilder();
//        server_uri = new MongoClientURI("mongodb://nick:1234@ds059692.mongolab.com:59692/code101");
        server_uri = new MongoClientURI(pathBuilder.getCollectionUrl());
        mongoClient = new MongoClient(server_uri);
        MongoDatabase database = mongoClient.getDatabase("code101");
        MongoCollection<Document> collection = database.getCollection("doc101");

        Document doc = new Document(docs[0]);
//        Document doc = new Document("URL", "MongoDB")
//                .append("type", "database")
//                .append("count", 1)
//                .append("info", new Document("x", 207).append("y", 110));

        collection.insertOne(doc);
        return "done";
    }
}

class MongoFind extends AsyncTask<Document , Void, String> {
    @Override
    protected String doInBackground(Document... docs) {
        MongoClientURI server_uri;
        MongoClient mongoClient;
        DBPathBuilder pathBuilder = new DBPathBuilder();
//        server_uri = new MongoClientURI("mongodb://nick:1234@ds059692.mongolab.com:59692/code101");
        server_uri = new MongoClientURI(pathBuilder.getCollectionUrl());
        mongoClient = new MongoClient(server_uri);
        MongoDatabase database = mongoClient.getDatabase("code101");
        MongoCollection<Document> collection = database.getCollection("doc101");

        Document doc = new Document(docs[0]);
//        Document doc = new Document("URL", "MongoDB")
//                .append("type", "database")
//                .append("count", 1)
//                .append("info", new Document("x", 207).append("y", 110));

        FindIterable<Document> iterable =  collection.find(doc);
        return "done";
    }

}


