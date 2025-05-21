package arielle.barlev.listmate_arielle_bar_lev;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class Alert_Scheduling extends AppCompatActivity {

    private Button date;
    private Button time;
    private Button schedule;

    private Calendar calendar;

    private int selected_year;
    private int selected_month;
    private int selected_day;
    private int selected_hour = -1;
    private int selected_minute = -1;

    private Utilities utilities;

    private void init() {
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        schedule = findViewById(R.id.schedule);

        utilities = new Utilities();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_scheduling);

        init();

        ActivityCompat.requestPermissions(Alert_Scheduling.this, new String[]{Manifest.permission.POST_NOTIFICATIONS},100);

        calendar = Calendar.getInstance();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog date_picker_dialog = new DatePickerDialog(Alert_Scheduling.this,
                    (v, year, month, day_of_month) -> {
                        selected_year = year;
                        selected_month = month;
                        selected_day = day_of_month;

                    Toast.makeText(
                            Alert_Scheduling.this,
                            "Date: " + selected_day + "/" + (selected_month + 1) + "/" + selected_year,
                            Toast.LENGTH_LONG
                    ).show();
                },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                );

                date_picker_dialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_hour == -1 && selected_minute == -1) {
                    selected_hour = calendar.get(Calendar.HOUR_OF_DAY);
                    selected_minute = calendar.get(Calendar.MINUTE);
                }

                TimePickerDialog time_picker_dialog = new TimePickerDialog(Alert_Scheduling.this,
                    (v, hour_of_day, minute) -> {
                        selected_hour = hour_of_day;
                        selected_minute = minute;

                    Toast.makeText(
                            Alert_Scheduling.this,
                            "Time: " + selected_hour + ":" + selected_minute,
                            Toast.LENGTH_LONG
                    ).show();

                    calendar.set(Calendar.HOUR_OF_DAY, selected_hour);
                    calendar.set(Calendar.MINUTE, selected_minute);
                },
                    selected_hour,
                    selected_minute,
                    true
                );

                time_picker_dialog.show();
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleNotification();
            }
        });

        createNotificationChannel();
    }

    private void scheduleNotification() {

        String message = "hello";

        Calendar schedule_time = Calendar.getInstance();
        schedule_time.set(selected_year, selected_month, selected_day, selected_hour, selected_minute, 0);

        Intent notification_intent = new Intent(Alert_Scheduling.this, NotificationReceiver.class);
        notification_intent.putExtra("message", message);

        int request_code = (int)System.currentTimeMillis();

        notification_intent.setAction("com.ariellebarlev.listmate.ACTION" + request_code);

        PendingIntent pending_intent = PendingIntent.getBroadcast(
                Alert_Scheduling.this,
                request_code,
                notification_intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long trigger_time = schedule_time.getTimeInMillis();

        if (alarm_manager != null) {
            alarm_manager.setExact(AlarmManager.RTC_WAKEUP, trigger_time, pending_intent);
            Toast.makeText(Alert_Scheduling.this, "Notification scheduled", Toast.LENGTH_LONG).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            utilities.make_snackbar(Alert_Scheduling.this, "hello");

            CharSequence name = "NotificationChannel";
            String description = "Channel for scheduled notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("notifyChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notification_manager = getSystemService(NotificationManager.class);
            notification_manager.createNotificationChannel(channel);

        }
    }
}