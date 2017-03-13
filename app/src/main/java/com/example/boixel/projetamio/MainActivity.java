package com.example.boixel.projetamio;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "Creation de l'activité");

        //CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);

        ToggleButton serviceButton = (ToggleButton) findViewById(R.id.serviceButton);
        serviceButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                Log.d("service", "Button check");
                if (button.isChecked()) {
                    startService(new Intent(getBaseContext(), WebService.class));
                }
                else{
                    stopService(new Intent(getBaseContext(), WebService.class));
                }

            }
        });

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("settings", "button");
                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(settingsIntent);
            }
        });

        Button mailButton = (Button) findViewById(R.id.mailButton);
        mailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("mail", "button");
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                //emailIntent.setData(Uri.parse("mailto:meskhen@gmail.com"));
                emailIntent.setType("message/rfc822");
                //emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"meskhen@gmail.com"});
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

        /*checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Checkbox", "Checkbox state changed");
                SharedPreferences sharedPref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);
                sharedPref.edit().putBoolean("checkBoxState", checkbox.isChecked()).apply();
            }
        });*/
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            //put here whaterver you want your activity to do with the intent received
            String value1 = intent.getStringExtra("light_value1");
            String value2 = intent.getStringExtra("light_value2");

            if(Float.parseFloat(value1) > 250){
                Log.d("Data", "Lumière allumée (mote1)");
                onLightChange();

            }else{
                Log.d("Data", "Lumière éteinte (mote1)");
                onLightChange();
            }
            if(Float.parseFloat(value2) > 250){
                Log.d("Data", "Lumière allumée (mote2)");
            }else{
                Log.d("Data", "Lumière éteinte (mote2)");
            }
            TextView moteText = (TextView) findViewById(R.id.mote1);
            moteText.setText("Mote 1 : "+value1+"\nMote 2 : "+value2);
        }
    };

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("value"));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }

    public void onLightChange(){
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_name)
                        .setContentTitle("Alerte lumière!")
                        .setContentText("Une lampe est restée allumée!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
     // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
     // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        int mId = 3;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

            //CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);
            Boolean checkBoxState = sharedPref.getBoolean("checkBoxState", false);
            Log.d("CheckBoxStatus", checkBoxState.toString());
            if(checkBoxState){

                if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                    Intent i = new Intent(context, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }

        }

    }
}
