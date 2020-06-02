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
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

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
  const facts =
      ['I have a sister also going to MIT',
      'I have lived in 7 cities and 3 countries',
      'I am a massive Star Wars fan',
      'I have never broken a bone',
      'My favorite artist is Jackson Pollock',
      'I can solve a Rubik\'s cube in under 10 hours', 
      'My favorite food is pizza! Yum'];
  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

function toggle(project) {
  var find = project.concat("_","more");
  var x = document.getElementById(find);
  if (x.style.display === "none") {
    x.style.display = "flex";
  } else {
    x.style.display = "none";
  }
}

function getComments() {
  var maxcom = document.getElementById("max-comments").value;
  var url = "/data?max-comments=".concat(maxcom);
  fetch(url).then(response => response.json()).then((comments) => {
    // comments is an object, not a string, so we have to
    // reference its fields to create HTML content

    const commentsListElement = document.getElementById('comment-section');
    commentsListElement.innerHTML = '';
    for (i = 0; i < comments.length; i++) {
        commentsListElement.appendChild(
        createListElement(comments[i]));
    }
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

function deleteComments() {
    const request = new Request('/delete-data');
    fetch(request).then(getComments());
}
