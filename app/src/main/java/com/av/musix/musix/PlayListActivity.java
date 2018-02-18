package com.av.musix.musix;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ankit on 13-01-2018.
 */

public class PlayListActivity extends ListActivity {

    public ArrayList<HashMap<String, String>> songsList = new ArrayList();
    public ArrayList<HashMap<String, String>> originalList = new ArrayList();

    public PlayListActivity() {
    }

    private ImageButton search;
    private EditText key;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.playlist);

        key = findViewById(R.id.key);
        search = findViewById(R.id.search);


        ArrayList<HashMap<String, String>> songsListData = new ArrayList();
        SongsManager plm = new SongsManager();
        this.songsList = plm.getPlayList();
        for (int i = 0; i < this.songsList.size(); ++i) {
            HashMap<String, String> song = (HashMap) this.songsList.get(i);
            songsListData.add(song);
        }

        ListAdapter adapter = new SimpleAdapter(this,
                songsListData,
                R.layout.playlist_item,
                new String[]{"songTitle"},
                new int[]{R.id.songTitle});
        this.setListAdapter(adapter);
        ListView lv = this.getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(PlayListActivity.this.getApplicationContext(), MusicPlayer.class);
                in.putExtra("songIndex", position);
                PlayListActivity.this.setResult(100, in);
                PlayListActivity.this.finish();
            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = key.getText() + "";
                if (s.length() == 0) {
                    Toast.makeText(PlayListActivity.this, "Enter some text", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<HashMap<String, String>> tempList = new ArrayList();
                    SongsManager plm = new SongsManager();
                    ArrayList<HashMap<String, String>> songlist = plm.getPlayList();
                    for (int i = 0; i < songlist.size(); ++i) {
                        boolean contain = false;
                        HashMap<String, String> song = (HashMap) songlist.get(i);
                        ArrayList<String> al = new ArrayList<>(song.keySet());
                        for (String str : al) {
                            if (str.contains(s)||song.get(str).contains(s)) {
                                contain = true;
                                Log.i("song found---",""+song.get(str));
                            }
                        }
                        if (contain)
                            tempList.add(song);
                    }
                    if (tempList.size() == 0) {
                        Toast.makeText(PlayListActivity.this, "No song found with this keyword", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayListActivity.this, ""+tempList.size()+" song found", Toast.LENGTH_SHORT).show();
                       set(tempList);
                    }
                }
            }
        });
    }

    public void set(ArrayList<HashMap<String, String>> sl) {
        songsList = sl;
        ListAdapter adapter = new SimpleAdapter(this,
                sl,
                R.layout.playlist_item,
                new String[]{"songTitle"},
                new int[]{R.id.songTitle});
        this.setListAdapter(adapter);
        ListView lv = this.getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(PlayListActivity.this.getApplicationContext(), MusicPlayer.class);
                in.putExtra("songIndex", position);
                PlayListActivity.this.setResult(100, in);
                PlayListActivity.this.finish();
            }
        });
    }
}
