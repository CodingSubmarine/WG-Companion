package braess.constantin.wgterminal;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import braess.constantin.wgcompanion.R;

//useless comment

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "channelID";
    private static final int NOTIFICATION_ID = 1;
    public List<Chore> allChores = new ArrayList<>();
    public List<Chore> janChore = new ArrayList<>();
    public List<Chore> marcChore = new ArrayList<>();
    public List<Chore> constantinChore = new ArrayList<>();
    public FirebaseDatabase data;

    public boolean jan = true;
    public boolean marc = true;
    public boolean constantin = true;


    public String self = "";


    private DataSnapshot dataSnapshotChores;
    private DataSnapshot dataSnapshotMates;
    private DataSnapshot dataSnapshotBackup;
    private ListView listView1, listView2, listView3;

    private TextView janTextHeader, marcTextHeader, constantinTextHeader;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_chores:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_todo:
                    Intent todoIntent = new Intent(getApplicationContext(), todo.class);
                    todoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(todoIntent);
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_undefined:
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final FirebaseDatabase database = Utils.getDatabase();
        //DatabaseReference myRef = database.getReference("wgterminal-ce537");
        final DatabaseReference myRef = database.getReference("Chores");
        final DatabaseReference mateRef = database.getReference("Mates");
        final DatabaseReference backupRef = database.getReference("Backup");
        data = database;
        listView1 = findViewById(R.id.janChores);
        listView2 = findViewById(R.id.marcChores);
        listView3 = findViewById(R.id.constantinChores);

        janTextHeader = findViewById(R.id.janHeader);
        marcTextHeader = findViewById(R.id.marcHeader);
        constantinTextHeader = findViewById(R.id.constantinHeader);

        refreshChoreList();

        self = getValue();

        if (self != null) {

            switch (self) {
                case "Jan":
                    janTextHeader.setBackgroundResource(R.color.selectedHeader);
                    marcTextHeader.setBackgroundResource(R.color.colorAccent);
                    constantinTextHeader.setBackgroundResource(R.color.colorAccent);
                    break;
                case "Marc":
                    janTextHeader.setBackgroundResource(R.color.colorAccent);
                    marcTextHeader.setBackgroundResource(R.color.selectedHeader);
                    constantinTextHeader.setBackgroundResource(R.color.colorAccent);
                    break;
                case "Constantin":
                    janTextHeader.setBackgroundResource(R.color.colorAccent);
                    marcTextHeader.setBackgroundResource(R.color.colorAccent);
                    constantinTextHeader.setBackgroundResource(R.color.selectedHeader);
                    break;
                default:
                    janTextHeader.setBackgroundResource(R.color.colorAccent);
                    marcTextHeader.setBackgroundResource(R.color.colorAccent);
                    constantinTextHeader.setBackgroundResource(R.color.colorAccent);
            }
        }  else {

            janTextHeader.setBackgroundResource(R.color.colorAccent);
            marcTextHeader.setBackgroundResource(R.color.colorAccent);
            constantinTextHeader.setBackgroundResource(R.color.colorAccent);

        }

        // Create an ArrayAdapter from List
        viewAllLists();

        janTextHeader.setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (marc && constantin) {
                    jan = !jan;
                    updateAbsence();
                    dataSnapshotMates.child("Jan").getRef().setValue(jan);
                    if (jan) {
                        loadBackup();
                    } else {
                        createBackup();
                        for (int i = 0; i < janChore.size(); i++) {
                            if (i % 2 == 0) {
                                janChore.get(i).moveOn();
                            }
                            janChore.get(i).moveOn();
                            DataSnapshot ref = findRefChores(janChore.get(i).name);
                            ref.child("turn").getRef().setValue(janChore.get(i).turn.toString());
                        }
                        refreshChoreList();
                        viewAllLists();
                    }
                } else {
                    toast("Only one mate can be marked as absent at once");
                }
                return true;
            }
        });
        marcTextHeader.setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (jan && constantin) {
                    marc = !marc;
                    updateAbsence();
                    dataSnapshotMates.child("Marc").getRef().setValue(marc);
                    if (marc) {
                        loadBackup();
                    } else {
                        createBackup();
                        for (int i = 0; i < marcChore.size(); i++) {
                            if (i % 2 == 0) {
                                marcChore.get(i).moveOn();
                            }
                            marcChore.get(i).moveOn();
                            DataSnapshot ref = findRefChores(marcChore.get(i).name);
                            ref.child("turn").getRef().setValue(marcChore.get(i).turn.toString());
                        }
                        refreshChoreList();
                        viewAllLists();
                    }
                } else {
                    toast("Only one mate can be marked as absent at once");
                }
                return true;
            }
        });
        constantinTextHeader.setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (jan && marc) {
                    constantin = !constantin;
                    updateAbsence();
                    dataSnapshotMates.child("Constantin").getRef().setValue(constantin);
                    if (constantin) {
                        loadBackup();
                    } else {
                        createBackup();
                        for (int i = 0; i < constantinChore.size(); i++) {
                            if (i % 2 == 0) {
                                constantinChore.get(i).moveOn();
                            }
                            constantinChore.get(i).moveOn();
                            DataSnapshot ref = findRefChores(constantinChore.get(i).name);
                            ref.child("turn").getRef().setValue(constantinChore.get(i).turn.toString());
                        }
                        //refreshChoreList();
                        //viewAllLists();
                    }
                } else {
                    toast("Only one mate can be marked as absent at once");
                }
                updateAbsence();
                return true;
            }
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                janChore.get(i).moveOn();
                if (!marc) {
                    janChore.get(i).moveOn();
                }
                janChore.get(i).setPriority(0);

                DataSnapshot ref = findRefChores(janChore.get(i).name);
                ref.child("turn").getRef().setValue(janChore.get(i).turn.toString());
                ref.child("priority").getRef().setValue(janChore.get(i).priority);

                refreshChoreList();
                viewAllLists();
            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                marcChore.get(i).moveOn();
                marcChore.get(i).setPriority(0);
                if (!constantin) {
                    marcChore.get(i).moveOn();
                }

                DataSnapshot ref = findRefChores(marcChore.get(i).name);
                ref.child("turn").getRef().setValue(marcChore.get(i).turn.toString());
                ref.child("priority").getRef().setValue(marcChore.get(i).priority);

                refreshChoreList();
                viewAllLists();
            }
        });
        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                constantinChore.get(i).moveOn();
                constantinChore.get(i).setPriority(0);
                if (!jan) {
                    constantinChore.get(i).moveOn();
                }

                DataSnapshot ref = findRefChores(constantinChore.get(i).name);
                ref.child("turn").getRef().setValue(constantinChore.get(i).turn.toString());
                ref.child("priority").getRef().setValue(constantinChore.get(i).priority);

                refreshChoreList();
                viewAllLists();
            }
        });
        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (janChore.get(i).getPriority() < 2) {
                    janChore.get(i).setPriority(janChore.get(i).getPriority() + 1);
                } else {
                    janChore.get(i).setPriority(0);
                }

                DataSnapshot ref = findRefChores(janChore.get(i).name);
                ref.child("priority").getRef().setValue(janChore.get(i).priority);

                refreshChoreList();
                viewAllLists();
                return true;
            }
        });
        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (marcChore.get(i).getPriority() < 2) {
                    marcChore.get(i).setPriority(marcChore.get(i).getPriority() + 1);
                } else {
                    marcChore.get(i).setPriority(0);
                }

                DataSnapshot ref = findRefChores(marcChore.get(i).name);
                ref.child("priority").getRef().setValue(marcChore.get(i).priority);

                refreshChoreList();
                viewAllLists();
                return true;
            }
        });
        listView3.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (constantinChore.get(i).getPriority() < 2) {
                    constantinChore.get(i).setPriority(constantinChore.get(i).getPriority() + 1);
                } else {
                    constantinChore.get(i).setPriority(0);
                }

                DataSnapshot ref = findRefChores(constantinChore.get(i).name);
                ref.child("priority").getRef().setValue(constantinChore.get(i).priority);

                refreshChoreList();
                viewAllLists();
                return true;
            }
        });


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshotChores = dataSnapshot;
                janChore.clear();
                marcChore.clear();
                constantinChore.clear();
                //allChores.clear();
                //DataSnapshot choresData = dataSnapshot.child("Chores");
                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                    String name = sn.child("name").getValue(String.class);
                    @SuppressWarnings("ConstantConditions") int priority = sn.child("priority").getValue(Integer.class);
                    String turnString = sn.child("turn").getValue(String.class);

                    assert turnString != null;
                    switch (turnString) {
                        case "CONSTANTIN":
                            constantinChore.add(new Chore(name, Roommate.CONSTANTIN, priority));
                            break;
                        case "JAN":
                            janChore.add(new Chore(name, Roommate.JAN, priority));
                            break;
                        default:
                            marcChore.add(new Chore(name, Roommate.MARC, priority));
                            break;
                    }
                    //toast(findRefChores("Jan gay").getKey());
                }
                joinAllChores();
                refreshChoreList();
                viewAllLists();
                checkForImportantChores();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
        mateRef.addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshotMates = dataSnapshot;
                jan = dataSnapshot.child("Jan").getValue(Boolean.class);
                marc = dataSnapshot.child("Marc").getValue(Boolean.class);
                constantin = dataSnapshot.child("Constantin").getValue(Boolean.class);
                updateAbsence();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        backupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshotBackup = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updateAbsence() {
        if (self != null) {
            switch (self) {
                case "Jan":
                    janTextHeader.setBackgroundResource(R.color.selectedHeader);
                    marcTextHeader.setBackgroundResource(R.color.colorAccent);
                    constantinTextHeader.setBackgroundResource(R.color.colorAccent);
                    break;
                case "Marc":
                    janTextHeader.setBackgroundResource(R.color.colorAccent);
                    marcTextHeader.setBackgroundResource(R.color.selectedHeader);
                    constantinTextHeader.setBackgroundResource(R.color.colorAccent);
                    break;
                case "Constantin":
                    janTextHeader.setBackgroundResource(R.color.colorAccent);
                    marcTextHeader.setBackgroundResource(R.color.colorAccent);
                    constantinTextHeader.setBackgroundResource(R.color.selectedHeader);
                    break;
            }
        } else {

            janTextHeader.setBackgroundResource(R.color.colorAccent);
            marcTextHeader.setBackgroundResource(R.color.colorAccent);
            constantinTextHeader.setBackgroundResource(R.color.colorAccent);

        }

        if (!jan) {
            janTextHeader.setBackgroundResource(R.color.absent);
            listView1.setBackgroundResource(R.color.absent);
        } else {
            listView1.setBackgroundResource(R.color.white);
        }
        if (!marc) {
            marcTextHeader.setBackgroundResource(R.color.absent);
            listView2.setBackgroundResource(R.color.absent);
        } else {
            listView2.setBackgroundResource(R.color.white);
        }
        if (!constantin) {
            constantinTextHeader.setBackgroundResource(R.color.absent);
            listView3.setBackgroundResource(R.color.absent);
        } else {
            listView3.setBackgroundResource(R.color.white);
        }
    }


    private void changePriorityAppearance(List<Chore> choreList, int position, TextView view) {
        if (choreList.get(position).getPriority() == 2) {
            view.setBackgroundColor(Color.RED);
        } else if (choreList.get(position).getPriority() == 1) {
            view.setBackgroundColor(Color.YELLOW);
        }
    }


    public List<String> getNameList(List<Chore> list) {
        List<String> stringList = new ArrayList<>();
        for (Chore item :
                list) {
            stringList.add(item.name);
        }
        return stringList;
    }

    public void refreshChoreList() {
        //Sort Chores by Roommate
        for (int i = 0; i < janChore.size(); i++) {
            if (janChore.get(i).turn != Roommate.JAN) {
                if (janChore.get(i).turn == Roommate.MARC) {
                    marcChore.add(janChore.get(i));
                } else {
                    constantinChore.add(janChore.get(i));
                }
                janChore.remove(janChore.get(i));
            }
        }

        for (int i = 0; i < marcChore.size(); i++) {
            if (marcChore.get(i).turn != Roommate.MARC) {
                if (marcChore.get(i).turn == Roommate.JAN) {
                    janChore.add(marcChore.get(i));
                } else {
                    constantinChore.add(marcChore.get(i));
                }
                marcChore.remove(marcChore.get(i));
            }
        }
        for (int i = 0; i < constantinChore.size(); i++) {
            if (constantinChore.get(i).turn != Roommate.CONSTANTIN) {
                if (constantinChore.get(i).turn == Roommate.JAN) {
                    janChore.add(constantinChore.get(i));
                } else {
                    marcChore.add(constantinChore.get(i));
                }
                constantinChore.remove(constantinChore.get(i));
            }
        }
        //sort each List by priority
        janChore = sortByPriority(janChore);
        marcChore = sortByPriority(marcChore);
        constantinChore = sortByPriority(constantinChore);
    }

    public List<Chore> sortByPriority(List<Chore> choreList) {
        for (int i = 0; i < choreList.size(); i++) {
            for (int j = i; j < choreList.size(); j++) {
                if (choreList.get(i).priority < choreList.get(j).priority) {
                    //swap positions in list
                    Chore tmp = choreList.get(i);
                    choreList.set(i, choreList.get(j));
                    choreList.set(j, tmp);
                }
            }
        }
        return choreList;
    }


    public void viewList(ListView listView, final List<Chore> choreList) {
        //if (choreList.size() != 0) {
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, getNameList(choreList)) {
            @NonNull
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                // Get the current item from ListView
                TextView view = (TextView) super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLACK);
                if (choreList.size() != 0) {
                    changePriorityAppearance(choreList, position, view);
                    view.setGravity(Gravity.CENTER);
                }
                return view;
            }
        };
        listView.setAdapter(arrayAdapter1);
    }

    public void viewAllLists() {
        viewList(listView1, janChore);
        viewList(listView2, marcChore);
        viewList(listView3, constantinChore);
    }

    public void toast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }


    public DataSnapshot findRefChores(String id) {
        for (DataSnapshot snap : dataSnapshotChores.getChildren()) {
            if (snap.exists()) {
                String name = snap.child("name").getValue(String.class);
                assert name != null;
                if (name.equals(id)) {
                    return snap;
                }
            }
        }
        return null;
    }

    public void joinAllChores() {
        allChores.clear();
        allChores.addAll(janChore);
        allChores.addAll(marcChore);
        allChores.addAll(constantinChore);
    }


    public void janOnClick(View view) {
        self = "Jan";

        janTextHeader.setBackgroundResource(R.color.selectedHeader);
        marcTextHeader.setBackgroundResource(R.color.colorAccent);
        constantinTextHeader.setBackgroundResource(R.color.colorAccent);

        setValue("Jan");
        updateAbsence();
    }

    public void marcOnClick(View view) {
        self = "Marc";

        janTextHeader.setBackgroundResource(R.color.colorAccent);
        marcTextHeader.setBackgroundResource(R.color.selectedHeader);
        constantinTextHeader.setBackgroundResource(R.color.colorAccent);

        setValue("Marc");
        updateAbsence();
    }

    public void constantinOnClick(View view) {
        self = "Constantin";

        janTextHeader.setBackgroundResource(R.color.colorAccent);
        marcTextHeader.setBackgroundResource(R.color.colorAccent);
        constantinTextHeader.setBackgroundResource(R.color.selectedHeader);

        setValue("Constantin");
        updateAbsence();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkForImportantChores() {
        int highestPrio = 0;
        if (self != null) {
            switch (self) {
                case "Jan":
                    for (Chore chore : janChore) {
                        if (chore.getPriority() > highestPrio) {
                            highestPrio = chore.getPriority();
                        }
                    }
                    break;

                case "Marc":
                    for (Chore chore : marcChore) {
                        if (chore.getPriority() > highestPrio) {
                            highestPrio = chore.getPriority();
                        }
                    }
                    break;


                case "Constantin":
                    for (Chore chore : constantinChore) {
                        if (chore.getPriority() > highestPrio) {
                            highestPrio = chore.getPriority();
                        }
                    }
                    break;

                default:
                    toast("Bitte wähle eine Liste aus");
                    break;
            }
        }  else {

            janTextHeader.setBackgroundResource(R.color.colorAccent);
            marcTextHeader.setBackgroundResource(R.color.colorAccent);
            constantinTextHeader.setBackgroundResource(R.color.colorAccent);

        }

        if (highestPrio == 1) {
            pushNotifiaction("Du hast noch Aufgaben zu erledgen!");
        } else if (highestPrio == 2) {
            pushNotifiaction("Sei mal kein Antim8 und hilf deiner WG!");
        } else {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    public void pushNotifiaction(String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("WG-Erinnerung")
                .setContentText(text)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            return;
        }
        mBuilder.build().notify();
    }

    void setValue(String text) {
        SharedPreferences.Editor editor = getSharedPreferences("abc", MODE_PRIVATE).edit();
        editor.putString("self", text);
        editor.apply();

    }

    String getValue() {
        SharedPreferences prefs = getSharedPreferences("abc", MODE_PRIVATE);
        String restoredText = prefs.getString("self", null);
        if (restoredText != null) {
            Log.d("sharedPref_string", restoredText);
        } else {
            Log.d("sharedPref_string", "not found");
        }
        return restoredText;
    }

    void createBackup() {
        for (DataSnapshot sn : dataSnapshotChores.getChildren()) {
            for (DataSnapshot snBackup : dataSnapshotBackup.getChildren()) {
                if (Objects.equals(sn.child("name").getValue(), snBackup.child("name").getValue())) {
                    snBackup.child("priority").getRef().setValue(sn.child("priority").getValue());
                    snBackup.child("turn").getRef().setValue(sn.child("turn").getValue());
                    break;
                }
            }
        }
    }

    void loadBackup() {
        for (DataSnapshot sn : dataSnapshotBackup.getChildren()) {
            for (DataSnapshot snBackup : dataSnapshotChores.getChildren()) {
                if (Objects.equals(sn.child("name").getValue(), snBackup.child("name").getValue())) {
                    snBackup.child("priority").getRef().setValue(sn.child("priority").getValue());
                    snBackup.child("turn").getRef().setValue(sn.child("turn").getValue());
                    break;
                }
            }
        }
    }
}