package bau.com.myexpensesapp.network;


import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Baurum 04-25-2017
 * An {@link IntentService} subclass for handling asynchronous server communication tasks.
 */

public class ServerCommunication extends IntentService {

    /***********************************************************************************************
     * Actions that the service can handle
     **********************************************************************************************/

    // Expenses
    private static final String ACTION_CREATE_EXPENSE = "createExpense";
    private static final String ACTION_INDEX_EXPENSE = "indexExpense";
    private static final String ACTION_GET_EXPENSE = "getExpense";
    private static final String ACTION_UPDATE_EXPENSE = "updateExpense";
    private static final String ACTION_DELETE_EXPENSE = "deleteExpense";



    /***********************************************************************************************
     * Intent name to broadcast results depending on each action
     **********************************************************************************************/

    // Expenses
    private static final String RESULTS_ACTION_CREATE_EXPENSE = "createExpenseResults";
    private static final String RESULTS_ACTION_INDEX_EXPENSE = "indexExpenseResults";
    private static final String RESULTS_ACTION_GET_EXPENSE = "getExpenseResults";
    private static final String RESULTS_ACTION_UPDATE_EXPENSE = "updateExpenseResults";
    private static final String RESULTS_ACTION_DELETE_EXPENSE = "deleteExpenseResults";


    /***********************************************************************************************
     * Parameters acceptable by the service
     **********************************************************************************************/

    private static final String PARAM_URL = "url";
    private static final String PARAM_RESPONSE_INTENT_TYPE = "responseIntentType";
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_RESOURCE = "resource";
    private static final String PARAM_JSON_OBJECT = "jsonObject";
    private static final String PARAM_CONTENT_TYPE_HEADER = "contentType";
    private static final String PARAM_WRITE_DATA = "writeData";
    private static final String PARAM_EMPTY = "";
    private static final String PARAM_RESULTS_INTENT_ACTION = "resultsIntentAction";

    /***********************************************************************************************
     * Extra parameters acceptable by the service
     **********************************************************************************************/
    // Common extra params
    private static final String EXTRA_PARAM_ID = "id";

    // Expense extra params
    private static final String EXTRA_PARAM_EXPENSE_AMOUNT = "amount";
    private static final String EXTRA_PARAM_EXPENSE_CONCEPT = "concept";

    /***********************************************************************************************
     * Server Response Intent Extras
     **********************************************************************************************/

    public static final String RESPONSE_SERVER = "ServerResponse";
    public static final String RESPONSE_STATUS_CODE = "StatusCode";
    public static final String RESPONSE_SUCCESS = "Success";

    /***********************************************************************************************
     * HTTP Methods
     **********************************************************************************************/

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PATCH = "PATCH";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";

    /***********************************************************************************************
     * Resources Available in the API
     **********************************************************************************************/

    private static final String RESOURCE_EXPENSE = "expense";

    /***********************************************************************************************
     * HTTP Headers Supported
     **********************************************************************************************/
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    /***********************************************************************************************
     * HTTP Content-Types Supported
     **********************************************************************************************/
    private static final String CONTENT_TYPE_JSON = "application/json";

    /***********************************************************************************************
     * URLs
     **********************************************************************************************/

    private static final String URL_BASE_API = "http://Ubuntu-rails-baurumtec276074.codeanyapp.com:3000/";
    private static final String URL_EXPENSES_RESOURCE = "expenses/";

    /***********************************************************************************************
     * Constants
     **********************************************************************************************/

    // LogCat
    private static final String TAG = ServerCommunication.class.getSimpleName();
    private static final String ERROR = "ERROR";
    private static final String NO_INTERNET_ERROR = "Unable to connect to the Internet";
    // Time Limit in seconds for a free spot to be valid
    private static final double TIME_LIMIT = 60 * 100000;
    private static final int TIMEOUT_LIMIT = 5000;

    /***********************************************************************************************
     * Constructor
     **********************************************************************************************/

    public ServerCommunication() {
        super("ServerCommunication");
    }

    /***********************************************************************************************
     * Methods to get the service started
     **********************************************************************************************/

    /**
     * Starts this service to perform action 'Create expense' with the given amount and concept as parameters
     * It accesses the 'expense' resource via POST method, so flags to write and read response
     * must be true.
     *
     * @see IntentService
     */

