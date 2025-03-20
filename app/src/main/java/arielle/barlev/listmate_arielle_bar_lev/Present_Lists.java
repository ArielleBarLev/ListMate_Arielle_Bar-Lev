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

public class Present_Lists extends AppCompatActivity {

    private Button add_list;

    private String Uid;

    private RecyclerView lists_layout;
    private List<String> lists_names;

    private Firebase_Helper helper;
    private Lists_Names_Adapter adapter;
    private Utilities utilities;

    private void init() {
        lists_layout = findViewById(R.id.lists_layout);
        add_list = findViewById(R.id.add_list);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");

        lists_names = new ArrayList<>();

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
                if (0 <= position && position < lists_names.size()) {
                    String list_name = lists_names.get(position);
                    utilities.make_snackbar(Present_Lists.this, "Clicked: " + list_name);
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

        helper.users_lists(Uid).thenAccept(retrievedListNames -> {
            if (retrievedListNames.isEmpty()) {
                utilities.make_snackbar(Present_Lists.this, "No lists found for this user.");
            } else {
                lists_names.addAll(retrievedListNames);
                adapter.notifyDataSetChanged();
            }
        }).exceptionally(e -> {
            utilities.make_snackbar(Present_Lists.this, "Failed to fetch lists: " + e.getMessage());
            return null;
        });
    }
}