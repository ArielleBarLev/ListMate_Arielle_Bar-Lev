package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Add_List extends AppCompatActivity {

    private EditText list_name;

    private Button create;

    private Firebase_Helper helper;
    private Utilities utilities;

    private String Uid;

    private void init() {

        list_name = findViewById(R.id.list_name);

        create = findViewById(R.id.create);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("Uid");

        helper = new Firebase_Helper(Add_List.this);
        utilities = new Utilities();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        init();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String list_name_content = list_name.getText().toString();

                helper.create_list(Uid, list_name_content);

                Intent intent = new Intent(Add_List.this, Home.class);
                intent.putExtra("Uid", Uid);
                startActivity(intent);
            }
        });
    }
}