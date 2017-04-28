package bau.com.myexpensesapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bau.com.myexpensesapp.R;
import bau.com.myexpensesapp.network.ServerCommunication;

public class ExpenseActivity extends AppCompatActivity {
    private final String TAG = ExpenseActivity.class.getSimpleName();
    private TextView tvAllExpenses;
    private EditText etId;

    private BroadcastReceiver getAllExpensesResultsHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String serverResponse = intent.getStringExtra(ServerCommunication.RESPONSE_SERVER);
            int statusCode = intent.getIntExtra(ServerCommunication.RESPONSE_STATUS_CODE, -1);
            boolean success =
                    intent.getBooleanExtra(ServerCommunication.RESPONSE_SUCCESS, false);
            Log.d(TAG, "Server response: " + serverResponse + "\n" + "Status code: " + statusCode
                    + "\n" + "Success: " + success);
            if(success) {
                try {
                    JSONArray response = new JSONArray(serverResponse);
                    String result = "";
                    for(int i = 0; i < response.length(); i++){

                        JSONObject currentExpense = (JSONObject) response.get(i);
                        if (currentExpense.has("id") &&
                                currentExpense.has("amount") && currentExpense.has("concept")){
                            result += currentExpense.getInt("id") + ": " +
                                        currentExpense.getString("concept") + " " +
                                                        currentExpense.getDouble("amount") + "€\n";

                        }
                    }
                    tvAllExpenses.setText(result);

                } catch(JSONException je){
                    je.printStackTrace();
                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
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
     * registrer broadcast receivers
     */

    private void registerReceivers(){
        IntentFilter getAllExpensesResult =
                new IntentFilter(ServerCommunication.RESULTS_ACTION_INDEX_EXPENSE);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(getAllExpensesResultsHandler, getAllExpensesResult);
    }

    /**
     * Unregister broadcast receivers
     */
    private void unregisterReceivers(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(getAllExpensesResultsHandler);
    }


    private void initApp(){
        tvAllExpenses = (TextView) findViewById(R.id.tv_user_expense);
        tvAllExpenses.setMovementMethod(new ScrollingMovementMethod());
        etId = (EditText) findViewById(R.id.et_user_id);
        getAllExpenses();
    }

    private void getAllExpenses(){
        ServerCommunication.startGetAllExpenses(this);

    }

    public void openAddExpenseScreen(View view){
        Intent i = new Intent (this, AddExpenseActivity.class);
        startActivity(i);
    }
    public void deleteExpense(View view){
        ServerCommunication.startDeleteExpense(this , etId.getText().toString());
    }
}
