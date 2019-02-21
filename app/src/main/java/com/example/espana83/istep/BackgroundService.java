package com.example.espana83.istep;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service implements SensorEventListener {

    public Context context = this;
    public static Runnable runnable = null;
    private SensorManager sensorManager;
    public static double totalSteps = 0;
    public static double steps = 0;
    public static double spentSteps = 0;
    private AmazonDynamoDBClient ddb = null;
    private static DynamoDBMapper mapper;
    private static final String userPoolId = "eu-west-1_BOCAbGMPe";
    private static final String clientId = "574hb7retiaoe801scj3tlpcjt";
    private static final String clientSecret = "mb6td7hni7ht09q8onoto51sml34r3sidli46nsvistfrn7ucgj";
    private static final Regions cognitoRegion = Regions.EU_WEST_1;
    private static CognitoUserPool userPool;
    private static CognitoUser user;
    private static AccountInfo a;
    public static ArrayList<Goal> goals = new ArrayList<>();
    public static Person p;
    private static String username, password;
    private static final String FILENAME = "userDetails";
    private CognitoCachingCredentialsProvider credentialsProvider;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        readDetails();
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
        cipClient.setRegion(Region.getRegion(cognitoRegion));
        userPool = new CognitoUserPool(getApplication().getBaseContext(), userPoolId, clientId, clientSecret, cipClient);
        user = userPool.getUser(username);
        user.getSessionInBackground(authenticationHandler);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager.getSensorList(Sensor.TYPE_STEP_COUNTER).size()!=0){
            Sensor step = sensorManager.getSensorList(Sensor.TYPE_STEP_COUNTER).get(0);
            sensorManager.registerListener(this, step, SensorManager.SENSOR_DELAY_GAME);
        }
        readValues();
        runnable = new Runnable() {
            public void run() {
                createNotification("Steps");
            }
        };
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long howMany = (c.getTimeInMillis()-System.currentTimeMillis());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                reset();// this code will be executed after Midnight - current Time
            }
        }, howMany);
        //p = new Person();
       //p.setUserID(MainActivity.getUser().getUserId());
        //loadGoals(getBaseContext());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onStart(Intent intent, int startid) {
    }
    public  void createNotification(String count)//original notification
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("iStep")
                .setContentText(count)
                .setSmallIcon(R.drawable.foot_print2)
                .setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap())
                .setOngoing(true)
                .setAutoCancel(false);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        notificationManager.notify(0, builder.build());
    }
    @Override
    public void onSensorChanged(SensorEvent event) {// this is a listener method that listens for all sensor changes on a phone
        //gets called everytime phone moves
        //long timeElapsed = getCurrentTime();
            if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){//this is what concerns us for now
            steps++;//starting value for steps taken
            writeValues(getBaseContext());
            Intent inten = new Intent("YourAction");
            Bundle bundle = new Bundle();
            bundle.putDouble("currentSteps", steps);
            bundle.putDouble("totalSteps", totalSteps);
            inten.putExtras(bundle);
            if(steps % 100 == 0) {
                try {
                    a = new AccountInfo();
                    a.setUserID(username);
                    a.setTotalSteps(totalSteps);
                    a.setSpentSteps(spentSteps);
                    a.setTodaysSteps(steps);
                    mapper.save(a);
                    updateGoals(context);
                } catch (Exception e) {

                }
            }
            for(int i = 0; i < goals.size(); i++)
            {
                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                c1.setTime(new Date());
                c2.setTime(goals.get(i).getCreated());
                if(goals.get(i).getActive())
                {
                    if(goals.get(i).getType().equals("Kilometers"))
                    {
                        goals.get(i).setProgress(goals.get(i).getProgress() + 0.000762);
                    }
                    else if(goals.get(i).getType().equals("Calories"))
                    {
                        goals.get(i).setProgress(goals.get(i).getProgress() + 0.05);
                    }
                    else
                    {
                        goals.get(i).setProgress(goals.get(i).getProgress() + 1);
                    }

                    if(goals.get(i).getProgress() >= goals.get(i).getTarget())
                    {
                        goals.get(i).setCompleted(true);
                        goals.get(i).setActive(false);
                    }
                    else if((c1.getTimeInMillis() - c2.getTimeInMillis()) >= (goals.get(i).getDays()*24*60*60*1000))
                    {
                        goals.get(i).setActive(false);
                    }
                }
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(inten);
            createNotification("Steps Taken "+(int)steps);

        }else if(event.sensor.getType() == Sensor.TYPE_GRAVITY){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
        }
    }

    private void reset()
    {
        totalSteps += steps;
        steps = 0;
        try{
            a = new AccountInfo();
            a.setUserID(username);
            a.setTotalSteps(totalSteps);
            a.setSpentSteps(spentSteps);
            a.setTodaysSteps(steps);
            mapper.save(a);
        }catch(Exception e){

        }
        createNotification("Steps");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long howMany = (c.getTimeInMillis()-System.currentTimeMillis());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                reset();// this code will be executed after 24 hours
            }
        }, howMany);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {//assume this method can be used later to adjust the accuracy of step counter
        // TODO Auto-generated method stub
    }

    public static void writeValues(Context context) {

        ObjectOutputStream outputStream = null;
        try {
            // create Android FileOutputStream, private file for this app only
            FileOutputStream fos = context.openFileOutput("values",
                    Context.MODE_PRIVATE);
            // Construct the output stream
            outputStream = new ObjectOutputStream(fos);
            outputStream.writeDouble(totalSteps);
            outputStream.writeDouble(steps);
            outputStream.writeDouble(spentSteps);
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

    // method that reads a value from a file
    private void readValues() {
        {
            ObjectInputStream inputStream = null;
            try {
                // Android get file input stream
                FileInputStream fis = openFileInput("values");
                // Construct the ObjectInputStream object
                inputStream = new ObjectInputStream(fis);
                totalSteps = inputStream.readDouble();
                steps = inputStream.readDouble();
                spentSteps = inputStream.readDouble();
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

    public static void updateGoals(Context context)
    {
        try{
            p = new Person();
            p.setUserID(username);
            p.setGoals(goals);
            mapper.save(p);

        }catch(Exception e){

        }
    }

    public static void updateAccount(Context context)
    {
        try {
            a = new AccountInfo();
            a.setUserID(username);
            a.setTotalSteps(totalSteps);
            a.setSpentSteps(spentSteps);
            a.setTodaysSteps(steps);
            mapper.save(a);
            updateGoals(context);
        } catch (Exception e) {

        }
    }

    public static void loadGoals(Context context)
    {
        try{
            p = mapper.load(Person.class, LoginActivity.getUser().getUserId());
            goals = p.getGoals();

        }catch(Exception e){

        }
    }


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

    private void goGoal()
    {

    }

    private void readDetails() {
        {
            ObjectInputStream inputStream = null;
            try {
                // Android get file input stream
                FileInputStream fis = openFileInput(FILENAME);
                // Construct the ObjectInputStream object
                inputStream = new ObjectInputStream(fis);
                username = inputStream.readUTF();
                password = inputStream.readUTF();

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

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession) {
            // Sign-in was successful, cognitoUserSession will contain tokens for the user
            // Get id token from CognitoUserSession.
            String idToken = cognitoUserSession.getIdToken().getJWTToken();

            // Create a credentials provider, or use the existing provider.
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "eu-west-1:4fbdb346-0197-48ad-97fb-643ca12ef0a5", // Identity Pool ID
                    Regions.EU_WEST_1 // Region
            );

            // Set up as a credentials provider.
            Map<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.eu-west-1.amazonaws.com/eu-west-1_BOCAbGMPe", cognitoUserSession.getIdToken().getJWTToken());
            credentialsProvider.setLogins(logins);
            refreshToken();
            ddb = new AmazonDynamoDBClient(credentialsProvider);
            ddb.setRegion(Region.getRegion(Regions.EU_WEST_1));
            mapper = new DynamoDBMapper(ddb);
            try{
                p = mapper.load(Person.class, username);
                goals = p.getGoals();

            }catch(Exception e){

            }
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

        }
    };

    private void refreshToken()
    {
        credentialsProvider.refresh();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                refreshToken();
            }
        }, 7140000);
    }
}