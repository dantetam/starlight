package com.example.currentplacedetailsonmap;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Dante on 5/18/2017.
 */
public class Settlement implements Serializable { //implements Parcelable {

    public Vector2f realGeoCoord, gameCoord;
    public Tile[][] tiles;
    public int numPeople;
    public String name;

    public Settlement(String name, Vector2f realGeoCoord, Vector2f gameCoord, int r, int c) {
        this.name = name;
        this.realGeoCoord = realGeoCoord;
        this.gameCoord = gameCoord;
        tiles = new Tile[r][c];
        numPeople = 10;
    }

    public void initializeSettlement(int[][] resources) {
        if (resources.length != tiles.length || tiles.length == 0) {
            throw new IllegalArgumentException("Resources array not aligned with world or world is of size 0");
        }
        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles[0].length; c++) {
                tiles[r][c] = new Tile(r,c);
                tiles[r][c].tileType = resources[r][c];
            }
        }
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    public static final Parcelable.Creator<MyParcelable> CREATOR
            = new Parcelable.Creator<MyParcelable>() {
        public MyParcelable createFromParcel(Parcel in) {
            return new MyParcelable(in);
        }

        public MyParcelable[] newArray(int size) {
            return new MyParcelable[size];
        }
    };

    private MyParcelable(Parcel in) {
        mData = in.readInt();
    }*/

}
