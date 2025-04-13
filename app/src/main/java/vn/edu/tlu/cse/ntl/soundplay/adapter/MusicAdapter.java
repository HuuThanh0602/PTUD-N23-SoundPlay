package vn.edu.tlu.cse.ntl.soundplay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tlu.cse.ntl.soundplay.R;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Music;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<Music> musicList;

    public MusicAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }

    public void setData(List<Music> newList) {
        this.musicList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.tvTitle.setText(music.getTitle());
        holder.tvArticle.setText(music.getArtist());
        Glide.with(holder.itemView.getContext())
                .load(music.getThumbnail())
                .into(holder.ivThumbnail);
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