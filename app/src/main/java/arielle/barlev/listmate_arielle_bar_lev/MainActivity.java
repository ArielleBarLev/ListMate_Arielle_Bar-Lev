package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private Button sign_up;
    private Button login;

    private Firebase_Helper helper;
    private Utilities utilities;

    private void init_component() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        sign_up = findViewById(R.id.sign_up);
        login = findViewById(R.id.login);

        helper = new Firebase_Helper(MainActivity.this);
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

                CompletableFuture<String> sign_up_future = helper.sign_up(email_content, password_content);

                sign_up_future.thenAccept(uid -> {
                    utilities.make_snackbar(MainActivity.this, uid);

                    Intent intent = new Intent(MainActivity.this, Present_Lists.class);
                    intent.putExtra("Uid", uid);
                    startActivity(intent);
                }).exceptionally(ex -> {
                    Log.e("LoginActivity", "Login error: " + ex.getMessage());
                    utilities.make_snackbar(MainActivity.this, ex.getMessage());
                    return null;
                });

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

                CompletableFuture<String> login_future = helper.login(email_content, password_content);

                login_future.thenAccept(uid -> {
                    utilities.make_snackbar(MainActivity.this, "success");

                    Intent intent = new Intent(MainActivity.this, Present_Lists.class);
                    intent.putExtra("Uid", uid);
                    startActivity(intent);
                }).exceptionally(ex -> {
                    Log.e("LoginActivity", "Login error: " + ex.getMessage());
                    utilities.make_snackbar(MainActivity.this, ex.getMessage());
                    return null;
                });

            }
        });
    }
}