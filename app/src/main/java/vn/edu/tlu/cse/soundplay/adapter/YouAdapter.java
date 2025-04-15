package vn.edu.tlu.cse.soundplay.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.soundplay.ui.PlaylistDetailActivity;

public class YouAdapter extends RecyclerView.Adapter<YouAdapter.YouViewHolder> {

    private List<Playlist> playLists;

    public YouAdapter(List<Playlist> playLists) {
        this.playLists = playLists;
    }


    @NonNull
    @Override
    public YouViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foryou, parent, false);
        return new YouViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YouViewHolder holder, int position) {
        Playlist playlist = playLists.get(position);
        Glide.with(holder.itemView.getContext())
                .load(playlist.getThumbnail())
                .into(holder.imgCover);

        holder.imgCover.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PlaylistDetailActivity.class);

            // Truyền các thông tin cần thiết qua Intent
            intent.putExtra("playlistId", playlist.getId());  // Truyền ID
            intent.putExtra("playlistName", playlist.getTitle());  // Tên Playlist
            intent.putExtra("playlistThumbnail", playlist.getThumbnail());  // Ảnh Playlist

            // Bắt đầu PlaylistDetailActivity
            holder.itemView.getContext().startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return playLists != null ? playLists.size() : 0;
    }

    public static class YouViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;

        public YouViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
        }
    }
}
