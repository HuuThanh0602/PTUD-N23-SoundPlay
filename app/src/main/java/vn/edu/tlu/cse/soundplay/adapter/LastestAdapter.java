package vn.edu.tlu.cse.soundplay.adapter;

import android.content.Context;
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

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.repository.MusicRepository;
import vn.edu.tlu.cse.soundplay.util.MusicPlayerUtil;

public class LastestAdapter extends RecyclerView.Adapter<LastestAdapter.LastestViewHolder> {

    private List<Music> musicList;
    private Context context;
    private MusicRepository musicRepository;


    public LastestAdapter(List<Music> musicList, MusicRepository musicRepository) {
        this.musicList = musicList != null ? musicList : new ArrayList<>();
        this.musicRepository = musicRepository;
    }

    @NonNull
    @Override
    public LastestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_music_lastest, parent, false);
        return new LastestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LastestViewHolder holder, int position) {
        Music music = musicList.get(position);
        if (music == null) {
            Log.w("LastestAdapter", "Music item at position " + position + " is null");
            return;
        }

        // Hiển thị tiêu đề
        holder.txtTitle.setText(music.getTitle() != null ? music.getTitle() : "Không có tiêu đề");

        // Hiển thị hình ảnh
        Glide.with(context)
                .load(music.getThumbnail())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_avatar)
                .into(holder.imgCover);

        // Xử lý sự kiện nhấn item
        holder.itemView.setOnClickListener(v -> {
            MusicPlayerUtil.openMusicPlayer(context, music, musicList, position);
            musicRepository.saveRecentPlay(music);
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class LastestViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle;

        LastestViewHolder(View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}