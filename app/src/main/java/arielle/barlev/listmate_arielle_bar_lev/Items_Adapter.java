package arielle.barlev.listmate_arielle_bar_lev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class Items_Adapter extends RecyclerView.Adapter<Items_Adapter.ViewHolder> {
    private final List<Map.Entry<String, Boolean>> itemList;

    public Items_Adapter(List<Map.Entry<String, Boolean>> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, Boolean> item = itemList.get(position);
        holder.item_name.setText(item.getKey());
        holder.item_value.setText(String.valueOf(item.getValue()));

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Clicked: " + item.getKey(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item_name;
        public TextView item_value;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_value = itemView.findViewById(R.id.item_value);
        }
    }



}
