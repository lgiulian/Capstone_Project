'use strict';

// Initiate firebase auth.
function initFirebaseAuth() {
  // Listen to auth state changes.
  firebase.auth().onAuthStateChanged(authStateObserver);
}

function loadTournaments() {
  var callback = function(snap) {
    var data = snap.val();
    displayTournament(snap.key, data.fullName, data.location, data.beginDate, data.endDate);
  };

  firebase.database().ref('/tournament/').limitToLast(100).on('child_added', callback);
  firebase.database().ref('/tournament/').limitToLast(100).on('child_changed', callback);
}


// Template for tournaments.
var TOURNAMENT_TEMPLATE =
    '<div class="tournament-container">' +
      '<div class="spacing"><div class="pic"></div></div>' +
      '<div class="tournament"></div>' +
      '<div class="location"></div>' +
      '<div class="results"></div>' +
    '</div>';

// A loading image URL.
var LOADING_IMAGE_URL = 'https://www.google.com/images/spin-32.gif?a';

// Displays a Tournament in the UI.
function displayTournament(key, fullName, location, beginDate, endDate) {
  var div = document.getElementById(key);
  // If an element for that tournament does not exists yet we create it.
  if (!div) {
    var container = document.createElement('div');
    container.innerHTML = TOURNAMENT_TEMPLATE;
    div = container.firstChild;
    div.setAttribute('id', key);
    div.setAttribute('onclick', 'displayResults(\'' + key + '\')');
    div.setAttribute('onmouseover', 'this.style.backgroundColor = \'lightgray\'');
    div.setAttribute('onmouseout', 'this.style.backgroundColor = \'\'');
    tournamentListElement.appendChild(div);
  }
  div.querySelector('.location').textContent = location;
  var tournamentElement = div.querySelector('.tournament');
  tournamentElement.textContent = fullName;
  // Replace all line breaks by <br>.
  tournamentElement.innerHTML = tournamentElement.innerHTML.replace(/\n/g, '<br>');
  // Show the card fading-in and scroll to view the new tournament.
  setTimeout(function() {div.classList.add('visible')}, 1);
  tournamentListElement.scrollTop = tournamentListElement.scrollHeight;
}

function displayResults(key) {
  var div = document.getElementById(key);
  var resultsElement = div.querySelector('.results');
  if (resultsElement.innerHTML.length > 2) {
    resultsElement.innerHTML = '';
  } else {
    var callback = function(snap) {
          var data = snap.val();
          resultsElement.innerHTML = data.content;
        };

      firebase.database().ref('/result/').child(key).child('resultHtml').on('value', callback);
      firebase.database().ref('/result/').child(key).on('child_changed', callback);
  }
}

// Checks that the Firebase SDK has been correctly setup and configured.
function checkSetup() {
  if (!window.firebase || !(firebase.app instanceof Function) || !firebase.app().options) {
    window.alert('You have not configured and imported the Firebase SDK. ' +
        'Make sure you go through the codelab setup instructions and make ' +
        'sure you are running the codelab using `firebase serve`');
  }
}

// Checks that Firebase has been imported.
checkSetup();

// Shortcuts to DOM Elements.
var tournamentListElement = document.getElementById('tournaments');

// initialize Firebase
//initFirebaseAuth();

// We load currently existing tournaments and listen to new ones.
loadTournaments();