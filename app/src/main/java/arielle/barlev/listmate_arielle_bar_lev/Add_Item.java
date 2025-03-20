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

public class Add_Item extends AppCompatActivity {

    private TextView title;

    private EditText item;

    private Button create;

    private String Uid;
    private String list_name;

    private Firebase_Helper helper;
    private Utilities utilities;

    private void init() {
        title = findViewById(R.id.title);

        item = findViewById(R.id.item);

        create = findViewById(R.id.create);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");
        list_name = intent.getStringExtra("list_name");

        helper = new Firebase_Helper(Add_Item.this);
        utilities = new Utilities();
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

                helper.add_item(Uid, list_name, item_content);

                Intent intent = new Intent(Add_Item.this, Present_Lists.class);
                intent.putExtra("Uid", Uid);
                startActivity(intent);
            }
        });
    }
}