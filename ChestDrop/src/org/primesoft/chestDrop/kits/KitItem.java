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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.primesoft.utils.adapter.IUtilsAdapter;
import org.primesoft.chestDrop.customItems.CustomItemProvider;
import org.primesoft.utils.nbt.TagBase;

/**
 *
 * @author SBPrime
 */
public class KitItem {
    /**
     * Random occurance percentage
     */
    private final int m_percentage;
    
    /**
     * The item material
     */
    private final Material m_material;
    
    /**
     * The material data
     */
    private final short m_data;
    
    /**
     * Stack size
     */
    private final int m_ammount;
    
    /**
     * Optional NBT data
     */
    private final TagBase m_nbt;
    
    
    /**
     * Item custom name
     */
    private final String m_customName;
    
    /**
     * Get random occurance percentage
     * @return 
     */
    public int getPercentage() {
        return m_percentage;
    }
    

    /**
     * Get kit itemstack
     * @param adapter
     * @param itemProvider
     * @return 
     */
    public ItemStack getItemStack(IUtilsAdapter adapter, CustomItemProvider itemProvider) {
        ItemStack is;
        
        if (itemProvider != null && m_customName != null) {
            is = itemProvider.getCustomItem(m_customName, m_material, m_data, m_ammount);
            
            if (is != null) {
                return is;
            }
        }
        
        is = new ItemStack(m_material, m_ammount, m_data);
        
        if (adapter != null && m_nbt != null) {
            is = adapter.setNbt(is, m_nbt);
        }
        
        return is;
    }
    
    KitItem(String customName, Material m, short data, int amount, int chance, TagBase nbt) {
        m_material = m;
        m_data = data;
        m_ammount = amount;
        m_percentage = chance;
        m_nbt = nbt;
        m_customName = customName;
    }
}
