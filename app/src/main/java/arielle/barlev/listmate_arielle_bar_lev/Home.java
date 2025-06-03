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

    private BottomNavigationView.OnNavigationItemSelectedListener navigation_listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_home) {
                        Toast.makeText(Home.this, "Home clicked", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        return true;
                    } else if (itemId == R.id.nav_add) {
                        Intent intent = new Intent(Home.this, Add_List.class);
                        intent.putExtra("Uid", Uid);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.nav_logout) {
                        logout();
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
            load_fragment(new Present_lists(), Uid);
        }
    }

    private void load_fragment(Fragment fragment, String user_id) {
        Bundle args = new Bundle();
        args.putString("Uid", user_id);

        fragment.setArguments(args);

        FragmentManager fragment_manager = getSupportFragmentManager();

        FragmentTransaction fragment_transaction = fragment_manager.beginTransaction();
        fragment_transaction.replace(R.id.fragment_container, fragment);
        fragment_transaction.commit();
    }

    private void logout() {
        //TODO: complete
    }
}