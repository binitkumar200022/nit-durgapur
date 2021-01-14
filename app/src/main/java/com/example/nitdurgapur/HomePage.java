package com.example.nitdurgapur;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;

public class HomePage extends AppCompatActivity {

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    private DrawerLayout drawer;
    private TextView mNotSignIn;
    private SliderLayout sliderLayout;

    private static void closeDrawer(DrawerLayout drawer) {
        //Close drawer Layout
        //Check condition
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //When drawer is open
            //Close drawer
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        closeDrawer(drawer);
                        return true;
                    case R.id.nav_nitdgpchat:
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            startActivity(new Intent(HomePage.this, NitDgpChat.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "NITDGP Chat is not available for you.", Toast.LENGTH_SHORT).show();
                        }
                        closeDrawer(drawer);
                        return true;
                    default:
                        return true;
                }
            }
        });


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        View header = navigationView.getHeaderView(0);
        mNotSignIn = header.findViewById(R.id.not_sign_in);

        if (currentUser == null) {
            mNotSignIn.setText("Not Logged In, Sign In?");
            mNotSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomePage.this, Login.class);
                    intent.putExtra("NotSignIn", true);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            String user_id = mAuth.getCurrentUser().getUid();
            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("users");
            current_user_db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child(user_id).child("Name").getValue(String.class);
                    name = "Hello, " + name;
                    mNotSignIn.setText(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mNotSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(HomePage.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        //Set up Marquee TextView
        TextView mMarquee = findViewById(R.id.activity_home_marquee);
        mMarquee.setSelected(true);
        mMarquee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://nitdgp.ac.in/AllPDF/d_msg/To_the_Students.mp4"; // your URL here
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
                //You can show progress dialog here until it prepared to play
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //Now dismiss progress dialog, Media player will start playing
                        mp.start();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        // dismiss progress bar here. It will come here when MediaPlayer
                        //  is not able to play file. You can show error message to user
                        return false;
                    }
                });
                mMarquee.setClickable(false);
            }
        });

        sliderLayout = findViewById(R.id.activity_home_carousel);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.FILL);
        sliderLayout.setScrollTimeInSec(3);

        setSliderViews();
    }

    private void setSliderViews() {
        for (int i = 0; i <= 18; i++) {
            DefaultSliderView sliderView = new DefaultSliderView(HomePage.this);
            String str = "deafault";
            switch (i) {
                case 0:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/Covid_Awarness_-_Hoarding_-_English_-_With_PM2.jpg");
                    str = "Covid Awarness - Hoarding";
                    break;
                case 1:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/Covid_Awarness_-_Hoarding_-_English_-_With_PM1_page-0002.jpg");
                    str = "Covid Awarness - Hoarding";
                    break;
                case 2:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/BRICS_International_School.JPG");
                    str = "BRICS International School";
                    break;
                case 3:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/74_independent_day.jpg");
                    str = "74th Independence Day celebrated by the Institute";
                    break;
                case 4:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/ban2.png");
                    str = "Engineering Tomorrow";
                    break;
                case 5:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/ban1.png");
                    str = "Engineering Tomorrow";
                    break;
                case 6:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/Semi-automatic_ventilator.png");
                    str = "Semi-Automatic Ventilator";
                    break;
                case 7:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/language_lab.jpg");
                    str = "Inauguration of Language Laboratory at NIT Durgapur";
                    break;
                case 8:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/Alumni_homecoming.jpg");
                    str = "Grand Alumni Homecoming 2018";
                    break;
                case 9:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/fingureprint.jpg");
                    str = "Nanotechnology Enabled Smart Phone based Detection of Latent Finger Prints : Nature India";
                    break;
                case 10:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/IMG-20181205-WA0113.jpg");
                    str = "MOU with Hohai University China";
                    break;
                case 11:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/DSC_4761-min.JPG");
                    str = "15th Convocation 2019";
                    break;
                case 12:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/18_-mod.JPG");
                    str = "Hindi Shikshan Yojna 2018";
                    break;
                case 13:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/guestHouse.jpg");
                    str = "Guest House, NIT Durgapur";
                    break;
                case 14:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/BiotechBuilding-mod.jpg");
                    str = "Biotechnology Department, NIT Durgapur";
                    break;
                case 15:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/ACADPIC2-mod.JPG");
                    str = "New Academic Building, NIT Durgapur";
                    break;
                case 16:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/DSC_0182.JPG");
                    str = "Ek Bharat Shreshtha Bharat";
                    break;
                case 17:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/MissionVisionPagePic.jpg");
                    str = "Main Academic Building, NIT Durgapur";
                    break;
                case 18:
                    sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/rs.jpg");
                    str = "STROKES NIT Durgapur";
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderView.setDescription(str);
            String finalStr = str;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    Toast.makeText(HomePage.this, finalStr, Toast.LENGTH_SHORT).show();
                }
            });

            sliderLayout.addSliderView(sliderView);
        }
    }
}