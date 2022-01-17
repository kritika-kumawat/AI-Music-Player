package com.example.aimusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView pausePlayBtn, nextBtn, previousBtn;
    private TextView songNameTxt;

    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;
    private String mode = "ON";

    static MediaPlayer myMediaPlayer;
    private int pos;
    private ArrayList<File> mySong;
    private String mSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoiceCommandPermission();

        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        imageView = findViewById(R.id.logo);


        lowerRelativeLayout = findViewById(R.id.lower);
       voiceEnabledBtn = findViewById(R.id.voice_enabled_btn);
        songNameTxt = findViewById(R.id.songName);



        RelativeLayout parentRelativeLayout = findViewById(R.id.parentRelativeLayout);
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        validateReceiveValueAndStartPlaying();
        imageView.setBackgroundResource(R.drawable.logo);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String>matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matchesFound != null)
                {
                    if(mode.equals("ON"))
                    {
                        keeper = matchesFound.get(0);
                        if(keeper.equals("stop song"))
                        {
                            playPauseSong();
                            Toast.makeText(MainActivity.this, " Song Paused ",Toast.LENGTH_LONG ).show();
                        }
                        else if(keeper.equals("play song"))
                        {
                            playPauseSong();
                            Toast.makeText(MainActivity.this, " Song Playing ",Toast.LENGTH_LONG ).show();
                        }
                        else if(keeper.equals("next song"))
                        {
                            playNextSong();
                            Toast.makeText(MainActivity.this, " Next Song Playing ",Toast.LENGTH_LONG ).show();
                        }
                        else if(keeper.equals("previous song"))
                        {
                            playPreviousSong();
                            Toast.makeText(MainActivity.this, " Previous Song Playing ",Toast.LENGTH_LONG ).show();
                        }
                    }


                }
            }


            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });



        parentRelativeLayout.setOnTouchListener((v, event) -> {

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    speechRecognizer.startListening(speechRecognizerIntent);
                    keeper = "";
                    break;
                case MotionEvent.ACTION_UP:
                    speechRecognizer.stopListening();
                    break;
            }
            return false;
        });


        voiceEnabledBtn.setOnClickListener(v -> {
            if(mode.equals("ON"))
            {
               mode = "OFF";
               voiceEnabledBtn.setText("Voice Enabled Mode - OFF");
               lowerRelativeLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                mode = "ON";
                voiceEnabledBtn.setText("Voice Enabled Mode - ON");
                lowerRelativeLayout.setVisibility(View.GONE);
            }
        });

        //pausePlayBtn.setOnClickListener(v -> playPauseSong());
        pausePlayBtn.setOnClickListener(v -> {
            if(myMediaPlayer.getCurrentPosition()>0)
            {
                playPauseSong();
            }
        });


        previousBtn.setOnClickListener(v -> {
            if(myMediaPlayer.getCurrentPosition()>0)
            {
                playPreviousSong();
            }
        });

        nextBtn.setOnClickListener(v -> {
            if(myMediaPlayer.getCurrentPosition()>0)
            {
                playNextSong();
            }
        });

    }


    private void validateReceiveValueAndStartPlaying()
    {
        if(myMediaPlayer!= null)
        {
            myMediaPlayer.stop();
            myMediaPlayer.reset();
            myMediaPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySong = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName = mySong.get(pos).getName();
        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        pos = bundle.getInt("position",0);
        Uri uri = Uri.parse(mySong.get(pos).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);
        myMediaPlayer.start();
    }




    private void checkVoiceCommandPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }


    private void playPauseSong()
    {
        imageView.setBackgroundResource(R.drawable.four);
        if(myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();
        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.five);
        }
    }

    private void playNextSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        pos = ((pos+1)%mySong.size());

        Uri uri = Uri.parse(mySong.get(pos).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        mSongName = mySong.get(pos).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.three);

        if(myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }

    }


    private void playPreviousSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        pos = ((pos-1)<0 ? (mySong.size()-1) : (pos-1));

        Uri uri = Uri.parse(mySong.get(pos).toString());
        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        mSongName = mySong.get(pos).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.two);

        if(myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }
    }



}