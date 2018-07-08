package com.crilu.gothandroid.data;

import android.support.annotation.NonNull;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.model.firestore.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.crilu.gothandroid.GothandroidApplication.USER_DOC_REF_PATH;

public class UserDao {

    public static void saveOrUpdateUserOnFirebase(final String uid, String firstName, String lastName, String egfPin, String ffgLic, String agaId) {
        Map<String, Object> userToSave = new HashMap<>();
        userToSave.put(User.UID, uid);
        userToSave.put(User.TOKEN, GothandroidApplication.getCurrentToken());
        userToSave.put(User.FIRST_NAME, firstName);
        userToSave.put(User.LAST_NAME, lastName);
        userToSave.put(User.EGF_PIN, egfPin);
        userToSave.put(User.FFG_LIC, ffgLic);
        userToSave.put(User.AGA_ID, agaId);
        userToSave.put(User.REGISTRATION_DATE, new Date(System.currentTimeMillis()));
        FirebaseFirestore db = GothandroidApplication.getFirebaseFirestore();
        db.collection(USER_DOC_REF_PATH).document(uid).set(userToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Timber.d("User %s was saved or updated", uid);
                } else {
                    Timber.d(task.getException());
                }
            }
        });
    }

}
