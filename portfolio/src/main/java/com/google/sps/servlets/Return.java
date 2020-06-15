package com.google.sps.servlets;

import java.util.ArrayList;

public class Return {
private ArrayList<Comment> comments = new ArrayList<Comment>();
private int totalComments = 0;
private String avgRating = "";
private String nps = "";

public Return(ArrayList<Comment> comments, int totalComments,String avgRating, String nps) {
        // the s stands for starter
        this.comments = comments;
        this.totalComments = totalComments;
        this.avgRating = avgRating;
        this.nps = nps;
}

public ArrayList<Comment> getComments() {
        return comments;
}
public int getTotalComments() {
        return totalComments;
}
public String getAvgRating() {
        return avgRating;
}
public String getNps() {
        return nps;
}
}
