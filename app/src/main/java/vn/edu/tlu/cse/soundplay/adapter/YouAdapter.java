package vn.edu.tlu.cse.soundplay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.model.Playlist;

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
        Playlist item = playLists.get(position);
        Glide.with(holder.itemView.getContext())
                .load(item.getThumbnail())
                .into(holder.imgCover);
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
