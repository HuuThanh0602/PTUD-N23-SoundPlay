package vn.edu.tlu.cse.ntl.soundplay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.ntl.soundplay.R;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlistList = new ArrayList<>();

    public PlaylistAdapter() {
        this.playlistList = new ArrayList<>();
    }

    public PlaylistAdapter(List<Playlist> playlistList) {
        this.playlistList = playlistList;
    }

    public void setData(List<Playlist> newList) {
        playlistList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlistList.get(position);
        holder.tvTitle.setText(playlist.title);
        holder.tvDesc.setText(playlist.description);
        holder.image.setImageResource(playlist.imageResId);
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView tvTitle, tvDesc;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageplaylist);
            tvTitle = itemView.findViewById(R.id.tvplaylistTitle);
            tvDesc = itemView.findViewById(R.id.tvplaylist);
        }
    }
}
