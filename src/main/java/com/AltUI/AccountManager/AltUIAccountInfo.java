package com.AltUI.AccountManager;

import com.AltUI.LootItem;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;

public class AltUIAccountInfo
{
    public String username;
    public int health;
    public int prayer;
    public int backpackSpaces;
    public ArrayList<LootItem> items;
    public boolean atAltarOrBank;
    public int GPhr;
    public int profit;

    //variable has to do with the fromString function
    public final int itemsStartAt = 5;

    public final WorldArea[] altarOrBank = {
                new WorldArea(3601, 3353, 8, 6,0),
                new WorldArea(3601, 3365, 8, 5,0)
    };

    public AltUIAccountInfo(String username, int health, int prayer, int backpackSpaces, ArrayList<LootItem> items, WorldPoint area, int profit, int GPhr) {
        this.username = username;
        this.health = health;
        this.prayer = prayer;
        this.backpackSpaces = backpackSpaces;
        this.items = items;
        this.profit = profit;
        this.GPhr = GPhr;

        this.atAltarOrBank = (area.isInArea2D(altarOrBank[0]) || area.isInArea2D(altarOrBank[1]));
    }

    private boolean collision(int x, int y, int w, int h, int x2, int y2) {
        return x + w >= x2 && y + h >= y2 && x2 >= x && y2 >= y;
    }

    public AltUIAccountInfo(String dataFromString) {
        fromString(dataFromString);
    }

    @Override
    public String toString() {
        //https://stackoverflow.com/questions/32987478/produce-a-comma-separated-list-using-stringbuilder
        String allToString = "";
        String separator = "";
        for(LootItem i : items) {
            allToString += separator + i.toString();
            separator = ",";
        }
        return username + "," + health + "," + prayer + "," + backpackSpaces + "," + atAltarOrBank + "," + profit + "," + GPhr + "," + allToString;
    }
    public void fromString(String string) {
        String[] split = string.split(",");
        this.username = split[0];
        this.health = Integer.parseInt(split[1]);
        this.prayer = Integer.parseInt(split[2]);
        this.backpackSpaces = Integer.parseInt(split[3]);
        this.atAltarOrBank = Boolean.parseBoolean(split[4]);
        this.profit = Integer.parseInt(split[5]);
        this.GPhr = Integer.parseInt(split[6]);

        items = new ArrayList<LootItem>();

        for(int i = 7; i < split.length; i++) {
            items.add(new LootItem(split[i]));
        }
    }
}
