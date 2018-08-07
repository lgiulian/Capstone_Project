package com.crilu.gothandroid.data;

import android.support.annotation.NonNull;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.model.firestore.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

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
        DatabaseReference db = GothandroidApplication.getFireDatabase();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(USER_DOC_REF_PATH + "/" + uid, userToSave);
        db.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.d("User %s was saved or updated", uid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.e(e);
                    }
                });
    }

}
