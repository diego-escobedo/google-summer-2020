package com.google.sps.servlets;

public class Comment {
    public String name = "";
    public String comm = "";
    public int rating = 0;
    public long id = 0; 
    public long ts = 0;

    public Comment(String Sname, String Scomm, int Srating,long Sid, long Sts ) {
    // the s stands for starter
    name = Sname;
    comm = Scomm;
    rating = Srating;
    id = Sid; 
    ts = Sts;
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

    public String convertToJson() {
    String json = "{";
    json += "\"name\": ";
    json += "\"" + this.getName() + "\"";
    json += ", ";
    json += "\"comment\": ";
    json += "\"" + this.getComment() + "\"";
    json += ", ";
    json += "\"rating\": ";
    json += this.getRating();
    json += ", ";
    json += "\"id\": ";
    json += this.getID();
    json += ", ";
    json += "\"timestamp\": ";
    json += this.getTimestamp();
    json += "}";
    return json;
  }
}