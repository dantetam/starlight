package com.example.currentplacedetailsonmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class SettlementBuildingsActivity extends AppCompatActivity {

    private Settlement settlement;

    private Drawable mCustomImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement_buildings);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            settlement = (Settlement) bundle.getSerializable("settlementData");
        }

        mCustomImage = this.getResources().getDrawable(R.drawable.forest_texture);
    }

    protected void onDraw(Canvas canvas) {
        Rect imageBounds = canvas.getClipBounds();  // Adjust this for where you want it

        mCustomImage.setBounds(imageBounds);
        mCustomImage.draw(canvas);
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
}
