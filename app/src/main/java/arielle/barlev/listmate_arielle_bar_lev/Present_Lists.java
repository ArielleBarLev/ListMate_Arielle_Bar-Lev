package arielle.barlev.listmate_arielle_bar_lev;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.health.connect.datatypes.units.Pressure;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Present_Lists extends AppCompatActivity {

    private Button add_list;

    private String Uid;

    private RecyclerView lists_layout;
    private List<String> lists_names;
    private List<String> lists_ids;

    private Firebase_Helper helper;
    private Lists_Names_Adapter adapter;
    private Utilities utilities;

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

        adapter = new Lists_Names_Adapter(lists_names, new Lists_Names_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (0 <= position && position < lists_ids.size()) {
                    String list_id = lists_ids.get(position);
                    utilities.make_snackbar(Present_Lists.this, "Clicked List ID: " + list_id);
                    Intent intent = new Intent(Present_Lists.this, Present_Items.class);
                    intent.putExtra("Uid", Uid);
                    intent.putExtra("list_id", list_id);
                    startActivity(intent);
                } else {
                    utilities.make_snackbar(Present_Lists.this, "Error: Invalid list item clicked.");
                }
            }
        });
        lists_layout.setAdapter(adapter);

        display_data();

        add_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Present_Lists.this, Add_List.class);
                intent.putExtra("Uid", Uid);
                startActivity(intent);
            }
        });
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
                                    Log.e(TAG, "Error getting list name", e);
                                }
                            }
                            lists_names.addAll(retrievedNames);
                            adapter.notifyDataSetChanged();
                        })
                        .exceptionally(e -> {
                            utilities.make_snackbar(Present_Lists.this, "Failed to fetch list names: " + e.getMessage());
                            Log.e(TAG, "Failed to fetch list names", e);
                            return null;
                        });
            }
        }).exceptionally(e -> {
            utilities.make_snackbar(Present_Lists.this, "Failed to fetch list IDs: " + e.getMessage());
            Log.e(TAG, "Failed to fetch list IDs", e);
            return null;
        });
    }
}