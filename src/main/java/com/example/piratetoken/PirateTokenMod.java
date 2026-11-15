package com.example.piratetoken;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = PirateTokenMod.MODID, name = "Pirate Shop Access", version = "1.0",
        acceptedMinecraftVersions = "[1.8.9]", acceptableRemoteVersions = "*")
public class PirateTokenMod {
    public static final String MODID = "piratetoken";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ChestHandler());
    }
}
