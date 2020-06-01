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
    String json = convertToJson(commentList);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
  //post method
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
     // Get what we want to store from the request.
    String name = request.getParameter("name");
    String comment = request.getParameter("comment");
    int rating = Integer.parseInt(request.getParameter("rating"));
    
    response.sendRedirect("/index.html");
     
    commentList.add(comment);//added comment to list
  }
}

