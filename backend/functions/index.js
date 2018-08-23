'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();


// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

exports.helloWorld = functions.https.onRequest((request, response) => {
  response.send("Hello from Firebase!");
});

/**
 * Triggers when a user gets a new follower and sends a notification.
 *
 * Followers add a flag to `/tournament/{topic}/subscriber/{followerUid}`.
 * Users save their device notification tokens to `/user/{topic}/notificationTokens/{notificationToken}`.
 */
exports.sendSubscriberNotification_FDB = functions.database.ref('/result/{topicId}/{docId}')
    .onCreate((snap, context) => {
      const topicId = context.params.topicId;
      console.log('We have a new results published for tournament:', topicId);

      var message = {
        data: {
          message: 'This is a notification with results published'
        },
        topic: topicId
      };

      // Send a message to devices subscribed to the provided topic.
      return admin.messaging().send(message)
        .then((response) => {
          // Response is a message ID string.
          console.log('Successfully sent message:', response);
          return 0;
        })
        .catch((error) => {
          console.log('Error sending message:', error);
        });
    });

exports.sendRegistrationNotification_FDB = functions.database.ref('/subscription/{topicId}/{docId}')
    .onCreate((snap, context) => {
      const newValue = snap.val();
      const topicId = context.params.topicId;

      if (newValue.intent !== 'participant') {
        return 0;
      }
      console.log('We have a new registration for tournament:', topicId);

      let creatorUid;
      let creatorToken;

      return admin.database()
        .ref('tournament')
        .child(topicId)
        .on("value", function(doc) {
            creatorUid = doc.val().creator;
            console.log('Got creator uid: ' + creatorUid);
            return admin.database().ref('user').child(creatorUid).on("value", function(snap) {
              creatorToken = snap.val().token;
              console.log('Got creator token: ' + creatorToken);
              var message = {
                data: {
                  command: 'registration',
                  tournament_name: doc.val().fullName,
                  tournament_id: topicId,
                  egf_pin: newValue.egfPin,
                  message: 'This is a notification for a new registration'
                },
                token: creatorToken
              };

              // Send a message to tournament creator device.
              return admin.messaging().send(message)
                .then((response) => {
                  // Response is a message ID string.
                  console.log('Successfully sent message:', response);
                  return 0;
                })
                .catch((error) => {
                  console.log('Error sending message:', error);
                });
              });
            });
        });

exports.sendTournamentMessage_FDB = functions.database.ref('/message/{topicId}/{docId}')
    .onCreate((snap, context) => {
      const topicId = context.params.topicId;
      const newMessage = snap.val();
      console.log('We have a new message to participant in tournament:', topicId);

      var message = {
        data: {
          message: newMessage.message,
          tournament_name: newMessage.from
        },
        topic: topicId
      };

      // Send a message to devices subscribed to the provided topic.
      return admin.messaging().send(message)
        .then((response) => {
          // Response is a message ID string.
          console.log('Successfully sent message:', response);
          return 0;
        })
        .catch((error) => {
          console.log('Error sending message:', error);
        });
    });
