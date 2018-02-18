package com.av.musix.musix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by Ankit on 13-01-2018.
 */

public class MusicPlayer extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, SensorEventListener {
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private ImageView thumbnail;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private LinearLayout head;
    private LinearLayout foot;
    private ImageButton search;
    private EditText key;
    public static Window window;
    public boolean b = false;
    // Media Player
    private MediaPlayer mp;
    private MediaPlayer mp2;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    ;
    private SongsManager songManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private RelativeLayout player_bkg;
    SensorManager SM;
    Sensor mySensor;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> originalList = new ArrayList<HashMap<String, String>>();
    headset h;
    IntentFilter f;
    AudioManager am;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_player);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        // All player buttons
        head = findViewById(R.id.header);
        foot = findViewById(R.id.footer);
        player_bkg = findViewById(R.id.player_background);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnfrw);
        btnBackward = (ImageButton) findViewById(R.id.btnbck);
        btnNext = (ImageButton) findViewById(R.id.btnnext);
        btnPrevious = (ImageButton) findViewById(R.id.btnprev);
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        songProgressBar = (SeekBar) findViewById(R.id.progress_bar);
        songTitleLabel = (TextView) findViewById(R.id.titlehere);
        songCurrentDurationLabel = (TextView) findViewById(R.id.timecovered);
        songTotalDurationLabel = (TextView) findViewById(R.id.timeleft);
        thumbnail = findViewById(R.id.thumbnail);


        h = new headset();
        f = new IntentFilter();
        f.addAction(Intent.ACTION_HEADSET_PLUG);

        // Mediaplayer
        mp = new MediaPlayer();
        mp2 = new MediaPlayer();
        songManager = new SongsManager();
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important

        // Getting all songs list

        songsList = songManager.getPlayList();
        if (songsList.size() == 0) {
            Log.i("tag--------------", "empty");
        }
        // By default play first song
        playSong(0);

        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        registerReceiver(h, f);
        am.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_RIGHT);
        am.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_LEFT,0);
//        am.adjustVolume( AudioManager.ADJUST_LOWER,1);

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp2.pause();
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    if (mp != null) {
                        mp.start();
                        mp2.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });

        /**
         * Forward button click event
         * Forwards song specified seconds
         * */
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });

        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition - seekBackwardTime >= 0) {
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                    mp2.seekTo(currentPosition - seekBackwardTime);
                } else {
                    // backward to starting position
                    mp.seekTo(0);
                    mp2.seekTo(0);

                }

            }
        });

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if (currentSongIndex < (songsList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (currentSongIndex > 0) {
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                } else {
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });

        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });

        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
         * */
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });

        /**
         * Button Click event for Play list click event
         * Launches list activity which displays list of songs
         * */
        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });
    }

    /**
     * Receiving song index from playlist view
     * and play the song
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }

    /**
     * Function to play a song
     *
     * @param songIndex - index of song
     */
    public void playSong(int songIndex) {
mp.setVolume(1.0f,0.000000001f);
mp2.setVolume(0.00000000001f,1.0f);
//       am.setStreamVolume(AudioManager.STREAM_MUSIC,10,1);
//        mp.setVolume(am.getStreamMaxVolume(STREAM_MUSIC)-0,am.getStreamMaxVolume(STREAM_MUSIC)-am.getStreamMaxVolume(STREAM_MUSIC));
//        _leftVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
//        am.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_RIGHT);
//        Toast.makeText(this, "Here it comes", Toast.LENGTH_SHORT).show();
        // Play song
        try {
            mp.reset();
            mp2.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp2.setDataSource(songsList.get((songIndex+1)%songsList.size()).get("songPath"));
            mp2.prepare();
            mp.prepare();
            mp2.start();
            mp.start();
            // Displaying Song title
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);


            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(songsList.get(songIndex).get("songPath"));

            byte[] data = mmr.getEmbeddedPicture();
            //coverart is an Imageview object

            // convert the byte array to a bitmap
            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                thumbnail.setImageBitmap(bitmap); //associated cover art in bitmap
                thumbnail.setAdjustViewBounds(true);
//                thumbnail.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
                int c = getDominantColor(bitmap);
                player_bkg.setBackgroundColor(c + 10);
                head.setBackgroundColor(c + 10);
                foot.setBackgroundColor(c + 10);
                getWindow().setStatusBarColor(c + 10);
            } else {
                thumbnail.setImageResource(R.drawable.adele); //any default cover resourse folder
                thumbnail.setAdjustViewBounds(true);
                player_bkg.setBackgroundColor(Color.rgb(163, 151, 151));
                head.setBackgroundColor(Color.rgb(145, 133, 133));
                foot.setBackgroundColor(Color.rgb(163, 151, 151));
                getWindow().setStatusBarColor(Color.rgb(163, 151, 151));
//                thumbnail.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
            }


            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);
