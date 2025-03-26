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

    private FirebaseDatabase database;
    private DatabaseReference listReference;
    private RecyclerView recycler_view_items;
    private Items_Adapter adapter;

    private String Uid;
    private String list_name;

    private void init() {
        recycler_view_items = findViewById(R.id.recycler_view_items);
        recycler_view_items.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");
        list_name = intent.getStringExtra("list_name");

        database = FirebaseDatabase.getInstance();
        listReference = database.getReference("users").child(Uid).child(list_name);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_items);

        init();

        listReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Map.Entry<String, Boolean>> items = new ArrayList<>();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String itemName = itemSnapshot.getKey();
                    Boolean itemValue = itemSnapshot.getValue(Boolean.class);

                    if (itemName != null && itemValue != null) {
                        items.add(new java.util.AbstractMap.SimpleEntry<>(itemName, itemValue));
                    }
                }

                adapter = new Items_Adapter(items);
                recycler_view_items.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("FirebaseError", "Failed to read value.", databaseError.toException());
                Toast.makeText(Present_Items.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}