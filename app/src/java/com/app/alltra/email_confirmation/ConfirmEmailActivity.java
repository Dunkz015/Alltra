package com.app.alltra.email_confirmation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.alltra.R;
import com.app.alltra.utils.JavaMailAPI;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfirmEmailActivity extends AppCompatActivity {

    private static ConfirmEmailCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_email);

        // Get username and password from intent extras
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        AtomicInteger token = new AtomicInteger(generateToken()); // To run inside lambda function
        sendMail(token.get(), username);

        Button resendEmail = findViewById(R.id.resendEmail);
        EditText token1 = findViewById(R.id.token1);
        EditText token2 = findViewById(R.id.token2);
        EditText token3 = findViewById(R.id.token3);
        EditText token4 = findViewById(R.id.token4);

        List<EditText> editTextList = Arrays.asList(token1, token2, token3, token4);

        for (int i = 0; i < editTextList.size(); i++) {
            EditText currentEditText = editTextList.get(i);
            int finalI = i;

            currentEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = clipboard.getPrimaryClip();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        try {
                            CharSequence text = clipData.getItemAt(0).getText();
                            if (Integer.parseInt(text.toString()) == token.get()){
                                // Get the input method manager
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                // Hide the soft keyboard
                                imm.hideSoftInputFromWindow(currentEditText.getWindowToken(), 0);

                                if (callback != null)
                                    callback.onEmailConfirmed(username, password);
                                finish();
                            }
                        }catch (Exception ignored) {}
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String number = token1.getText().toString() + token2.getText().toString() +
                            token3.getText().toString() + token4.getText().toString();
                    if (number.length() == 4) {
                        checkToken(number, token, currentEditText, username, password);
                    } else if (!editable.toString().equals("") && currentEditText != token4) {
                        EditText nextEditText = editTextList.get(finalI + 1);
                        nextEditText.requestFocus();
                    }
                }
            });
        }

        long timerLengthInMillis = 30000;
        resendEmail.setOnClickListener(view -> {
            resendEmail.startAnimation(bounceAnim);
            token.set(generateToken());
            sendMail(token.get(), username);

            resendEmail.setEnabled(false);

            // Create the countdown timer
            new CountDownTimer(timerLengthInMillis, 1000) {

                // This method will be called every second until the timer finishes
                public void onTick(long millisUntilFinished) {
                    // Update the button's text with the remaining time
                    String buttonText = millisUntilFinished / 1000 + "s";
                    resendEmail.setText(buttonText);
                }

                // This method will be called when the timer finishes
                public void onFinish() {
                    // Enable the resend email button and reset its text
                    resendEmail.setEnabled(true);
                    resendEmail.setText(getString(R.string.resend_email));
                }
            }.start();
        });
    }

    private void checkToken(String number, AtomicInteger token, EditText editText, String username, String password){
        final Animation bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        TextView error = findViewById(R.id.error);

        if (Integer.parseInt(number) == token.get()) {
            //Correct code
            new Handler().postDelayed(() -> {
                // Get the input method manager
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // Hide the soft keyboard
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                if (callback != null)
                    callback.onEmailConfirmed(username, password);
                finish();
            }, 500);
        } else {
            if (error.getVisibility() == View.GONE) {
                error.setVisibility(View.VISIBLE);
                error.startAnimation(bounceIn);
            }
        }
    }

    private int generateToken() {
        Random random = new Random();
        return random.nextInt(8999) + 1000;
    }

    private void sendMail(int token, String email) {
        String subject = "Token Alltra";
        String body = getString(R.string.email_token_1) + token + getString(R.string.email_token_2);
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, body);
        javaMailAPI.sendEmail();
    }

    public static void setCallback(ConfirmEmailCallback callback) {
        ConfirmEmailActivity.callback = callback;
    }

    public interface ConfirmEmailCallback {
        void onEmailConfirmed(String username, String password);
    }
}
