package com.example.espana83.istep;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProvider;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements TabListener {

    private ViewPager viewPager;
    private TabPagerAdapter tabPagerAdapter;
    private ActionBar actionBar;
    private String username;
    private String password;
    private static final String FILENAME = "userDetails";
    private static final String userPoolId = "eu-west-1_BOCAbGMPe";
    private static final String clientId = "574hb7retiaoe801scj3tlpcjt";
    private static final String clientSecret = "mb6td7hni7ht09q8onoto51sml34r3sidli46nsvistfrn7ucgj";
    private static final Regions cognitoRegion = Regions.EU_WEST_1;
    private static CognitoUserPool userPool;
    private static CognitoUser user;
    private AmazonDynamoDBClient ddb = null;
    private static DynamoDBMapper mapper;
    private ArrayList<Goal> goals = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //readDetails();
        //ClientConfiguration clientConfiguration = new ClientConfiguration();
        //AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
        //cipClient.setRegion(Region.getRegion(cognitoRegion));
         // Create a CognitoUserPool object to refer to your user pool
       // userPool = new CognitoUserPool(getApplication().getBaseContext(), userPoolId, clientId, clientSecret, cipClient);
       // user = userPool.getUser(username);
       // user.getSessionInBackground(authenticationHandler);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            actionBar.addTab(actionBar.newTab().
                    setTabListener(this).setIcon(R.drawable.home_button));
        actionBar.addTab(actionBar.newTab().
                setTabListener(this).setIcon(R.drawable.leader_board_button));
        actionBar.addTab(actionBar.newTab().
                setTabListener(this).setIcon(R.drawable.store_button));

        actionBar.addTab(actionBar.newTab().
                setTabListener(this).setIcon(R.drawable.goal_button));



        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int postion) {
                actionBar.setSelectedNavigationItem(postion);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view =getActionBar().getCustomView();

        ImageButton imageButton= (ImageButton)view.findViewById(R.id.action_bar_profile);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileFragment.class);
                startActivity(intent);
            }
        });
        startService(new Intent(this, BackgroundService.class));
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
// TODO Auto-generated method stub
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
// TODO Auto-generated method stub
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//Override method that creates a use menu
        MenuInflater inflater = getMenuInflater();//declare menu inflater
        inflater.inflate(R.menu.options, menu); //add your menu xml file to the inflater
        return super.onCreateOptionsMenu(menu);//create the menu
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Override method that processes the menu item selected
        switch (item.getItemId()) {//create a switch

            case R.id.item3://set second item id
                Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
                LoginActivity.getUser().signOut();
                deleteFile(FILENAME);
                Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(goToLogin);
                return true;
            default:
                return super.onOptionsItemSelected(item);//return default menu item
        }
    }

    // Callback handler for the sign-in process
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession) {
            // Sign-in was successful, cognitoUserSession will contain tokens for the user
            // Get id token from CognitoUserSession.
            String idToken = cognitoUserSession.getIdToken().getJWTToken();

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
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, password, null);

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
            Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goToLogin);
        }
    };
    @Override
    public void onBackPressed()
    {

    }
    //public static DynamoDBMapper getMapper(){return mapper;}
    //public static CognitoUser getUser(){return user;}
}
