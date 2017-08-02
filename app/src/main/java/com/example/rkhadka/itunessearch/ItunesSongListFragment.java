package com.example.rkhadka.itunessearch;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ItunesSongListFragment extends Fragment {
    private ListView mListView;
    private SongAdapter mSongAdapter;
    private NetworkImageView imageView;
    private TextView trackName;
    private TextView artistName;
    private TextView albumName;
    private TextView mTextView;

    private MediaPlayer mMediaPlayer;
    private String mCurrentlyPlayingUrl;

    private Button mPlayButton;
    private LinearLayout mLinearLayout;
    private List<Songs> mSongs;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View v = inflater.inflate(R.layout.itunes_listfragment, container, false );

        mLinearLayout = (LinearLayout) v.findViewById(R.id.swiperefresh);
        mTextView = (TextView) v.findViewById(R.id.search_result) ;
        mTextView.setText("Searching for : " + ItunesSongSource.getQuery());
        mListView = (ListView) v.findViewById(R.id.list_view);
        mSongAdapter = new SongAdapter(getActivity());
        mListView.setAdapter(mSongAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Songs song = (Songs) parent.getAdapter().getItem(position);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "This track is good");
                shareIntent.putExtra(Intent.EXTRA_TEXT, song.getmTrackViewUrl());
                startActivity(Intent.createChooser(shareIntent, "Share link using"));

            }
        });

        // If there is content to display, show it, otherwise refresh content.
        if (mSongs != null) {
            mSongAdapter.setItems(mSongs);
        } else {
            refreshArticles();
        }
        return v;

    }
    private void refreshArticles() {
        ItunesSongSource.get(getContext()).getSongs(new ItunesSongSource.ItemListener() {
            @Override
            public void onSongResponse(List<Songs> songList) {
                mSongs = songList;
                // Stop the spinner and update the list view.
                mSongAdapter.setItems(songList);
            }
        });
    }
    private void clickedAudioURL(String url) {
        if (mMediaPlayer.isPlaying()) {
            if (mCurrentlyPlayingUrl.equals(url)) {
                mMediaPlayer.stop();
                mSongAdapter.notifyDataSetChanged();
                return;
            }
        }

        mCurrentlyPlayingUrl = url;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mSongAdapter.notifyDataSetChanged();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mSongAdapter.notifyDataSetChanged();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class SongAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<Songs> mDataSource;

        public SongAdapter(Context context){
            mContext = context;
            mDataSource = new ArrayList<>();
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<Songs> songsList){
            mDataSource.clear();
            mDataSource.addAll(songsList);
            mTextView.setText("Showing results for: " + ItunesSongSource.getQuery());

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDataSource.size();
        }

        @Override
        public Object getItem(int position){
            return mDataSource.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Songs song = mDataSource.get(position);
            View rowView = mInflater.inflate(R.layout.itunes_itemlist, parent, false);
            boolean isPlaying = mMediaPlayer.isPlaying() &&
                    mCurrentlyPlayingUrl.equals(song.getmURLString());
            mPlayButton = (Button) rowView.findViewById(R.id.play_button);

            if (isPlaying) {
                mPlayButton.setBackgroundResource(R.mipmap.btn_pause);  //Your playbutton image
            } else {
                mPlayButton.setBackgroundResource(R.mipmap.btn_play);  //Your pausebutton image
            }
            // Here, add code to set the play/pause button icon based on isPlaying
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedAudioURL(song.getmURLString());
                }
            });

            imageView = (NetworkImageView) rowView.findViewById(R.id.thumbnail);
            ImageLoader loader = ItunesSongSource.get(getContext()).getImageLoader();
            imageView.setImageUrl(song.getImageURLString(),loader);

            trackName = (TextView) rowView.findViewById(R.id.track_name);
            trackName.setText(song.getmTrackName());

            albumName = (TextView) rowView.findViewById(R.id.album_name);
            albumName.setText(song.getmAlbumName());

            artistName = (TextView) rowView.findViewById(R.id.artist_name);
            artistName.setText(song.getmArtistName());

            return rowView;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }


}
