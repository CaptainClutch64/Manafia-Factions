package com.massivecraft.factions.lunar;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.Util;


public class Lunar {




    public static void lunarSetup() {

        if (FactionsPlugin.getInstance().getFileManager().getLunar().getConfig().getBoolean("Integration.Enabled", true))

        System.out.println("Initializing lunar integration...");
        Util.checkLunar();
        System.out.println("WARNING: This is still in beta! Use at own risk. Many things may not work!");





    }

}
