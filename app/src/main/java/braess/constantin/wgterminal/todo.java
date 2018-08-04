package braess.constantin.wgterminal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

    public List<TodoElement> todoList = new ArrayList<>();
    public ListView todoListView;
    private DataSnapshot todoSnapshot;

    public String task;

    private FloatingActionButton addTodoButton;

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


        addTodoButton = findViewById(R.id.addTodoButton);
        todoListView = findViewById(R.id.todoList);
        todoList.clear();

        final FirebaseDatabase database = Utils.getDatabase();
        final DatabaseReference todoRef = database.getReference("Todo");

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_todo);

        todoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todoList.clear();
                todoSnapshot = dataSnapshot;
                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                    String name = (String) sn.child("name").getValue();
                    assert name != null;
                    if (!name.equals("2a1fd6da")) {
                        boolean state = true;
                        if (sn.child("state").getValue() != null) {
                            state = sn.child("state").getValue(Boolean.class);
                        }
                        TodoElement todoelement = new TodoElement(name, state);
                        todoList.add(todoelement);
                    }
                }
                refreshList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        todoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataSnapshot sn = getSnapshot(todoList.get(i));
                String deleteKey = sn.getKey();
                database.getReference("Todo").child(deleteKey).removeValue();
                return false;
            }
        });

        todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //todoList.get(i).state = !todoList.get(i).state;
                //sortTodoList();
                DataSnapshot sn = getSnapshot(todoList.get(i));
                sn.child("state").getRef().setValue(!todoList.get(i).state);
                refreshList();
            }
        });

        addTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog(todo.this);
            }
        });
    }

    private DataSnapshot getSnapshot(TodoElement todoElement) {
        for (DataSnapshot sn : todoSnapshot.getChildren()) {
            if (todoElement.name.equals(sn.child("name").getValue())) {
                return sn;
            }
        }
        return null;
    }

    void refreshList() {
        sortTodoList();
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, getNameList(todoList)) {
            @NonNull
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                // Get the current item from ListView
                // FIXME: 03.08.2018 Null Pointer Exception
                TextView view = (TextView) super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                if (todoList.get(position).state) {
                    textView.setTextColor(Color.BLACK);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.GRAY);
                }
                //view.setBackground(getContext().getDrawable(R.drawable.listview_item_border));
                if (todoList.size() != 0) {
                    view.setGravity(Gravity.CENTER);
                }
                return view;
            }
        };
        todoListView.setAdapter(arrayAdapter1);
    }


    private void sortTodoList() {
        for (int i = 0; i < todoList.size(); i++) {
            for (int j = 0; j < todoList.size() - 1; j++) {
                if (!todoList.get(j).state && todoList.get(j + 1).state) {
                    TodoElement tmp = todoList.get(j);
                    todoList.set(j, todoList.get(j + 1));
                    todoList.set(j + 1, tmp);
                }
            }
        }
    }

    private List<String> getNameList(List<TodoElement> todoList) {
        List<String> stringList = new ArrayList<>();
        for (TodoElement todoElement : todoList) {
            if (!todoElement.name.equals("2a1fd6da")) {
                stringList.add(todoElement.name);
            }
        }
        return stringList;
    }

    private void showAddItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new task")
                .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase database = Utils.getDatabase();
                        String name = String.valueOf(taskEditText.getText());
                        String todoID = database.getReference("Todo").push().getKey();
                        database.getReference("Todo").child(todoID).child("name").setValue(name);
                        database.getReference("Todo").child(todoID).child("state").setValue(true);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}
