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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings = ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Displays a random fact about me.
 */
function addRandomFact() {
  const facts = ['I have a sister also going to MIT',
    'I have lived in 7 cities and 3 countries',
    'I am a massive Star Wars fan',
    'I have never broken a bone',
    'My favorite artist is Jackson Pollock',
    'I can solve a Rubik\'s cube in under 10 hours',
    'My favorite food is pizza! Yum'
  ];
  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

function toggle(project) {
  var find = project.concat("_", "more");
  var x = document.getElementById(find);
  if (x.style.display === "none") {
    x.style.display = "flex";
  } else {
    x.style.display = "none";
  }
}


function getComments() {
  const maxcom = document.getElementById("max-comments-select").value;
  const sort = document.getElementById("sort-select").value;
  var url = "/data?max-comments=".concat(maxcom, "&sort=", sort);
  fetch(url).then(response => response.json()).then((returnObj) => {

    var comments = returnObj["comments"]
    const commentsStatsElement = document.getElementById('comment-stats');
    commentsStatsElement.innerHTML = '';
    commentsStatsElement.appendChild(
      writeStats(returnObj["totalComments"], "Total Comments"));
    commentsStatsElement.appendChild(
      writeStats(returnObj["avgRating"], "Average Rating"));
    commentsStatsElement.appendChild(
      writeStats(returnObj["nps"], "Net Promoter Score"));

    const nps_icon = document.getElementById('nps-icon');
    if (returnObj["nps"] > 30) {
      nps_icon.style.color = "rgba(118,186,90,.85)"
    }
    if (returnObj["nps"] < 0) {
      nps_icon.style.color = "rgba(220,50,30,.85)"
    }

    const commentsListElement = document.getElementById('comments-ul');
    commentsListElement.innerHTML = '';
    for (i = 0; i < comments.length; i++) {
      commentsListElement.appendChild(
        createListElement(comments[i]));
    }
  });
}

function writeStats(stat, name) {
  const liElement = document.createElement('li');
  liElement.setAttribute("id", "stat");
  if (name === "Total Comments") {
    liElement.innerHTML = '<i class="fas fa-comments fa-2x" id="comments-icon"></i>' +
      '<p>' + name + ": " + stat + '</p>';
  }
  if (name === "Average Rating") {
    liElement.innerHTML = '<i class="fas fa-star-half-alt fa-2x" id="rtg-icon"></i>' +
      '<p>' + name + ": " + stat + '</p>';
  }
  if (name === "Net Promoter Score") {
    liElement.innerHTML = '<i class="fas fa-arrow-circle-up fa-2x" id="nps-icon"></i>' +
      '<p>' + name + ": " + stat + '</p>';
  }

  return liElement;
}

/* Creates an <li> element containing text. */
function createListElement(commentObj) {
  var name = commentObj["name"];
  var comment = commentObj["comm"];
  var rating = commentObj["rating"];
  var id = commentObj["id"];
  var date = timeConverter(commentObj["ts"]);

  const liElement = document.createElement('li');
  liElement.innerHTML = liHTML(name, comment, rating, date, id);
  return liElement;
}

function liHTML(name, comment, rating, date, id) {
  var html = '<footer class="post-info">' +
    '<div class="separated horiz-flex-div" >' +
    '<abbr title="' + date + '">' + date + '</abbr>' +
    '<button onClick="postDeleteComment(' + id + ');" class="inv-btn">' +
    '<i class="far fa-trash-alt"></i>' +
    '</button>' +
    '</div>' +
    '<div class="horiz-flex-div">' +
    '<address class="author"> <b>' + 'By ' + name + '</b></address>' +
    '<address class="rtg">' + 'Rating: ' + rating + '</address>' +
    '</div>' +
    '</footer>' +
    '<div class="comment-text-div">' + '<p>' + comment + '</p>' + '</div>';

  return html;
}

function postDeleteComment(id) {
  var params = new URLSearchParams();
  params.append('id', id);
  let response = fetch('/delete-comment', {
    method: 'POST',
    body: params
  }).then(getComments());
}

function timeConverter(UNIX_timestamp) {
  var a = new Date(UNIX_timestamp);
  var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  var year = a.getFullYear();
  var month = months[a.getMonth()];
  var date = a.getDate();
  var hour = a.getHours();
  var min = a.getMinutes() < 10 ? '0' + a.getMinutes() : a.getMinutes();
  var time = date + ' ' + month + ' ' + year + ' ' + hour + ':' + min;
  return time;
}

function deleteComments() {
  const request = new Request('/delete-data');
  fetch(request).then(getComments());
}
