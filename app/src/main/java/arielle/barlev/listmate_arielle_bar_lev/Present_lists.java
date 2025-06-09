package arielle.barlev.listmate_arielle_bar_lev;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Present_lists extends Fragment {

    private String Uid;

    private RecyclerView lists_layout;

    private List<String> lists_names;
    private List<String> lists_ids;

    private Calendar calendar;

    private int selected_year;
    private int selected_month;
    private int selected_day;
    private int selected_hour = -1;
    private int selected_minute = -1;

    private String scheduling_list_name;

    private Firebase_Helper helper;
    private Lists_Names_Adapter adapter;
    private Utilities utilities;

    private Handler handler = new Handler(Looper.getMainLooper());
    private AtomicBoolean is_fetching_data = new AtomicBoolean(false);
    private static final long UPDATE_INTERVAL = 5000;

    private void init(View view) {
        lists_layout = view.findViewById(R.id.lists_layout);

        Bundle args = getArguments();
        if (args != null) {
            Uid = args.getString("Uid");
        }

        lists_names = new ArrayList<>();
        lists_ids = new ArrayList<>();

        helper = new Firebase_Helper(requireContext());
        utilities = new Utilities();

        calendar = Calendar.getInstance();
    }

    private Runnable data_update_runnable = new Runnable() {
        @Override
        public void run() {
            if (isAdded()) {
                fetch_display_data();
                handler.postDelayed(this, UPDATE_INTERVAL);
            } else {
                handler.removeCallbacks(this);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_present_lists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        LinearLayoutManager layout_manager = new LinearLayoutManager(requireContext());
        lists_layout.setLayoutManager(layout_manager);

        adapter = new Lists_Names_Adapter(lists_names);
        lists_layout.setAdapter(adapter);

        adapter.setOnItemClickListener(new Lists_Names_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String list_name) {
                int position = lists_names.indexOf(list_name);
                if (position != -1 && position < lists_ids.size()) {
                    String list_id = lists_ids.get(position);

                    if (getActivity() instanceof Home) {
                        ((Home) getActivity()).load_fragment(new Present_Items(), Uid, list_id);
                    } else {
                        utilities.make_snackbar(requireContext(), "Error: Cannot navigate, host activity not recognized.");
                    }
                } else {
                    if (requireContext() != null) {
                        utilities.make_snackbar(requireContext(), "Error: Cannot navigate, host activity not recognized.");
                    }
                }
            }
        });

        adapter.setOnShareClickListener(new Lists_Names_Adapter.OnShareClickListener() {
            @Override
            public void onShareClick(String list_name) {
                int position = lists_names.indexOf(list_name);
                if (position != -1 && position < lists_ids.size()) {
                    String list_id = lists_ids.get(position);

                    Context context = requireContext();

                    AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(context);
                    alert_dialog_builder.setTitle("Share list:");

                    final EditText text_box = new EditText(context);
                    text_box.setInputType(InputType.TYPE_CLASS_TEXT);
                    text_box.setHint("Enter user id");
                    text_box.setWidth(100);
                    text_box.setEms(10);
                    text_box.setGravity(Gravity.CENTER);
                    text_box.setPadding(10, 10, 10, 10);
                    text_box.setBackgroundColor(Color.parseColor("#FFFFDD"));
                    alert_dialog_builder.setView(text_box);
                    alert_dialog_builder.setPositiveButton("Ok", (dialog, which) -> {
                        String user_id = text_box.getText().toString().trim();
                        if (!user_id.isEmpty()) {
                            helper.share_list(list_id, user_id);
                            Toast.makeText(context, "List shared successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Please enter an user's email", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert_dialog_builder.create().show();

                } else {
                    utilities.make_snackbar(requireContext(), "Error: Could not find list ID for sharing.");
                }
            }
        });

        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS},100);

        create_notification_channel();

        adapter.setOnCalendarClickListener(new Lists_Names_Adapter.OnCalendarClickListener() {
            @Override
            public void onCalendarClick(String list_name) {

                scheduling_list_name = list_name;

                DatePickerDialog date_picker_dialog = new DatePickerDialog(requireContext(),
                        (v, year, month, day_of_month) -> {
                            selected_year = year;
                            selected_month = month;
                            selected_day = day_of_month;

                            show_time_dialog(scheduling_list_name);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                date_picker_dialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetch_display_data();
        handler.postDelayed(data_update_runnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(data_update_runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lists_layout = null;
        adapter = null;
        handler.removeCallbacks(data_update_runnable);
    }

    private void fetch_display_data() {
        if (is_fetching_data.compareAndSet(false, true)) {
            helper.users_lists(Uid).thenAccept(retrieved_list_ids -> {
                if (isAdded()) {
                    if (retrieved_list_ids.isEmpty()) {
                        requireActivity().runOnUiThread(() -> {
                            if (isAdded() && getView() != null) {
                                lists_names.clear();
                                lists_ids.clear();
                                adapter.notifyDataSetChanged();
                                utilities.make_snackbar(requireContext(), "No lists found for this user.");
                            }
                            is_fetching_data.set(false);
                        });
                    } else {
                        List<CompletableFuture<String>> name_futures = new ArrayList<>();
                        for (String id : retrieved_list_ids) {
                            name_futures.add(helper.get_list_name(id));
                        }

                        CompletableFuture.allOf(name_futures.toArray(new CompletableFuture[0])).thenAccept(v -> {
                            if (isAdded()) {
                                List<String> retrieved_names = new ArrayList<>();

                                for (CompletableFuture<String> name_future : name_futures) {
                                    try {
                                        String name = name_future.get();
                                        if (name != null) {
                                            retrieved_names.add(name);
                                        }
                                    } catch (InterruptedException | ExecutionException e) {
                                        if (requireContext() != null) {
                                            utilities.make_snackbar(requireContext(), "Error getting list name: " + e.getMessage());
                                        }
                                    }
                                }

                                requireActivity().runOnUiThread(() -> {
                                    if (isAdded() && getView() != null) {
                                        lists_names.clear();
                                        lists_names.addAll(retrieved_names);
                                        lists_ids.clear();
                                        lists_ids.addAll(retrieved_list_ids);
                                        adapter.notifyDataSetChanged();
                                    }
                                    is_fetching_data.set(false);
                                });
                            } else {
                                is_fetching_data.set(false);
                            }
                        }).exceptionally(e -> {
                            if (requireContext() != null) {
                                utilities.make_snackbar(requireContext(), "Failed to fetch list names: " + e.getMessage());
                            }
                            is_fetching_data.set(false);
                            return null;
                        });
                    }
                } else {
                    is_fetching_data.set(false);
                }
            }).exceptionally(e -> {
                if (requireContext() != null) {
                    utilities.make_snackbar(requireContext(), "Failed to fetch list IDs: " + e.getMessage());
                }
                is_fetching_data.set(false);
                return null;
            });
        }
    }

    private void show_time_dialog(String list_name) {
        if(selected_hour == -1 && selected_minute == -1) {
            selected_hour = calendar.get(Calendar.HOUR_OF_DAY);
            selected_minute = calendar.get(Calendar.MINUTE);
        }
    
        TimePickerDialog time_picker_dialog = new TimePickerDialog(requireContext(),
                (v, hour_of_day, minute) -> {
                    selected_hour = hour_of_day;
                    selected_minute = minute;

                    calendar.set(Calendar.HOUR_OF_DAY, selected_hour);
                    calendar.set(Calendar.MINUTE, selected_minute);
    
                    schedule_notification(list_name);
                },
                selected_hour,
                selected_minute,
                true
        );
    
        time_picker_dialog.show();
    }

    private void schedule_notification(String list_name) {
    
        if (selected_year == 0 || selected_hour == -1) {
            Toast.makeText(
                    requireContext(),
                    "Please select both a date and time to schedule the notification.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
    
        if (list_name == null) {
            Toast.makeText(
                    requireContext(),
                    "List name not loaded, cannot schedule notification.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
    
        String message = String.format("Remember to check your \"%s\" list", list_name);
    
        Calendar schedule_time = Calendar.getInstance();
        schedule_time.set(selected_year, selected_month, selected_day, selected_hour, selected_minute, 0);
    
        if (schedule_time.before(Calendar.getInstance())) {
            Toast.makeText(
                    requireContext(),
                    "Cannot schedule a notification in the past. Please select a future date/time.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }
    
        Intent notification_intent = new Intent(requireContext(), Notification_Receiver.class);
        notification_intent.putExtra("message", message);

        int request_code = (list_name + schedule_time.getTimeInMillis()).hashCode();
    
        notification_intent.setAction("com.ariellebarlev.listmate.ACTION" + request_code);
    
        PendingIntent pending_intent = PendingIntent.getBroadcast(
                requireContext(),
                request_code,
                notification_intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarm_manager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
    
        long trigger_time = schedule_time.getTimeInMillis();
    
        if (alarm_manager != null) {
            alarm_manager.setExact(AlarmManager.RTC_WAKEUP, trigger_time, pending_intent);
            Toast.makeText(requireContext(), "Notification scheduled", Toast.LENGTH_LONG).show();
        }
    }

    private void create_notification_channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "ListMate Reminders";
            String description = "Channel for scheduled list reminders and notifications.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
    
            NotificationChannel channel = new NotificationChannel("notifyChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notification_manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (notification_manager != null) {
                notification_manager.createNotificationChannel(channel);
            }
        }
}

}