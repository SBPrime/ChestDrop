/*
 * RandomChests a plugin that provides random loot chests
 * Copyright (c) 2015, SBPrime <https://github.com/SBPrime/>
 * Copyright (c) RandomChests contributors
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted free of charge provided that the following 
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution,
 * 3. Redistributions of source code, with or without modification, in any form 
 *    other then free of charge is not allowed,
 * 4. Redistributions in binary form in any form other then free of charge is 
 *    not allowed.
 * 5. Any derived work based on or containing parts of this software must reproduce 
 *    the above copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided with the 
 *    derived work.
 * 6. The original author of the software is allowed to change the license 
 *    terms or the entire license of the software as he sees fit.
 * 7. The original author of the software is allowed to sublicense the software 
 *    or its parts using any license terms he sees fit.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.primesoft.chestDrop;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.chestDrop.commands.DropChestCommand;
import org.primesoft.utils.adapter.AdapterProvider;
import org.primesoft.utils.adapter.IUtilsAdapter;
import org.primesoft.utils.adapter.NullAdapter;
import org.primesoft.chestDrop.commands.DumpItemCommand;
import org.primesoft.chestDrop.commands.GiveKitCommand;
import org.primesoft.chestDrop.commands.ReloadCommand;
import org.primesoft.chestDrop.customItems.CustomItemProvider;
import org.primesoft.chestDrop.kits.KitProvider;

/**
 *
 * @author SBPrime
 */
public class ChestDropMain extends JavaPlugin {

    private static final Logger s_log = Logger.getLogger("Minecraft.MidiPlayer");
    private static String s_prefix = null;
    private static final String s_logFormat = "%s %s";

    /**
     * The instance of the class
     */
    private static ChestDropMain s_instance;

    /**
     * Send message to the log
     *
     * @param msg
     */
    public static void log(String msg) {
        if (s_log == null || msg == null || s_prefix == null) {
            return;
        }

        s_log.log(Level.INFO, String.format(s_logFormat, s_prefix, msg));
    }

    /**
     * Sent message directly to player
     *
     * @param player
     * @param msg
     */
    public static void say(Player player, String msg) {
        if (player == null) {
            log(msg);
        } else {
            player.sendRawMessage(msg);
        }
    }

    /**
     * The instance of the class
     *
     * @return
     */
    public static ChestDropMain getInstance() {
        return s_instance;
    }

    /**
     * The plugin version
     */
    private String m_version;

    /**
     * The native adapter
     */
    private IUtilsAdapter m_adapter;

    /**
     * The reload command handler
     */
    private ReloadCommand m_reloadCommandHandler;

    /**
     * The kit provider
     */
    private KitProvider m_kitProvider = new KitProvider();

    /**
     * The drop manager
     */
    private DropManager m_dropManager;

    /**
     * The custom item provider
     */
    private CustomItemProvider m_customItemProvider = new CustomItemProvider();

    public DropManager getDropManager() {
        return m_dropManager;
    }

    public CustomItemProvider getCustomItemProvider() {
        return m_customItemProvider;
    }

    public void setKitProvider(KitProvider kitProvider) {
        m_kitProvider = kitProvider != null ? kitProvider : new KitProvider();
    }

    public KitProvider getKitProvider() {
        return m_kitProvider;
    }

    public String getVersion() {
        return m_version;
    }

    /**
     * Get the native adapter
     *
     * @return
     */
    public IUtilsAdapter getAdapter() {
        return m_adapter;
    }

    @Override
    public void onEnable() {
        PluginDescriptionFile desc = getDescription();
        s_prefix = String.format("[%s]", desc.getName());
        s_instance = this;

        if (m_dropManager != null) {
            m_dropManager.clean();
        }
        m_dropManager = new DropManager(this);
        m_version = desc.getVersion();
        m_customItemProvider = new CustomItemProvider();

        InitializeCommands();

        if (!m_reloadCommandHandler.reloadConfig(null)) {
            log("Error loading config");
            return;
        }

        IUtilsAdapter adapter = AdapterProvider.get(getServer());
        if (adapter == null) {
            adapter = new NullAdapter();
        }
        m_adapter = adapter;

        log(String.format("Adapter set to: %1$s", m_adapter.getClass().getCanonicalName()));

        super.onEnable();
    }

    /**
     * Initialize the commands
     *
     * @return
     */
    private void InitializeCommands() {
        m_reloadCommandHandler = new ReloadCommand(this);
        DumpItemCommand dumpItemCommandHandler = new DumpItemCommand(this);
        DropChestCommand dropCommandHandler = new DropChestCommand(this);
        GiveKitCommand kitCommandHandler = new GiveKitCommand(this);

        PluginCommand commandReload = getCommand("cdreload");
        commandReload.setExecutor(m_reloadCommandHandler);

        PluginCommand commandPlayGlobal = getCommand("cddump");
        commandPlayGlobal.setExecutor(dumpItemCommandHandler);

        PluginCommand commandGive = getCommand("cddrop");
        commandGive.setExecutor(dropCommandHandler);

        PluginCommand commandKit = getCommand("cdrandom");
        commandKit.setExecutor(kitCommandHandler);
    }

    @Override
    public void onDisable() {
        m_dropManager.stop();

        super.onDisable();
    }
}
