package arielle.barlev.listmate_arielle_bar_lev;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Lists_Names_Adapter extends RecyclerView.Adapter<Lists_Names_Adapter.ViewHolder> {

    private List<String> listNames;
    private OnItemClickListener listener;
    private static final String TAG = "Lists_Names_Adapter";

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public Lists_Names_Adapter(List<String> listNames, OnItemClickListener listener) {
        //Log.d(TAG, "Lists_Names_Adapter constructor called");
        this.listNames = listNames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);
        if (position < 0 || position >= listNames.size()) {
            Log.e(TAG, "onBindViewHolder: Invalid position: " + position);
            return; // Exit if position is invalid
        }
        String listName = listNames.get(position);
        holder.listItemText.setText(listName);
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "ItemView clicked at position: " + position);
            if (listener != null) {
                listener.onItemClick(position);
            } else {
                Log.e(TAG, "listener is null in onBindViewHolder");
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = listNames.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView listItemText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemText = itemView.findViewById(R.id.list_item_text);
            Log.d(TAG, "ViewHolder created for item: " + itemView);
        }
    }
}