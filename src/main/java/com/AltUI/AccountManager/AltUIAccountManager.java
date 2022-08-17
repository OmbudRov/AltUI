package com.AltUI.AccountManager;

import com.AltUI.LootItem;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;

@Slf4j
public class AltUIAccountManager
{

    private final File directory;

    private final int tooOldAge = 5000;

    public AltUIAccountManager(File qoltingDirectory) {
        directory = qoltingDirectory;
        verifyQoltingDirectory();
    }

    public ArrayList<AltUIAccountInfo> getAllAccountInfo() {
        verifyQoltingDirectory();

        ArrayList<AltUIAccountInfo> out = new ArrayList<>();
        if(directory.listFiles() == null) {
            return out;
        }
        for(File f : directory.listFiles()) {
            if(Instant.now().toEpochMilli() - f.lastModified() >= tooOldAge) {
                continue;
            }
            try {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                out.add(new AltUIAccountInfo(reader.readLine()));
                reader.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        return out;
    }

    //Returns false if unable to save to file
    public boolean saveAccountInfo(String name, int prayer, int health, int backpackSpace, ArrayList<LootItem> groundItems, WorldPoint playerPosition, int profit, int GPhr) {
        verifyQoltingDirectory();

        AltUIAccountInfo info = new AltUIAccountInfo(name,health,prayer,backpackSpace,groundItems, playerPosition,profit, GPhr);

        File file = new File(directory,name + ".txt");

        try {
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(info.toString());
            writer.close();

        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }

    //Returns true if already exists, false if directory created
    public boolean verifyQoltingDirectory() {
        return !directory.mkdir();
    }
}
