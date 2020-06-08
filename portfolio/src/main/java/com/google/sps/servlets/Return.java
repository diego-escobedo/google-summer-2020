package com.google.sps.servlets;

public class Return {
    private ArrayList<Comment> comments = new ArrayList<Comment>();
    private int totalComments = 0;
    private double avgRating = 0;
    private double nps = 0;

    public Return(ArrayList<Comment> comments, int totalComments,double avgRating, double nps) {
    // the s stands for starter
    this.coments = comments;
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
    public double getAvgRating() {
        return avgRating;
    }
    public double getNps() {
        return nps;
    }
}
