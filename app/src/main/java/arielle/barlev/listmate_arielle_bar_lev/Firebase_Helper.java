package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
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
        A function to add an item to a list.
        Input: user's id, list's name, item
        Return value: none
     */
    public void add_item(String Uid, String list_name, String item) {
        DatabaseReference users_reference = _database.getReference("users");

        users_reference.child(Uid).child(list_name).child(item).setValue(false)
                .addOnSuccessListener(aVoid -> {
                    utilities.make_snackbar(_context, "succeed");
                })
                .addOnFailureListener(e -> {
                    utilities.make_snackbar(_context, "fail");
                });
    }

    /*
        A function to return all user's lists.
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

    /*
        A function to return all list's items.
        Input: user's id, list's name
        Return value: list's items
     */
    public CompletableFuture<Map<String, Boolean>> lists_items(String Uid, String list_name) {
        CompletableFuture<Map<String, Boolean>> future = new CompletableFuture<>();
        DatabaseReference user_reference = _database.getReference("users").child(Uid).child(list_name);

        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Boolean> items = new HashMap<>();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        String item_title = itemSnapshot.getKey();
                        Boolean item_value = itemSnapshot.getValue(Boolean.class);
                        if (item_title != null && item_value != null) {
                            items.put(item_title, item_value);
                        }
                    }
                    future.complete(items);
                } else {
                    future.complete(new HashMap<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(new Exception("Failed to fetch lists: " + error.getMessage()));
            }
        });
        return future;
    }

    public void update_items_value(String Uid, String list_name, String item) {
        DatabaseReference item_reference = _database.getReference("users").child(Uid).child(list_name).child(item);
        
        item_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean current_value = snapshot.getValue(Boolean.class);
                    if (current_value != null) {
                        item_reference.setValue(!current_value);
                    } else {
                        // Handle case where the value is null (optional logging)
                        System.err.println("Warning: Item value is null for " + item + " in " + list_name);
                    }
                } else {
                    // Handle case where the item doesn't exist (optional logging)
                    System.err.println("Warning: Item '" + item + "' not found in list '" + list_name + "'");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error during read (optional logging)
                System.err.println("Error fetching item data for toggle: " + error.getMessage());
            }
        });
    }
}
