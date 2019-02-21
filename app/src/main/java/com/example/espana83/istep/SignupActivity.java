package com.example.espana83.istep;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProvider;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SignupActivity extends Activity {

    private static CognitoUserPool userPool;

    private EditText username, password, name, email;
    private Button signUpButton;
    private static final String userPoolId = "eu-west-1_BOCAbGMPe";
    private static final String clientId = "574hb7retiaoe801scj3tlpcjt";
    private static final String clientSecret = "mb6td7hni7ht09q8onoto51sml34r3sidli46nsvistfrn7ucgj";
    private static final Regions cognitoRegion = Regions.EU_WEST_1;
    private static final String FILENAME = "userDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = (EditText) findViewById(R.id.signUpUsername);
        password = (EditText) findViewById(R.id.signUpPassword);
        name = (EditText) findViewById(R.id.signUpGivenName);
        email = (EditText) findViewById(R.id.signUpEmail);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClientConfiguration clientConfiguration = new ClientConfiguration();

                AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
                cipClient.setRegion(Region.getRegion(cognitoRegion));

                // Create a CognitoUserPool object to refer to your user pool
                userPool = new CognitoUserPool(v.getContext(), userPoolId, clientId, clientSecret, cipClient);
                // Create a CognitoUserAttributes object and add user attributes
                CognitoUserAttributes userAttributes = new CognitoUserAttributes();

                // Add the user attributes. Attributes are added as key-value pairs
                // Adding user's given name.
                // Note that the key is "given_name" which is the OIDC claim for given name
                userAttributes.addAttribute("given_name", name.getText().toString());

                // Adding user's email address
                userAttributes.addAttribute("email", email.getText().toString());

                userPool.signUpInBackground(username.getText().toString(), password.getText().toString(), userAttributes, null, signupCallback);
            }
        });
    }

    SignUpHandler signupCallback = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Sign-up was successful
            writeDetails();
            cognitoUser.getSessionInBackground(authenticationHandler);
            // Sign in the user
            if(userConfirmed) {
            }
            else {

            }
        }

        @Override
        public void onFailure(Exception exception) {
            Toast.makeText(getApplication(), formatException(exception), Toast.LENGTH_LONG).show();
            // Sign-up failed, check exception for the cause
        }
    };

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e("App Error",exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if(temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return  formattedString;
    }

    // method that writes a value to a file
    private void writeDetails() {

        ObjectOutputStream outputStream = null;
        try {
            // create Android FileOutputStream, private file for this app only
            FileOutputStream fos = openFileOutput(FILENAME,
                    Context.MODE_PRIVATE);
            // Construct the output stream
            outputStream = new ObjectOutputStream(fos);
            outputStream.writeUTF(username.getText().toString());
            outputStream.writeUTF(password.getText().toString());
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            // Close the ObjectOutputStream
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    // Callback handler for the sign-in process
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession) {
            // Sign-in was successful, cognitoUserSession will contain tokens for the user
            Toast.makeText(getApplication(), "Sign up successful", Toast.LENGTH_LONG).show();
            Intent goToMain = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(goToMain);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(username.getText().toString(), password.getText().toString(), null);

            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);

            // Allow the sign-in to continue
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            // Allow the sign-in process to continue
            multiFactorAuthenticationContinuation.continueTask();
        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-in failed, check exception for the cause
            Toast.makeText(getApplication(), formatException(exception), Toast.LENGTH_LONG).show();
        }
    };
}
