package vn.edu.tlu.cse.hyn.soundplay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tlu.cse.hyn.soundplay.R;
import vn.edu.tlu.cse.hyn.soundplay.data.model.Music;
import vn.edu.tlu.cse.hyn.soundplay.data.model.PlayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<PlayList> list;

    public MusicAdapter(List<PlayList> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        PlayList item = list.get(position);
        holder.txtTitle.setText(item.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(item.getThumbnail()) // Load ảnh từ URL
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .into(holder.imgCover);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
