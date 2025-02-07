package arielle.barlev.listmate_arielle_bar_lev;

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

    private Button send;

    private FirebaseAuth firebase_auth;

    private void init_component() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        send = findViewById(R.id.send);

        firebase_auth =FirebaseAuth.getInstance();
    }

    private void sign_up(String email, String password) {
        firebase_auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_component();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_content = email.getText().toString();
                String password_content = password.getText().toString();

                if (email_content.isEmpty() || password_content.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if (password_content.length() < 6) {
                    Toast.makeText(MainActivity.this, "Password to short", Toast.LENGTH_LONG).show();
                }

                sign_up(email_content, password_content);
            }
        });
    }
}