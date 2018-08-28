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

function onRecordFormSubmit(e) {
  e.preventDefault();
  // Check that the user entered a player and is signed in.
  if (pinInputElement.value && checkSignedInWithMessage()) {
    saveRecord(pinInputElement.value, lastNameInputElement.value, nameInputElement.value, locationInputElement.value, clubInputElement.value,
        paymentDateInputElement.value, birthDateInputElement.value, amountInputElement.value, paymentDocTypeInputElement.value).then(function() {
      // Clear text fields and re-enable the SEND button.
      resetMaterialTextfield(pinInputElement);
      resetMaterialTextfield(locationInputElement);
      resetMaterialTextfield(paymentDateInputElement);
      resetMaterialTextfield(nameInputElement);
      resetMaterialTextfield(clubInputElement);
      resetMaterialTextfield(countryInputElement);
      resetMaterialTextfield(strengthInputElement);
      resetMaterialTextfield(gorInputElement);
      resetMaterialTextfield(lastNameInputElement);
      resetMaterialTextfield(birthDateInputElement);
      resetMaterialTextfield(paymentDocTypeInputElement);
      resetMaterialTextfield(amountInputElement);
      toggleButton();
    });
  }
}

function saveRecord(pin, lastName, name, location, club, paymentDate, birthDate, amount, paymentDocType) {
  return firebase.database().ref('/members/' + pin).set({
    operator: getUserName(),
    amount: amount,
    birth_date: birthDate,
    city: location,
    club: club,
    doc_type: paymentDocType,
    name: lastName + ' ' + name,
    payment_date: paymentDate,
    pin: pin
  }).catch(function(error) {
    console.error('Error writing new member to Firebase Database', error);
  });
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

// Resets the given MaterialTextField.
function resetMaterialTextfield(element) {
  element.value = '';
  //element.parentNode.MaterialTextfield.boundUpdateClassesHandler();
}

// Enables or disables the submit button depending on the values of the input
// fields.
function toggleButton() {
  if (pinInputElement.value) {
    submitButtonElement.removeAttribute('disabled');
  } else {
    submitButtonElement.setAttribute('disabled', 'true');
  }
}

function loadMembers() {
  var callback = function(snap) {
    var data = snap.val();
    displayMember(snap.key, data.name, data.city, data.payment_date);
  };

  firebase.database().ref('/members/').on('child_added', callback);
  firebase.database().ref('/members/').on('child_changed', callback);
}

var MEMBER_TEMPLATE =
    '<div class="member-container">' +
      '<div class="spacing"><div class="pic"></div></div>' +
      '<div class="member"></div>' +
    '</div>';

// A loading image URL.
var LOADING_IMAGE_URL = 'https://www.google.com/images/spin-32.gif?a';


function displayMember(key, name, city, paymentDate) {
  var div = document.getElementById(key);
  if (!div) {
    var container = document.createElement('div');
    container.innerHTML = MEMBER_TEMPLATE;
    div = container.firstChild;
    div.setAttribute('id', key);
    pinListElement.appendChild(div);
  }
  var memberElement = div.querySelector('.member');
  memberElement.textContent = '[' + key + '] ' + name + ' from ' + city + ' paid on ' + paymentDate;
  setTimeout(function() {div.classList.add('visible')}, 1);
  pinListElement.scrollTop = pinListElement.scrollHeight;
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
var pinListElement = document.getElementById('pins');
var recordFormElement = document.getElementById('record-form');
var pinInputElement = document.getElementById('pin');
var locationInputElement = document.getElementById('location');
var paymentDateInputElement = document.getElementById('payment_date');
var nameInputElement = document.getElementById('name');
var clubInputElement = document.getElementById('club');
var countryInputElement = document.getElementById('country');
var strengthInputElement = document.getElementById('strength');
var gorInputElement = document.getElementById('gor');
var lastNameInputElement = document.getElementById('last_name');
var birthDateInputElement = document.getElementById('birth_date');
var amountInputElement = document.getElementById('amount');
var paymentDocTypeInputElement = document.getElementById('payment_doc_type');
var submitButtonElement = document.getElementById('submit');
var imageButtonElement = document.getElementById('submitImage');
var imageFormElement = document.getElementById('image-form');
var mediaCaptureElement = document.getElementById('mediaCapture');
var userPicElement = document.getElementById('user-pic');
var userNameElement = document.getElementById('user-name');
var signInButtonElement = document.getElementById('sign-in');
var signOutButtonElement = document.getElementById('sign-out');
var signInSnackbarElement = document.getElementById('must-signin-snackbar');

var tournamentListElement = document.getElementById('tournaments');

// Saves record on form submit.
recordFormElement.addEventListener('submit', onRecordFormSubmit);
signOutButtonElement.addEventListener('click', signOut);
signInButtonElement.addEventListener('click', signIn);

// Toggle for the button.
pinInputElement.addEventListener('keyup', toggleButton);
pinInputElement.addEventListener('change', toggleButton);

// initialize Firebase
initFirebaseAuth();

loadMembers();
