package com.crilu.gothandroid;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.crilu.gothandroid.data.GothaPreferences;
import com.crilu.gothandroid.data.UserDao;
import com.crilu.gothandroid.databinding.ActivityMyAccountBinding;
import com.crilu.opengotha.RatedPlayer;
import com.crilu.opengotha.RatingList;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class MyAccount extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int RC_SIGN_IN = 4212;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;

    private ActivityMyAccountBinding mBinding;
    private ArrayAdapter<RatedPlayer> mAdapter;
    private RatedPlayer mEgdPlayer;
    private String mCurrentPhotoPath;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_account);

        setSupportActionBar(mBinding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            mBinding.loginBtn.setEnabled(false);
            mBinding.logoutBtn.setEnabled(true);
        } else {
            mBinding.loginBtn.setEnabled(true);
            mBinding.logoutBtn.setEnabled(false);
        }
    }

    private void initComponents() {
        RatingList ratingList = GothandroidApplication.getRatingList();
        if (ratingList != null) {
            mAdapter = new ArrayAdapter<RatedPlayer>(this,
                    android.R.layout.simple_dropdown_item_1line, ratingList.getALRatedPlayers());
            mBinding.egfPlayer.setAdapter(mAdapter);
            mBinding.egfPlayer.setOnItemClickListener(this);
        }
        mBinding.firstName.setText(GothaPreferences.getUserFirstName(this));
        mBinding.lastName.setText(GothaPreferences.getUserLastName(this));
        mBinding.egfPlayer.setText(GothaPreferences.getUserEgfPin(this));
        mBinding.agaId.setText(GothaPreferences.getUserAgaId(this));
        mBinding.ffgLic.setText(GothaPreferences.getUserFfgLic(this));
    }

    public void onClickLogin(View view) {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        GothaPreferences.saveUserFirstName(this, mBinding.firstName.getText().toString());
        GothaPreferences.saveUserLastName(this, mBinding.lastName.getText().toString());
        GothaPreferences.saveUserAgaId(this, mBinding.agaId.getText().toString());
        GothaPreferences.saveUserFfgLic(this, mBinding.ffgLic.getText().toString());
        GothaPreferences.saveUserEgfPin(this, mBinding.egfPlayer.getText().toString());
    }

    public void onClickLogout(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Timber.d("signed out");
                        GothandroidApplication.setCurrentUser(null);
                        Snackbar.make(mBinding.coordinatorLayout, getString(R.string.you_are_logged_out_now), Snackbar.LENGTH_LONG).show();
                        updateUI();
                    }
                });
    }

    public void onClickTakePhoto(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Timber.e(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mEgdPlayer = mAdapter.getItem(position);
        mBinding.egfPlayer.setText(mEgdPlayer.getEgfPin());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Timber.d("user=%s, uid=%s", user, user.getUid());
                GothandroidApplication.setCurrentUser(user.getUid());
                saveUserOnFirestore(user.getUid());
                Snackbar.make(mBinding.coordinatorLayout, getString(R.string.you_are_logged_in_now), Snackbar.LENGTH_LONG).show();
                updateUI();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Snackbar.make(mBinding.coordinatorLayout, getString(R.string.error_logging_in), Snackbar.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void saveUserOnFirestore(final String currUser) {
        if (!TextUtils.isEmpty(currUser)) {
            UserDao.saveOrUpdateUserOnFirebase(currUser, mBinding.firstName.getText().toString(), mBinding.lastName.getText().toString(),
                    mBinding.egfPlayer.getText().toString(), mBinding.ffgLic.getText().toString(), mBinding.agaId.getText().toString());
        } else {
            Snackbar.make(mBinding.coordinatorLayout, getString(R.string.no_uid_available), Snackbar.LENGTH_LONG).show();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mBinding.profileBtn.getWidth();
        int targetH = mBinding.profileBtn.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mBinding.profileBtn.setImageBitmap(bitmap);
    }
}
