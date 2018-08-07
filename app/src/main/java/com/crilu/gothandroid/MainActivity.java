package com.crilu.gothandroid;

import android.app.ActivityOptions;
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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crilu.gothandroid.adapter.TournamentPublishedListAdapter;
import com.crilu.gothandroid.data.GothaContract;
import com.crilu.gothandroid.data.GothaPreferences;
import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.model.TournamentsViewModel;
import com.crilu.gothandroid.model.firestore.Subscription;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.sync.GothaSyncUtils;
import com.crilu.gothandroid.utils.FileUtils;
import com.crilu.gothandroid.utils.ParsePlayersIntentService;
import com.crilu.gothandroid.utils.TournamentUtils;
import com.crilu.opengotha.TournamentInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static com.crilu.gothandroid.GothandroidApplication.RESULT_DOC_ID_H9;
import static com.crilu.gothandroid.GothandroidApplication.RESULT_DOC_ID_HTML;
import static com.crilu.gothandroid.GothandroidApplication.RESULT_DOC_REF_PATH;
import static com.crilu.gothandroid.GothandroidApplication.SUBSCRIPTION_DOC_REF_PATH;
import static com.crilu.gothandroid.GothandroidApplication.TOURNAMENT_DOC_REF_PATH;

public class MainActivity extends AppCompatAdActivity
        implements NavigationView.OnNavigationItemSelectedListener, TournamentPublishedListAdapter.OnTournamentClickListener {

    private FirebaseUser mCurrentUser;

    private CoordinatorLayout mCoordinatorLayout;
    private TextView mTournamentName;
    private List<Tournament> mPublishedTournament = new ArrayList<>();
    private TournamentPublishedListAdapter mAdapter;
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

        initialize();
        setupViewModel();
        GothaSyncUtils.initialize(this);

        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        mTournamentName = navigationView.getHeaderView(0).findViewById(R.id.tournament_name);
        RecyclerView mRecyclerView = findViewById(R.id.tournaments_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
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

    private void initialize() {
        Intent parsePlayersIntent = new Intent(this, ParsePlayersIntentService.class);
        startService(parsePlayersIntent);
    }

    private void setupViewModel() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Timber.d("Getting mCurrentUser");
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null) {
            Timber.d("Setting current user");
            GothandroidApplication.setCurrentUser(mCurrentUser.getUid());
        }
        TournamentsViewModel mModel = ViewModelProviders.of(this).get(TournamentsViewModel.class);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        int position = mAdapter.getSelectedItemForContextMenu();
        Tournament selectedTournament = mPublishedTournament.get(position);
        switch (item.getItemId()) {
            case R.id.open:
                openTournament(selectedTournament);
                return true;
            case R.id.edit:
                editTournament();
                return true;
            case R.id.publish_results:
                publishResultsOnFirestore(selectedTournament);
                return true;
            case R.id.publish_tournament:
                createLocalFileAndPublishOnFirestore(selectedTournament);
                return true;
            case R.id.send_message_to_all:
                sendMessageToAll(selectedTournament);
                return true;
            case R.id.save_and_upload:
                saveAndUploadOnFirestore(selectedTournament);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_new:
                createNewTournament();
                break;
            case R.id.nav_players_manager:
                managePlayers();
                break;
            case R.id.nav_pair:
                pair();
                break;
            case R.id.nav_results:
                displayResults();
                break;
            case R.id.nav_tournament_options:
                tournamentSettings();
                break;
            case R.id.nav_game_options:
                gamesSettings();
                break;
            case R.id.nav_my_account:
                myAccount();
                break;
            case R.id.nav_message:
                displayMessageScreen();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void myAccount() {
        Intent intent = new Intent(this, MyAccount.class);
        startNewActivity(intent);
    }

    private void createLocalFileAndPublishOnFirestore(final Tournament selectedTournament) {
        if (selectedTournament == null) {
            Snackbar.make(mCoordinatorLayout, getString(R.string.no_tournament_selected), Snackbar.LENGTH_LONG).show();
            return;
        }
        String UID = checkUserLoggedIn();
        if (TextUtils.isEmpty(UID)) return;

        String currUser = GothandroidApplication.getCurrentUser();
        Date creationDate = selectedTournament.getCreationDate() != null? selectedTournament.getCreationDate(): new Date();
        if (!TextUtils.isEmpty(currUser) && !TextUtils.isEmpty(selectedTournament.getContent())) {
            selectedTournament.setLastModificationDate(creationDate);
            DatabaseReference db = GothandroidApplication.getFireDatabase();
            DatabaseReference tournamentRef = db.child(TOURNAMENT_DOC_REF_PATH).push();
            final String givenId = tournamentRef.getKey();
            tournamentRef.setValue(selectedTournament)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Timber.d("Tournament %s was saved", selectedTournament.getFullName());
                            selectedTournament.setIdentity(givenId);
                            ContentResolver gothaContentResolver = getContentResolver();
                            ContentValues cv = GothaSyncUtils.getSingleTournamentContentValues(selectedTournament);
                            gothaContentResolver.update(
                                    ContentUris.withAppendedId(GothaContract.TournamentEntry.CONTENT_URI, selectedTournament.getId()),
                                    cv,
                                    null,
                                    null);
                            Snackbar.make(mCoordinatorLayout, getString(R.string.tournament_published), Snackbar.LENGTH_LONG).show();
                            displayAds();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.d(e);
                            Snackbar.make(mCoordinatorLayout, getString(R.string.tournament_publish_error), Snackbar.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void saveAndUploadOnFirestore(final Tournament selectedTournament) {
        if (selectedTournament == null) {
            Snackbar.make(mCoordinatorLayout, getString(R.string.no_tournament_selected), Snackbar.LENGTH_LONG).show();
            return;
        }
        String UID = checkUserLoggedIn();
        if (TextUtils.isEmpty(UID)) return;
        final TournamentInterface currentOpenedTournament = checkTournamentOpened();
        if (currentOpenedTournament == null) return;
        if (!TextUtils.isEmpty(selectedTournament.getIdentity())
                && selectedTournament.getIdentity().equals(currentOpenedTournament.getTournamentIdentity())) {
            Timber.d("saving tournament %s", selectedTournament.getIdentity());
            TournamentDao.saveCurrentTournamentAndUploadOnFirestore(this, selectedTournament, UID, true, mCoordinatorLayout);
        }
    }

    private void sendMessageToAll(Tournament selectedTournament) {
        if (selectedTournament == null) {
            Snackbar.make(mCoordinatorLayout, getString(R.string.no_tournament_selected), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!TextUtils.isEmpty(selectedTournament.getIdentity())) {
            Timber.d("sending message to tournament %s", selectedTournament.getIdentity());
            Intent intent = new Intent(this, SendMessageActivity.class);
            intent.putExtra(SendMessageActivity.TOURNAMENT_KEY, selectedTournament.getIdentity());
            startNewActivity(intent);
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

    private String checkUserLoggedIn() {
        String UID = GothandroidApplication.getCurrentUser();
        if (TextUtils.isEmpty(UID)) {
            Snackbar.make(mCoordinatorLayout, getString(R.string.you_are_not_logged_in), Snackbar.LENGTH_LONG).show();
            return null;
        }
        return UID;
    }

    private void publishResultsOnFirestore(Tournament selectedTournament) {
        String tournamentIdentity = selectedTournament.getIdentity();
        Timber.d("prepare to save results for tournament %s", tournamentIdentity);

        String resultsHtml = getResultsHtml(selectedTournament);
        DatabaseReference db = GothandroidApplication.getFireDatabase();
        DatabaseReference resultsRef = db.child(RESULT_DOC_REF_PATH + "/" + tournamentIdentity + "/" + RESULT_DOC_ID_HTML + "/" + Tournament.RESULT_CONTENT);
        resultsRef.setValue(resultsHtml)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.d("Results were saved in format html");
                        Snackbar.make(mCoordinatorLayout, getString(R.string.tournament_result_published), Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.d(e);
                        Snackbar.make(mCoordinatorLayout, getString(R.string.tournament_result_error), Snackbar.LENGTH_LONG).show();
                    }
                });

        String resultsH9 = getResultsH9(selectedTournament);
        resultsRef = db.child(RESULT_DOC_REF_PATH + "/" + tournamentIdentity + "/" + RESULT_DOC_ID_H9 + "/" + Tournament.RESULT_CONTENT);
        resultsRef.setValue(resultsH9)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.d("Results were saved in format h9");
                        displayAds();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.d(e);
                    }
                });
    }

    private String getResultsHtml(Tournament tournament) {
        String filename = tournament.getFullName() + "_Standings.html";
        File file = new File(getFilesDir(), filename);
        GothandroidApplication.getPublishInstance().btnPublishStActionPerformed(file);
        String resultsHtml = null;
        try {
            resultsHtml = FileUtils.getFileContents(file);
        } catch (IOException e) {
            Timber.d("Failed to read html file");
            e.printStackTrace();
        }
        return resultsHtml;
    }

    private String getResultsH9(Tournament tournament) {
        String filename = tournament.getFullName() + "_Standings.h9";
        File file = new File(getFilesDir(), filename);
        GothandroidApplication.getPublishInstance().exportRLEGF(file);
        String resultsH9 = null;
        try {
            resultsH9 = FileUtils.getFileContents(file, true);
        } catch (IOException e) {
            Timber.d("Failed to read h9 file");
            e.printStackTrace();
        }
        return resultsH9;
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
        String UID = checkUserLoggedIn();
        if (TextUtils.isEmpty(UID)) return;

        FirebaseMessaging.getInstance().subscribeToTopic(tournamentIdentity);
        final Subscription subscription = new Subscription(selectedTournament.getId(), UID,
                GothaPreferences.getUserEgfPin(this), GothaPreferences.getUserFfgLic(this),
                GothaPreferences.getUserAgaId(this), intention,
                new Date(), Subscription.STATE_ACTIVE);
        DatabaseReference db = GothandroidApplication.getFireDatabase();
        DatabaseReference subscriptionRef = db.child(SUBSCRIPTION_DOC_REF_PATH + "/" + selectedTournament.getIdentity()).push();
        final String givenId = subscriptionRef.getKey();
        subscriptionRef.setValue(subscription)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.d("Subscription for pin %s was saved", GothaPreferences.getUserEgfPin(MainActivity.this));
                        subscription.setIdentity(givenId);
                        ContentResolver gothaContentResolver = getContentResolver();
                        ContentValues cv = GothaSyncUtils.getSingleSubscriptionContentValues(subscription);
                        gothaContentResolver.insert(
                                GothaContract.SubscriptionEntry.CONTENT_URI,
                                cv);
                        Snackbar.make(mCoordinatorLayout, getString(successMessage), Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.d(e);
                        Snackbar.make(mCoordinatorLayout, getString(errorMessage), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void editTournament() {
        tournamentSettings();
    }

    private void openTournament(Tournament selectedTournament) {
        String fileContents = selectedTournament.getContent();
        TournamentUtils.openTournament(this, fileContents, selectedTournament.getIdentity());
        updateUI();
    }

    private void updateUI() {
        if (mCurrentUser != null) {
            GothandroidApplication.setCurrentUser(mCurrentUser.getUid());
            // Name, email address, and profile photo Url
            String name = mCurrentUser.getDisplayName();
            String email = mCurrentUser.getEmail();
            Uri photoUrl = mCurrentUser.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = mCurrentUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = mCurrentUser.getUid();
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
        startNewActivity(intent);
    }

    private void createNewTournament() {
        String UID = checkUserLoggedIn();
        if (TextUtils.isEmpty(UID)) return;
        Intent intent = new Intent(this, CreateTournamentActivity.class);
        startNewActivity(intent);
    }

    private void pair() {
        final TournamentInterface tournament = checkTournamentOpened();
        if (tournament == null) return;

        Intent intent = new Intent(this, PairActivity.class);
        startNewActivity(intent);
    }

    private void displayResults() {
        startNewActivity(new Intent(this, ResultActivity.class));
    }

    private void tournamentSettings() {
        final TournamentInterface tournament = checkTournamentOpened();
        if (tournament == null) return;

        Intent intent = new Intent(this, TournamentSettingsActivity.class);
        startNewActivity(intent);
    }

    private void gamesSettings() {
        final TournamentInterface tournament = checkTournamentOpened();
        if (tournament == null) return;

        Intent intent = new Intent(this, GamesOptionsActivity.class);
        startNewActivity(intent);
    }

    private void displayMessageScreen() {
        startNewActivity(new Intent(this, MessageActivity.class));
    }

    private void startNewActivity(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onTournamentSelected(int position) {

    }
}
