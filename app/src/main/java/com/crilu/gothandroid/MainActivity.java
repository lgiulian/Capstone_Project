package com.crilu.gothandroid;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.crilu.gothandroid.adapter.TournamentPublishedListAdapter;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.utils.FileUtils;
import com.crilu.opengotha.ExternalDocument;
import com.crilu.opengotha.TournamentInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.crilu.gothandroid.GothandroidApplication.TOURNAMENT_DOC_REF_PATH;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TournamentPublishedListAdapter.OnTournamentClickListener {

    public static final String FULL_NAME = "fullName";
    public static final String SHORT_NAME = "shortName";
    public static final String CONTENT = "content";
    public static final String BEGIN_DATE = "beginDate";
    public static final String LOCATION = "location";
    public static final String DIRECTOR = "director";
    public static final String RESULT_CONTENT = "content";
    public static final String CREATOR = "creator";
    private FirebaseAuth mAuth;

    private CoordinatorLayout mCoordinatorLayout;
    private TextView mTournamentName;
    private ImageView mProfilePhoto;
    private List<Tournament> mPublishedTournament = new ArrayList<>();
    private TournamentPublishedListAdapter mAdapter;

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

        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        mTournamentName = navigationView.getHeaderView(0).findViewById(R.id.tournament_name);
        mProfilePhoto = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        RecyclerView recyclerView = findViewById(R.id.tournaments_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TournamentPublishedListAdapter(this, mPublishedTournament);
        recyclerView.setAdapter(mAdapter);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Timber.d("token: %s", refreshedToken);

        fetchTournaments();
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
            createLocalFileAndPublishOnFirestore();
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

    private void createLocalFileAndPublishOnFirestore() {
        final TournamentInterface tournament = checkTournamentOpened();
        if (tournament == null) return;

        String filename = tournament.getFullName() + ".xml";
        File file = new File(getFilesDir(), filename);
        ExternalDocument.generateXMLFile(tournament, file);
        String currUser = GothandroidApplication.getCurrentUser();
        try {
            String tournamentContent = FileUtils.getFileContents(file);
            if (!TextUtils.isEmpty(currUser) && !TextUtils.isEmpty(tournamentContent)) {
                Map<String, Object> tournamentToSave = new HashMap<>();
                tournamentToSave.put(FULL_NAME, tournament.getFullName());
                tournamentToSave.put(SHORT_NAME, tournament.getShortName());
                tournamentToSave.put(BEGIN_DATE, tournament.getTournamentParameterSet().getGeneralParameterSet().getBeginDate());
                tournamentToSave.put(LOCATION, tournament.getTournamentParameterSet().getGeneralParameterSet().getLocation());
                tournamentToSave.put(DIRECTOR, tournament.getTournamentParameterSet().getGeneralParameterSet().getDirector());
                tournamentToSave.put(CONTENT, tournamentContent);
                tournamentToSave.put(CREATOR, GothandroidApplication.getCurrentUser());
                //DocumentReference docRef = FirebaseFirestore.getInstance().document(TOURNAMENT_DOC_REF_PATH
                //        + "/" + currUser + "-#-" + tournament.getFullName());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(TOURNAMENT_DOC_REF_PATH).add(tournamentToSave).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Timber.d("Tournament %s was saved", tournament.getFullName());
                            String givenId = task.getResult().getId();
                        } else {
                            Timber.d(task.getException());
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    private void publishResultsOnFirestore(String tournamentId) {
        Timber.d("prepare to save results for tournament %s", tournamentId);
        Map<String, Object> resultToSave = new HashMap<>();
        resultToSave.put(RESULT_CONTENT, "R1 R2 R3 R4 R5");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(TOURNAMENT_DOC_REF_PATH + "/" + tournamentId + "/result").add(resultToSave).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Timber.d("Result %s was saved", task.getResult().getId());
                } else {
                    Timber.d(task.getException());
                }
            }
        });
    }

    private void fetchTournaments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(TOURNAMENT_DOC_REF_PATH).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mPublishedTournament.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Timber.d(document.getId() + " => " + document.getData());
                        Tournament tournament = document.toObject(Tournament.class);
                        tournament.setIdentity(document.getId());
                        mPublishedTournament.add(tournament);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Timber.d(task.getException());
                }
            }
        });
    }

    private void updateUI() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
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
        Tournament selectedTournament = mPublishedTournament.get(position);
        String tournamentId = selectedTournament.getIdentity();
        Timber.d("selectedTournament: %s", tournamentId);
        //FirebaseMessaging.getInstance().subscribeToTopic(tournamentId);
        publishResultsOnFirestore(tournamentId);
    }
}
