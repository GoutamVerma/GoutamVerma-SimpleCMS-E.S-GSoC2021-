package com.lglab.goutam.simple_cms.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.top_bar.TobBarActivity;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;

public class LogIn extends TobBarActivity {

    public static final String TAG_DEBUG = "LOGIN";

    private Button buttAccount, buttLogIn, buttChangePassword, buttUpdatePassword, buttLogOut;
    private TextView adminPassword, currentAdminPassword, newAdminPassword, confirmAdminPassword;
    private EditText textAdminPassword, textCurrentAdminPassword, textNewAdminPassword, textConfirmAdminPassword;
    private ImageView imageLogo, imageView3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_log_in);

        View topBar = findViewById(R.id.top_bar);
        buttAccount = topBar.findViewById(R.id.butt_account);

        adminPassword = findViewById(R.id.admin_password);
        currentAdminPassword = findViewById(R.id.current_admin_password);
        newAdminPassword = findViewById(R.id.new_admin_password);
        confirmAdminPassword = findViewById(R.id.confirm_new_admin_password);

        textAdminPassword = findViewById(R.id.text_name);
        textCurrentAdminPassword = findViewById(R.id.text_current_admin_password);
        textNewAdminPassword = findViewById(R.id.text_new_admin_password);
        textConfirmAdminPassword = findViewById(R.id.text_confirm_new_admin_password);

        buttLogIn = findViewById(R.id.butt_log_in);
        buttChangePassword = findViewById(R.id.butt_change_password);
        buttUpdatePassword = findViewById(R.id.butt_update_password);
        buttLogOut = findViewById(R.id.butt_logout);

        imageLogo = findViewById(R.id.image_logo);
        imageView3 = findViewById(R.id.imageView3);

        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        if(sharedPreferences.getBoolean(ConstantsLogInLogOut.IS_LOGIN.name(), false)){
            setLogIN();
        }

        buttLogIn.setOnClickListener((view) -> logIn());
        buttChangePassword.setOnClickListener((view -> changePassword()));
        buttUpdatePassword.setOnClickListener((view) -> updatePassword());
        buttLogOut.setOnClickListener((view) -> logOut());


        changeButtonClickableBackgroundColor();
    }


    /**
     * Log in in to the application
     */
    private void logIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        String passwordSaved = sharedPreferences.getString(ConstantsLogInLogOut.LOGIN.name(), "lg");
        String password = textAdminPassword.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(password.equals(passwordSaved)){
            editor.putBoolean(ConstantsLogInLogOut.IS_LOGIN.name(), true);
            setLogIN();
            CustomDialogUtility.showDialog(LogIn.this,  getResources().getString(R.string.message_log_in_success));
        }else{
            editor.putBoolean(ConstantsLogInLogOut.IS_LOGIN.name(), false);
            CustomDialogUtility.showDialog(LogIn.this,  getResources().getString(R.string.message_log_in_fail));
        }
        editor.apply();
    }

    /**
     * Set the UI log in
     */
    private void setLogIN() {
        adminPassword.setVisibility(View.INVISIBLE);
        textAdminPassword.setVisibility(View.INVISIBLE);
        buttLogIn.setVisibility(View.INVISIBLE);
        buttChangePassword.setVisibility(View.INVISIBLE);
        currentAdminPassword.setVisibility(View.INVISIBLE);
        textCurrentAdminPassword.setVisibility(View.INVISIBLE);
        newAdminPassword.setVisibility(View.INVISIBLE);
        textNewAdminPassword.setVisibility(View.INVISIBLE);
        confirmAdminPassword.setVisibility(View.INVISIBLE);
        textConfirmAdminPassword.setVisibility(View.INVISIBLE);
        buttUpdatePassword.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageLogo.setVisibility(View.VISIBLE);
        buttLogOut.setVisibility(View.VISIBLE);
    }

    /**
     * Set the UI for changing the password
     */
    private void changePassword() {
        adminPassword.setVisibility(View.INVISIBLE);
        textAdminPassword.setVisibility(View.INVISIBLE);
        buttLogIn.setVisibility(View.INVISIBLE);
        buttChangePassword.setVisibility(View.INVISIBLE);

        currentAdminPassword.setVisibility(View.VISIBLE);
        textCurrentAdminPassword.setVisibility(View.VISIBLE);
        newAdminPassword.setVisibility(View.VISIBLE);
        textNewAdminPassword.setVisibility(View.VISIBLE);
        confirmAdminPassword.setVisibility(View.VISIBLE);
        textConfirmAdminPassword.setVisibility(View.VISIBLE);
        buttUpdatePassword.setVisibility(View.VISIBLE);
    }

    /**
     * Update Password
     */
    private void updatePassword() {

        String currentPassword = textCurrentAdminPassword.getText().toString();
        String newPassword = textNewAdminPassword.getText().toString();
        String confirmPassword = textConfirmAdminPassword.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        String passwordSaved = sharedPreferences.getString(ConstantsLogInLogOut.LOGIN.name(), "lg");
        if(passwordSaved.equals(currentPassword)){
            if(newPassword.equals(confirmPassword)){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ConstantsLogInLogOut.LOGIN.name(), confirmPassword);
                editor.apply();
                setTextEditUpdatePassword();
                CustomDialogUtility.showDialog(LogIn.this,
                        getResources().getString(R.string.message_your_password_update));
            }else{
                CustomDialogUtility.showDialog(LogIn.this,
                        getResources().getString(R.string.message_new_confirm_password_not_equal));
            }
        }else{
            CustomDialogUtility.showDialog(LogIn.this,
                    getResources().getString(R.string.message_current_password_is_wrong));
        }
    }

    /**
     * Set the UI for update the password
     */
    private void setTextEditUpdatePassword() {
        adminPassword.setVisibility(View.VISIBLE);
        textAdminPassword.setVisibility(View.VISIBLE);
        buttLogIn.setVisibility(View.VISIBLE);
        buttChangePassword.setVisibility(View.VISIBLE);

        currentAdminPassword.setVisibility(View.INVISIBLE);
        textCurrentAdminPassword.setVisibility(View.INVISIBLE);
        newAdminPassword.setVisibility(View.INVISIBLE);
        textNewAdminPassword.setVisibility(View.INVISIBLE);
        confirmAdminPassword.setVisibility(View.INVISIBLE);
        textConfirmAdminPassword.setVisibility(View.INVISIBLE);
        buttUpdatePassword.setVisibility(View.INVISIBLE);
    }

    /**
     * Log out of the application
     */
    private void logOut() {
        SharedPreferences.Editor editor = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE).edit();
        editor.putBoolean(ConstantsLogInLogOut.IS_LOGIN.name(), false);
        editor.apply();
        setLogOut();
    }

    /**
     * Set the UI log out
     */
    private void setLogOut() {
        adminPassword.setVisibility(View.VISIBLE);
        textAdminPassword.setVisibility(View.VISIBLE);
        buttLogIn.setVisibility(View.VISIBLE);
        buttChangePassword.setVisibility(View.VISIBLE);
        imageView3.setVisibility(View.VISIBLE);
        imageLogo.setVisibility(View.INVISIBLE);
        buttLogOut.setVisibility(View.INVISIBLE);
    }


    /**
     * Change the background color and the option clickable to false of the button_connect
     */
    private void changeButtonClickableBackgroundColor() {
        changeButtonClickableBackgroundColor(getApplicationContext(), buttAccount);
    }
}
