package com.example.espana83.istep;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import 	android.widget.ImageButton;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class ProfileFragment extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;//request code neede for camera to work i guess
    private ImageView imageView;
    private View view;
    Dialog dialog;
    private EditText detail;
    private TextView username;
    private TextView level;
    private TextView height;
    private TextView weight;
    private TextView age;
    private TextView gender;
    private TextView bmi;
    private TextView country;
    private Profile p;
    private DynamoDBMapper mapper = LoginActivity.getMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username = (TextView) findViewById(R.id.usernameTextView);
        level = (TextView) findViewById(R.id.levelTextView);
        height = (TextView) findViewById(R.id.heightTextView);
        weight = (TextView) findViewById(R.id.weightTextView);
        age = (TextView) findViewById(R.id.ageTextView);
        gender = (TextView) findViewById(R.id.genderTextView);
        bmi = (TextView) findViewById(R.id.bmiTextView);
        country = (TextView) findViewById(R.id.countryTextView);
        fill();
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_for_profile);
        view = getSupportActionBar().getCustomView();

        ImageButton imageButton= (ImageButton)view.findViewById(R.id.action_bar_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.imageView = (ImageView)this.findViewById(R.id.imageView);//imageview to conatin the profile picture
        loadImage();


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);//places the captured picture in the imageview, doesnt save yet
            saveImage(photo);//save photo to file
        }
    }
    public void onPress(View v)
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);//opens up the camera
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    public void heightInput(View v2)
    {
        dialog = new Dialog(ProfileFragment.this);
        dialog.setContentView(R.layout.profile_details_pop_up);
        dialog.setTitle("Set Your Height(cm)");
        dialog.setCancelable(true);
        Button button = (Button) dialog.findViewById(R.id.submitProfileDetailButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                detail = (EditText) dialog.findViewById(R.id.profileDetailEditText);
                if(detail.getText()!=null && detail.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    height.setText("Height\n" + detail.getText());
                    // add new height to Profile
                    p.setHeight(Double.parseDouble(detail.getText().toString()));
                    // Save Profile to database
                    mapper.save(p);
                    refreshPage();
                    dialog.dismiss();//closes pop
                }
                else
                {
                    Toast.makeText(getApplication(), "Invalid Height entered", Toast.LENGTH_LONG).show();
                }
            }
        });
         dialog.show();
    }

    public void weightInput(View v2)
    {
        dialog = new Dialog(ProfileFragment.this);
        dialog.setContentView(R.layout.profile_details_pop_up);
        dialog.setTitle("Set Your Weight(kg)");
        dialog.setCancelable(true);
        Button button = (Button) dialog.findViewById(R.id.submitProfileDetailButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                detail = (EditText) dialog.findViewById(R.id.profileDetailEditText);
                if(detail.getText()!=null && detail.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                weight.setText("Weight\n"+detail.getText());
                // add new height to Profile
                p.setWeight(Double.parseDouble(detail.getText().toString()));
                // Save Profile to database
                mapper.save(p);
                    refreshPage();
                dialog.dismiss();//closes pop
                //refreshPage();
                }
                else
                {
                    Toast.makeText(getApplication(), "Invalid Weight entered", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();

    }

    public void ageInput(View v2)
    {
        dialog = new Dialog(ProfileFragment.this);
        dialog.setContentView(R.layout.profile_details_pop_up);
        dialog.setTitle("Set Your Age");
        dialog.setCancelable(true);
        Button button = (Button) dialog.findViewById(R.id.submitProfileDetailButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                detail = (EditText) dialog.findViewById(R.id.profileDetailEditText);
                if(detail.getText()!=null && detail.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                age.setText("Age\n"+detail.getText());
                // add new height to Profile
                p.setAge(Integer.parseInt(detail.getText().toString()));
                // Save Profile to database
                mapper.save(p);
                dialog.dismiss();//closes pop
                //refreshPage();
                }
                else
                {
                    Toast.makeText(getApplication(), "Invalid Age entered", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }

    public void genderInput(View v2)
    {
        dialog = new Dialog(ProfileFragment.this);
        dialog.setContentView(R.layout.profile_details_pop_up);
        dialog.setTitle("Set Your Gender");
        dialog.setCancelable(true);
        Button button = (Button) dialog.findViewById(R.id.submitProfileDetailButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                detail = (EditText) dialog.findViewById(R.id.profileDetailEditText);
                gender.setText("\tGender\n"+detail.getText());
                // add new height to Profile
                p.setGender(true);
                // Save Profile to database
                mapper.save(p);
                dialog.dismiss();//closes pop
            }
        });
        dialog.show();
    }

    public void countryInput(View v2)
    {
        dialog = new Dialog(ProfileFragment.this);
        dialog.setContentView(R.layout.profile_details_pop_up);
        dialog.setTitle("Set Your Location");
        dialog.setCancelable(true);
        Button button = (Button) dialog.findViewById(R.id.submitProfileDetailButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                detail = (EditText) dialog.findViewById(R.id.profileDetailEditText);
                country.setText("\t\rLocation\n"+detail.getText());
                // add new height to Profile
                p.setCountry(detail.getText().toString());
                // Save Profile to database
                mapper.save(p);
                dialog.dismiss();//closes pop
            }
        });
        dialog.show();
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

    private void fill()
    {
        // load profile
        try{
            // use getters to populate fields in here
            p = mapper.load(Profile.class, LoginActivity.getUser().getUserId());
            username.setText(LoginActivity.getUser().getUserId());
            int l = (int)(BackgroundService.totalSteps + BackgroundService.steps)/100;
            level.setText("Level " + l);
            if(p.getHeight() != 0)
            {
                height.setText("Height\n"+Double.toString(p.getHeight()));
            }
            if(p.getWeight() != 0)
            {
                weight.setText("Weight\n"+Double.toString(p.getWeight()));
            }
            if(p.getAge() != 0)
            {
                age.setText("Age\n"+Integer.toString(p.getAge()));
            }
            if(p.getGender() != false)
            {
                gender.setText("\tGender\n"+"Male");
            }
            if(p.getHeight() != 0 && p.getWeight() != 0)
            {
                double heightInMeters = p.getHeight()/100;
                p.setBmi(p.getWeight()/(heightInMeters * heightInMeters));
                mapper.save(p);
                bmi.setText("BMI\n"+String.format("%.2f", p.getBmi()));
            }
            country.setText("\t\tLocation\n"+"\t"+p.getCountry());

        }catch(Exception e){
            p = new Profile();
            p.setUserID(LoginActivity.getUser().getUserId());
            username.setText(LoginActivity.getUser().getUserId());
        }
    }
    private void refreshPage()
    {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
    private static void saveImage(Bitmap bitmap) {
        OutputStream output;
        String recentImageInCache;
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/iStep/profile");
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, "profilePic.jpg");
        try {

            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    private void loadImage()
    {
        String photoPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/iStep/profile/profilePic.jpg";
        Bitmap bitmap1 = BitmapFactory.decodeFile(photoPath);
        imageView.setImageBitmap(bitmap1);
    }


}