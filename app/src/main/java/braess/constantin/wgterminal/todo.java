package braess.constantin.wgterminal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import braess.constantin.wgcompanion.R;

public class todo extends AppCompatActivity {

    private TextView mTextMessage;

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
                    mTextMessage.setText(R.string.title_dashboard);
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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_todo);
    }
}
