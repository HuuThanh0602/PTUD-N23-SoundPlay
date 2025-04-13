package vn.edu.tlu.cse.hyn.soundplay.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tlu.cse.hyn.soundplay.R;
import vn.edu.tlu.cse.hyn.soundplay.data.model.PlayList;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    private final List<PlayList> recentList;

    public RecentAdapter(List<PlayList> recentList) {
        this.recentList = recentList;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent, parent, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        PlayList item = recentList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(item.getThumbnail())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(holder.imgCover);
    }

    @Override
    public int getItemCount() {
        return recentList != null ? recentList.size() : 0;
    }

    public static class RecentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
        }
    }
}
