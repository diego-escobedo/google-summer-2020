// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.sps.servlets.Comment;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  //class methhods: convert to JSon and get parameter
  private String convertToJsonStrArrayList(ArrayList<String> commentList) {
    Gson gson = new Gson();
    return gson.toJson(commentList);
  }

  private String convertToJson(ArrayList<String> stats, ArrayList<Comment> commentList) {
    String ret = "[";
    ret += "{";
    ret += "\"total-comments\": " + stats.get(0) + ",";
    ret += "\"avg-rating\": " + stats.get(1) + ",";
    ret += "\"nps\": " + stats.get(2);
    ret += "},";
    for (Comment c : commentList) {
        ret += c.convertToJson();
        ret += ",";
    }
    ret = ret.substring(0, ret.length() - 1);
    ret += "]";
    return ret;
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  private Query getQuerySort(String sort_method) {
    if (sort_method.equals("ts_desc")) {
        return new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    }
    else if (sort_method.equals("ts_asc")) {
        return new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    }
    else if (sort_method.equals("rtg_desc")) {
        return new Query("Comment").addSort("rating", SortDirection.DESCENDING);
    }
    else if (sort_method.equals("rtg_asc")) {
        return new Query("Comment").addSort("rating", SortDirection.ASCENDING);
    }
    else if (sort_method.equals("author_desc")) {
        return new Query("Comment").addSort("name", SortDirection.DESCENDING);
    }
    else if (sort_method.equals("author_asc")) {
        return new Query("Comment").addSort("name", SortDirection.ASCENDING);
    }
    else if (sort_method.equals("comm_len_desc")) {
        return new Query("Comment").addSort("comment_length", SortDirection.DESCENDING);
    }
    else if(sort_method=="comm_len_asc") {
        return new Query("Comment").addSort("comment_length", SortDirection.ASCENDING);
    }
    return new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
  }

  private static double round (double value, int precision) {
    int scale = (int) Math.pow(10, precision);
    return (double) Math.round(value * scale) / scale;
    }

  //get method
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String sort_method = (String) getParameter(request,"sort", "ts_desc");
    Query query = getQuerySort(sort_method);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery r = datastore.prepare(query);
    List<Entity> results = r.asList(FetchOptions.Builder.withDefaults());

    int max_comments = Integer.parseInt(getParameter(request,"max-comments", "5"));
    ArrayList<Comment> comments = new ArrayList<Comment>();  
    
    for (int i = 0; i < Math.min(max_comments, results.size()); i++) {
      Entity entity = results.get(i);
      String name = (String) entity.getProperty("name");
      String comm = (String) entity.getProperty("comment");
      int rating = ((Long)entity.getProperty("rating")).intValue();
      long id = (long) entity.getKey().getId();
      long ts = (long) entity.getProperty("timestamp");

      Comment c = new Comment(name, comm, rating, id, ts);
      comments.add(c);
    }

    double cumsum = 0;
    double nps_positive = 0;
    double nps_negative = 0;
    for (int i = 0; i < results.size(); i++) {
        Entity entity = results.get(i);
        int rating = ((Long)entity.getProperty("rating")).intValue();
        if (rating > 8) {nps_positive+=1;}
        else if (rating < 7) {nps_negative+=1;}
        cumsum +=  rating;
    }
    double nps = (nps_positive / (double) results.size() - nps_negative / (double) results.size())*100;
    double avg = cumsum / (double) results.size();
    ArrayList<String> stats = new ArrayList<String>();
    stats.add(String.valueOf(results.size()));
    stats.add(String.valueOf(round(avg, 1)));
    stats.add(String.valueOf(round(nps, 0)));

    response.setContentType("application/json");
    response.getWriter().println(convertToJson(stats,comments));
  }
  //post method
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
     // Get what we want to store from the request.
    
    Entity commentEnt = new Entity("Comment");
    commentEnt.setProperty("name", request.getParameter("name"));
    commentEnt.setProperty("comment", request.getParameter("comment"));
    commentEnt.setProperty("rating", Integer.parseInt(request.getParameter("rating")));
    commentEnt.setProperty("timestamp", System.currentTimeMillis());
    commentEnt.setProperty("comment_length", request.getParameter("comment").length());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEnt);
    response.sendRedirect("/index.html");
  }
}