    public static void startCreateExpense(Context context, double amount, String concept) {
        // Prepare the String representation of the JSON Object to send to the server
        JSONObject jsonParent = new JSONObject();
        JSONObject jsonContent = new JSONObject();
        try{
            jsonContent.put(EXTRA_PARAM_EXPENSE_AMOUNT, amount);
            jsonContent.put(EXTRA_PARAM_EXPENSE_CONCEPT, concept);
            jsonParent.put(RESOURCE_EXPENSE, jsonContent);
        } catch (JSONException e){
            if(true) Log.d(TAG, "JSONException was raised: \n\n" + e.toString());
            // TODO: Decide what to do when a JSONException is raised
        }
        // Get the full URL
        String url = getURL(URL_BASE_API, RESOURCE_EXPENSE, PARAM_EMPTY);
        // Set up the intent
        Intent intent = prepareIntent(context, ACTION_CREATE_EXPENSE, jsonParent.toString(),
                url, METHOD_POST, CONTENT_TYPE_JSON, true, RESULTS_ACTION_CREATE_EXPENSE);
        // Fire up the service
        context.startService(intent);
    }

    /**
     * Starts this service to perform action 'Logout' with the authToken as the only parameter.
     * No need to set the Authorization header since the auth token will be sent as a parameter
     * via URL.
     *
     * E.g. DELETE https://serveraddress/api/sessions/klj324lkj324kljfd==
     *
     * @see IntentService
     */

//    public static void startLogout(Context context, String authToken) {
//        // Get the full URL
//        String url = getURL(URL_BASE_API, RESOURCE_SESSION, authToken);
//        // Set up the intent
//        Intent intent = prepareIntent(context, ACTION_LOGOUT, PARAM_EMPTY,
//                url, METHOD_DELETE, CONTENT_TYPE_JSON, authToken, false, RESULT_LOGOUT);
//        // Fire up the service
//        context.startService(intent);
//    }




    /**
     * Starts this service with the action 'Get user coupons'. It sends a GET request
     * and expects to retrieve a list of coupons.
     *
     * @param context The context that wants to call the service
     * @param authToken The authorization token
     */
//    public static void startGetUserCoupons(Context context, String authToken){
//        // Get the full URL
//        String url = getURL(URL_BASE_API, RESOURCE_COUPON, PARAM_EMPTY);
//        // Set up the intent
//        Intent intent = prepareIntent(context, ACTION_GET_USER_COUPONS, PARAM_EMPTY,
//                url, METHOD_GET, CONTENT_TYPE_JSON, authToken, false, RESULT_GET_USER_COUPONS);
//        // Fire up the service
//        context.startService(intent);
//    }

    /**
     * Starts this service with the action 'Remove invitation'. It sends a PUT request
     * and expects to set the invitation with the specified id to free.
     *
     * @param context The context that wants to call the service
     * @param invitation The invitation object
     * @param invitationId The invitation id
     * @param authToken The authorization token
     */
//    public static void startUpdateInvitation(Context context, String invitation,
//                                             String invitationId, String authToken){
//        // Get the full URL
//        String url = getURL(URL_BASE_API, RESOURCE_INVITATION, invitationId);
//        // Set up the intent
//        Intent intent = prepareIntent(context, ACTION_UPDATE_INVITATION, invitation,
//                url, METHOD_PUT, CONTENT_TYPE_JSON, authToken, true, RESULT_UPDATE_INVITATION);
//        // Fire up the service
//        context.startService(intent);
//    }



    /**
     * Starts this service with the action 'Get user'.
     *
     * @param context The context that wants to call the service
     * @param authToken The authorization token
     */
//    public static void startGetUser(Context context, String authToken){
//        // Get the full URL
//        String url = getURL(URL_BASE_API, RESOURCE_USER, authToken);
//        // Set up the intent
//        Intent intent = prepareIntent(context, ACTION_GET_USER, PARAM_EMPTY,
//                url, METHOD_GET, CONTENT_TYPE_JSON, authToken, false, RESULT_GET_USER);
//        // Fire up the service
//        context.startService(intent);
//    }



    /***********************************************************************************************
     * OnHandleIntent implementation
     **********************************************************************************************/

