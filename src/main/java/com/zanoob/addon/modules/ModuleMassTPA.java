package com.zanoob.addon.modules;

import com.zanoob.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.StringHelper;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleMassTPA extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = this.settings.createGroup("Render");

    /**
     * Example setting.
     * The {@code name} parameter should be in kebab-case.
     * If you want to access the setting from another class, simply make the setting {@code public}, and use
     * {@link meteordevelopment.meteorclient.systems.modules.Modules#get(Class)} to access the {@link Module} object.
     */
    private final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("The size of the marker.")
        .defaultValue(10.0d)
        .range(5d, 30.0d)
        .build()
    );

    private final Setting<SettingColor> color = sgRender.add(new ColorSetting.Builder()
        .name("color")
        .description("The color of the marker.")
        .defaultValue(Color.MAGENTA)
        .build()
    );

    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public ModuleMassTPA() {
        super(AddonTemplate.CATEGORY, "masstp", "tpa to all on the server");
    }

    private CopyOnWriteArrayList<PlayerListEntry> players;
    private final ArrayList<String> player = new ArrayList<>();
    String playerName = mc.getSession().getUsername();

    private int index = 0;
    private double timer=0;
    private String command = "/tpa";

    @Override
    public void onActivate() {

        player.clear();
        index = 0;
        timer = 0;


        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()){
            String name = entry.getProfile().getName();
            name = StringHelper.stripTextFormat(name);

            if(name.equalsIgnoreCase(playerName))
                continue;

            player.add(name);
        }

    }

    @Override
    public void onDeactivate() {
        player.clear();
    }


    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if(timer > 0)
        {
            timer--;
            return;
        }

        if(index >= player.size())
        {
            toggle();
            return;
        }

        ChatUtils.sendPlayerMsg(command + " " + player.get(index));

        index++;
        timer = (delay.get() * 2) - 1.0;

    }

//    @EventHandler
//    private void onReceivedMessage(ReceiveMessageEvent event){
//        String message = event.toString().toLowerCase();
//        message = StringHelper.stripTextFormat(message);
//        if (message.contains("accepted") && message.contains("request")){
//            toggle();
//        }
//    }

    @EventHandler
    private void onScreenOpen(OpenScreenEvent event) {
        if (event.screen instanceof DisconnectedScreen) {toggle();}
    }
    @EventHandler
    private void onGameLeft(GameLeftEvent event) {toggle();}

}

