package arielle.barlev.listmate_arielle_bar_lev;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Present_Items extends Fragment {

    private TextView title;
    private RecyclerView recycler_view_items;

    private String Uid;
    private String list_id;

    private Items_Adapter adapter;
    private Firebase_Helper helper;
    private Utilities utilities;

    private List<Map.Entry<String, Boolean>> current_items_list;

    private Handler handler = new Handler(Looper.getMainLooper());
    private static final long UPDATE_INTERVAL = 5000;

    private Runnable data_update_runnable = new Runnable() {
        @Override
        public void run() {
            if (isAdded()) {
                fetch_list_items();
                handler.postDelayed(this, UPDATE_INTERVAL);
            } else {
                handler.removeCallbacks(this);
            }
        }
    };

    private void init(View view) {
        recycler_view_items = view.findViewById(R.id.recycler_view_items);
        recycler_view_items.setLayoutManager(new LinearLayoutManager(requireContext()));

        title = view.findViewById(R.id.title);

        helper = new Firebase_Helper(requireContext());
        utilities = new Utilities();

        Bundle args = getArguments();
        if (args != null) {
            Uid = args.getString("Uid");
            list_id = args.getString("list_id");
        } else {
            if (getContext() != null) {
                utilities.make_snackbar(getContext(), "Error: Uid or list_id arguments are missing.");
            }
        }

        current_items_list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_present_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        if (list_id != null) {
            helper.get_list_name(list_id).thenAccept(listName -> {
                if (getContext() != null) {
                    if (listName != null) {
                        title.setText(listName);
                        utilities.make_snackbar(getContext(), "Retrieved list name: " + listName);
                    } else {
                        utilities.make_snackbar(getContext(), "List name not found for ID: " + list_id);
                    }
                }
            }).exceptionally(error -> {
                if (getContext() != null) {
                    utilities.make_snackbar(getContext(), "Failed to retrieve list name: " + error.getMessage());
                }
                return null;
            });
        } else {
            utilities.make_snackbar(getContext(), "Error: List ID not provided to fragment.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (list_id != null && getContext() != null) {
            handler.postDelayed(data_update_runnable, 0);
        } else if (getContext() != null) {
            utilities.make_snackbar(getContext(), "Warning: Cannot fetch items, list ID is null or view not ready (onResume).");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(data_update_runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        title = null;
        recycler_view_items = null;
        adapter = null;
        handler.removeCallbacks(data_update_runnable);
    }

    private void fetch_list_items() {
        if (list_id == null) {
            if (getContext() != null) {
                utilities.make_snackbar(getContext(), "Warning: fetch_list_items called with null list_id.");
            }
            return;
        }

        helper.lists_items(list_id).thenAccept(itemsMap -> {
            if (getContext() != null) {
                current_items_list.clear();
                current_items_list.addAll(itemsMap.entrySet());
                requireActivity().runOnUiThread(() -> {
                    if (getView() != null && recycler_view_items != null) {
                        if (adapter == null) {
                            adapter = new Items_Adapter(current_items_list);
                            setup_item_click_listeners();
                            recycler_view_items.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        if (getContext() != null) {
                            utilities.make_snackbar(getContext(), "Warning: UI components not available for item list update.");
                        }
                    }
                });
            }
        }).exceptionally(e -> {
            if (getContext() != null) {
                utilities.make_snackbar(getContext(), "Failed to fetch items: " + e.getMessage());
            }
            return null;
        });
    }

    private void setup_item_click_listeners() {
        if (adapter == null) {
            if (getContext() != null) {
                utilities.make_snackbar(getContext(), "Error: Adapter is null, cannot set item click listeners.");
            }
            return;
        }

        adapter.setOnItemClickListener(item -> {
            Context context = requireContext();
            AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(context);
            alert_dialog_builder.setTitle("Update Item:");
            final EditText text_box = new EditText(context);
            text_box.setInputType(InputType.TYPE_CLASS_TEXT);
            text_box.setHint("Enter new item name");
            text_box.setWidth(100);
            text_box.setEms(10);
            text_box.setGravity(Gravity.CENTER);
            text_box.setPadding(10, 10, 10, 10);
            text_box.setBackgroundColor(Color.parseColor("#FFFFDD"));
            alert_dialog_builder.setView(text_box);
            alert_dialog_builder.setPositiveButton("Ok", (dialog, which) -> {
                String new_item = text_box.getText().toString().trim();
                if (!new_item.isEmpty()) {
                    helper.update_item(list_id, item, new_item);
                    adapter.updateItemName(item, new_item);
                } else {
                    Toast.makeText(context, "Please enter an item name", Toast.LENGTH_SHORT).show();
                }
            });
            alert_dialog_builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            alert_dialog_builder.create().show();
        });

        adapter.setOnTrashClickListener(item -> {
            helper.delete_item(list_id, item);
            adapter.deleteItem(item);
            utilities.make_snackbar(getContext(), "Item deleted.");
        });

        adapter.setOnCircleClickListener(item -> {
            helper.update_items_value(list_id, item);
            for (int i = 0; i < current_items_list.size(); i++) {
                if (current_items_list.get(i).getKey().equals(item)) {
                    current_items_list.set(i, new java.util.AbstractMap.SimpleEntry<>(item, !current_items_list.get(i).getValue()));
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
            if (getContext() != null) {
                utilities.make_snackbar(getContext(), "Item status updated.");
            }
        });
    }
}