mp2.seekTo(currentPosition);
        // update timer progress again
        updateProgressBar();
    }

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (songsList.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        mp2.release();
    }

    public static int getDominantColor(Bitmap bitmap) {
        if (bitmap == null) {
            return Color.TRANSPARENT;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];
        //Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int color;
        int r = 0;
        int g = 0;
        int b = 0;
        int a;
        int count = 0;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];
            a = Color.alpha(color);
            if (a > 0) {
                r += Color.red(color);
                g += Color.green(color);
                b += Color.blue(color);
                count++;
            }
        }
        r /= count;
        g /= count;
        b /= count;
        r = (r << 16) & 0x00FF0000;
        g = (g << 8) & 0x0000FF00;
        b = b & 0x000000FF;
        color = 0xFF000000 | r | g | b;
        return color;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        long now = System.currentTimeMillis();
        if ((now - lastUpdate) > SHAKE_TIMEOUT) {
            shakes = 0;
        }
        if ((now - lastUpdate) > TIME_THRESHOLD) {
            long diff = now - lastUpdate;
            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((++shakes >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    shakes = 0;
                    Toast.makeText(this, "PLAYING NEXT SONG", Toast.LENGTH_SHORT).show();
                    AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                    if (mAudioManager.isMusicActive()) {
//                        Intent i = new Intent(SERVICECMD);
//                        i.putExtra(CMDNAME , CMDNEXT );
//                        this.sendBroadcast(i);
                        if (currentSongIndex < (songsList.size() - 1)) {
                            playSong(currentSongIndex + 1);
                            currentSongIndex = currentSongIndex + 1;
                        } else {
                            // play first song
                            playSong(0);
                            currentSongIndex = 0;
                        }
                        Log.i("HERE", "COME");
                    }
                }
                mLastForce = now;
            }
            lastUpdate = now;
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    private float last_x;
    private float last_y;
    private float last_z;
    private long mLastShake;
    private long mLastForce;

    public static int shakes = 0;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int TIME_THRESHOLD = 130;
    private static final int SHAKE_COUNT = 3;
    private static final int SHAKE_DURATION = 500;
    private static final int FORCE_THRESHOLD = 500;
    public static long lastUpdate;

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    protected void onResume() {
        super.onResume();


//            playSong(currentSongIndex);
    }

    @Override
    protected void onPause() {
        //we will  need to unregister otherwise it will not be able to set color since app is not active while on pause
        super.onPause();
//        unregisterReceiver(h);
    }


    public class headset extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (b) {
//                Toast.makeText(context, "PAUSED", Toast.LENGTH_SHORT).show();
            } else{
                b = true;
            return;
            }
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
            if (intent == null)
                return;
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
//                fl.setBackgroundColor(Color.BLUE);
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
mp2.pause();

                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    if (mp != null) {
                        mp.start();
                        mp2.start();
                        Toast.makeText(context, "RESUME", Toast.LENGTH_SHORT).show();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }
            }
        }

    }
}