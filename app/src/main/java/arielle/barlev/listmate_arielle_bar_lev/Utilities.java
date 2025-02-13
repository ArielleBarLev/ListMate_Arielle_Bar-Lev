package arielle.barlev.listmate_arielle_bar_lev;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class Utilities {

    @SuppressLint("RestrictedApi")
    public void make_snackbar(Context context, String message) {

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            View view = activity.findViewById(android.R.id.content);

            if (view != null) {
                Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction("Dismiss", v -> {});

                snackbar.show(); // Show the Snackbar after setting the action

            } else {
                Log.e("Snackbar", "Root view not found!");
                Toast.makeText(context, "Error displaying message.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("Snackbar", "Context is not an Activity!");
            Toast.makeText(context, "Error displaying message.", Toast.LENGTH_SHORT).show();
        }
    }
}
