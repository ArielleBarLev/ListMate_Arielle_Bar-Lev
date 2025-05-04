package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
import java.util.concurrent.ExecutionException;

public class Firebase_Helper {

    private Context _context;

    private Utilities _utilities = new Utilities();

    private FirebaseAuth _firebase_auth;
    private FirebaseDatabase _database;

    /*
        A function to insert user to the realtime database.
        Input: none
        Return value: none
     */
    private void add_user_realtime_database(String email) {
        DatabaseReference users_reference = _database.getReference("users");

        String uid = _firebase_auth.getCurrentUser().getUid();

        //Insert user's id
        users_reference.child(uid).setValue(false)
                .addOnSuccessListener(aVoid -> {
                    _utilities.make_snackbar(_context, "succeed");
                })
                .addOnFailureListener(e -> {
                    _utilities.make_snackbar(_context, "fail");
                });

        //Insert user's details
        users_reference.child(uid).child("details").child("name").setValue(email).addOnSuccessListener(aVoid -> {
                    _utilities.make_snackbar(_context, "succeed");
                })
                .addOnFailureListener(e -> {
                    _utilities.make_snackbar(_context, "fail");
                });

        //Insert user's lists (blank)
        users_reference.child(uid).child("lists").setValue(false);
    }

    /*
        A function to return item's value.
        Input: list's id, item
        Return value: item's value
     */
    private CompletableFuture<Boolean> item_value(String list_id, String item) {
        DatabaseReference item_reference = _database.getReference("lists").child(list_id).child("items").child(item);

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        item_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean value = snapshot.getValue(Boolean.class);
                    _utilities.make_snackbar(_context, "value: " + value.toString());
                    if (value != null) {
                        future.complete(value);
                    } else {
                        // Handle case where the value is null (optional logging)
                        _utilities.make_snackbar(_context, "Warning: Item value is null for " + item + " in " + list_id);
                        future.complete(null);
                    }
                } else {
                    // Handle case where the item doesn't exist (optional logging)
                    _utilities.make_snackbar(_context, "Warning: Item '" + item + "' not found in list '" + list_id + "'");
                    future.complete(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error during read (optional logging)
                _utilities.make_snackbar(_context, "Error fetching item data for toggle: " + error.getMessage());
            }
        });

        return future;
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

        _firebase_auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = _firebase_auth.getCurrentUser();
                String uid = user.getUid();
                future.complete(uid);
                add_user_realtime_database(email);
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
        //Create list
        DatabaseReference lists_reference = _database.getReference("lists");
        lists_reference = lists_reference.push();
        String id = lists_reference.getKey();
        lists_reference.child("name").setValue(name);

        //Add list to user
        DatabaseReference users_reference = _database.getReference("users");
        users_reference.child(Uid).child("lists").child(id).setValue(false);
    }

    /*
        A function to add an item to a list.
        Input: list's id, item
        Return value: none
     */
    public void add_item(String list_id, String item, Boolean value) {
        DatabaseReference users_reference = _database.getReference("lists");

        users_reference.child(list_id).child("items").child(item).setValue(value)
                .addOnSuccessListener(aVoid -> {
                    _utilities.make_snackbar(_context, "succeed");
                })
                .addOnFailureListener(e -> {
                    _utilities.make_snackbar(_context, "fail");
                });
    }

    /*
    A function to return list's name.
    Input: list's id
    Return value: A CompletableFuture containing the list's name.
 */
    public CompletableFuture<String> get_list_name(String list_id) {
        CompletableFuture<String> future = new CompletableFuture<>();
        DatabaseReference list_reference = _database.getReference("lists").child(list_id).child("name");

        list_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String listName = snapshot.getValue(String.class);
                    future.complete(listName);
                } else {
                    future.complete(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(new Exception("Failed to fetch list name for ID " + list_id + ": " + error.getMessage()));
            }
        });

        return future;
    }

    /*
        A function to return all user's lists.
        Input: user's id
        Return value: user's lists
     */
    public CompletableFuture<List<String>> users_lists(String Uid) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        DatabaseReference user_reference = _database.getReference("users").child(Uid).child("lists");

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
        Input: list's id
        Return value: list's items
     */
    public CompletableFuture<Map<String, Boolean>> lists_items(String list_id) {
        CompletableFuture<Map<String, Boolean>> future = new CompletableFuture<>();
        DatabaseReference user_reference = _database.getReference("lists").child(list_id).child("items");

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

    /*
        A function to update item's value
        Input: list's id, item
        Return value: none
     */
    public void update_items_value(String list_id, String item) {
        DatabaseReference item_reference = _database.getReference("lists").child(list_id).child("items").child(item);

        item_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean current_value = snapshot.getValue(Boolean.class);
                    if (current_value != null) {
                        item_reference.setValue(!current_value);
                    } else {
                        // Handle case where the value is null (optional logging)
                        _utilities.make_snackbar(_context, "Warning: Item value is null for " + item + " in " + list_id);
                    }
                } else {
                    // Handle case where the item doesn't exist (optional logging)
                    _utilities.make_snackbar(_context, "Warning: Item '" + item + "' not found in list '" + list_id + "'");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error during read (optional logging)
                _utilities.make_snackbar(_context, "Error fetching item data for toggle: " + error.getMessage());
            }
        });
    }

    /*
        A function to delete item from the list
        Input: list's id, item
        Return value: none
     */
    public void delete_item(String list_id, String item) {
        DatabaseReference item_reference = _database.getReference("lists").child(list_id).child("items").child(item);

        item_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    item_reference.removeValue().addOnSuccessListener(aVoid -> {
                    }).addOnFailureListener(e -> {
                        _utilities.make_snackbar(_context, "Error toggling off '" + item + "' in list '" + list_id + "': " + e.getMessage());
                    });
                } else {
                    // Handle case where the item doesn't exist (optional logging)
                    _utilities.make_snackbar(_context, "Warning: Item '" + item + "' not found in list '" + list_id + "'");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error during read (optional logging)
                _utilities.make_snackbar(_context, "Error fetching item data for toggle: " + error.getMessage());
            }
        });
    }

    /*
        A function to update an item.
        Input: list's id, item, new item
        Return value: none
     */
    public void update_item(String list_id, String item, String new_item) {
        CompletableFuture<Boolean> future = item_value(list_id, item);

        future.thenAccept(value -> {
            if (value != null) {
                add_item(list_id, new_item, value);
                delete_item(list_id, item);
                _utilities.make_snackbar(_context, "Item '" + item + "' updated to '" + new_item + "'");
            } else {
                _utilities.make_snackbar(_context, "Error: Could not retrieve value for item '" + item + "'");
            }
        }).exceptionally(ex -> {
            _utilities.make_snackbar(_context, "Error updating item '" + item + "': " + ex.getMessage());
            return null;
        });
    }
}
