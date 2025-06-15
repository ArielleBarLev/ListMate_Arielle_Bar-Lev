package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private Button sign_up;
    private Button login;

    private TextView remember_me;

    private Firebase_Helper helper;
    private Utilities utilities;

    private Boolean from_home = false;

    private static final String PREFERENCE_NANE = "ListMate_Preferences";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private void init() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        sign_up = findViewById(R.id.sign_up);
        login = findViewById(R.id.login);

        remember_me = findViewById(R.id.remember_me);

        helper = new Firebase_Helper(MainActivity.this);
        utilities = new Utilities();

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("source_activity")) {
            String source = intent.getStringExtra("source_activity");
            if ("Home".equals(source)) {
                from_home = true;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if (!from_home) {
            load_saved_preferences();
        }

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

                    Intent intent = new Intent(MainActivity.this, Home.class);
                    intent.putExtra("Uid", uid);
                    startActivity(intent);
                }).exceptionally(ex -> {
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

                    Intent intent = new Intent(MainActivity.this, Home.class);
                    intent.putExtra("Uid", uid);
                    startActivity(intent);
                }).exceptionally(ex -> {
                    utilities.make_snackbar(MainActivity.this, ex.getMessage());
                    return null;
                });
            }
        });

        remember_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_content = email.getText().toString();
                String password_content = password.getText().toString();

                if (email_content.isEmpty() || password_content.isEmpty()) {
                    utilities.make_snackbar(MainActivity.this, "Fields are empty");
                    return;
                }

                save_preferences(email_content, password_content);
            }
        });
    }

    private void load_saved_preferences() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NANE, MODE_PRIVATE);

        String saved_email = preferences.getString(KEY_EMAIL, "");
        String saved_password = preferences.getString(KEY_PASSWORD, "");

        if (!saved_email.isEmpty() && !saved_password.isEmpty()) {
            apply_settings(saved_email, saved_password);
        }
    }

    private void save_preferences(String email, String password) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NANE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);

        editor.apply();

        apply_settings(email, password);

        Toast.makeText(MainActivity.this, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void apply_settings(String email, String password) {
        CompletableFuture<String> login_future = helper.login(email, password);

        login_future.thenAccept(uid -> {
            Intent intent = new Intent(MainActivity.this, Home.class);
            intent.putExtra("Uid", uid);
            startActivity(intent);
        }).exceptionally(ex -> {
            utilities.make_snackbar(MainActivity.this, ex.getMessage());
            return null;
        });
    }
}