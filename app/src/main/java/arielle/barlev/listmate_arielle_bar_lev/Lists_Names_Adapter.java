package arielle.barlev.listmate_arielle_bar_lev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Lists_Names_Adapter extends RecyclerView.Adapter<Lists_Names_Adapter.ViewHolder> {

    private final List<String> _list_names;
    private OnItemClickListener _listener;
    private OnShareClickListener _shareClickListener;
    private OnCalendarClickListener _calendarClickListener;

    public interface OnItemClickListener {
        void onItemClick(String list_name);
    }

    public interface OnShareClickListener {
        void onShareClick(String list_name);
    }

    public interface OnCalendarClickListener {
        void onCalendarClick(String list_name);
    }

    public Lists_Names_Adapter(List<String> list_names) {
        _list_names = list_names;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        _listener = listener;
    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        _shareClickListener = listener;
    }

    public void setOnCalendarClickListener(OnCalendarClickListener listener) {
        _calendarClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String list_name = _list_names.get(position); // Get the list name at the current position

        holder.list_name.setText(list_name);

        holder.itemView.setOnClickListener(v -> {
            if (_listener != null) {
                _listener.onItemClick(list_name); // Pass the list name
            }
        });

        holder.icon_share.setOnClickListener(v -> {
            if (_shareClickListener != null) {
                _shareClickListener.onShareClick(list_name); // Pass the list name
            }
        });

        holder.icon_calendar.setOnClickListener(v -> {
            if (_calendarClickListener != null) {
                _calendarClickListener.onCalendarClick(list_name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _list_names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView list_name;
        public ImageView icon_share;
        public ImageView icon_calendar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.list_name);
            icon_share = itemView.findViewById(R.id.icon_share);
            icon_calendar = itemView.findViewById(R.id.icon_calendar);
        }
    }
}