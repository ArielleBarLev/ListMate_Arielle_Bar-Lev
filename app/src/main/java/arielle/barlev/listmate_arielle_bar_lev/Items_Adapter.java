package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Items_Adapter extends RecyclerView.Adapter<Items_Adapter.ViewHolder> {

    private List<Map.Entry<String, Boolean>> _itemsList; // Use a List of Map.Entry
    private OnItemClickListener _listener;
    private static final String TAG = "Items_Adapter";
    private Utilities _utilities; // Add Utilities instance
    private Context _context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public Items_Adapter(Map<String, Boolean> items, OnItemClickListener listener, Context context) {
        Log.d(TAG, "Items_Adapter constructor called");
        _itemsList = new ArrayList<>(items.entrySet()); // Convert Map to List of Entries
        _listener = listener;
        _utilities = new Utilities(); // Initialize Utilities
        _context = context;
        _utilities.make_snackbar(_context, "Items_Adapter constructor called. Items: " + items.toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);
        _utilities.make_snackbar(_context, "onBindViewHolder called for position: " + position);
        if (position < 0 || position >= _itemsList.size()) {
            Log.e(TAG, "onBindViewHolder: Invalid position: " + position);
            _utilities.make_snackbar(_context, "onBindViewHolder: Invalid position: " + position);
            return; // Exit if position is invalid
        }
        Map.Entry<String, Boolean> entry = _itemsList.get(position);
        String itemTitle = entry.getKey();
        Boolean itemValue = entry.getValue();

        holder.itemTitleTextView.setText(itemTitle);
        holder.itemValueTextView.setText(String.valueOf(itemValue)); // Convert boolean to String

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "ItemView clicked at position: " + position);
            _utilities.make_snackbar(_context, "ItemView clicked at position: " + position);
            if (_listener != null) {
                _listener.onItemClick(position);
            } else {
                Log.e(TAG, "_listener is null in onBindViewHolder");
                _utilities.make_snackbar(_context, "_listener is null in onBindViewHolder");
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = _itemsList.size();
        Log.d(TAG, "getItemCount: " + count);
        _utilities.make_snackbar(_context, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitleTextView;
        TextView itemValueTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitleTextView = itemView.findViewById(R.id.item_title);
            itemValueTextView = itemView.findViewById(R.id.item_value);
            Log.d(TAG, "ViewHolder created for item: " + itemView);
        }
    }
}