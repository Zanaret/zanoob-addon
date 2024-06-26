package com.zanoob.addon;

import com.zanoob.addon.commands.CommandExample;
import com.zanoob.addon.hud.HudExample;
import com.zanoob.addon.modules.ModuleMassTPA;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("zanoob");
    public static final HudGroup HUD_GROUP = new HudGroup("zanoob");

    @Override
    public void onInitialize() {
        LOG.info("zanoob addon initialized");

        // Modules
        //Modules.get().add(new ModuleExample());
        Modules.get().add(new ModuleMassTPA());

        // Commands
        Commands.add(new CommandExample());

        // HUD
        Hud.get().register(HudExample.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.zanoob.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("Zanaret", "zanoob-addon");
    }
}
