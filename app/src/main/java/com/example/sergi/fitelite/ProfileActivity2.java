package com.example.sergi.fitelite;

import android.content.Intent;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ProfileActivity2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /***
     *The elements are firstly declared in order to be initialised in the onCreate method.
     *There is a textView that is used to display the user information in the navigation drawer, the
     *firebaseAuth element which fetches the user email and password and the databaseReference which
     *fetches all the user information from the Firebase Database.
     * @param savedInstanceState
     */

    private FirebaseAuth firebaseAuth;

    private TextView textViewTitle;

    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /***
         * In the onCreate method, the elements are being initialised and the user email and password are fetched and set
         * to a textView in the navigation drawer.
         * One important thing to be specified is that there is another element declared that fetches the logged in user Name
         * and Surname.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        //In this stage, the user is being fetched if the user is not null. And the database reference is being declared
        //with the required path to get the user information.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        textViewTitle = (TextView)header.findViewById(R.id.textViewTitle);

        //This value event listener takes the data from the database and sets it to the textView.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName =(String) dataSnapshot.child("userName").getValue();
                String userSurname = (String) dataSnapshot.child("userSurname").getValue();
                textViewTitle.setText(userName+" "+userSurname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        TextView textViewLogOut = (TextView) header.findViewById(R.id.textViewProfile);

        //This line actually gets the email of the user and sets it in the header of the navigation drawer.
        textViewLogOut.setText(user.getEmail());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
        getMenuInflater().inflate(R.menu.profile_activity2, menu);
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

        /***
         * This is a part that we wanted to include in the functionality of the app. This part actually takes
         * care of the camera and lets the user take pictures and store them locally.
         * We decided to remove this feature as it is redundant and we implemented a similar one in the
         * ProfileActivity2 which lets the user take a picture and store it on the firebase storage.
         * Another thing worth specifying is that there is a mediaScanner that scans for the pictures taken
         * and shows them in the gallery straight away.
         * One more reason that made us remove the camera function is that we could not get our heads around the
         * permissions needed to write to the external storage.
         * As another function when we were still working on the camera implementation, we decided to let
         * the user open the gallery to access the pictures taken.
         *
         */
        /*if (id == R.id.nav_camera) {

            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String pictureName = pictureName();
            File imageFile = new File(pictureDirectory,pictureName);
            final Uri pictureUri = Uri.fromFile(imageFile);
            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
            startActivityForResult(camera_intent, CAM_REQUEST);
            MediaScannerConnection.scanFile(this
                    ,new String[] {imageFile.toString()},null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });


        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "content://media/internal/images/media"));
            startActivity(intent);**/
        /***
         * In this part, there are fragment managers which manage the created fragments which are accessed
         * when the user presses an item. The fragment manager assigns tags to the fragments which can be accessed at a later
         * time.
         */
        if (id == R.id.nav_slideshow) {
            ProfileFragment profileFragment = new ProfileFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragmentlayout, profileFragment, profileFragment.getTag()).commit();
        } else if (id == R.id.nav_manage) {
            MapFragment mapFragment = new MapFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragmentlayout, mapFragment, mapFragment.getTag()).commit();
        } else if (id == R.id.nav_share) {
            PlansFragment plansFragment = new PlansFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragmentlayout, plansFragment, plansFragment.getTag()).commit();

            /***
             * This part handles the LogOut process which basically tells firebase to sign out the current user and to
             * restore the mainActivity which is the login page of the app.
             */
        } else if (id == R.id.nav_send) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
