package arielle.barlev.listmate_arielle_bar_lev;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Present_Lists extends AppCompatActivity {

    private Button add_list;

    private String Uid;

    private RecyclerView lists_layout;

    private List<String> lists_names;
    private List<String> lists_ids;

    private Firebase_Helper helper;
    private Lists_Names_Adapter adapter;
    private Utilities utilities;

    private Handler handler = new Handler();
    private AtomicBoolean is_fetching_data = new AtomicBoolean(false);
    private static final long UPDATE_INTERVAL = 5000;

    private Runnable data_update_runnable = new Runnable() {
        @Override
        public void run() {
            fetch_display_data();
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    private void init() {
        lists_layout = findViewById(R.id.lists_layout);
        add_list = findViewById(R.id.add_list);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");

        lists_names = new ArrayList<>();
        lists_ids = new ArrayList<>();

        helper = new Firebase_Helper(Present_Lists.this);
        utilities = new Utilities();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_lists);

        init();

        LinearLayoutManager layout_manager = new LinearLayoutManager(Present_Lists.this);
        lists_layout.setLayoutManager(layout_manager);

        adapter = new Lists_Names_Adapter(lists_names);
        lists_layout.setAdapter(adapter);

        adapter.setOnItemClickListener(new Lists_Names_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String list_name) {
                int position = lists_names.indexOf(list_name);
                if (position != -1 && position < lists_ids.size()) {
                    String list_id = lists_ids.get(position);
                    utilities.make_snackbar(Present_Lists.this, "Clicked List: " + list_name + " (ID: " + list_id + ")");
                    Intent intent = new Intent(Present_Lists.this, Present_Items.class);
                    intent.putExtra("Uid", Uid);
                    intent.putExtra("list_id", list_id);
                    startActivity(intent);
                } else {
                    utilities.make_snackbar(Present_Lists.this, "Error: Invalid list item clicked.");
                }
            }
        });

        adapter.setOnShareClickListener(new Lists_Names_Adapter.OnShareClickListener() {
            @Override
            public void onShareClick(String list_name) {
                int position = lists_names.indexOf(list_name);
                if (position != -1 && position < lists_ids.size()) {
                    String list_id = lists_ids.get(position);
                    utilities.make_snackbar(Present_Lists.this, "Share: " + list_id);

                    Context context = Present_Lists.this;
                    AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(context);
                    alert_dialog_builder.setTitle("Share list:");
                    final EditText text_box = new EditText(getApplicationContext());
                    text_box.setInputType(InputType.TYPE_CLASS_TEXT);
                    text_box.setHint("Enter user id");
                    text_box.setWidth(100);
                    text_box.setEms(10);
                    text_box.setGravity(Gravity.CENTER);
                    text_box.setPadding(10, 10, 10, 10);
                    text_box.setBackgroundColor(Color.parseColor("#FFFFDD"));
                    alert_dialog_builder.setView(text_box);
                    alert_dialog_builder.setPositiveButton("Ok", (dialog, which) -> {
                        String user_id = text_box.getText().toString().trim();
                        if (!user_id.isEmpty()) {
                            helper.share_list(list_id, user_id);
                        } else {
                            Toast.makeText(context, "Please enter an item name", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert_dialog_builder.create().show();

                } else {
                    utilities.make_snackbar(Present_Lists.this, "Error: Could not find list ID for sharing.");
                }
            }
        });

        add_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Present_Lists.this, Add_List.class);
                intent.putExtra("Uid", Uid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetch_display_data();
        handler.postDelayed(data_update_runnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(data_update_runnable);
    }

    private void display_data() {
        lists_names.clear();
        lists_ids.clear();

        helper.users_lists(Uid).thenAccept(retrievedListIds -> {
            if (retrievedListIds.isEmpty()) {
                utilities.make_snackbar(Present_Lists.this, "No lists found for this user.");
            } else {
                lists_ids.addAll(retrievedListIds);
                List<CompletableFuture<String>> nameFutures = new ArrayList<>();

                for (String id : retrievedListIds) {
                    nameFutures.add(helper.get_list_name(id));
                }

                CompletableFuture.allOf(nameFutures.toArray(new CompletableFuture[0]))
                        .thenAccept(v -> {
                            List<String> retrievedNames = new ArrayList<>();
                            for (CompletableFuture<String> nameFuture : nameFutures) {
                                try {
                                    String name = nameFuture.get();
                                    if (name != null) {
                                        retrievedNames.add(name);
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    utilities.make_snackbar(Present_Lists.this, "Error getting list name: " + e.getMessage());
                                }
                            }
                            lists_names.addAll(retrievedNames);
                            adapter.notifyDataSetChanged();
                        })
                        .exceptionally(e -> {
                            utilities.make_snackbar(Present_Lists.this, "Failed to fetch list names: " + e.getMessage());
                            return null;
                        });
            }
        }).exceptionally(e -> {
            utilities.make_snackbar(Present_Lists.this, "Failed to fetch list IDs: " + e.getMessage());
            return null;
        });
    }

    private void fetch_display_data() {
        if (is_fetching_data.compareAndSet(false, true)) {
            helper.users_lists(Uid).thenAccept(retrieved_list_ids -> {
                if (retrieved_list_ids.isEmpty()) {
                    runOnUiThread(() -> {
                        lists_names.clear();
                        lists_ids.clear();
                        adapter.notifyDataSetChanged();
                        utilities.make_snackbar(Present_Lists.this, "No lists found for this user.");
                        is_fetching_data.set(false);
                    });
                } else {
                    List<CompletableFuture<String>> name_futures = new ArrayList<>();
                    for (String id : retrieved_list_ids) {
                        name_futures.add(helper.get_list_name(id));
                    }

                    CompletableFuture.allOf(name_futures.toArray(new CompletableFuture[0])).thenAccept(v -> {
                        List<String> retrieved_names = new ArrayList<>();

                        for (CompletableFuture<String> name_future : name_futures) {
                            try {
                                String name = name_future.get();
                                if (name != null) {
                                    retrieved_names.add(name);
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                utilities.make_snackbar(Present_Lists.this, "Error getting list name: " + e.getMessage());
                            }
                        }

                        runOnUiThread(() -> {
                            lists_names.clear();
                            lists_names.addAll(retrieved_names);
                            lists_ids.clear();
                            lists_ids.addAll(retrieved_list_ids);
                            adapter.notifyDataSetChanged();
                            is_fetching_data.set(false);
                        });
                    }).exceptionally(e -> {
                        utilities.make_snackbar(Present_Lists.this, "Failed to fetch list names: " + e.getMessage());
                        is_fetching_data.set(false);
                        return null;
                    });
                }
            }).exceptionally(e -> {
                utilities.make_snackbar(Present_Lists.this, "Failed to fetch list IDs: " + e.getMessage());
                is_fetching_data.set(false);
                return null;
            });
        } else {
            utilities.make_snackbar(Present_Lists.this, "fetch_display_data() - already fetching data, skipping");
        }
    }
}