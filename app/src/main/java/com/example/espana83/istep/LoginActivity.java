package com.example.espana83.istep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProvider;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    private EditText username, password;
    private TextView link;
    private Button loginButton;
    private static final String userPoolId = "eu-west-1_BOCAbGMPe";
    private static final String clientId = "574hb7retiaoe801scj3tlpcjt";
    private static final String clientSecret = "mb6td7hni7ht09q8onoto51sml34r3sidli46nsvistfrn7ucgj";
    private static final Regions cognitoRegion = Regions.EU_WEST_1;
    private static CognitoUserPool userPool;
    private static CognitoUser user;
    private static final String FILENAME = "userDetails";
    private static final String STORE_FILENAME = "myItems";
    private AmazonDynamoDBClient ddb = null;
    private static DynamoDBMapper mapper;
    private String usernme = "";
    private String passwrd = "";
    public static ArrayList<StoreItem> items = new ArrayList<StoreItem>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        readStore();
        if(items.size() == 0)
        {
            StoreItem s = new StoreItem();
            s.setName("Extra goal slot");
            s.setPrice(10000);
            s.setDescription("Allows you to create an extra goal");
            s.setRecipe(false);
            s.setPurchased(false);
            StoreItem s1 = new StoreItem();
            s1.setName("JM's Super Juice Recipe");
            s1.setPrice(5000);
            s1.setDescription("This is a 7 day juice plan to help you lose weight");
            s1.setRecipe(true);
            s1.setRecipeText("\n\nThis juice detox by Jason Vale recipe is an ultra-quick method to reshape your body, while promising to provide you with all the nutrients your body needs. This juice plan promises quick weight loss to help you lose 7lb in only 7 days.\n\nJM’s SUPER JUICE\n\n½ small pineapple\n½ stick celery\n1in chunk of cucumber\n1 small handful of spinach leaves\n1 small piece of peeled lime\n2 apples\n½ ripe avocado\nIce cubes\n\nJuice everything and enjoy\nNote:Please take a screenshot to save this recipe!");
            s1.setPurchased(false);
            StoreItem s2 = new StoreItem();
            s2.setName("Blackened Chicken Marinade");
            s2.setPrice(5000);
            s2.setDescription("A nice and simple marinade for chicken that delivers great flavour at little extra caloric cost");
            s2.setRecipe(true);
            s2.setRecipeText("Now the recipe calls for two large chicken breast but to be honest as long as the marinade covers the chicken put in as much or as little as you like.\n"
                            +"Also as far as cooking goes, grill, barbecue, bake, pan-fried are all good\n\n"
                            +"Ingredients:\n"
                            +"\t2 skinless boneless chicken breasts\n"
                            +"\t1/4 cup of Soy Sauce\n"
                            +"\t2 tablespoons of Brown Sugar\n"
                            +"\t2 tablespoons of Olive Oil\n"
                            +"\t2 tablespoons of Lemon Juice\n"
                            +"\t2 crushed cloves of Garlic\n"
                            +"\t1/2 teaspoon of Salt\n\n"
                            +"Instructions - Throw it all in a ziplock bag and give it a good shake.\n"
                            +"Seal the bag, refridgerate for at least two hours and then its ready to cook!\n\n"
                            +"Note:Please take a screenshot to save this recipe!"
            );
            s2.setPurchased(false);
            items.add(s);
            items.add(s1);
            items.add(s2);
            writeStore();
        }

        username = (EditText) findViewById(R.id.usernameET);
        password = (EditText) findViewById(R.id.passwordET);
        link = (TextView) findViewById(R.id.setUpLinkTV);
        loginButton = (Button) findViewById(R.id.loginButton);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
        readDetails();
        if(!(usernme.equals("") && passwrd.equals("")))
        {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
            cipClient.setRegion(Region.getRegion(cognitoRegion));
            // Create a CognitoUserPool object to refer to your user pool
            userPool = new CognitoUserPool(getApplication().getBaseContext(), userPoolId, clientId, clientSecret, cipClient);
            userPool.getUser(usernme).getSessionInBackground(authenticationHandler);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().equals("") || password.getText().equals(""))
                {
                    Toast.makeText(getApplication(), "Please fill both fields!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    usernme = username.getText().toString();
                    passwrd = password.getText().toString();
                    ClientConfiguration clientConfiguration = new ClientConfiguration();
                    AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
                    cipClient.setRegion(Region.getRegion(cognitoRegion));
                    // Create a CognitoUserPool object to refer to your user pool
                    userPool = new CognitoUserPool(getApplication().getBaseContext(), userPoolId, clientId, clientSecret, cipClient);
                    userPool.getUser(usernme).getSessionInBackground(authenticationHandler);
                }
            }
        });

    }

    // Callback handler for the sign-in process
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession) {
            // Sign-in was successful, cognitoUserSession will contain tokens for the user
            // Create a credentials provider, or use the existing provider.
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "eu-west-1:4fbdb346-0197-48ad-97fb-643ca12ef0a5", // Identity Pool ID
                    Regions.EU_WEST_1 // Region
            );

            // Set up as a credentials provider.
            Map<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.eu-west-1.amazonaws.com/eu-west-1_BOCAbGMPe", cognitoUserSession.getIdToken().getJWTToken());
            credentialsProvider.setLogins(logins);

            ddb = new AmazonDynamoDBClient(credentialsProvider);
            ddb.setRegion(Region.getRegion(Regions.EU_WEST_1));
            mapper = new DynamoDBMapper(ddb);
            user = userPool.getUser(usernme);
            writeDetails();
            Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(goToMain);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(usernme, passwrd, null);

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
            Toast.makeText(getApplication(), "Invalid login details", Toast.LENGTH_SHORT).show();
        }
    };

    // method that writes a value to a file
    private void writeDetails() {

        ObjectOutputStream outputStream = null;
        try {
            // create Android FileOutputStream, private file for this app only
            FileOutputStream fos = openFileOutput(FILENAME,
                    Context.MODE_PRIVATE);
            // Construct the output stream
            outputStream = new ObjectOutputStream(fos);
            outputStream.writeUTF(usernme);
            outputStream.writeUTF(passwrd);
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

    private void readDetails() {
        {
            ObjectInputStream inputStream = null;
            try {
                // Android get file input stream
                FileInputStream fis = openFileInput(FILENAME);
                // Construct the ObjectInputStream object
                inputStream = new ObjectInputStream(fis);
                usernme = inputStream.readUTF();
                passwrd = inputStream.readUTF();

            } catch (EOFException ex) {
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            } finally {
                // Close the ObjectInputStream
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
    }

    public void writeStore() {

        ObjectOutputStream outputStream = null;
        try {
            // create Android FileOutputStream, private file for this app only
            FileOutputStream fos = openFileOutput(STORE_FILENAME,
                    Context.MODE_PRIVATE);
            // Construct the output stream
            outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(items);
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

    private void readStore() {
        {
            ObjectInputStream inputStream = null;
            try {
                // Android get file input stream
                FileInputStream fis = openFileInput(STORE_FILENAME);
                // Construct the ObjectInputStream object
                inputStream = new ObjectInputStream(fis);
                items = (ArrayList<StoreItem>) inputStream.readObject();

            } catch (EOFException ex) {
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            } catch (ClassNotFoundException exs) {
            }
            finally {
                // Close the ObjectInputStream
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
    }

    public static DynamoDBMapper getMapper()
    {
        return mapper;
    }

    public static CognitoUser getUser()
    {
        return user;
    }
}