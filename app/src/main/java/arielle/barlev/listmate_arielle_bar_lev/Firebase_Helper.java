package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Firebase_Helper {

    private Context _context;

    private Utilities utilities = new Utilities();

    private FirebaseAuth firebase_auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private void add_user_realtime_database() {
        DatabaseReference users_reference = database.getReference("users");

        String uid = firebase_auth.getCurrentUser().getUid();

        users_reference.child(uid).setValue(false)
                .addOnSuccessListener(aVoid -> {
                    utilities.make_snackbar(_context, "succeed");
                })
                .addOnFailureListener(e -> {
                    utilities.make_snackbar(_context, "fail");
                });
    }

    public Firebase_Helper(Context context) {
        _context = context;
    }

    public CompletableFuture<String> sign_up(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();

        firebase_auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebase_auth.getCurrentUser();
                        String uid = user.getUid();
                        future.complete(uid);
                        add_user_realtime_database();
                    } else {
                        Exception exception = task.getException();
                        String errorMessage = "Login failed: " + (exception != null ? exception.getMessage() : "Unknown error");
                        future.completeExceptionally(new Exception(errorMessage));
                    }
                });

        return future;
    }

    public CompletableFuture<String> login(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();

        firebase_auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebase_auth.getCurrentUser();
                String uid = user.getUid();
                future.complete(uid);
            } else {
                Exception exception = task.getException();
                String errorMessage = "Login failed: " + (exception != null ? exception.getMessage() : "Unknown error");
                future.completeExceptionally(new Exception(errorMessage));
            }
        });

        return future;
    }

    public void create_list(String Uid, String name) {
        DatabaseReference users_reference = database.getReference("users");

        users_reference.child(Uid).child(name).setValue(false)
                .addOnSuccessListener(aVoid -> {
                    utilities.make_snackbar(_context, "succeed");
                })
                .addOnFailureListener(e -> {
                    utilities.make_snackbar(_context, "fail");
                });
    }
}
