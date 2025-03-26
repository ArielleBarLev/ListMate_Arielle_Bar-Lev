package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private RecyclerView recycler_view_items;

    private String Uid;
    private String list_name;

    private Items_Adapter adapter;
    private Firebase_Helper helper;

    private void init() {
        recycler_view_items = findViewById(R.id.recycler_view_items);
        recycler_view_items.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");
        list_name = intent.getStringExtra("list_name");

        helper = new Firebase_Helper(Present_Items.this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_items);

        init();

        fetch_list_items();
    }

    private void fetch_list_items() {
        helper.lists_items(Uid, list_name)
                .thenAccept(itemsMap -> {
                    List<Map.Entry<String, Boolean>> itemsList = new ArrayList<>(itemsMap.entrySet());
                    runOnUiThread(() -> {
                        adapter = new Items_Adapter(itemsList); // Create adapter
                        adapter.setOnItemClickListener(new Items_Adapter.OnItemClickListener() { // Set the listener
                            @Override
                            public void onItemClick(String itemName) {
                                Toast.makeText(Present_Items.this, "Clicked: " + itemName, Toast.LENGTH_SHORT).show();
                                helper.update_items_value(Uid, list_name, itemName);
                                fetch_list_items();
                                // The ValueEventListener will handle UI updates
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