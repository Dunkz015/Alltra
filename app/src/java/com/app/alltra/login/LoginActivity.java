package com.app.alltra.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.alltra.R;
import com.app.alltra.database.DataBaseFunctions;
import com.app.alltra.database.UserData;
import com.app.alltra.email_confirmation.ConfirmEmailActivity;
import com.app.alltra.home.HomeActivity;
import com.app.alltra.signup.SignUpActivity;
import com.app.alltra.utils.Dialogs;
import com.app.alltra.utils.Functions;
import com.app.alltra.utils.LottieDialog;
import com.app.alltra.utils.MultipleClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements ConfirmEmailActivity.ConfirmEmailCallback {

    private static Context appContext;
    private final boolean[] result = {false, false};

    public static void logout() {
        SharedPreferences preferences = appContext.getSharedPreferences("checkbox", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.apply();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_login);
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        CheckBox rememberme = findViewById(R.id.rememberme);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        TextView createAccount = findViewById(R.id.createAccount);
        Button loginButton = findViewById(R.id.login);

        // Set the appContext field to the application context
        appContext = getApplicationContext();

        /*----------------------------------------------------------------------------------------*/
        // Checking remember me:
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String remember = preferences.getString("remember", "");
        if (remember.equals("true")) {
            if (Functions.isConnectedInternet(this)) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                new Handler().postDelayed(() -> {
                    logout();
                    Dialogs.showConnectionErrorDialog(this);
                }, 500);
            }
        }

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
                loginButton.setEnabled(result[0] && result[1]);
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
                if (DataBaseFunctions.isPasswordValid(editable.toString()))
                    passwordEditText.setError(null);
                else
                    passwordEditText.setError(getString(R.string.invalid_password));
                result[1] = DataBaseFunctions.isPasswordValid(editable.toString());
                loginButton.setEnabled(result[0] && result[1]);
            }
        });

        /*----------------------------------------------------------------------------------------*/
        //login Button:
        loginButton.setOnClickListener(view -> {
            loginButton.startAnimation(bounceAnim);

            new Handler().postDelayed(() -> {
                if (Functions.isConnectedInternet(this)) {
                    doLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());

                    SharedPreferences preference = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    if (rememberme.isChecked())
                        editor.putString("remember", "true");
                    else
                        editor.putString("remember", "false");
                    editor.apply();
                } else
                    Dialogs.showConnectionErrorDialog(this);
            }, 200);
        });

        //Remember me checkbox:
        rememberme.setOnCheckedChangeListener((compoundButton, b) -> rememberme.startAnimation(bounceAnim));

        // Forgot password
        forgotPassword.setOnClickListener(new MultipleClickListener() {
            @Override
            public void onDoubleClick() {
                //do nothing
            }

            @Override
            public void onSingleClick() {
                forgotPassword.startAnimation(bounceAnim);
                new Handler().postDelayed(() -> {
                    if (Functions.isConnectedInternet(LoginActivity.this))
                        Dialogs.showGetEmailDialog(LoginActivity.this, usernameEditText.getText().toString(), email ->
                                DataBaseFunctions.checkUsernameExists(LoginActivity.this, LoginActivity.this, email, usernameExists -> {
                                    if (!usernameExists) {
                                        // Show an error message to the user that the username does not exists
                                        Dialogs.showErrorDialog(LoginActivity.this, getString(R.string.account_does_not_exist));
                                    } else
                                        showConfirmEmailDialog(email);
                                }));
                    else
                        Dialogs.showConnectionErrorDialog(LoginActivity.this);
                }, 200);
            }
        });

        //Create new account TextView:
        createAccount.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            createAccount.startAnimation(bounceAnim);
            new Handler().postDelayed(() -> startActivity(intent), 100);
        });
        /*----------------------------------------------------------------------------------------*/
    }

    private void doLogin(String username, String password) {
        Intent intent = new Intent(this, HomeActivity.class);
        LottieDialog.startLoadingDialog1(this);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(DataBaseFunctions.path);

        // Cria uma consulta para buscar o usuário pelo email
        Query query = databaseReference.orderByChild("email").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LottieDialog.dismissDialog();
                // Verifica se a consulta retornou algum resultado
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    UserData usuario = userSnapshot.getValue(UserData.class);
                    // id do usuário no bd
                    String id = userSnapshot.getKey();

                    if (usuario != null && usuario.getSenha().equals(password)) {
                        // Login bem sucedido
                        //Toast.makeText(LoginActivity.this, "Login bem sucedido!", Toast.LENGTH_SHORT).show();

                        // Pega a referência do Firebase Realtime Database apontando para a chave do usuário
                        assert id != null;
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(DataBaseFunctions.path).child(id);

                        // Adiciona um listener para ler os dados do usuário
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Verifica se a chave existe no banco de dados
                                if (dataSnapshot.exists()) {
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Chave não encontrada no banco de dados
                                    Dialogs.showErrorDialog(LoginActivity.this, getString(R.string.error_login));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Trata erros de banco de dados
                                Dialogs.showErrorDialog(LoginActivity.this, getString(R.string.generic_error));
                            }
                        });
                    } else {
                        // Senha incorreta
                        Dialogs.showErrorDialog(LoginActivity.this, getString(R.string.error_login));
                    }
                } else {
                    // Usuário não encontrado
                    Dialogs.showErrorDialog(LoginActivity.this, getString(R.string.error_login));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Trata erros de banco de dados
                Dialogs.showErrorDialog(LoginActivity.this, getString(R.string.generic_error));
            }
        });

    }

    private void forgotPassword(String username) {
        Dialogs.showCreateNewPasswordDialog(this, (newPassword, dialog) -> DataBaseFunctions.changePassword(this, this, username, newPassword, bool -> {
            if (bool)
                dialog.dismiss();
        }));
    }

    private void showConfirmEmailDialog(String username) {
        Intent intent = new Intent(LoginActivity.this, ConfirmEmailActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", "");
        ConfirmEmailActivity.setCallback(this);
        startActivity(intent);
    }

    @Override
    public void onEmailConfirmed(String username, String password) {
        // Handle the email confirmation response
        forgotPassword(username);
    }
}
