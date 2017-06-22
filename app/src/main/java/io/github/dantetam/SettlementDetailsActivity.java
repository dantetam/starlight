package io.github.dantetam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.github.dantetam.R;

import io.github.dantetam.world.Settlement;

import java.util.ArrayList;

public class SettlementDetailsActivity extends AppCompatActivity {

    private Settlement settlement;
    private ArrayList<String> address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement_details);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            settlement = (Settlement) b.getSerializable("settlementData");
            address = (ArrayList<String>) b.getSerializable("settlementAddress");

            ((TextView) findViewById(R.id.settlementName)).setText(settlement.name);
            ((TextView) findViewById(R.id.settlementFoundDate)).setText("Founded " + settlement.formattedDate);
            ((TextView) findViewById(R.id.settlementPopulation)).setText("Population " + settlement.people.size());

            if (address != null) {
                String addrString = "";
                for (String stringy: address) {
                    addrString += stringy + ", ";
                }
                if (!addrString.isEmpty()) {
                    addrString = addrString.substring(0, addrString.length() - 2);
                }
                ((TextView) findViewById(R.id.settlementAddress)).setText(addrString);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settlement_details, menu);
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

    public void openPeopleMenuButton(View v) {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("settlementData", settlement);
        i.putExtras(b);
        i.setClass(this, SettlementPeopleActivity.class);
        startActivity(i);
    }

    public void openBuildingsMenuButton(View v) {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("settlementData", settlement);
        i.putExtras(b);
        i.setClass(this, SettlementBuildingsActivity.class);
        startActivity(i);
    }

}
