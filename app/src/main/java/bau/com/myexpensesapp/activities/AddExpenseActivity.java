package bau.com.myexpensesapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import bau.com.myexpensesapp.R;
import bau.com.myexpensesapp.network.ServerCommunication;

public class AddExpenseActivity extends AppCompatActivity {
    private final String TAG = AddExpenseActivity.class.getSimpleName();
    private EditText etAmount;
    private EditText etConcept;

    /**
     * Broadcast receiver for start create expense request
     */

    private BroadcastReceiver createExpenseResultsHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String serverResponse = intent.getStringExtra(ServerCommunication.RESPONSE_SERVER);
            int statusCode = intent.getIntExtra(ServerCommunication.RESPONSE_STATUS_CODE, -1);
            boolean success =
                    intent.getBooleanExtra(ServerCommunication.RESPONSE_SUCCESS, false);
            Log.d(TAG, "Server response: " + serverResponse + "\n" + "Status code: " + statusCode
            + "\n" + "Success: " + success);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        initApp();

    }

    /**
     * Method activity lifecycle on start
     *
     */
    @Override
    protected void onStart(){
        super.onStart();
        registerReceivers();
    }
    /**
     * Method activity lifecycle on stop
     *
     */
    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceivers();
    }


    /**
     *  Method to init app
     */

    private void initApp(){
        etAmount = (EditText) findViewById(R.id.et_user_amount);
        etConcept = (EditText) findViewById(R.id.et_user_concept);

    }

    /**
     * registrer broadcast receivers
     */

    private void registerReceivers(){
        IntentFilter getCreateExpenseResults =
                new IntentFilter(ServerCommunication.RESULTS_ACTION_CREATE_EXPENSE);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(createExpenseResultsHandler, getCreateExpenseResults);
    }

    /**
     * Unregister broadcast receivers
     */
    private void unregisterReceivers(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(createExpenseResultsHandler);
    }


    /**
     * Method to create a new Expensive
     * @param view
     */
    public void createExpense(View view){
        if (etConcept.getText().toString().equals("")){
            etConcept.setError("Write a concept");
        } else if (etAmount.getText().toString().equals("")){
            etAmount.setError("Add an amount");
        } else {
            Intent i = new Intent(this, ExpenseActivity.class);
            startActivity(i);
            ServerCommunication.startCreateExpense
                    (this, Double.parseDouble(etAmount.getText().toString()),
                            etConcept.getText().toString());
            finish();
        }
    }

}
