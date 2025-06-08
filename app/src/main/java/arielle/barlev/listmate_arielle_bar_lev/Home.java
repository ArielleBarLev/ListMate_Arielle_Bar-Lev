package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    private String Uid;
    private BottomNavigationView bottom_navigation_view;

    private Firebase_Helper helper = new Firebase_Helper(Home.this);

    private BottomNavigationView.OnNavigationItemSelectedListener navigation_listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_back) {
                        onBackPressed();
                        return true;
                    } else if (itemId == R.id.nav_add) {
                        Fragment current_fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                        if (current_fragment instanceof Present_lists) {
                            load_fragment(new Add_List(), Uid);
                        } else if (current_fragment instanceof Present_Items) {
                            String current_list_id = null;
                            if (current_fragment.getArguments() != null) {
                                current_list_id = current_fragment.getArguments().getString("list_id");
                            }
                            if (current_list_id != null) {
                                load_fragment(new Add_Item(), Uid, current_list_id);
                            } else {
                                Toast.makeText(Home.this, "Cannot add item: List ID not available.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Home.this, "Add button clicked from an unrecognized screen.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (itemId == R.id.nav_logout) {
                        helper.logout();

                        Intent intent = new Intent(Home.this, MainActivity.class);
                        startActivity(intent);

                        return true;
                    }
                    return false;
                }
            };


    private void init() {
        bottom_navigation_view = findViewById(R.id.bottom_navigation);
        bottom_navigation_view.setOnNavigationItemSelectedListener(navigation_listener);

        Uid = getIntent().getStringExtra("Uid");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

        if(savedInstanceState == null) {
            FragmentManager fragment_manager = getSupportFragmentManager();
            FragmentTransaction fragment_transaction = fragment_manager.beginTransaction();
            Present_lists initialFragment = new Present_lists();
            Bundle args = new Bundle();
            args.putString("Uid", Uid);
            initialFragment.setArguments(args);
            fragment_transaction.replace(R.id.fragment_container, initialFragment);
            fragment_transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void load_fragment(Fragment fragment, String user_id) {
        Bundle args = new Bundle();
        args.putString("Uid", user_id);

        fragment.setArguments(args);

        FragmentManager fragment_manager = getSupportFragmentManager();

        FragmentTransaction fragment_transaction = fragment_manager.beginTransaction();

        fragment_transaction.replace(R.id.fragment_container, fragment);
        fragment_transaction.addToBackStack(null);
        fragment_transaction.commit();
    }

    public void load_fragment(Fragment fragment, String user_id, String list_id) {
        Bundle args = new Bundle();
        args.putString("Uid", user_id);
        args.putString("list_id", list_id);

        fragment.setArguments(args);

        FragmentManager fragment_manager = getSupportFragmentManager();

        FragmentTransaction fragment_transaction = fragment_manager.beginTransaction();

        fragment_transaction.replace(R.id.fragment_container, fragment);
        fragment_transaction.addToBackStack(null);
        fragment_transaction.commit();
    }

    private void logout() {
        //TODO: complete
    }
}