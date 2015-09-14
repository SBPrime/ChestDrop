/*
 * RandomChests a plugin that provides random loot chests
 * Copyright (c) 2015, SBPrime <https://github.com/SBPrime/>
 * Copyright (c) ChestDrop contributors
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
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.primesoft.chestDrop.ChestDropMain;
import static org.primesoft.chestDrop.ChestDropMain.say;
import org.primesoft.chestDrop.kits.Kit;
import org.primesoft.chestDrop.kits.KitProvider;
import org.primesoft.chestDrop.strings.MessageType;

/**
 *
 * @author SBPrime
 */
public class DropChestCommand  extends BaseCommand {

    private final ChestDropMain m_pluginMain;

    public DropChestCommand(ChestDropMain pluginMain) {
        m_pluginMain = pluginMain;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String name, String[] args) {
        if (args == null || args.length != 1) {
            return false;
        }

        Player p = cs instanceof Player ? (Player) cs : null;

        String kitName = args[0].toLowerCase();
        KitProvider kitProvider = m_pluginMain.getKitProvider();
        Kit kit = kitProvider.getKit(kitName);

        if (kit == null) {
            say(p, MessageType.CMD_DROP_NOT_FOUND.format(kitName));
            return true;
        }

        m_pluginMain.getDropManager().dropKit(kit);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String name, String[] args) {
        List<String> result = new ArrayList<String>();
        if (args != null && args.length != 1) {
            return result;
        }

        String kitName = args != null && args.length > 0 ? args[0].toLowerCase() : "";
        KitProvider kitProvider = m_pluginMain.getKitProvider();
        String[] kits = kitProvider.getKitsNames();
        for (String kit : kits) {
            if (kit.startsWith(kitName)) {
                result.add(kit);
            }
        }

        return result;
    }
}
