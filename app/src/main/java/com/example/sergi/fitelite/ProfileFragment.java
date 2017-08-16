package com.example.sergi.fitelite;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    /***
     * Elements are declared.
     */
    View mView;
    Button updateButton;
    TextView nameSurname;
    TextView dateOfBirth;
    TextView weightShow;
    TextView height;
    EditText weight;
    ImageView profilePic;
    private static final int CAMERA_REQUEST_CODE = 1;

    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String profileDownloadedPic;


    public ProfileFragment() {
        // Required empty public constructor
    }


    /***
     * In this method, the layout is inflated in the content and it's named mView.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get the instance of the firebaseAuth.
        firebaseAuth = FirebaseAuth.getInstance();

        //Gets the current user.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Checks if a user is logged in and gets the database entry for the user by the Uid.
        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        }

        /***
         * This value event listener fetches the information from the database and sets it to the textViews.
         */
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName =(String) dataSnapshot.child("userName").getValue();
                String userSurname = (String) dataSnapshot.child("userSurname").getValue();
                String userAge = (String) dataSnapshot.child("userAge").getValue();
                String userHeight = (String) dataSnapshot.child("userHeight").getValue();
                String userWeight = (String) dataSnapshot.child("userWeight").getValue();
                nameSurname.setText(userName+" "+userSurname);
                dateOfBirth.setText("Date of Birth: "+userAge);
                height.setText("Height: "+userHeight);
                weightShow.setText("Weight: "+userWeight);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        updateButton = (Button) mView.findViewById(R.id.button2);

        nameSurname = (TextView) mView.findViewById(R.id.textView5);
        dateOfBirth = (TextView) mView.findViewById(R.id.textView6);
        height = (TextView) mView.findViewById(R.id.textView8);
        weightShow = (TextView) mView.findViewById(R.id.textView9);

        weight = (EditText) mView.findViewById(R.id.editTextWeightProfile);

        profilePic = (ImageView) mView.findViewById(R.id.imageView4);

        /***
         * There are two listeners, one on the update button and one on the profilePic imageView.
         */
        updateButton.setOnClickListener(this);
        profilePic.setOnClickListener(this);


        //Initialises the firebase storage.
        mStorage = FirebaseStorage.getInstance().getReference();

        System.out.println(mStorage+"%%%%%%%%%%%%%%%%%%%%%%%%%%%%$$$$$$$$$$$$$$$$");

        //Gets the path for the user in order to store the picture by uid and specifies the name of the file to be accessed.
        StorageReference imageStorage = mStorage.child("ProfilePics").child(firebaseAuth.getCurrentUser().getUid()).child("fire");

        /***
         * In the onSuccessListener, we get the uri using the Picasso dependency and set it to the imageView profilePic.
         */
        imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + uri);
                Picasso.with(getContext()).load(uri).into(profilePic);
            }
        });

        //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + imageUri);
        mProgress = new ProgressDialog(getActivity());

        System.out.println(profileDownloadedPic + "££££££££££££££££££££££££££££££££££££££££££££££££££££££££");

        //profilePic.setImageURI(Uri.parse(imageUri));
    }

    /***
     * Here, we add the functionality of the clickable items.
     * First, when the profilePic is pressed, we start the camera and we show the progress dialog.
     * Secondly, if the updateButton is clicked, it gets the text from the editText box, converts it to a string
     * and sets the value of the userWeight element to the entered one.
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == profilePic){
            mProgress.setMessage("Uploading picture...");
            mProgress.show();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);


        }
        if (v == updateButton){
            weight.getText();
            String hoho = String.valueOf(weight.getText());
            if (hoho != null){
            databaseReference.child("userWeight").setValue(hoho);
            System.out.println(hoho);
            }


        }
    }

    /***
     * Here, we take the picture, we convert it to a bitmap, we set the bitmap to the profilePic just temporarily,
     * we specify the file name to be uploaded, we specify the path to the storage and we upload the pic to the storage.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] databaos = baos.toByteArray();

            profilePic.setImageBitmap(bitmap);
            String img = "fire";

            StorageReference imagesref = mStorage.child("ProfilePics").child(firebaseAuth.getCurrentUser().getUid()).child(img);

            UploadTask uploadTask = imagesref.putBytes(databaos);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgress.dismiss();
                }
            });

            imagesref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + uri);
                    profileDownloadedPic = uri.toString();
                }
            });

        }
    }
}
