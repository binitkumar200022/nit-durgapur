package com.example.nitdurgapur.ui;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nitdurgapur.R;
import com.google.firebase.auth.FirebaseAuth;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;

public class HomeFragment extends Fragment {

    SliderLayout sliderLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Set up Marquee TextView
        TextView mMarquee = (TextView) root.findViewById(R.id.fragment_home_marquee);
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
                        //Now dismiss progress dialog, Media palyer will start playing
                        mp.start();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        // dissmiss progress bar here. It will come here when MediaPlayer
                        //  is not able to play file. You can show error message to user
                        return false;
                    }
                });
                mMarquee.setClickable(false);
            }
        });

        sliderLayout = (SliderLayout) root.findViewById(R.id.fragment_home_carousel);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.FILL);
        sliderLayout.setScrollTimeInSec(3);

        setSliderViews();

        return root;
    }

    private void setSliderViews() {
        for (int i=0; i<=18; i++) {
            DefaultSliderView sliderView = new DefaultSliderView(getActivity());
            String str = "deafault";
            switch (i) {
                case 0 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/Covid_Awarness_-_Hoarding_-_English_-_With_PM2.jpg");
                         str = "Covid Awarness - Hoarding";
                         break;
                case 1 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/Covid_Awarness_-_Hoarding_-_English_-_With_PM1_page-0002.jpg");
                         str = "Covid Awarness - Hoarding";
                         break;
                case 2 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/BRICS_International_School.JPG");
                         str = "BRICS International School";
                         break;
                case 3 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/74_independent_day.jpg");
                         str = "74th Independence Day celebrated by the Institute";
                         break;
                case 4 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/ban2.png");
                         str = "Engineering Tomorrow";
                         break;
                case 5 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/ban1.png");
                         str = "Engineering Tomorrow";
                         break;
                case 6 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/Semi-automatic_ventilator.png");
                         str = "Semi-Automatic Ventilator";
                         break;
                case 7 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/language_lab.jpg");
                         str = "Inauguration of Language Laboratory at NIT Durgapur";
                         break;
                case 8 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/Alumni_homecoming.jpg");
                         str = "Grand Alumni Homecoming 2018";
                         break;
                case 9 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/fingureprint.jpg");
                         str = "Nanotechnology Enabled Smart Phone based Detection of Latent Finger Prints : Nature India";
                         break;
                case 10 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/IMG-20181205-WA0113.jpg");
                          str = "MOU with Hohai University China";
                          break;
                case 11 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/DSC_4761-min.JPG");
                          str = "15th Convocation 2019";
                          break;
                case 12 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/18_-mod.JPG");
                          str = "Hindi Shikshan Yojna 2018";
                          break;
                case 13 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/guestHouse.jpg");
                          str = "Guest House, NIT Durgapur";
                          break;
                case 14 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/BiotechBuilding-mod.jpg");
                          str = "Biotechnology Department, NIT Durgapur";
                          break;
                case 15 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2018/ACADPIC2-mod.JPG");
                          str = "New Academic Building, NIT Durgapur";
                          break;
                case 16 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/DSC_0182.JPG");
                          str = "Ek Bharat Shreshtha Bharat";
                          break;
                case 17 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2019/MissionVisionPagePic.jpg");
                          str = "Main Academic Building, NIT Durgapur";
                          break;
                case 18 : sliderView.setImageUrl("https://admin.nitdgp.ac.in/files/carousel/2020/rs.jpg");
                          str = "STROKES NIT Durgapur";
                          break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderView.setDescription(str);
            String finalStr = str;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    Toast.makeText(getActivity(), finalStr, Toast.LENGTH_SHORT).show();
                }
            });

            sliderLayout.addSliderView(sliderView);
        }
    }
}