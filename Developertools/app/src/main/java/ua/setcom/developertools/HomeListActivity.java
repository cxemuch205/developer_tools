package ua.setcom.developertools;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import ua.setcom.developertools.Helpers.ExecuteAsRootBase;


public class HomeListActivity extends ActionBarActivity {

    private static final String TAG = "HomeListActivity";

    private TextView tvMsg;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);
        tvMsg = (TextView) findViewById(R.id.tv_progress_msg);
        pb = (ProgressBar) findViewById(R.id.pb_load);
        tvMsg.setText(getString(R.string.wait_getting_su_permission));
        new Thread(new Runnable() {
            @Override
            public void run() {
                ExecuteAsRootBase.canRunRootCommands();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(ProgressBar.GONE);
                        tvMsg.setVisibility(TextView.GONE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PackageManager p = getPackageManager();
                                ComponentName componentName = new ComponentName(HomeListActivity.this, ua.setcom.developertools.HomeListActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
                                p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                                finish();
                            }
                        }, 500);
                    }
                });
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
