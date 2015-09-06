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
package org.primesoft.chestDrop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import static org.primesoft.chestDrop.ChestDropMain.log;
import static org.primesoft.chestDrop.ChestDropMain.say;
import org.primesoft.chestDrop.kits.Kit;
import org.primesoft.chestDrop.strings.MessageProvider;
import org.primesoft.chestDrop.strings.MessageType;

/**
 *
 * @author SBPrime
 */
public class DropManager implements Runnable, Listener {

    private final static int SECOND = 20;

    private final ChestDropMain m_parent;
    private final BukkitScheduler m_scheduler;
    private final BukkitTask m_task;
    private final HashMap<Location, Kit> m_chestLocations = new LinkedHashMap<Location, Kit>();
    private final HashMap<Location, InventoryHolder> m_removedChests = new LinkedHashMap<Location, InventoryHolder>();

    DropManager(ChestDropMain main) {
        m_parent = main;

        Server server = m_parent.getServer();

        server.getPluginManager().registerEvents(this, main);
        m_scheduler = server.getScheduler();

        m_task = m_scheduler.runTaskTimer(main, this, SECOND, SECOND);
    }

    public void stop() {
        m_scheduler.cancelTask(m_task.getTaskId());
        clean();
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        HashSet<Kit> kits = new HashSet<Kit>();
        synchronized (m_chestLocations) {
            for (Kit k : m_chestLocations.values()) {
                if (!kits.contains(k)) {
                    kits.add(k);
                }
            }
        }

        for (Kit k : kits) {
            if (k.isDead(now)) {
                for (Location l : k.getChestLocations()) {
                    removeChest(l, false);
                }
            }
        }

        for (Kit k : m_parent.getKitProvider().getKits()) {
            if (k.canSpawn(now)) {
                dropKit(k);
            }
        }
    }

    /**
     * Handle player close chest
     *
     * @param e
     */
    @EventHandler
    public void OnPlayerCloseChest(InventoryCloseEvent e) {
        synchronized (m_chestLocations) {
            HumanEntity p = e.getPlayer();
            InventoryHolder eih = e.getInventory().getHolder();
            InventoryHolder[] invHolders;
            Location l;

            boolean found = false;
            for (InventoryHolder t : m_removedChests.values()) {
                if (t.equals(eih)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return;
            }

            if (eih instanceof Chest) {
                Chest chest = (Chest) eih;
                invHolders = new InventoryHolder[]{eih};
                l = chest.getLocation();
            } else if (eih instanceof DoubleChest) {
                DoubleChest chest = (DoubleChest) eih;
                invHolders = new InventoryHolder[]{
                    chest.getLeftSide(), chest.getRightSide()
                };

                l = chest.getLocation();
            } else {
                return;
            }

            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();
            World w = l.getWorld();
            l = new Location(w, x, y, z);

            if (!m_chestLocations.containsKey(l)) {
                return;
            }

            for (InventoryHolder ih : invHolders) {
                Inventory inv = ih.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        return;
                    }
                }
            }

            for (int i = -1; i < 2; i++) {
                removeChest(new Location(w, x + i, y, z), true);
                removeChest(new Location(w, x, y, z + i), true);
            }
        }
    }

    @EventHandler
    public void OnBreakBlock(BlockBreakEvent e) {
        synchronized (m_chestLocations) {
            HumanEntity p = e.getPlayer();
            Block b = e.getBlock();

            if (b == null || b.getType() != Material.CHEST) {
                return;
            }

            Location l = b.getLocation();

            if (!m_chestLocations.containsKey(l)) {
                return;
            }

            e.setCancelled(true);
        }
    }

    /**
     * Handle player open chest
     *
     * @param e
     */
    @EventHandler
    public void OnPlayerOpenChest(InventoryOpenEvent e) {
        synchronized (m_chestLocations) {
            HumanEntity p = e.getPlayer();
            InventoryHolder ih = e.getInventory().getHolder();
            Location l;

            if (ih instanceof Chest) {
                l = ((Chest) ih).getLocation();
            } else if (ih instanceof DoubleChest) {
                l = ((DoubleChest) ih).getLocation();
            } else {
                return;
            }

            if (!m_chestLocations.containsKey(l)) {
                return;
            }

            if (p == null || !p.hasPermission("chestdrop.use")) {
                e.setCancelled(true);
            }

            m_removedChests.put(l, ih);
        }
    }

    /**
     * Clean all kit entries
     */
    public void clean() {
        synchronized (m_chestLocations) {
            for (Location l : m_chestLocations.keySet().toArray(new Location[0])) {
                removeChest(l, false);
            }
            m_chestLocations.clear();
        }
    }

    /**
     * Force kit drop
     *
     * @param kit
     */
    public void dropKit(Kit kit) {
        if (kit == null) {
            return;
        }

        final Location[] toRemove = kit.getChestLocations();
        final Location[] toAdd = kit.dropChests(m_parent.getAdapter(), m_parent.getCustomItemProvider());

        synchronized (m_chestLocations) {
            for (Location l : toRemove) {
                removeChest(l, false);
                m_chestLocations.remove(l);
            }

            for (Location l : toAdd) {
                m_chestLocations.put(l, kit);
            }
        }

        String msg = MessageProvider.formatMessage(MessageType.CMD_DROP, kit.getDisplayName());
        for (Player p : m_parent.getServer().getOnlinePlayers()) {
            say(p, msg);
        }
        log(msg);
    }

    /**
     * Remove the chest on location
     *
     * @param l
     * @param dropItems
     */
    private void removeChest(Location l, boolean dropItems) {
        synchronized (m_chestLocations) {
            Kit k = m_chestLocations.get(l);
            if (k != null) {
                k.removeLocation(l);
                m_chestLocations.remove(l);

                Block b = l.getBlock();
                if (b.getType() == Material.CHEST) {
                    if (!dropItems) {
                        Chest chest = (Chest) b.getState();
                        chest.getBlockInventory().clear();
                    }
                    b.setType(Material.AIR);
                }
            }

            if (m_removedChests.containsKey(l)) {
                m_removedChests.remove(l);
            }
        }
    }
}
