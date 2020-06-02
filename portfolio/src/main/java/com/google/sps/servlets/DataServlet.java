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
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  //class methhods: convert to JSon and get parameter
  private String convertToJson(ArrayList<String> commentList) {
    Gson gson = new Gson();
    String json = gson.toJson(commentList);
    return json;
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
  // our datastructure for now.... very simple
  private ArrayList<String> commentList=new ArrayList<String>();//Creating arraylist
  //get method
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    int maxcomments = Integer.parseInt(getParameter(request,"max-comments", "5"));
    ArrayList<String> comments=new ArrayList<String>();
    int count = 0;
    for (Entity entity : results.asIterable()) {
      if (count == maxcomments) {break;}
      String name = (String) entity.getProperty("name");
      String comment = (String) entity.getProperty("comment");
      //int rating = Integer.parseInt(entity.getProperty("rating"));
      count++;
      // Task task = new Task(id, title, timestamp); once we create custom data struct
      comments.add(comment);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }
  //post method
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
     // Get what we want to store from the request.
    String name = request.getParameter("name");
    String comment = request.getParameter("comment");
    int rating = Integer.parseInt(request.getParameter("rating"));
    long timestamp = System.currentTimeMillis();
    
    Entity commentEnt = new Entity("Comment");
    commentEnt.setProperty("name", name);
    commentEnt.setProperty("comment", comment);
    commentEnt.setProperty("rating", rating);
    commentEnt.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEnt);

    response.sendRedirect("/index.html");
     
    commentList.add(comment);//added comment to list
  }
}

