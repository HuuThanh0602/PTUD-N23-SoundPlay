package vn.edu.tlu.cse.ntl.soundplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.tlu.cse.ntl.soundplay.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> itemList; // Dữ liệu của bạn có thể là danh sách tên, hình ảnh hoặc bất kỳ đối tượng nào
    private Context context;

    public CategoryAdapter(Context context, List<String> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carto, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String item = itemList.get(position);
        // Giả sử bạn muốn hiển thị tên của item
        holder.imgCarto.setImageResource(R.drawable.icon_kpop); // Thay đổi với dữ liệu thực tế
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCarto;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCarto = itemView.findViewById(R.id.imgCarto);
        }
    }
}

