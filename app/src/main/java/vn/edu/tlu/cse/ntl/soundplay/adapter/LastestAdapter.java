package vn.edu.tlu.cse.ntl.soundplay.adapter;

import android.content.Context;
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

public class LastestAdapter extends RecyclerView.Adapter<LastestAdapter.LastestViewHolder> {

    private List<Music> musicList;
    private Context context;

    public LastestAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public LastestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext(); // để dùng Glide
        View view = LayoutInflater.from(context).inflate(R.layout.item_music_lastest, parent, false);
        return new LastestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LastestViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.txtTitle.setText(music.getTitle());

        // Load ảnh bằng Glide
        Glide.with(context)
                .load(music.getThumbnail())
                .placeholder(R.drawable.ic_music) // Ảnh loading
                .error(R.drawable.ic_avatar)     // Ảnh lỗi
                .into(holder.imgCover);
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    public static class LastestViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle;

        public LastestViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
