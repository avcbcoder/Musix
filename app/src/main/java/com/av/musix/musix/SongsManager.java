package com.av.musix.musix;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ankit on 13-01-2018.
 */

public class SongsManager {
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    File home = new File("/storage/emulated/0/Songs");

    public static int count = 0;
    public SongsManager() {
    }

    public ArrayList<HashMap<String, String>> getPlayList() {
        songsList = new ArrayList<HashMap<String, String>>();
        fill(home);
        return songsList;
    }

    private void fill(File file) {
        if (file == null)
            return;
        if (file.isDirectory() && file.listFiles().length > 0) {
            for (File f : file.listFiles()) {
                fill(f);
            }
        } else {
            if (file.getName().length() > 4) {
                if (file.getName().endsWith("mp3") || file.getName().endsWith("MP3")) {
                    Log.i("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                    Log.i("songPath", file.getPath());
                    count++;
                    HashMap<String, String> song = new HashMap<String, String>();
                    song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                    song.put("songPath", file.getPath());

                    // Adding each song to SongList
                    songsList.add(song);

                }
            }
        }
    }

}
