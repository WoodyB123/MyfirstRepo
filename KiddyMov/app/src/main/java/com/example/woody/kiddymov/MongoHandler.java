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
import java.util.ArrayList;

public class MongoHandler {

    public AsyncTask insertDoc(Document doc){
        MongoInsert insertTask = new MongoInsert();
        insertTask.execute(doc);
        return insertTask;
        // TODO: Bring here all the wait system from add new vid.
    }

    public void updateDoc(Document origin_doc, Document doc_with_fields_to_update)
    {
        MongoUpdate mongoUpdate = new MongoUpdate();
        mongoUpdate.execute(origin_doc,doc_with_fields_to_update);
    }

    public ArrayList<Document> findDoc(Document doc) {
        MongoFind findTask = new MongoFind();
        ArrayList<Document> iterable = null;
        try {
            iterable =  findTask.execute(doc).get();
        } catch (Exception e) {}
        return iterable ;
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

class MongoUpdate extends AsyncTask<Document , Void, Void> {
    @Override
    protected Void doInBackground(Document... docs) {
        MongoClientURI server_uri;
        MongoClient mongoClient;
        DBPathBuilder pathBuilder = new DBPathBuilder();
//        server_uri = new MongoClientURI("mongodb://nick:1234@ds059692.mongolab.com:59692/code101");
        server_uri = new MongoClientURI(pathBuilder.getCollectionUrl());
        mongoClient = new MongoClient(server_uri);
        MongoDatabase database = mongoClient.getDatabase("code101");
        MongoCollection<Document> collection = database.getCollection("doc101");

        Document origin_doc = new Document(docs[0]);
        Document new_doc = new Document(docs[1]);
//        ArrayList<Document> iterable =  collection.find(doc).into(new ArrayList<Document>());
        collection.updateOne(origin_doc,new Document("$set", new_doc));
        return null;

    }

}

class MongoFind extends AsyncTask<Document , Void, ArrayList<Document>> {
    @Override
    protected ArrayList<Document> doInBackground(Document... docs) {
        MongoClientURI server_uri;
        MongoClient mongoClient;
        DBPathBuilder pathBuilder = new DBPathBuilder();
//        server_uri = new MongoClientURI("mongodb://nick:1234@ds059692.mongolab.com:59692/code101");
        server_uri = new MongoClientURI(pathBuilder.getCollectionUrl());
        mongoClient = new MongoClient(server_uri);
        MongoDatabase database = mongoClient.getDatabase("code101");
        MongoCollection<Document> collection = database.getCollection("doc101");

        Document doc = new Document(docs[0]);
//        ArrayList<Document> iterable =  collection.find(doc).into(new ArrayList<Document>());
        ArrayList<Document> iterable =  collection.find().into(new ArrayList<Document>());
        return iterable;
    }

}


