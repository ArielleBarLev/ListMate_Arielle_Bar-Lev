package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private String Uid;
    private String list_id;
    private String list_name;

    private Items_Adapter adapter;
    private Firebase_Helper helper;
    private Utilities utilities;

    private void init() {
        recycler_view_items = findViewById(R.id.recycler_view_items);
        recycler_view_items.setLayoutManager(new LinearLayoutManager(this));

        title = findViewById(R.id.title);
        add_item = findViewById(R.id.add_item);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");
        list_id = intent.getStringExtra("list_id");

        helper = new Firebase_Helper(Present_Items.this);
        utilities = new Utilities();

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
    }

    private void fetch_list_items() {
        helper.lists_items(list_id)
                .thenAccept(itemsMap -> {
                    List<Map.Entry<String, Boolean>> itemsList = new ArrayList<>(itemsMap.entrySet());
                    runOnUiThread(() -> {
                        adapter = new Items_Adapter(itemsList);
                        adapter.setOnItemClickListener(new Items_Adapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(String itemName) {
                                Toast.makeText(Present_Items.this, "Clicked: " + itemName, Toast.LENGTH_SHORT).show();
                                helper.update_items_value(Uid, list_name, itemName);
                                fetch_list_items();
                            }
                        });
                        recycler_view_items.setAdapter(adapter);
                    });
                })
                .exceptionally(e -> {
                    Log.e("FirebaseError", "Failed to fetch items: " + e.getMessage());
                    runOnUiThread(() ->
                            Toast.makeText(Present_Items.this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    return null;
                });
    }
}