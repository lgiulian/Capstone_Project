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
exports.sendSubscriberNotification = functions.firestore
    .document('/tournament/{topicId}/result/{docId}')
    .onCreate((snap, context) => {
      const topicId = context.params.topicId;
      console.log('We have a new results published for tournament:', topicId);

      var message = {
        data: {
          score: '850',
          time: '2:45',
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

exports.sendRegistrationNotification = functions.firestore
    .document('/tournament/{topicId}/subscription/{docId}')
    .onCreate((snap, context) => {
      const newValue = snap.data();
      const topicId = context.params.topicId;

      if (newValue.intent !== 'participant') {
        return 0;
      }
      console.log('We have a new registration for tournament:', topicId);

      let creatorToken;
      return admin.firestore()
        .collection('tournament')
        .doc(topicId)
        .get()
        .then(doc => {
              creatorToken = doc.data().creator;
              console.log('Got creator token: ' + creatorToken);
              var message = {
                data: {
                  command: 'registration',
                  tournament_name: doc.data().fullName,
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
