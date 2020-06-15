package com.google.sps.servlets;

public class Comment {
public String name = "";
public String comm = "";
public int rating = 0;
public long id = 0;
public long ts = 0;

public Comment(String name, String comm, int rating,long id, long ts ) {
        // the s stands for starter
        this.name = name;
        this.comm = comm;
        this.rating = rating;
        this.id = id;
        this.ts = ts;
}

public String getName() {
        return name;
}
public String getComment() {
        return comm;
}
public int getRating() {
        return rating;
}
public long getID() {
        return id;
}
public long getTimestamp() {
        return ts;
}
}
