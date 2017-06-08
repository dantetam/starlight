package io.github.dantetam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.currentplacedetailsonmap.R;

import io.github.dantetam.world.Settlement;

public class SettlementBuildingsActivity extends AppCompatActivity {

    public Settlement settlement;

    private StarlightSurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            settlement = (Settlement) bundle.getSerializable("settlementData");
        }

        setContentView(R.layout.activity_settlement_buildings);

        //surfaceView = (StarlightSurfaceView) ((RelativeLayout) findViewById(R.id.gameLayout)).getChildAt(0);

        //mHandler = new Handler();
        //startRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settlement_buildings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*private int mInterval = 3000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (surfaceView != null) {
                    surfaceView.drawSettlement();
                }
                else {
                    surfaceView = (StarlightSurfaceView) ((RelativeLayout) findViewById(R.id.gameLayout)).getChildAt(0);
                }
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void startRepeatingTask() {
        mStatusChecker.run();
    }

    private void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }*/

}
