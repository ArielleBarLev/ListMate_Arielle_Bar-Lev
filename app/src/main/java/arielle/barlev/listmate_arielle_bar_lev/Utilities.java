package arielle.barlev.listmate_arielle_bar_lev;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.Queue;

public class Utilities {

    private Queue<String> snackbarQueue = new LinkedList<>();
    private Snackbar currentSnackbar;
    private boolean isShowing = false;

    @SuppressLint("RestrictedApi")
    public void make_snackbar(Context context, String message) {
        snackbarQueue.offer(message);
        if(!isShowing){
            showNextSnackbar(context);
        }
    }

    private void showNextSnackbar(Context context) {
        if (snackbarQueue.isEmpty()) {
            isShowing = false;
            return;
        }

        String message = snackbarQueue.poll();

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            View view = activity.findViewById(android.R.id.content);

            if (view != null) {
                isShowing = true;
                activity.runOnUiThread(() -> {
                    // between runOnUiThread call and execution
                    if (activity.findViewById(android.R.id.content) == null) {
                        Log.e("Snackbar", "Activity content view null after runOnUiThread.");
                        isShowing = false;
                        return;
                    }

                    currentSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);

                    currentSnackbar.setAction("Dismiss", v -> {
                        currentSnackbar.dismiss();
                        showNextSnackbar(context);
                    });

                    currentSnackbar.addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            // This callback is on the main thread
                            showNextSnackbar(context);
                        }
                    });

                    currentSnackbar.show();
                });
            } else {
                Log.e("Snackbar", "Root view not found!");
                activity.runOnUiThread(() ->
                        Toast.makeText(context, "Error displaying message.", Toast.LENGTH_SHORT).show()
                );
            }
        } else {
            Log.e("Snackbar", "Context is not an Activity!");
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Error displaying message.", Toast.LENGTH_SHORT).show()
            );
        }
    }
}