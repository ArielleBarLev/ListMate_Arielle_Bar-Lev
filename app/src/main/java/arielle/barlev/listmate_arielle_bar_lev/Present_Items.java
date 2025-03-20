package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Present_Items extends AppCompatActivity {

    private TextView title;

    private Button add_item;

    private String Uid;
    private String list_name;

    private RecyclerView items_layout;
    private Map<String, Boolean> items;

    private Firebase_Helper helper;
    private Items_Adapter adapter;
    private Utilities utilities;

    private void init() {
        items_layout = findViewById(R.id.items_layout);
        add_item = findViewById(R.id.add_item);
        title = findViewById(R.id.title);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");
        list_name = intent.getStringExtra("list_name");

        items = new HashMap<>();

        helper = new Firebase_Helper(Present_Items.this);
        utilities = new Utilities();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_items);

        init();

        title.setText(list_name);

        LinearLayoutManager layout_manager = new LinearLayoutManager(Present_Items.this);
        items_layout.setLayoutManager(layout_manager);

        adapter = new Items_Adapter(items, position -> {
            if (0 <= position && position < items.size()) {
                String itemTitle = new java.util.ArrayList<>(items.entrySet()).get(position).getKey();
                Boolean itemValue = new java.util.ArrayList<>(items.entrySet()).get(position).getValue();
                utilities.make_snackbar(Present_Items.this, "Clicked: " + itemTitle + ", " + itemValue);
            } else {
                utilities.make_snackbar(Present_Items.this, "Error: Invalid list item clicked.");
            }
        }, Present_Items.this);
        items_layout.setAdapter(adapter);

        display_data();

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Present_Items.this, Add_Item.class);
                intent.putExtra("Uid", Uid);
                intent.putExtra("list_name", list_name);
                startActivity(intent);
            }
        });

    }

    private void display_data0() {
        items.clear();

        helper.lists_items(Uid, list_name).thenAccept(retrievedItems -> {
            if (retrievedItems.isEmpty()) {
                utilities.make_snackbar(Present_Items.this, "No items found in this list.");
            } else {
                items.putAll(retrievedItems);
                adapter.notifyDataSetChanged();
            }
        }).exceptionally(e -> {
            utilities.make_snackbar(Present_Items.this, "Failed to fetch lists: " + e.getMessage());
            return null;
        });
    }

    private void display_data() {
        items.clear();
        utilities.make_snackbar(Present_Items.this, "display_data called");

        helper.lists_items(Uid, list_name).thenAccept(retrievedItems -> {
            utilities.make_snackbar(Present_Items.this, "Firebase retrieval successful");
            utilities.make_snackbar(Present_Items.this, "retrievedItems: " + retrievedItems.toString());

            if (retrievedItems.isEmpty()) {
                utilities.make_snackbar(Present_Items.this, "No items found in this list.");
                utilities.make_snackbar(Present_Items.this, "retrievedItems is empty");
            } else {
                items.putAll(retrievedItems);
                utilities.make_snackbar(Present_Items.this, "Items map size: " + items.size());
                utilities.make_snackbar(Present_Items.this, "items: " + items.toString());

                runOnUiThread(() -> {
                    utilities.make_snackbar(Present_Items.this, "adapter.notifyDataSetChanged called");
                    adapter.notifyDataSetChanged();
                    utilities.make_snackbar(Present_Items.this, "adapter.notifyDataSetChanged finished");
                });
            }
        }).exceptionally(e -> {
            utilities.make_snackbar(Present_Items.this, "Failed to fetch items: " + e.getMessage());
            return null;
        });
    }
}