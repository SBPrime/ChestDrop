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
package org.primesoft.chestDrop.customItems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import static org.primesoft.chestDrop.ChestDropMain.log;
import org.primesoft.utils.ExceptionHelper;

/**
 *
 * @author SBPrime
 */
public class CustomItemProvider {

    /**
     * List of all custom items
     */
    private final HashMap<String, IItemProvider> m_customItems = new HashMap<String, IItemProvider>();

    /**
     * Register custom item provider for provided item names
     *
     * @param provider Custom item provider
     * @param itemNames Item names (cass insensitive)
     */
    public void register(IItemProvider provider, String... itemNames) {
        if (provider == null || itemNames == null || itemNames.length == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;

        synchronized (m_customItems) {
            for (String name : itemNames) {
                name = name.toLowerCase();
                m_customItems.put(name, provider);

                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(name);
            }
        }

        log(String.format("CustomItems for %1$s: %2$s", provider.getName(), sb.toString()));
    }

    /**
     * Remove custom item provider
     *
     * @param provider
     */
    public void removeProvider(IItemProvider provider) {
        if (provider == null) {
            return;
        }

        final HashSet<String> toRemove = new LinkedHashSet<String>();
        synchronized (m_customItems) {
            for (Map.Entry<String, IItemProvider> entrySet : m_customItems.entrySet()) {
                String name = entrySet.getKey();
                IItemProvider k = entrySet.getValue();
                
                if (k == provider) {
                    toRemove.add(name);
                }
            }
            
            for (String name  :toRemove) {
                m_customItems.remove(name);
            }
        }
    }

    /**
     * Create a custom item stack
     *
     * @param itemName custom item name
     * @param material material data
     * @param data data
     * @param amount stack size
     * @return
     */
    public ItemStack getCustomItem(String itemName,
            Material material, short data, int amount) {
        if (itemName == null) {
            return null;
        }

        itemName = itemName.toLowerCase();
        IItemProvider provider;
        synchronized (m_customItems) {
            if (m_customItems.containsKey(itemName)) {
                log(String.format("Unable to find provider for %1$s.", itemName));
                return null;
            }

            provider = m_customItems.get(itemName);
        }

        try {
            return provider.getCustomItem(itemName, material, data, amount);
        } catch (Exception ex) {
            ExceptionHelper.printException(ex, "Unable to create custom item");
            return null;
        }
    }
}
