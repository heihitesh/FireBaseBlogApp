package com.itshiteshverma.firebaseblogapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itshiteshverma.firebaseblogapp.Helper_Classes.Blog_GetterSetter;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    TextView userName, userEmailId;
    CircleImageView userImage;
    RecyclerView mBlogList;
    DatabaseReference dataBaseReference_Blog, databaseReference_User, databaseReference_Likes;
    Query mQueryCurrentUser;

    boolean process_like = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        dataBaseReference_Blog = FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseReference_User = FirebaseDatabase.getInstance().getReference().child("user");
        databaseReference_Likes = FirebaseDatabase.getInstance().getReference().child("likes");

        // mQueryCurrentUser = dataBaseReference_Blog.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
//pass it inside the firebase Adapter for a specif user

        dataBaseReference_Blog.keepSynced(true);
        databaseReference_User.keepSynced(true);
        databaseReference_Likes.keepSynced(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0); //added
        userName = (TextView) headerView.findViewById(R.id.tvUserName);
        userImage = (CircleImageView) headerView.findViewById(R.id.imageViewProfilePhoto);
        userEmailId = (TextView) headerView.findViewById(R.id.tvUserEmailId);

        userName.setText(user.getDisplayName());
        userEmailId.setText(user.getEmail());

        Picasso.with(this)
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.user_green)
                .error(R.drawable.user_red)
                .into(userImage);

        navigationView.setNavigationItemSelectedListener(this);


        mBlogList = (RecyclerView) findViewById(R.id.rvBlogList);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, Authentication.class));
                }
            }
        };

        logUserintoFabrics();

    }

    private void logUserintoFabrics() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("123456");
        Crashlytics.setUserEmail("itshiteshverma@gmail.com");
        Crashlytics.setUserName("hitesh");



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
        if (id == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_log_out) {
            mAuth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog_GetterSetter, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog_GetterSetter, BlogViewHolder>
                (
                        Blog_GetterSetter.class,
                        R.layout.blog_card_layout,
                        BlogViewHolder.class,
                        dataBaseReference_Blog


                ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog_GetterSetter model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setName(model.getName());
                viewHolder.setLikeBtn(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MainActivity.this, "You Clicked a view" + post_key, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, SingleBlogView.class);
                        i.putExtra("post_id", post_key);
                        startActivity(i);
                    }
                });

                viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        process_like = true;

                        databaseReference_Likes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (process_like) {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        //means that the post is already liked by the user now unlike it
                                        databaseReference_Likes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        process_like = false;
                                    } else {
                                        //the user has not like the post ..and now its registering the post
                                        databaseReference_Likes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("True");
                                        process_like = false;
                                    }
                                } else {
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView title;
        TextView desc;
        TextView userName;
        ImageView imageView;
        ImageButton likeBtn;
        DatabaseReference mDataBaseref;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            title = (TextView) mView.findViewById(R.id.tvPost);
            desc = (TextView) mView.findViewById(R.id.tvDescription);
            imageView = (ImageView) mView.findViewById(R.id.imageViewBlogPhoto);
            userName = (TextView) mView.findViewById(R.id.tvNameOftheUserPost);
            likeBtn = (ImageButton) mView.findViewById(R.id.imageButtonLikeBtn);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mView.getContext(), "Title Clicked", Toast.LENGTH_SHORT).show();
                }
            });

            mDataBaseref = FirebaseDatabase.getInstance().getReference().child("likes");
            mAuth = FirebaseAuth.getInstance();


            mDataBaseref.keepSynced(true);

        }


        public void setLikeBtn(final String post_key) {
            mDataBaseref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                        //user has like the post
                        likeBtn.setImageResource(R.drawable.thumbs_up_red);
                    } else {
                        //user has not likes the button
                        likeBtn.setImageResource(R.drawable.thumbs_up_gray);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setName(String name) {
            userName.setText(name);
        }

        public void setTitle(String Title) {
            title.setText(Title);
        }

        public void setDesc(String Desc) {
            desc.setText(Desc);
        }

        public void setImage(Context applicationContext, String image) {
            // Picasso.with(applicationContext).load(image).into(imageView);
            Picasso.with(applicationContext)
                    .load(image)
                    .error(R.drawable.add_photo)
                    .placeholder(R.drawable.loading_animation)
                    .into(imageView);
        }
    }
}
