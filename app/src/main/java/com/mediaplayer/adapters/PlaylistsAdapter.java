package com.mediaplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mediaplayer.R;
import com.mediaplayer.beans.Playlist;
import com.mediaplayer.utilities.Utilities;

import java.util.ArrayList;

public class PlaylistsAdapter extends BaseAdapter {
    private ArrayList<Playlist> playlistInfoList;
    private static LayoutInflater inflater = null;

    public PlaylistsAdapter(Context context, ArrayList<Playlist> playlistInfoList) {
        this.playlistInfoList = playlistInfoList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = inflater.inflate(R.layout.item_playlist, null);
        Playlist playlist = playlistInfoList.get(position);
        String infoText = playlist.getPlaylistSize() + " songs,\t" + Utilities.milliSecondsToTimer(playlist.getPlaylistDuration());

        holder.playlistTitle = (TextView) rowView.findViewById(R.id.playlistTitle);
        holder.playlistInfo = (TextView) rowView.findViewById(R.id.playlistInfo);
        holder.moreOptions = (ImageButton) rowView.findViewById(R.id.morePlaylistOptionsButton);

        holder.playlistTitle.setText(playlistInfoList.get(position).getPlaylistName());
        holder.playlistInfo.setText(infoText);

        return rowView;
    }

    @Override
    public int getCount() {
        return playlistInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView playlistTitle;
        TextView playlistInfo;
        ImageButton moreOptions;
    }
}