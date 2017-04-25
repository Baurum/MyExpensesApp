package bau.com.myexpensesapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import bau.com.myexpensesapp.R;

public class AddExpenseActivity extends AppCompatActivity {
    public EditText etAmount;
    public EditText etConcept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
    }

    /**
     *  Method to init app
     */

    private void initApp(){
        etAmount = (EditText) findViewById(R.id.et_user_amount);
        etConcept = (EditText) findViewById(R.id.et_user_concept);

    }

    public void createExpense(View view){
        if (etConcept.getText().toString().equals("")){
            etConcept.setError("Write a concept");
        }
        if (etAmount.getText().toString().equals("")){
            etAmount.setError("Add an amount");
        }
    }
}
