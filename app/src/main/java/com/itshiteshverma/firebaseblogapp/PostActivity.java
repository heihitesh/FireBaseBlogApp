package com.itshiteshverma.firebaseblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private static final int REUEST_CODE = 1;
    ImageButton imageButton;
    EditText mTitle, mDescription;
    Uri uri = null;
    ProgressDialog mProgress;

    private StorageReference mStorageReference;
    private DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mProgress = new ProgressDialog(this);
        mTitle = (EditText) findViewById(R.id.etPost);
        mDescription = (EditText) findViewById(R.id.etDescription);
        imageButton = (ImageButton) findViewById(R.id.imageViewImageSelect);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, REUEST_CODE);
            }
        });


    }


    @Override // this will be fired when the acc orientation is changed [check manifest activity]
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Toast.makeText(this, "Sreeen Change", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            imageButton.setImageURI(uri);
        }
    }

    public void OnPostClick(View v) {
        // when the post button is clicked
        final String title = mTitle.getText().toString().trim();
        final String des = mDescription.getText().toString().trim();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(des) && uri != null) {
            mProgress.setMessage("Posting Blog");
            mProgress.setIndeterminate(true);
            mProgress.show();
            StorageReference filePath = mStorageReference.child("Blog_Images").child(uri.getLastPathSegment());
            //first child is the name of the folder
            // Second child is the name of the image .. it must be unique.

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //  Toast.makeText(PostActivity.this, "URL: "+downloadUrl, Toast.LENGTH_SHORT).show();
//                    DatabaseReference newPost = mDataBase.push(); //creates a unique no
//                    newPost.child("title").setValue(title);
//                    newPost.child("desc").setValue(des);
//                    newPost.child("image").setValue(downloadUrl.toString());
//                    newPost.child("UID").setValue(user.getUid());
//                    newPost.child("name").setValue(user.getDisplayName());

                    String key = mDataBase.push().getKey(); // this will create a new unique key
                    Map<String, Object> value = new HashMap<>();
                    value.put("title", title);
                    value.put("desc", des);
                    value.put("image", downloadUrl.toString());
                    value.put("name", user.getDisplayName());
                    value.put("UID", user.getUid());

                    SimpleDateFormat s = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                    String format = s.format(new Date());

                    value.put("timestamp", format);


                    mDataBase.child(key).setValue(value);


                    mProgress.dismiss();
                    Toast.makeText(PostActivity.this, "Blog Posted Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "Please Fill all the Info", Toast.LENGTH_SHORT).show();

        }
    }

}
