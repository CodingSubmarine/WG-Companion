package braess.constantin.wgterminal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import braess.constantin.wgcompanion.R;

public class todo extends AppCompatActivity {

    public List<String> todoList = new ArrayList<>();
    public ListView todoListView;
    private DataSnapshot TodoSnapchot;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_chores:
                    Intent choreIntent = new Intent(getApplicationContext(), MainActivity.class);
                    choreIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(choreIntent);
                    return true;
                case R.id.navigation_todo:
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_undefined:
                    //mTextMessage.setText(R.string.title_notifications);
                    //return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);


        todoListView = findViewById(R.id.todoList);
        todoList.add("Test Standard");

        final FirebaseDatabase database = Utils.getDatabase();
        final DatabaseReference todoRef = database.getReference("Todo");

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_todo);

        todoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //todoList.clear();
                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                    todoList.add((String) sn.child("name").getValue());
                }
                refreshList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    void refreshList() {
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, todoList) {
            @NonNull
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                // Get the current item from ListView
                // FIXME: 03.08.2018 Null Pointer Exception
                TextView view = (TextView) super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLACK);
                //view.setBackground(getContext().getDrawable(R.drawable.listview_item_border));
                if (todoList.size() != 0) {
                    view.setGravity(Gravity.CENTER);
                }
                return view;
            }
        };
        todoListView.setAdapter(arrayAdapter1);
    }


}
