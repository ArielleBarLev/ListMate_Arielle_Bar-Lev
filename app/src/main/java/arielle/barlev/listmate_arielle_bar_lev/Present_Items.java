package arielle.barlev.listmate_arielle_bar_lev;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Map;

public class Present_Items extends AppCompatActivity {

    private TextView title;
    private RecyclerView recycler_view_items;

    private Button add_item;
    private Button back;

    private String Uid;
    private String list_id;
    private String list_name;

    private Items_Adapter adapter;
    private Firebase_Helper helper;
    private Utilities utilities;

    private List<Map.Entry<String, Boolean>> current_items_list;

    private void init() {
        recycler_view_items = findViewById(R.id.recycler_view_items);
        recycler_view_items.setLayoutManager(new LinearLayoutManager(this));

        title = findViewById(R.id.title);
        add_item = findViewById(R.id.add_item);
        back = findViewById(R.id.back);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");
        list_id = intent.getStringExtra("list_id");

        helper = new Firebase_Helper(Present_Items.this);
        utilities = new Utilities();

        current_items_list = new ArrayList<>();

        helper.get_list_name(list_id)
                .thenAccept(listName -> {
                    if (listName != null) {
                        utilities.make_snackbar(Present_Items.this, "Retrieved list name: " + listName);
                    } else {
                        utilities.make_snackbar(Present_Items.this, "List name not found for ID: " + list_id);
                    }
                })
                .exceptionally(error -> {
                    utilities.make_snackbar(Present_Items.this, "Failed to retrieve list name: " + error.getMessage());
                    return null;
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_items);

        init();

        title.setText(list_name);

        fetch_list_items();

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Present_Items.this, Add_Item.class);
                intent.putExtra("Uid", Uid);
                intent.putExtra("list_id", list_id);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Present_Items.this, Present_Lists.class);
                intent.putExtra("Uid", Uid);
                startActivity(intent);
            }
        });
    }

    private void fetch_list_items() {
        helper.lists_items(list_id).thenAccept(itemsMap -> {
            current_items_list.clear();
            current_items_list.addAll(itemsMap.entrySet());
            runOnUiThread(() -> {
                if (adapter == null) {
                    adapter = new Items_Adapter(current_items_list);
                    setup_item_click_listeners(); // Set up listeners after adapter is created
                    recycler_view_items.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged(); // Just refresh the adapter if data changes
                }
            });
        }).exceptionally(e -> {
            Log.e("FirebaseError", "Failed to fetch items: " + e.getMessage());
            runOnUiThread(() ->
                    utilities.make_snackbar(Present_Items.this, "Failed to load data: " + e.getMessage()));
            return null;
        });
    }

    private void setup_item_click_listeners() {
        adapter.setOnItemClickListener(item -> {
            Context context = Present_Items.this;
            AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(context);
            alert_dialog_builder.setTitle("Update Item:");
            final EditText text_box = new EditText(getApplicationContext());
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
                    adapter.updateItemName(item, new_item); // Update the UI locally
                } else {
                    Toast.makeText(context, "Please enter an item name", Toast.LENGTH_SHORT).show();
                }
            });
            alert_dialog_builder.create().show();
        });

        adapter.setOnTrashClickListener(item -> {
            helper.delete_item(list_id, item);
            adapter.deleteItem(item); // Update the UI locally
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
        });
    }
}