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
package org.primesoft.chestDrop.kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.primesoft.chestDrop.customItems.CustomItemProvider;
import org.primesoft.utils.adapter.IUtilsAdapter;

/**
 *
 * @author SBPrime
 */
public class Kit {

    /**
     * Random number generator
     */
    private final static Random s_rnd = new Random();

    /**
     * The kit name
     */
    private final String m_name;

    /**
     * The display name
     */
    private final String m_displayName;

    /**
     * The kit items
     */
    private final KitItem[] m_items;

    /**
     * Kit items percentage
     */
    private final int[] m_percentage;

    /**
     * Max random value
     */
    private final int m_maxValue;

    /**
     * Number of chests to drop
     */
    private final int m_chests;

    /**
     * The interval
     */
    private final int m_interval;

    /**
     * The world
     */
    private final World m_world;

    /**
     * The from vector
     */
    private final Vector m_from;

    /**
     * The to vector
     */
    private final Vector m_to;

    /**
     * The old drop blocks
     */
    private final List<Location> m_oldDrop;

    /**
     * The last drop time
     */
    private long m_lastDrop;

    /**
     * How long do the chest live for
     */
    private final int m_live;

    /**
     * Minimum number of items
     */
    private final int m_minItems;

    /**
     * Maximum number of items
     */
    private final int m_maxItems;
    
    /**
     * The group name
     */
    private final String m_groupName;

    /**
     * Get the kit name
     *
     * @return
     */
    public String getName() {
        return m_name;
    }

    /**
     * Get the display name
     *
     * @return
     */
    public String getDisplayName() {
        return m_displayName;
    }

    /**
     * Get the number of chests
     *
     * @return
     */
    public int getNumberOfChests() {
        return m_chests;
    }

    /**
     * The chest drop interval
     *
     * @return
     */
    public int getInterval() {
        return m_interval;
    }

    /**
     * The min coords
     *
     * @return
     */
    public Vector getMin() {
        return m_from;
    }

    /**
     * Get the max coords
     *
     * @return
     */
    public Vector getMax() {
        return m_to;
    }

    /**
     * Get the world
     *
     * @return
     */
    public World getWorld() {
        return m_world;
    }
    
    /**
     * Get the group name
     * @return 
     */
    public String getGroupName() {
        return m_groupName;
    }

    Kit(String name, String displayName, String groupName,
            KitItem[] items,
            int chests, int minItems, int maxItems,
            int interval, int live,
            Vector from, Vector to, World world) {
        m_lastDrop = System.currentTimeMillis();
        m_oldDrop = new ArrayList<Location>();
        m_displayName = displayName;
        m_groupName = groupName;
        m_chests = chests;
        m_interval = interval;
        m_minItems = minItems;
        m_maxItems = maxItems;
        m_live = live;
        m_from = from;
        m_to = to;
        m_world = world;
        m_name = name;
        m_items = items;
        m_percentage = new int[m_items.length];

        int sum = 0;
        for (int i = 0; i < m_items.length; i++) {
            sum += m_items[i].getPercentage();

            m_percentage[i] = sum;
        }

        m_maxValue = sum;
    }

    /**
     * Get a random item from kit
     *
     * @return
     */
    public KitItem getRandomItem() {
        int value = s_rnd.nextInt(m_maxValue);

        for (int i = 0; i < m_percentage.length; i++) {
            if (m_percentage[i] >= value) {
                return m_items[i];
            }
        }

        return m_items[0];
    }

    /**
     * Get the chest locations
     *
     * @return
     */
    public Location[] getChestLocations() {
        synchronized (m_oldDrop) {
            return m_oldDrop.toArray(new Location[0]);
        }
    }

    /**
     * Remove the location from drops
     *
     * @param l
     */
    public void removeLocation(Location l) {
        if (l == null) {
            return;
        }
        synchronized (m_oldDrop) {
            m_oldDrop.remove(l);
        }
    }

    /**
     * Drop random chests
     *
     * @param adapter
     * @param itemProvider
     * @return
     */
    public Location[] dropChests(IUtilsAdapter adapter, CustomItemProvider itemProvider) {
        List<Location> newLocations = new ArrayList<Location>();

        m_lastDrop = System.currentTimeMillis();

        int minX = Math.min(m_from.getBlockX(), m_to.getBlockX());
        int minY = Math.min(m_from.getBlockY(), m_to.getBlockY());
        int minZ = Math.min(m_from.getBlockZ(), m_to.getBlockZ());

        int maxX = Math.max(m_from.getBlockX(), m_to.getBlockX());
        int maxY = Math.max(m_from.getBlockY(), m_to.getBlockY());
        int maxZ = Math.max(m_from.getBlockZ(), m_to.getBlockZ());

        for (int i = 0; i < m_chests; i++) {
            int x = s_rnd.nextInt(maxX - minX) + minX;
            int z = s_rnd.nextInt(maxZ - minZ) + minZ;

            int y = maxY;
            Location l = null;
            Material m = Material.AIR;
            for (; y >= minY && (m == Material.AIR || m == Material.WATER || m == Material.STATIONARY_WATER); y--) {
                l = new Location(m_world, x, y, z);
                m = l.getBlock().getType();
            }

            if (y < minY || l == null) {
                continue;
            }

            newLocations.add(l.add(0, 1, 0));
        }

        synchronized (m_oldDrop) {
            for (Location l : newLocations) {
                int cnt = s_rnd.nextInt(m_maxItems - m_minItems) + m_minItems;
                for (int i = 0; i < cnt; i++) {
                    KitItem item = getRandomItem();
                    if (item == null) {
                        continue;
                    }

                    ItemStack iStack = item.getItemStack(adapter, itemProvider);

                    Block block = l.getBlock();
                    block.setType(Material.CHEST);
                    Chest chest = (Chest) block.getState();

                    Inventory inv = chest.getInventory();
                    int idx = inv.firstEmpty();
                    if (idx < 0) {
                        break;
                    }
                    inv.setItem(idx, iStack);
                }
                m_oldDrop.add(l);
            }
        }

        return newLocations.toArray(new Location[0]);
    }

    /**
     * Is the kit drop dead
     *
     * @param now
     * @return
     */
    public boolean isDead(long now) {
        return (now - m_lastDrop) > m_live * 1000l;
    }

    public boolean canSpawn(long now) {
        return (now - m_lastDrop) > m_interval * 1000l;
    }
}
