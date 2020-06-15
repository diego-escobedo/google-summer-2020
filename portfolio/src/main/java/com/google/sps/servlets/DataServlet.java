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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.io.IOException;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

//class methhods: convert to JSON, get parameter, and get a query
private String convertToJson(Return commentList) {
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

private Query getQuerySort(String sortMethod) {
        SortDirection sortDir = sortMethod.endsWith("asc") ? SortDirection.ASCENDING : SortDirection.DESCENDING;
        String sortCriteria = "";

        if (sortMethod.startsWith("ts")) {sortCriteria = "timestamp";}
        else if (sortMethod.startsWith("rtg")) {sortCriteria = "rating";}
        else if (sortMethod.startsWith("author")) {sortCriteria = "name";}
        else if (sortMethod.startsWith("commlen")) {sortCriteria = "comment_length";}

        return new Query("Comment").addSort(sortCriteria,sortDir);
}

//get method
@Override
public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sortMethod = (String) getParameter(request,"sort", "ts_desc");
        Query query = getQuerySort(sortMethod);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery r = datastore.prepare(query);
        List<Entity> results = r.asList(FetchOptions.Builder.withDefaults());

        int maxComments = Integer.parseInt(getParameter(request,"max-comments", "5"));
        ArrayList<Comment> comments = new ArrayList<Comment>();

        for (int i = 0; i < Math.min(maxComments, results.size()); i++) {
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
        double npsPositive = 0;
        double npsNegative = 0;
        for (int i = 0; i < results.size(); i++) {
                Entity entity = results.get(i);
                int rating = ((Long)entity.getProperty("rating")).intValue();
                if (rating > 8) {npsPositive+=1;}
                else if (rating < 7) {npsNegative+=1;}
                cumsum +=  rating;
        }
        double nps = (npsPositive / (double) results.size() - npsNegative / (double) results.size())*100;
        double avg = cumsum / (double) results.size();

        DecimalFormat roundOne = new DecimalFormat("#.#");
        DecimalFormat roundZero = new DecimalFormat("#");

        Return ret = new Return(comments, results.size(), roundOne.format(avg), roundZero.format(nps));

        response.setContentType("application/json");
        response.getWriter().println(convertToJson(ret));
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
