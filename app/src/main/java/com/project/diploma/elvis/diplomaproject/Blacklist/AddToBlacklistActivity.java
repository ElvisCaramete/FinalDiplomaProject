package com.project.diploma.elvis.diplomaproject.Blacklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.diploma.elvis.diplomaproject.R;
import com.project.diploma.elvis.diplomaproject.Utils.DatabaseUtility_Blacklist;

/**
 * Created by Elvis on 1/1/2018.
 */

public class AddToBlacklistActivity extends Activity implements OnClickListener {

    // Declaration all on screen components
    private EditText name_et, phone_et;
    private Button reset_btn, submit_btn;

    // Declaration of DatabaseUtility to interact with SQlite database
    private DatabaseUtility_Blacklist blacklist_utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blacklist);

        // Initialization of the DAO object.
        blacklist_utils = new DatabaseUtility_Blacklist(this);

        name_et = (EditText) findViewById(R.id.name_et);
        phone_et = (EditText) findViewById(R.id.phone_et);
        reset_btn = (Button) findViewById(R.id.reset_btn);
        submit_btn = (Button) findViewById(R.id.submit_btn);

        reset_btn.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == submit_btn) {
            // All input fields are mandatory, so made a check
            if (name_et.getText().toString().trim().length() > 0 &&
                    phone_et.getText().toString().trim().length() > 0) {
                // Once click on "Submit", it's first creates the blacklist object
                final Blacklist phone = new Blacklist();

                // Then, set all the values from user input
                phone.name = name_et.getText().toString();
                phone.phoneNumber = phone_et.getText().toString();


                blacklist_utils.create(phone);

                // Show the success message to user
                showDialog();
            }
            // Show a dialog with appropriate message in case input fields are blank
            else {
                showMessageDialog("All fields are mandatory !!");
            }
        } else if (v == reset_btn) {
            reset();
        }
    }

    // Clear the entered text
    private void reset() {
        name_et.setText("");
        phone_et.setText("");
    }

    private void showDialog() {
        Toast.makeText(getApplicationContext(),"Added succesfully to blacklist!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showMessageDialog(final String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
