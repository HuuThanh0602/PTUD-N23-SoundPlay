package vn.edu.tlu.cse.soundplay.adapter;

import android.content.Context;
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

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.ui.PlaySongActivity;

public class LastestAdapter extends RecyclerView.Adapter<LastestAdapter.LastestViewHolder> {

    private List<Music> musicList;
    private Context context;

    public LastestAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }

    public void setData(List<Music> newList) {
        this.musicList = newList;
        notifyDataSetChanged();
        Log.d("LastestAdapter", "Updated music list with size: " + (newList != null ? newList.size() : 0));
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

        holder.txtTitle.setText(music.getTitle() != null ? music.getTitle() : "Không có tiêu đề");
        Glide.with(context)
                .load(music.getThumbnail())
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_avatar)
                .into(holder.imgCover);

        holder.itemView.setOnClickListener(v -> {
            String url = music.getUrl();
            if (url == null || url.isEmpty()) {
                Log.e("LastestAdapter", "Invalid URL for song: " + music.getTitle());
                return;
            }

            Intent intent = new Intent(context, PlaySongActivity.class);
            intent.putExtra("MUSIC_ID", music.getId());
            intent.putExtra("MUSIC_TITLE", music.getTitle());
            intent.putExtra("MUSIC_THUMBNAIL", music.getThumbnail());
            intent.putExtra("MUSIC_URL", url);

            // Gửi danh sách bài hát
            ArrayList<String> songUrls = new ArrayList<>();
            ArrayList<String> songTitles = new ArrayList<>();
            ArrayList<String> songThumbnails = new ArrayList<>();
            for (Music m : musicList) {
                songUrls.add(m.getUrl());
                songTitles.add(m.getTitle());
                songThumbnails.add(m.getThumbnail());
            }
            intent.putStringArrayListExtra("songUrls", songUrls);
            intent.putStringArrayListExtra("songTitles", songTitles);
            intent.putStringArrayListExtra("songThumbnails", songThumbnails);
            intent.putExtra("currentIndex", position);

            Log.d("LastestAdapter", "Starting PlaySongActivity with URL: " + url + ", Title: " + music.getTitle());
            context.startActivity(intent);
        });
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