    /**
     * Analyzes the intent that started the service and makes the server request, analyzing the
     * response and notifying the appropriate receiver about the results.
     * @param intent
     */
    @Override
    public void onHandleIntent(Intent intent) {
        if (true) Log.i(TAG, "5. Enter onHandleIntent method");
        if (intent != null) {
            // 1) Get the extras from the intent
            Bundle extras = intent.getExtras();
            // 2) Extract relevant variables
            boolean writeData = extras.getBoolean(PARAM_WRITE_DATA);
            String method = extras.getString(PARAM_METHOD);
            String serverResponse = "";
            String resultIntentAction = extras.getString(PARAM_RESULTS_INTENT_ACTION);
            int status = 0;
            boolean success = false;

            try {
                // 3) Set the URL
                URL url = new URL(extras.getString(PARAM_URL));
                // 4) Open the Connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(TIMEOUT_LIMIT);
                // 5) Set DoOutput flag
                urlConnection.setDoOutput(writeData);
                // 6) Set request method
                urlConnection.setRequestMethod(method);
                // 7) Set headers
                urlConnection.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
                
                // 8) Write data if the flag is set to true
                if (writeData) {
                    OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                    wr.write(extras.getString(PARAM_JSON_OBJECT));
                    wr.close();
                }
                // 9) Get the status code
                status = urlConnection.getResponseCode();
                if (true) Log.i(TAG, "6. Obtained response from server");
                // 10) Check if the request was successful
                success = 200 <= status && status < 300;
                // 11) Read inputStream on success and errorStream on failure
                if (success) {
                    if (true) Log.d(TAG, "Success! Server responded with " + status + " code.");
                    serverResponse = readStream(urlConnection.getInputStream());
                } else {
                    if (true) Log.d(TAG, "Failure! Server responded with " + status + " code.");
                    serverResponse = readStream(urlConnection.getErrorStream());
                }
                if (true) Log.d(TAG, "Server Response: \n" + serverResponse);
                // 12) Close the connection
                urlConnection.disconnect();
            } catch (MalformedURLException e) {
                // TODO: Handle what happens when MalformedURLException is raised
                if(true) Log.d(TAG, "MalformedURLException was raised.");
                return;
            } catch (IOException e) {

                if(true) Log.d(TAG, "IOException was raised.");
            }



        }
    }

    /***********************************************************************************************
     * Action Handlers
     **********************************************************************************************/
    /**
     * Finishes up the service by broadcasting the result to the right receiver.
     *
     * @param intentAction The intent action that will be filtered by the right receiver.
     * @param serverResponse The server response
     * @param statusCode The server status code response
     */
    private void broadcastResult(String intentAction, String serverResponse,
                                 int statusCode, boolean success){
        if (true) Log.i(TAG, "7. Enter broadcastResult method");
        Intent i = new Intent(intentAction);
        i.putExtra(RESPONSE_SERVER , serverResponse);
        i.putExtra(RESPONSE_STATUS_CODE, statusCode);
        i.putExtra(RESPONSE_SUCCESS, success);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    /***********************************************************************************************
     * Helper Methods
     **********************************************************************************************/

    /**
     * This method configures the intent that will fire up the service. Through all these parameters
     * we can customize the intent to perform any desired request.
     *
     * @param context The context where the service needs to be started from
     * @param action The action to perform
     * @param jsonObject The JSON Object to send to the server
     * @param url The URL we want to access
     * @param method The HTTP Method we want to use
     * @param contentTypeHeader The content type header we are going to use
     * @param writeData A boolean indicating whether we need to send data to the server or just read
     * @param resultIntentAction The Intent Name the proper broadcast receiver is listening for
     * @return A fully configured intent that will fire up the service and broadcast the results
     *              to the right broadcast receiver
     */
    private static Intent prepareIntent(Context context, String action, String jsonObject, String url,
                                        String method, String contentTypeHeader,
                                        boolean writeData, String resultIntentAction){
        if (true) Log.i(TAG, "4. Enter prepareIntent method");
        // Set up the intent
        Intent intent = new Intent(context, ServerCommunication.class);
        // Set action
        intent.setAction(action);
        // Set the JSON Object String representation
        intent.putExtra(PARAM_JSON_OBJECT, jsonObject);
        // Set URL
        intent.putExtra(PARAM_URL, url);
        // Set Method
        intent.putExtra(PARAM_METHOD, method);
        // Set Headers
        intent.putExtra(PARAM_CONTENT_TYPE_HEADER, contentTypeHeader);
        // Set Flags
        intent.putExtra(PARAM_WRITE_DATA, writeData);
        // Set the name of the intent that will be broadcast with the results
        intent.putExtra(PARAM_RESULTS_INTENT_ACTION, resultIntentAction);
        // Return the intent
        return intent;
    }

    /**
     * Returns the full URL needed to make the server request.
     * @param server The server address
     * @param resource The resource we want to access
     * @param params The parameters we need
     * @return
     */
    private static String getURL(String server, String resource, String params){
        String resourceUrl;
        switch(resource){
            case RESOURCE_EXPENSE:
                resourceUrl = URL_EXPENSES_RESOURCE;
                break;
            default:
                resourceUrl = ERROR;
                break;
        }
        return server + resourceUrl + params;
    }

    /**
     * Converts a certain response into a readable String
     * @param in The InputStream we want to read
     * @return The readable string
     */
    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder data = new StringBuilder("");
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (NullPointerException npe){
            if(true) Log.d(TAG, "Null Pointer Exception caught while reading server response...");
            npe.printStackTrace();
        } catch (IOException e) {
            if(true) Log.d(TAG, "IOException");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }



}
