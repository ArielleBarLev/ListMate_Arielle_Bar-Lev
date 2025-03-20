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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Firebase_Helper {

    private Context _context;

    private Utilities utilities = new Utilities();

    private FirebaseAuth _firebase_auth;
    private FirebaseDatabase _database;

    /*
        A function to insert user's id to the realtime database.
        Input: none
        Return value: none
     */
    private void add_user_realtime_database() {
        DatabaseReference users_reference = _database.getReference("users");

        String uid = _firebase_auth.getCurrentUser().getUid();

        users_reference.child(uid).setValue(false)
                .addOnSuccessListener(aVoid -> {
                    utilities.make_snackbar(_context, "succeed");
                })
                .addOnFailureListener(e -> {
                    utilities.make_snackbar(_context, "fail");
                });
    }

    //Constructor
    public Firebase_Helper(Context context) {
        _context = context;
        _firebase_auth = FirebaseAuth.getInstance();
        _database = FirebaseDatabase.getInstance();
    }

    /*
        A function to register a new user to the auth database.
        Input: email, password
        Return value: user's id
     */
    public CompletableFuture<String> sign_up(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();

        _firebase_auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = _firebase_auth.getCurrentUser();
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

    /*
        A function to login user.
        Input: email, password
        Return value: user's id
     */
    public CompletableFuture<String> login(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();

        _firebase_auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = _firebase_auth.getCurrentUser();
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

    /*
        A function to create new list.
        Input: user's id, list's name
        Return value: none
     */
    public void create_list(String Uid, String name) {
        DatabaseReference users_reference = _database.getReference("users");

        users_reference.child(Uid).child(name).setValue(false)
                .addOnSuccessListener(aVoid -> {
                    utilities.make_snackbar(_context, "succeed");
                })
                .addOnFailureListener(e -> {
                    utilities.make_snackbar(_context, "fail");
                });
    }

    /*
        A function to return all user's lists
        Input: user's id
        Return value: user's lists
     */
    public CompletableFuture<List<String>> users_lists(String Uid) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        DatabaseReference user_reference = _database.getReference("users").child(Uid);

        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> list_names = new ArrayList<>();
                    for (DataSnapshot listSnapshot : snapshot.getChildren()) {
                        String listName = listSnapshot.getKey();
                        if (listName != null) {
                            list_names.add(listName);
                        }
                    }
                    future.complete(list_names);
                } else {
                    future.complete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(new Exception("Failed to fetch lists: " + error.getMessage()));
            }
        });
        return future;
    }
}
