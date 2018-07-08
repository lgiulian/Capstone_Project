package com.crilu.gothandroid;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.crilu.gothandroid.adapter.TournamentPublishedListAdapter;
import com.crilu.gothandroid.data.GothaContract;
import com.crilu.gothandroid.data.GothaPreferences;
import com.crilu.gothandroid.model.TournamentsViewModel;
import com.crilu.gothandroid.model.firestore.Subscription;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.sync.GothaSyncUtils;
import com.crilu.gothandroid.utils.TournamentUtils;
import com.crilu.opengotha.TournamentInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.crilu.gothandroid.GothandroidApplication.RESULT_DOC_REF_RELATIVE_PATH;
import static com.crilu.gothandroid.GothandroidApplication.SUBSCRIPTION_DOC_REF_RELATIVE_PATH;
import static com.crilu.gothandroid.GothandroidApplication.TOURNAMENT_DOC_REF_PATH;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TournamentPublishedListAdapter.OnTournamentClickListener {

    private FirebaseAuth mAuth;

    private TournamentsViewModel mModel;

    private CoordinatorLayout mCoordinatorLayout;
    private TextView mTournamentName;
    private ImageView mProfilePhoto;
    private List<Tournament> mPublishedTournament = new ArrayList<>();
    private TournamentPublishedListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mRefreshedToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTournament();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupViewModel();
        GothaSyncUtils.initialize(this);

        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        mTournamentName = navigationView.getHeaderView(0).findViewById(R.id.tournament_name);
        mProfilePhoto = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        mRecyclerView = findViewById(R.id.tournaments_list_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TournamentPublishedListAdapter(this, mPublishedTournament);
        mRecyclerView.setAdapter(mAdapter);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                mRefreshedToken = instanceIdResult.getToken();
                GothandroidApplication.setCurrentToken(mRefreshedToken);
                Timber.d("token: %s", mRefreshedToken);
            }
        });

        //fetchTournaments();
    }

    private void setupViewModel() {
        mModel = ViewModelProviders.of(this).get(TournamentsViewModel.class);
        final Observer<List<Tournament>> listObserver = new Observer<List<Tournament>>() {
            @Override
            public void onChanged(@Nullable final List<Tournament> newList) {
                // Update the UI
                Timber.d("in MainActivity, in onChange of the observer set on viewModel");
                mPublishedTournament = newList;
                mAdapter.setData(newList);
                mAdapter.notifyDataSetChanged();
            }
        };

        mModel.getTournaments().observe(this, listObserver);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = mAdapter.getSelectedItemForContextMenu();
        Tournament selectedTournament = mPublishedTournament.get(position);
        switch (item.getItemId()) {
            case R.id.open:
                openTournament(selectedTournament);
                return true;
            case R.id.edit:
                editTournament(selectedTournament);
                return true;
            case R.id.delete:
                deleteTournament(selectedTournament);
                return true;
            case R.id.publish_results:
                publishResultsOnFirestore(selectedTournament);
                return true;
            case R.id.publish_tournament:
                createLocalFileAndPublishOnFirestore(selectedTournament);
                return true;
            case R.id.register:
                registerTournament(selectedTournament);
                return true;
            case R.id.subscribe:
                observeTournament(selectedTournament);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new) {
            createNewTournament();
        } else if (id == R.id.nav_open_unpublished) {

        } else if (id == R.id.nav_save_locally) {

        } else if (id == R.id.nav_players_manager) {
            managePlayers();
        } else if (id == R.id.nav_pair) {
            pair();
        } else if (id == R.id.nav_tournament_options) {
        } else if (id == R.id.nav_game_options) {
        } else if (id == R.id.nav_my_account) {
            myAccount();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_publish) {

        } else if (id == R.id.nav_publish_results) {
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void myAccount() {
        Intent intent = new Intent(this, MyAccount.class);
        startActivity(intent);
    }

    private void createLocalFileAndPublishOnFirestore(final Tournament selectedTournament) {
        if (selectedTournament == null) {
            Snackbar.make(mCoordinatorLayout, getString(R.string.no_tournament_selected), Snackbar.LENGTH_LONG).show();
            return;
        }

        String currUser = GothandroidApplication.getCurrentUser();
        Date creationDate = selectedTournament.getCreationDate() != null? selectedTournament.getCreationDate(): new Date();
        if (!TextUtils.isEmpty(currUser) && !TextUtils.isEmpty(selectedTournament.getContent())) {
            Map<String, Object> tournamentToSave = new HashMap<>();
            tournamentToSave.put(Tournament.FULL_NAME, selectedTournament.getFullName());
            tournamentToSave.put(Tournament.SHORT_NAME, selectedTournament.getShortName());
            tournamentToSave.put(Tournament.BEGIN_DATE, selectedTournament.getBeginDate());
            tournamentToSave.put(Tournament.LOCATION, selectedTournament.getLocation());
            tournamentToSave.put(Tournament.DIRECTOR, selectedTournament.getDirector());
            tournamentToSave.put(Tournament.CONTENT, selectedTournament.getContent());
            tournamentToSave.put(Tournament.CREATOR, mRefreshedToken);
            tournamentToSave.put(Tournament.CREATION_DATE, creationDate);
            FirebaseFirestore db = GothandroidApplication.getFirebaseFirestore();
            db.collection(TOURNAMENT_DOC_REF_PATH).add(tournamentToSave).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Timber.d("Tournament %s was saved", selectedTournament.getFullName());
                        String givenId = task.getResult().getId();
                        selectedTournament.setIdentity(givenId);
                        ContentResolver gothaContentResolver = getContentResolver();
                        ContentValues cv = GothaSyncUtils.getSingleTournamentContentValues(selectedTournament);
                        gothaContentResolver.update(
                                ContentUris.withAppendedId(GothaContract.TournamentEntry.CONTENT_URI, selectedTournament.getId()),
                                cv,
                                null,
                                null);
                        Snackbar.make(mCoordinatorLayout, getString(R.string.tournament_published), Snackbar.LENGTH_LONG).show();
                    } else {
                        Timber.d(task.getException());
                        Snackbar.make(mCoordinatorLayout, getString(R.string.tournament_publish_error), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Nullable
    private TournamentInterface checkTournamentOpened() {
        final TournamentInterface tournament = GothandroidApplication.getGothaModelInstance().getTournament();
        if (tournament == null) {
            Snackbar.make(mCoordinatorLayout, getString(R.string.no_currently_opened_tournament), Snackbar.LENGTH_LONG).show();
            return null;
        }
        return tournament;
    }

    private void publishResultsOnFirestore(Tournament selectedTournament) {
        String tournamentIdentity = selectedTournament.getIdentity();
        Timber.d("prepare to save results for tournament %s", tournamentIdentity);

        Map<String, Object> resultToSave = new HashMap<>();
        resultToSave.put(Tournament.RESULT_CONTENT, "R1 R2 R3 R4 R5");
        FirebaseFirestore db = GothandroidApplication.getFirebaseFirestore();
        db.collection(TOURNAMENT_DOC_REF_PATH + "/" + tournamentIdentity + RESULT_DOC_REF_RELATIVE_PATH)
                .add(resultToSave)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Timber.d("Result %s was saved", task.getResult().getId());
                    Snackbar.make(mCoordinatorLayout, getString(R.string.tournament_result_published), Snackbar.LENGTH_LONG).show();
                } else {
                    Timber.d(task.getException());
                    Snackbar.make(mCoordinatorLayout, getString(R.string.tournament_result_error), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void observeTournament(Tournament selectedTournament) {
        recordSubscriptionOnFirebase(selectedTournament, Subscription.INTENT_OBSERVER, R.string.tournament_subscription_success, R.string.tournament_subscription_error);
    }

    private void registerTournament(Tournament selectedTournament) {
        recordSubscriptionOnFirebase(selectedTournament, Subscription.INTENT_PARTICIPANT, R.string.tournament_registration_success, R.string.tournament_registration_error);
    }

    private void recordSubscriptionOnFirebase(Tournament selectedTournament, String intention, final int successMessage, final int errorMessage) {
        String tournamentIdentity = selectedTournament.getIdentity();
        Timber.d("selectedTournament: %s", tournamentIdentity);
        if (TextUtils.isEmpty(tournamentIdentity)) {
            Snackbar.make(mCoordinatorLayout, getString(R.string.tournament_not_published), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(GothandroidApplication.getCurrentUser())) {
            Snackbar.make(mCoordinatorLayout, getString(R.string.subscription_you_are_not_loggedin), Snackbar.LENGTH_LONG).show();
            return;
        }
        FirebaseMessaging.getInstance().subscribeToTopic(tournamentIdentity);
        final Subscription subscription = new Subscription(selectedTournament.getId(), mRefreshedToken,
                GothaPreferences.getUserEgfPin(this), GothaPreferences.getUserFfgLic(this),
                GothaPreferences.getUserAgaId(this), intention,
                new Date(), Subscription.STATE_ACTIVE);
        Map<String, Object> subscriptionToSave = new HashMap<>();
        subscriptionToSave.put(Subscription.AGA_ID, subscription.getAgaId());
        subscriptionToSave.put(Subscription.EGF_PIN, subscription.getEgfPin());
        subscriptionToSave.put(Subscription.FFG_LIC, subscription.getFfgLic());
        subscriptionToSave.put(Subscription.INTENT, subscription.getIntent());
        subscriptionToSave.put(Subscription.STATE, subscription.getState());
        subscriptionToSave.put(Subscription.SUBSCRIPTION_DATE, subscription.getSubscriptionDate());
        subscriptionToSave.put(Subscription.TOKEN, mRefreshedToken);
        FirebaseFirestore db = GothandroidApplication.getFirebaseFirestore();
        db.collection(TOURNAMENT_DOC_REF_PATH + "/" + selectedTournament.getIdentity() + SUBSCRIPTION_DOC_REF_RELATIVE_PATH)
                .add(subscriptionToSave)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Timber.d("Subscription for pin %s was saved", GothaPreferences.getUserEgfPin(MainActivity.this));
                    String givenId = task.getResult().getId();
                    subscription.setIdentity(givenId);
                    ContentResolver gothaContentResolver = getContentResolver();
                    ContentValues cv = GothaSyncUtils.getSingleSubscriptionContentValues(subscription);
                    gothaContentResolver.insert(
                            GothaContract.SubscriptionEntry.CONTENT_URI,
                            cv);
                    Snackbar.make(mCoordinatorLayout, getString(successMessage), Snackbar.LENGTH_LONG).show();
                } else {
                    Timber.d(task.getException());
                    Snackbar.make(mCoordinatorLayout, getString(errorMessage), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void deleteTournament(Tournament selectedTournament) {

    }

    private void editTournament(Tournament selectedTournament) {

    }

    private void openTournament(Tournament selectedTournament) {
        String fileContents = selectedTournament.getContent();
        TournamentUtils.openTournament(this, fileContents);
    }

    private void updateUI() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            GothandroidApplication.setCurrentUser(currentUser.getUid());
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = currentUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = currentUser.getUid();
            Timber.d("Name -%s , email -%s , address -%s , profile photo -%s, uid -%s", name, email, photoUrl, emailVerified, uid);
        }
        TournamentInterface currentTournament = GothandroidApplication.getGothaModelInstance().getTournament();
        if (currentTournament != null) {
            mTournamentName.setText(currentTournament.getFullName());
        }
    }

    private void managePlayers() {
        final TournamentInterface tournament = checkTournamentOpened();
        if (tournament == null) return;

        Intent intent = new Intent(this, PlayersManagerActivity.class);
        startActivity(intent);
    }

    private void createNewTournament() {
        Intent intent = new Intent(this, CreateTournamentActivity.class);
        startActivity(intent);
    }

    private void pair() {
        final TournamentInterface tournament = checkTournamentOpened();
        if (tournament == null) return;

        Intent intent = new Intent(this, PairActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTournamentSelected(int position) {

    }
}
