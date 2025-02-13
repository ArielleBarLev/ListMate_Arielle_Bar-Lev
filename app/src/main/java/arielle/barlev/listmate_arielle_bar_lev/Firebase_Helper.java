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

public class Firebase_Helper {

    private FirebaseAuth firebase_auth = FirebaseAuth.getInstance();
    private Utilities utilities = new Utilities();

    public void sign_up(Context context, String email, String password) {
        firebase_auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    utilities.make_snackbar(context, "succeed");
                } else {
                    Exception exception = task.getException();
                    String error_message = "Sign-up failed: ";

                    if (exception != null) {
                        if (exception instanceof FirebaseAuthException) {
                            FirebaseAuthException authException = (FirebaseAuthException) exception;
                            error_message += authException.getMessage();
                        } else {
                            error_message += exception.getMessage();
                        }
                    } else {
                        error_message += "An unknown error occurred.";
                    }

                    utilities.make_snackbar(context, error_message);
                    Log.e("Firebase_Helper", error_message);
                }
            }
        });
    }

    public void login(Context context, String email, String password) {

        firebase_auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() { // Use addOnCompleteListener
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    utilities.make_snackbar(context, "succeed");
                } else {
                    Exception exception = task.getException();
                    String error_message = "Login failed: ";

                    if (exception != null) {
                        if (exception instanceof FirebaseAuthException) {
                            FirebaseAuthException authException = (FirebaseAuthException) exception;
                            error_message += authException.getMessage();
                        } else {
                            error_message += exception.getMessage();
                        }
                    } else {
                        error_message += "An unknown error occurred.";
                    }

                    utilities.make_snackbar(context, error_message);
                    Log.e("Firebase_Helper", error_message);
                }
            }
        });

    }
}
