package com.app.alltra.database;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.app.alltra.R;
import com.app.alltra.utils.Dialogs;
import com.app.alltra.utils.LottieDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class DataBaseFunctions {
    /**
     * Class containing utility functions for several use cases in database
     */

    public static final String path = "usuarios";

    /*--------------------------------------------------------------------------------------------*/
    //Checks username and password:
    public static boolean isUsernameValid(String username) {
        if (username == null) {
            return false;
        }else
            return android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 5;
    }
    /*--------------------------------------------------------------------------------------------*/
    public static void checkUsernameExists(Activity activity, Context context, String username, MethodCallback listener) {
        LottieDialog.startLoadingDialog1(activity);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LottieDialog.dismissDialog();
                boolean usernameExists = false;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    UserData user = childSnapshot.getValue(UserData.class);
                    if (user != null && user.getEmail().equals(username)) {
                        usernameExists = true;
                        break;
                    }
                }
                listener.onCallback(usernameExists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Dialogs.showErrorDialog(context, context.getString(R.string.generic_error));
            }
        });
    }

    public static void changePassword(Activity activity, Context context, String email, String newPassword, MethodCallback callback){
        LottieDialog.startLoadingDialog1(activity);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        // Cria uma consulta para buscar a chave do usuário pelo email
        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LottieDialog.dismissDialog();
                // Verifica se a consulta retornou algum resultado
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    String userKey = userSnapshot.getKey();

                    if (userKey != null) {
                        // Atualiza a senha do usuário
                        try {
                            databaseReference.child(userKey).child("senha").setValue(newPassword);
                            Dialogs.showSuccessDialog(context, context.getString(R.string.changed_password), null);
                            callback.onCallback(true);
                        } catch (Exception e) {
                            Dialogs.showErrorDialog(context, context.getString(R.string.generic_error));
                            callback.onCallback(false);
                        }
                    } else {
                        // Usuário não encontrado
                        Dialogs.showErrorDialog(context, context.getString(R.string.error_login));
                        callback.onCallback(false);
                    }
                } else {
                    // Usuário não encontrado
                    Dialogs.showErrorDialog(context, context.getString(R.string.error_login));
                    callback.onCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Trata erros de banco de dados
                Dialogs.showErrorDialog(context, context.getString(R.string.generic_error));
                callback.onCallback(false);
            }
        });
    }

    public interface MethodCallback {
        void onCallback(boolean bool);
    }

}
