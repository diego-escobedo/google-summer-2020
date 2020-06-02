package com.google.sps.servlets;

public class Comment {
    public String name = "";
    public String comm = "";
    public int rating = 0;
    public String key = ""; 
    public long ts = 0;

    public Comment(String Sname, String Scomm, int Srating,String Skey, long Sts ) {
    // the s stands for starter
    name = Sname;
    comm = Scomm;
    rating = Srating;
    key = Skey; 
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
    public String getKey() {
        return key;
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
    json += "\"key\": ";
    json += this.getKey();
    json += "\"timestamp\": ";
    json += this.getTimestamp();
    json += "}";
    return json;
  }
}