package vn.edu.tlu.cse.soundplay.adapter;

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

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.model.Favourite;
import vn.edu.tlu.cse.soundplay.data.repository.FavouriteRepository;


public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

    private final Context context;
    private List<Favourite> favouriteList;
    private final FavouriteRepository favouriteViewModel;

    public FavouriteAdapter(Context context, List<Favourite> favouriteList, FavouriteRepository favouriteViewModel) {
        this.context = context;
        this.favouriteList = favouriteList;
        this.favouriteViewModel = favouriteViewModel;
    }

    public void updateData(List<Favourite> newList) {
        this.favouriteList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Favourite item = favouriteList.get(position);

        holder.txtTitle.setText(item.getTitle());
        holder.txtArtist.setText(item.getArtist());
// Load ảnh thumbnail từ URL
        Glide.with(context)
                .load(item.getThumbnail())// chính là link ảnh từ Zing
                .placeholder(R.drawable.ic_music) // ảnh hiển thị tạm khi đang tải
                .error(R.drawable.ic_music) // ảnh hiển thị nếu lỗi
                .into(holder.imgThumbnail);

        // Click trái tim để unlike (gọi toggle)
        holder.imgLike.setImageResource(R.drawable.ic_heart_filled); // đang là bài đã thích
        holder.imgLike.setOnClickListener(v -> {
            favouriteViewModel.toggleFavourite(item);
        });

        // Click vào item để mở nhạc (nếu muốn)
        holder.itemView.setOnClickListener(v -> {
            // TODO: mở PlayerActivity, truyền dữ liệu bài hát
        });
    }

    @Override
    public int getItemCount() {
        return favouriteList != null ? favouriteList.size() : 0;
    }

    static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail, imgLike;
        TextView txtTitle, txtArtist;

        public FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            imgLike = itemView.findViewById(R.id.imgLike);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtArtist = itemView.findViewById(R.id.txtArtist);
        }
    }
}
