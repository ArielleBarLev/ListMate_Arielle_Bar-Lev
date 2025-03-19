package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Present_Lists extends AppCompatActivity {

    private LinearLayout users_layout;

    private Button add_list;

    private DatabaseReference database_reference;

    private String Uid;

    private Utilities utilities;

    private void init() {
        users_layout = findViewById(R.id.users_layout);

        add_list = findViewById(R.id.add_list);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database_reference = database.getReference("users");

        utilities = new Utilities();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_lists);

        init();

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
        DatabaseReference user_reference = database_reference.child(Uid);

        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user_reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot data_snapshot) {
                            users_layout.removeAllViews();
                            if (data_snapshot.exists()) {
                                List<String> list_names = new ArrayList<>();
                                for (DataSnapshot listSnapshot : data_snapshot.getChildren()) {
                                    String listName = listSnapshot.getKey();
                                    if (listName != null) {
                                        list_names.add(listName);
                                    }
                                }
                                
                                if (list_names.isEmpty()) {
                                    utilities.make_snackbar(Present_Lists.this, "No lists found for this user.");
                                } else {
                                    for (String listName : list_names) {
                                        TextView listTextView = new TextView(Present_Lists.this);
                                        listTextView.setText(listName);
                                        users_layout.addView(listTextView);
                                    }
                                }
                            } else {
                                utilities.make_snackbar(Present_Lists.this, "No lists found for this user.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            utilities.make_snackbar(Present_Lists.this, "Firebase error: " + error.getMessage());
                        }
                    });
                } else {
                    utilities.make_snackbar(Present_Lists.this, "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                utilities.make_snackbar(Present_Lists.this, "Firebase error: " + error.getMessage());
            }
        });
    }
}