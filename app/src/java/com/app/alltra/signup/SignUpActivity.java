package com.app.alltra.signup;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.app.alltra.R;
import com.app.alltra.database.DataBaseFunctions;
import com.app.alltra.database.UserData;
import com.app.alltra.email_confirmation.ConfirmEmailActivity;
import com.app.alltra.home.HomeActivity;
import com.app.alltra.utils.Dialogs;
import com.app.alltra.utils.Functions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements ConfirmEmailActivity.ConfirmEmailCallback {

    private final boolean[] result = {false, false, false};
    private String userType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_signup);
        FirebaseApp.initializeApp(this);
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        EditText passwordConfirmationEditText = findViewById(R.id.password2);
        Button signupButton = findViewById(R.id.signup);
        TextView title = findViewById(R.id.title);

        /*----------------------------------------------------------------------------------------*/
        //Username and Password fields changed:
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (DataBaseFunctions.isUsernameValid(editable.toString()))
                    usernameEditText.setError(null);
                else
                    usernameEditText.setError(getString(R.string.invalid_username));
                result[0] = DataBaseFunctions.isUsernameValid(editable.toString());
                signupButton.setEnabled(result[0] && result[1] && result[2]);
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals(passwordConfirmationEditText.getText().toString()))
                    passwordConfirmationEditText.setError(null);
                else if (!passwordConfirmationEditText.getText().toString().equals(""))
                    passwordConfirmationEditText.setError(getString(R.string.different_password));

                if (DataBaseFunctions.isPasswordValid(editable.toString()))
                    passwordEditText.setError(null);
                else
                    passwordEditText.setError(getString(R.string.invalid_password));
                result[1] = DataBaseFunctions.isPasswordValid(editable.toString());
                result[2] = editable.toString().equals(passwordConfirmationEditText.getText().toString());
                signupButton.setEnabled(result[0] && result[1] && result[2]);
            }
        });

        passwordConfirmationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean isEqual = editable.toString().equals(passwordEditText.getText().toString());

                if (isEqual)
                    passwordConfirmationEditText.setError(null);
                else
                    passwordConfirmationEditText.setError(getString(R.string.different_password));
                result[2] = isEqual;

                signupButton.setEnabled(result[0] && result[1] && result[2]);
            }
        });

        /*----------------------------------------------------------------------------------------*/
        //Signup Button:
        signupButton.setOnClickListener(view -> {
            signupButton.startAnimation(bounceAnim);

            new Handler().postDelayed(() -> {
                if (Functions.isConnectedInternet(this))
                    DataBaseFunctions.checkUsernameExists(this, this,
                            usernameEditText.getText().toString(),
                            usernameExists -> {
                                if (usernameExists) {
                                    // Show an error message to the user that the username already exists
                                    Dialogs.showErrorDialog(SignUpActivity.this, getString(R.string.account_already_exist));
                                } else {
                                    showConfirmEmailDialog(usernameEditText.getText().toString(),
                                            passwordEditText.getText().toString());
                                }
                            });
                else
                    Dialogs.showConnectionErrorDialog(this);
            }, 200);
        });

        //Back Button:
        title.setOnClickListener(view -> {
            title.startAnimation(bounceAnim);
            new Handler().postDelayed(this::onBackPressed, 100);
        });
        /*----------------------------------------------------------------------------------------*/
    }

    private void doSignup(String username, String password) {
        createUser(username, password);
    }

    private void createUser(String username, String password) {
        // Create a new user object
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(DataBaseFunctions.path);
        String userId = databaseReference.push().getKey();
        UserData user = new UserData(username, password);

        // Add the user to the database
        assert userId != null;
        databaseReference.child(userId).setValue(user, (error, ref) -> {
            if (error != null) {
                if (error.getCode() == DatabaseError.NETWORK_ERROR)
                    Dialogs.showConnectionErrorDialog(SignUpActivity.this);
                else
                    Dialogs.showErrorDialog(SignUpActivity.this, getString(R.string.generic_error));
            } else {
                Dialogs.showSuccessDialog(SignUpActivity.this, getString(R.string.success_signup), () -> {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }

    private void showConfirmEmailDialog(String username, String password) {
        Intent intent = new Intent(SignUpActivity.this, ConfirmEmailActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        ConfirmEmailActivity.setCallback(this);
        startActivity(intent);
    }

    @Override
    public void onEmailConfirmed(String username, String password) {
        // Handle the email confirmation response
        doSignup(username, password);
    }

}
