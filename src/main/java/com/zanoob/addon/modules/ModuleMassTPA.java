package com.zanoob.addon.modules;

import com.zanoob.addon.MainAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleMassTPA extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgNames = settings.createGroup("Names");

    /**
     * Example setting.
     * The {@code name} parameter should be in kebab-case.
     * If you want to access the setting from another class, simply make the setting {@code public}, and use
     * {@link meteordevelopment.meteorclient.systems.modules.Modules#get(Class)} to access the {@link Module} object.
     *
     */



    private final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("delay time between tpa request.")
        .defaultValue(10.0d)
        .range(5d, 30.0d)
        .sliderMax(30d)
        .sliderMin(5d)
        .build()
    );



    private enum requestType {
        RANDOMIZED,
        INORDER,
        ENDTOFRONT

    }

    private final Setting<requestType> requestOrder = sgGeneral.add(new EnumSetting.Builder<requestType>()
            .name("List of names order")
            .description("order of names to send tpa")
            .defaultValue(requestType.INORDER)
            .build()
    );
    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public ModuleMassTPA() {
        super(MainAddon.CATEGORY, "masstp", "tpa to all on the server");
    }

    public final ArrayList<String> players = new ArrayList<>();
    String playerName = mc.getSession().getUsername();

    private int index = 0;
    private double timer=0;
    private final String command = "/tpa";

    @Override
    public void onActivate() {

        players.clear();
        index = 0;
        timer = 0;


        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()){
            String name = entry.getProfile().getName();
            name = StringHelper.stripTextFormat(name);

            if(name.equalsIgnoreCase(playerName))
                continue;

            players.add(name);
        }

        switch (requestOrder.get()){
            case RANDOMIZED:
                Collections.shuffle(players);
                break;
            case ENDTOFRONT:
                Collections.reverse(players);
                break;
            case INORDER:
                Collections.sort(players);
                break;
        }


    }



    @Override
    public void onDeactivate() {
        players.clear();
    }


    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if(timer > 0)
        {
            timer--;
            return;
        }

        if(index >= players.size())
        {
            toggle();
            return;
        }

        //ChatUtils.sendPlayerMsg(command + " " + players.get(index));

        index++;
        timer = (delay.get() * 2) - 1.0;

    }

    @EventHandler
    private void onReceivedMessage(ReceiveMessageEvent event){
        String message = event.toString().toLowerCase();
        message = StringHelper.stripTextFormat(message);
        if (message.contains("accepted") && message.contains("request")){
            ChatUtils.sendPlayerMsg("/tpacancel");
            toggle();
        }
    }

    @EventHandler
    private void onScreenOpen(OpenScreenEvent event) {
        if (event.screen instanceof DisconnectedScreen) {toggle();}
    }
    @EventHandler
    private void onGameLeft(GameLeftEvent event) {toggle();}

}

