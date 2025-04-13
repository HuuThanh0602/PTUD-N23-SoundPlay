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
import vn.edu.tlu.cse.hyn.soundplay.data.model.Music;
import vn.edu.tlu.cse.hyn.soundplay.data.model.PlayList;

public class MixAdapter extends RecyclerView.Adapter<MixAdapter.MixViewHolder> {

    private List<PlayList> mixList;

    public MixAdapter(List<PlayList> mixList) {
        this.mixList = mixList;
    }

    @NonNull
    @Override
    public MixViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_square_mix, parent, false);
        return new MixViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MixViewHolder holder, int position) {
        PlayList item = mixList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(item.getThumbnail())
                .into(holder.imgCover);
    }


    @Override
    public int getItemCount() {
        return mixList.size();
    }

    public static class MixViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;

        public MixViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
        }
    }
}
