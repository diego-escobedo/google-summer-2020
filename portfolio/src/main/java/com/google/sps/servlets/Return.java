package com.google.sps.servlets;

public class Return {
    public ArrayList<Comment> comments = new ArrayList<Comment>();
    public int totalComments = 0;
    public double avgRating = 0;
    public double id = 0; 

    public Comment(ArrayList<Comment> comments, int totalComments,double avgRating) {
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