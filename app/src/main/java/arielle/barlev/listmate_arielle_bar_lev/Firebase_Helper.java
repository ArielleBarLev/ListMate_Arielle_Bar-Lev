package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class Firebase_Helper {

    private FirebaseAuth firebase_auth;

    public void sign_up(Context context, String email, String password) {
        firebase_auth =FirebaseAuth.getInstance();

        firebase_auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(
                            context,
                            "succeed",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    Exception exception = task.getException();
                    String errorMessage = "Sign-up failed: ";

                    if (exception != null) {
                        if (exception instanceof FirebaseAuthException) {
                            FirebaseAuthException authException = (FirebaseAuthException) exception;
                            errorMessage += authException.getMessage();
                        } else {
                            errorMessage += exception.getMessage();
                        }
                    } else {
                        errorMessage += "An unknown error occurred.";
                    }

                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("Firebase_Helper", errorMessage);
                }
            }
        });
    }
}
