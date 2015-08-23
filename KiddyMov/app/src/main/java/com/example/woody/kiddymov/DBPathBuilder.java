package com.example.woody.kiddymov;

public class DBPathBuilder {
    private String db_name = "code101";
    private String mongodb_header = "mongodb://";
    private String user_name = "nick";
    private String user_pass = "1234";
    private String base_url = "ds059692.mongolab.com:59692";

    public void setDb_name(String new_db_name){
        this.db_name = new_db_name;
    }

    public void setUser_name(String new_user_name){
        this.user_name = new_user_name;
    }

    public void setUser_pass(String new_user_pass){
        this.user_pass = new_user_pass;
    }

    public void setBase_url(String new_base_url){
        this.base_url= new_base_url;
    }

//    public void setMongodb_header(String new_mongodb_header){
//        this.mongodb_header = new_mongodb_header;
//    }

    public String getCollectionUrl() {
        return this.mongodb_header+this.user_name+":"+this.user_pass+"@"+
                this.base_url+"/"+this.db_name;
    }

}