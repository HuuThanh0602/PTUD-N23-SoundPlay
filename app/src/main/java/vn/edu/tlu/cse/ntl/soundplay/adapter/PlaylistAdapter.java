package vn.edu.tlu.cse.ntl.soundplay.adapter;

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

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.ntl.soundplay.R;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.ntl.soundplay.ui.PlaylistDetailActivity;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlists = new ArrayList<>();

    public PlaylistAdapter() {}

    public PlaylistAdapter(List<Playlist> playlistList) {
        this.playlists = playlistList;
    }

    public void setData(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
        Log.d("PlaylistAdapter", "Số lượng playlist sau khi cập nhật: " + playlists.size());
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        if (playlist.getThumbnail() != null && !playlist.getThumbnail().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(playlist.getThumbnail())
                    .placeholder(R.drawable.icon_kpop)
                    .error(R.drawable.icon_kpop)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.icon_kpop);
            Log.d("PlaylistAdapter", "Thumbnail is empty for playlist: " + playlist.getTitle());
        }

        // ✅ Thêm OnClick vào ảnh playlist
        holder.itemView.setOnClickListener(v -> {
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
        return playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgPlayList);
        }
    }
}