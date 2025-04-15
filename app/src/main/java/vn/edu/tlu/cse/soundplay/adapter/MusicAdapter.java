package vn.edu.tlu.cse.soundplay.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.repository.MusicRepository;
import vn.edu.tlu.cse.soundplay.ui.PlaySongActivity;
import vn.edu.tlu.cse.soundplay.util.MusicPlayerUtil;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<Music> musicList;
    private Context context;
    private MusicRepository musicRepository;


    public MusicAdapter(List<Music> musicList , MusicRepository musicRepository) {
        this.musicList = musicList;
        this.musicRepository = musicRepository;
    }

    public void setData(List<Music> newList) {
        this.musicList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.tvTitle.setText(music.getTitle());
        holder.tvArticle.setText(music.getArtist());
        Glide.with(context)
                .load(music.getThumbnail())
                .into(holder.ivThumbnail);


        holder.itemView.setOnClickListener(v -> {
            MusicPlayerUtil.openMusicPlayer(context, music, musicList, position);
            musicRepository.saveRecentPlay(music);
        });

    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle,tvArticle;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.thumbnail);
            tvTitle = itemView.findViewById(R.id.tvmusicTitle);
            tvArticle = itemView.findViewById(R.id.tvArticle);
        }
    }
}