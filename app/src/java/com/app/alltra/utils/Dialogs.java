package com.app.alltra.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.alltra.R;
import com.app.alltra.database.DataBaseFunctions;

public class Dialogs {

    public static void showErrorDialog(Context context, String message) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Dialog dialog = new Dialog(context, R.style.DialogAnimation);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_error);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button tryAgainButton = dialog.findViewById(R.id.tryAgain);
        TextView errorMessage = dialog.findViewById(R.id.message);

        errorMessage.setText(message);

        tryAgainButton.setOnClickListener(view -> {
            tryAgainButton.startAnimation(bounceAnim);
            dialog.dismiss();
            //new Handler().postDelayed(dialog::dismiss, 150);
        });

        dialog.show();
    }

    public static void showUnderConstructionDialog(Context context) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Dialog dialog = new Dialog(context, R.style.DialogAnimation);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_underconstruction);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button tryAgainButton = dialog.findViewById(R.id.tryAgain);

        tryAgainButton.setOnClickListener(view -> {
            tryAgainButton.startAnimation(bounceAnim);
            dialog.dismiss();
            //new Handler().postDelayed(dialog::dismiss, 150);
        });

        dialog.show();
    }

    public static void showDemoDialog(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
            final Dialog dialog = new Dialog(context, R.style.DialogAnimation);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_demo);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button tryAgainButton = dialog.findViewById(R.id.tryAgain);

            tryAgainButton.setOnClickListener(view -> {
                tryAgainButton.startAnimation(bounceAnim);
                dialog.dismiss();
            });

            dialog.show();

            // Update shared preferences to indicate that the dialog has been shown
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();
        }
    }

    public static void showSuccessDialog(Context context, String message,OnDialogButtonClickListener listener) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Dialog dialog = new Dialog(context, R.style.DialogAnimation);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button ok = dialog.findViewById(R.id.ok);
        TextView errorMessage = dialog.findViewById(R.id.message);

        errorMessage.setText(message);

        ok.setOnClickListener(view -> {
            ok.startAnimation(bounceAnim);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onDialogButtonClick();
                }
            }, 150);
        });

        dialog.show();
    }

    public static void showConnectionErrorDialog(Context context) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Dialog dialog = new Dialog(context, R.style.DialogAnimation);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_connection_error);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button tryAgainButton = dialog.findViewById(R.id.tryAgain);

        tryAgainButton.setOnClickListener(view -> {
            tryAgainButton.startAnimation(bounceAnim);
            new Handler().postDelayed(dialog::dismiss, 150);
        });

        dialog.show();
    }

    public static void showCreateNewPasswordDialog(Context context, OnDialogCreateNewPasswordClickListener listener) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Dialog dialog = new Dialog(context, R.style.DialogAnimation);
        final boolean[] result = {false, false};

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_new_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button ok = dialog.findViewById(R.id.ok);
        EditText passwordEditText = dialog.findViewById(R.id.password);
        EditText passwordConfirmationEditText = dialog.findViewById(R.id.password2);

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
                    passwordConfirmationEditText.setError(context.getString(R.string.different_password));

                if (DataBaseFunctions.isPasswordValid(editable.toString()))
                    passwordEditText.setError(null);
                else
                    passwordEditText.setError(context.getString(R.string.invalid_password));

                result[0] = DataBaseFunctions.isPasswordValid(editable.toString());
                result[1] = editable.toString().equals(passwordConfirmationEditText.getText().toString());

                ok.setEnabled(result[0] && result[1]);
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
                    passwordConfirmationEditText.setError(context.getString(R.string.different_password));
                result[1] = isEqual;

                ok.setEnabled(result[0] && result[1]);
            }
        });

        ok.setOnClickListener(view -> {
            ok.startAnimation(bounceAnim);
            new Handler().postDelayed(() -> {
                if (listener != null) {
                    listener.onDialogCreateNewPasswordClickListener(passwordEditText.getText().toString(), dialog);
                }
            }, 150);
        });

        dialog.show();
    }

    public static void showGetEmailDialog(Context context, String email, OnDialogButtonClickListenerString listener) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Dialog dialog = new Dialog(context, R.style.DialogAnimation);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_get_email);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button ok = dialog.findViewById(R.id.ok);
        EditText usernameEditText = dialog.findViewById(R.id.username);

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
                    usernameEditText.setError(context.getString(R.string.invalid_username));
                ok.setEnabled(DataBaseFunctions.isUsernameValid(editable.toString()));
            }
        });

        if(!email.equals(""))
            usernameEditText.setText(email);

        ok.setOnClickListener(view -> {
            ok.startAnimation(bounceAnim);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                if (listener != null) {
                    listener.onDialogButtonClickListenerString(usernameEditText.getText().toString());
                }
            }, 150);
        });

        dialog.show();
    }

    public interface OnDialogButtonClickListener {
        void onDialogButtonClick();
    }

    public interface OnDialogButtonClickListenerString {
        void onDialogButtonClickListenerString(String s);
    }

    public interface OnDialogCreateNewPasswordClickListener {
        void onDialogCreateNewPasswordClickListener(String newPassword, Dialog dialog);
    }
}
