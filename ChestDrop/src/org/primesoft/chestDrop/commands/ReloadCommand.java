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
package org.primesoft.chestDrop.commands;

import org.primesoft.utils.commands.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.primesoft.chestDrop.ChestDropMain;
import static org.primesoft.chestDrop.ChestDropMain.log;
import static org.primesoft.chestDrop.ChestDropMain.say;
import org.primesoft.chestDrop.kits.KitParser;
import org.primesoft.chestDrop.kits.KitProvider;
import org.primesoft.chestDrop.strings.MessageProvider;
import org.primesoft.chestDrop.strings.MessageType;

/**
 * Reload configuration command
 * @author SBPrime
 */
public class ReloadCommand extends BaseCommand {

    private final ChestDropMain m_pluginMain;

    public ReloadCommand(ChestDropMain pluginMain) {
        m_pluginMain = pluginMain;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String name, String[] args) {
        if (args != null && args.length > 0) {
            return false;
        }

        Player player = (cs instanceof Player) ? (Player) cs : null;

        m_pluginMain.reloadConfig();
        
        reloadConfig(player);
        
        return true;
    }
    
    
    /**
     * Reload the configuration
     * @param player
     * @return 
     */
    public boolean reloadConfig(Player player) {
        boolean result = initialiseStrings() &&
                initialiseKits();
        
        if (!result) {
            return false;
        }
        
        say(player, MessageType.CMD_RELOAD_DONE.format());
        return true;
    }
    
    private boolean initialiseKits() {
        if (!KitParser.saveDefault(m_pluginMain)) {
            log("Unable to save default kits to plugin folder.");
        }
        
        KitProvider kitProvider = KitParser.load(m_pluginMain);
        
        m_pluginMain.setKitProvider(kitProvider);
        
        return kitProvider != null;
    }
    
    /**
     * Initialise the strings
     */
    private boolean initialiseStrings() {
        if (!MessageProvider.saveDefault(m_pluginMain)) {
            log("Unable to save english.yml to plugin folder.");
        }

        boolean lDefault = MessageProvider.loadDefault(m_pluginMain);
        boolean lCustom = MessageProvider.loadFile(m_pluginMain.getDataFolder(), "strings.yml");
        
        if (!lDefault) {
            log("Error loading default strings file, no internal fallback available!.");
        }
        if (!lCustom) {
            log("Error loading strings file, using internal fallback.");
        }
        
        return lCustom || lDefault;
    }
}
