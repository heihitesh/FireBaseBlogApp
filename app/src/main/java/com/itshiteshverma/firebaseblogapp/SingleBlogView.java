package com.itshiteshverma.firebaseblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleBlogView extends AppCompatActivity {
    TextView desc, title;
    ImageView photo;
    Button bRemovePost;

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private String postId = null;

    ProgressDialog mainProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog_view);

        postId = getIntent().getExtras().getString("post_id");

        desc = (TextView) findViewById(R.id.tvDescription_Blog_Single);
        title = (TextView) findViewById(R.id.tvTitle_Blog_single);
        photo = (ImageView) findViewById(R.id.imageViewImageSelect_Blog_Single);
        bRemovePost = (Button) findViewById(R.id.bRemovePost);
        mainProgress = new ProgressDialog(this);
        mainProgress.setIndeterminate(true);
        mainProgress.setMessage("Loading");
        mainProgress.show();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Blog");
        mAuth = FirebaseAuth.getInstance();

        mDatabaseRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String titleString = (String) dataSnapshot.child("title").getValue();
                String desString = (String) dataSnapshot.child("desc").getValue();
                String image = (String) dataSnapshot.child("image").getValue();
                String uID = (String) dataSnapshot.child("UID").getValue();
                title.setText(titleString);
                desc.setText(desString);
                Picasso.with(SingleBlogView.this)
                        .load(image)
                        .error(R.drawable.add_photo)
                        .placeholder(R.drawable.loading_animation)
                        .into(photo);

                if (mAuth.getCurrentUser().getUid().equals(uID)) {
                    bRemovePost.setVisibility(View.VISIBLE);
                }

                mainProgress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mainProgress.dismiss();
            }
        });

        bRemovePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog pd = new ProgressDialog(SingleBlogView.this);
                pd.setIndeterminate(true);
                pd.setMessage("Removing");
                pd.show();
                mDatabaseRef.child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Intent i = new Intent(SingleBlogView.this, MainActivity.class);
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(SingleBlogView.this, "Error!!!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
