package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.CompletableFuture;

public class Add_Item extends AppCompatActivity {

    private TextView title;

    private EditText item;

    private Button create;

    private String Uid;
    private String list_id;
    private String list_name;

    private Firebase_Helper helper;
    private Utilities utilities;

    private void init() {
        title = findViewById(R.id.title);

        item = findViewById(R.id.item);

        create = findViewById(R.id.create);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");
        list_id = intent.getStringExtra("list_id");

        helper = new Firebase_Helper(Add_Item.this);
        utilities = new Utilities();

        helper.get_list_name(list_id)
                .thenAccept(listName -> {
                    if (listName != null) {
                        utilities.make_snackbar(Add_Item.this, "Retrieved list name: " + listName);
                    } else {
                        utilities.make_snackbar(Add_Item.this, "List name not found for ID: " + list_id);
                    }
                })
                .exceptionally(error -> {
                    utilities.make_snackbar(Add_Item.this, "Failed to retrieve list name: " + error.getMessage());
                    return null;
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        init();

        title.setText(list_name);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item_content = item.getText().toString();

                utilities.make_snackbar(Add_Item.this, list_id);

                helper.add_item(list_id, item_content, false);

                Intent intent = new Intent(Add_Item.this, Present_Items.class);
                intent.putExtra("Uid", Uid);
                intent.putExtra("list_id", list_id);
                startActivity(intent);
            }
        });
    }
}