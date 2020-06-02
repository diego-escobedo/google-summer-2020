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
import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.FetchOptions;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  //class methhods: convert to JSon and get parameter
  private String convertToJson(ArrayList<String> commentList) {
    Gson gson = new Gson();
    return gson.toJson(commentList);
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
  // our datastructure for now.... very simple
  private ArrayList<String> commentList = new ArrayList<String>();//Creating arraylist
  //get method
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery r = datastore.prepare(query);

    int max_comments = Integer.parseInt(getParameter(request,"max-comments", "5"));
    ArrayList<String> comments = new ArrayList<String>();

    List<Entity> results = r.asList(FetchOptions.Builder.withDefaults());
    for (int i = 0; i < Math.min(max_comments, results.size()); i++) {
      Entity entity = results.get(i);
      String name = (String) entity.getProperty("name");
      String comment = (String) entity.getProperty("comment");
      //int rating = Integer.parseInt(entity.getProperty("rating"));
      // ^ for some reason this crashes everything
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
    
    Entity commentEnt = new Entity("Comment");
    commentEnt.setProperty("name", request.getParameter("name"));
    commentEnt.setProperty("comment", request.getParameter("comment"));
    commentEnt.setProperty("rating", Integer.parseInt(request.getParameter("rating")));
    commentEnt.setProperty("timestamp", System.currentTimeMillis());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEnt);
    response.sendRedirect("/index.html");
  }
}

