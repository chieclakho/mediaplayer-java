package com.clk.musicplayerapp.adapter;


import static com.clk.musicplayerapp.view.fragment.MainFragment.LEVER_IDLE;
import static com.clk.musicplayerapp.view.fragment.MainFragment.LEVER_PLAY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clk.musicplayerapp.MediaManager;
import com.clk.musicplayerapp.R;
import com.clk.musicplayerapp.view.model.Song;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHodel> {
    private final List<Song> listSong;
    private final Context context;
    private final View.OnClickListener event;
    private int currentSong = -1;
    private String current;

    public SongAdapter(List<Song> listSong, Context context, View.OnClickListener event) {
        this.listSong = listSong;
        this.context = context;
        this.event = event;
    }

    @NonNull
    @Override
    public SongViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHodel(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull SongViewHodel holder, int position) {
        Song song = listSong.get(position);
        if (current != null) {
            currentSong = Integer.parseInt(current);
            current = null;
        }
        if (position == currentSong) {
            holder.tableRow.setBackgroundResource(R.color.purple_500);
            if (MediaManager.getInstance().getState() == 2) {
                holder.ivPlay.setImageLevel(LEVER_PLAY);
            }
        } else {
            holder.tableRow.setBackgroundResource(R.color.alpha);
            holder.ivPlay.setImageLevel(LEVER_IDLE);
        }
        holder.ivSong.setImageResource(R.drawable.ic_music);
        holder.tvIndex.setText(String.format("#%s", position + 1));
        holder.tvName.setText(song.title);
        holder.tvSinger.setText(song.artist);
        holder.tableRow.setTag(song);
        long durationInMillis = Long.parseLong(song.cover);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) - TimeUnit.MINUTES.toSeconds(minutes);
        holder.tvTime.setText(String.format("%s:%s", minutes, seconds));
    }

    @Override
    public int getItemCount() {
        return listSong.size();
    }

    public void upDateUi(int currentSong) {
        this.currentSong = currentSong;
        notifyItemRangeChanged(0, listSong.size());
    }
    public void setCurrent(String current) {
        this.current = current;
    }
    public class SongViewHodel extends RecyclerView.ViewHolder {
        TextView tvIndex, tvName, tvSinger, tvTime;
        TableRow tableRow;
        ImageView ivSong, ivPlay;
        public SongViewHodel(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvIndex = itemView.findViewById(R.id.tv_index);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSinger = itemView.findViewById(R.id.tv_singer);
            tableRow = itemView.findViewById(R.id.tb_song);
            ivSong = itemView.findViewById(R.id.iv_song);
            ivPlay = itemView.findViewById(R.id.iv_play_song_item);
            tableRow.setOnClickListener(v -> event.onClick(tableRow));
        }
    }
}
