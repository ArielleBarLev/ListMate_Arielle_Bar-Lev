package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private Button sign_up;
    private Button login;
    private Button temp;

    private Firebase_Helper helper;
    private Utilities utilities;

    private void init_component() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        sign_up = findViewById(R.id.sign_up);
        login = findViewById(R.id.login);
        temp = findViewById(R.id.temp);

        helper = new Firebase_Helper();
        utilities = new Utilities();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_component();

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_content = email.getText().toString();
                String password_content = password.getText().toString();

                if (email_content.isEmpty() || password_content.isEmpty()) {
                    utilities.make_snackbar(MainActivity.this, "Fields are empty");
                    return;
                }

                helper.sign_up(MainActivity.this, email_content, password_content);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_content = email.getText().toString();
                String password_content = password.getText().toString();

                if (email_content.isEmpty() || password_content.isEmpty()) {
                    utilities.make_snackbar(MainActivity.this, "Fields are empty");
                    return;
                }

                helper.login(MainActivity.this, email_content, password_content);
            }
        });

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Add_List.class);
                startActivity(intent);
            }
        });
    }
}