'use strict';

// Initiate firebase auth.
function initFirebaseAuth() {
  // Listen to auth state changes.
  firebase.auth().onAuthStateChanged(authStateObserver);
}

function signIn() {
  // Sign in Firebase using popup auth and Google as the identity provider.
  var provider = new firebase.auth.GoogleAuthProvider();
  firebase.auth().signInWithPopup(provider);
}

function signOut() {
  // Sign out of Firebase.
  firebase.auth().signOut();
}

function getProfilePicUrl() {
  return firebase.auth().currentUser.photoURL || '/images/profile_placeholder.png';
}

// Returns the signed-in user's display name.
function getUserName() {
  return firebase.auth().currentUser.displayName;
}

// Returns true if a user is signed-in.
function isUserSignedIn() {
  return !!firebase.auth().currentUser;
}

// Triggers when the auth state change for instance when the user signs-in or signs-out.
function authStateObserver(user) {
  if (user) { // User is signed in!
    // Get the signed-in user's profile pic and name.
    var profilePicUrl = getProfilePicUrl();
    var userName = getUserName();

    // Set the user's profile pic and name.
    userPicElement.style.backgroundImage = 'url(' + profilePicUrl + ')';
    userNameElement.textContent = userName;

    // Show user's profile and sign-out button.
    userNameElement.removeAttribute('hidden');
    userPicElement.removeAttribute('hidden');
    signOutButtonElement.removeAttribute('hidden');

    // Hide sign-in button.
    signInButtonElement.setAttribute('hidden', 'true');

  } else { // User is signed out!
    // Hide user's profile and sign-out button.
    userNameElement.setAttribute('hidden', 'true');
    userPicElement.setAttribute('hidden', 'true');
    signOutButtonElement.setAttribute('hidden', 'true');

    // Show sign-in button.
    signInButtonElement.removeAttribute('hidden');
  }
}

// Returns true if user is signed-in. Otherwise false and displays a message.
function checkSignedInWithMessage() {
  // Return true if the user is signed in Firebase
  if (isUserSignedIn()) {
    return true;
  }

  // Display a message to the user using a Toast.
  var data = {
    message: 'You must sign-in first',
    timeout: 2000
  };
  signInSnackbarElement.MaterialSnackbar.showSnackbar(data);
  return false;
}

function loadTournaments() {
  var callback = function(snap) {
    var data = snap.val();
    displayTournament(snap.key, data.fullName, data.location, data.beginDate, data.endDate);
  };

  firebase.database().ref('/tournament/').orderByChild('negativeBeginDate').limitToLast(100).on('child_added', callback);
  firebase.database().ref('/tournament/').orderByChild('negativeBeginDate').limitToLast(100).on('child_changed', callback);
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
var imageButtonElement = document.getElementById('submitImage');
var imageFormElement = document.getElementById('image-form');
var mediaCaptureElement = document.getElementById('mediaCapture');
var userPicElement = document.getElementById('user-pic');
var userNameElement = document.getElementById('user-name');
var signInButtonElement = document.getElementById('sign-in');
var signOutButtonElement = document.getElementById('sign-out');
var signInSnackbarElement = document.getElementById('must-signin-snackbar');

var tournamentListElement = document.getElementById('tournaments');

signOutButtonElement.addEventListener('click', signOut);
signInButtonElement.addEventListener('click', signIn);

// initialize Firebase
initFirebaseAuth();

// We load currently existing tournaments and listen to new ones.
loadTournaments();