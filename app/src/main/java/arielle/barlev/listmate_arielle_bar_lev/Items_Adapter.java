package arielle.barlev.listmate_arielle_bar_lev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class Items_Adapter extends RecyclerView.Adapter<Items_Adapter.ViewHolder> {
    private final List<Map.Entry<String, Boolean>> _items_list;
    private OnItemClickListener _listener;
    private OnTrashClickListener _trashClickListener;
    private OnCircleClickListener _circleClickListener;

    public interface OnItemClickListener {
        void onItemClick(String item_name);
    }

    public interface OnTrashClickListener {
        void onTrashClick(String item_name);
    }

    public interface OnCircleClickListener {
        void onCircleClick(String item_name);
    }


    public Items_Adapter(List<Map.Entry<String, Boolean>> items_list) {
        _items_list = items_list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        _listener = listener;
    }

    public void setOnTrashClickListener(OnTrashClickListener listener) {
        _trashClickListener = listener;
    }

    public void setOnCircleClickListener(OnCircleClickListener listener) {
        _circleClickListener = listener;
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
        Map.Entry<String, Boolean> itemEntry = _items_list.get(position);
        String item_name = itemEntry.getKey();
        Boolean item_value = itemEntry.getValue();

        holder.item_name.setText(item_name);
        holder.item_value.setText(String.valueOf(item_value));

        holder.itemView.setOnClickListener(v -> {
            if (_listener != null) {
                _listener.onItemClick(item_name);
            }
        });

        holder.icon_trash.setOnClickListener(v -> {
            _trashClickListener.onTrashClick(item_name);
        });

        // Set click listener for the circle icon
        holder.icon_circle.setOnClickListener(v -> {
            _circleClickListener.onCircleClick(item_name);
        });
    }

    @Override
    public int getItemCount() {
        return _items_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item_name;
        public TextView item_value;

        public ImageView icon_trash; // Declare ImageView for trash icon
        public ImageView icon_circle; // Declare ImageView for circle icon

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_value = itemView.findViewById(R.id.item_value);

            icon_trash = itemView.findViewById(R.id.icon_trash); // Initialize trash icon ImageView
            icon_circle = itemView.findViewById(R.id.icon_circle); // Initialize circle icon ImageView
        }
    }



}
