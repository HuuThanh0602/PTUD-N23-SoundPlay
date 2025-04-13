package vn.edu.tlu.cse.hyn.soundplay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.tlu.cse.hyn.soundplay.R;
import vn.edu.tlu.cse.hyn.soundplay.data.model.Music;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    private List<Music> recentList;

    public RecentAdapter(List<Music> recentList) {
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
        Music item = recentList.get(position);
        //holder.imgCover.setImageResource(item.getImageRes());
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public static class RecentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
        }
    }
}
