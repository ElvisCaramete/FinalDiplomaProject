package com.project.diploma.elvis.diplomaproject.Whitelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.diploma.elvis.diplomaproject.Utils.DatabaseUtility;
import com.project.diploma.elvis.diplomaproject.R;

/**
 * Created by Elvis on 1/1/2018.
 */

public class AddToWhitelistActivity extends Activity implements OnClickListener {

    // Declaration all on screen components
    private EditText name_et, phone_et;
    private Button reset_btn, submit_btn;

    // Declaration of DatabaseUtility to interact with SQlite database
    private DatabaseUtility whitelist_utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Initialization of the DAO object.
        whitelist_utils = new DatabaseUtility(this);

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
                // Once click on "Submit", it's first creates the Whitelist object
                final Whitelist phone = new Whitelist();

                // Then, set all the values from user input
                phone.name = name_et.getText().toString();
                phone.phoneNumber = phone_et.getText().toString();


                whitelist_utils.create(phone);

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
        Toast.makeText(getApplicationContext(),"Added succesfully to whitelist!", Toast.LENGTH_SHORT).show();
        finish();
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage("Phone Number added to whitelist successfully !!");
//
//        // Add a positive button and it's action. In our case action would be, just hide the dialog box ,
//        // and erase the user inputs.
//        alertDialogBuilder.setPositiveButton("Add More",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        reset();
//                    }
//                });
//
//        // Add a negative button and it's action. In our case, close the current screen
//        alertDialogBuilder.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
    }

    private void showMessageDialog(final String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
