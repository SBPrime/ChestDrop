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

import java.util.HashMap;
import java.util.LinkedHashMap;
import static org.primesoft.chestDrop.ChestDropMain.log;

/**
 *
 * @author SBPrime
 */
public class KitProvider {
    /**
     * The known kits
     */
    private final HashMap<String, Kit> m_kits = new LinkedHashMap<String, Kit>();

    /**
     * Creat new instance of empty kit provider
     */
    public KitProvider() {
    }

    /**
     * Create new instance of kit provider
     *
     * @param kits
     */
    KitProvider(Kit[] kits) {
        if (kits != null) {
            for (Kit kit : kits) {
                if (kit == null) {
                    continue;
                }
                String name = kit.getName();
                if (m_kits.containsKey(name)) {
                    log(String.format("Duplicate kit name %1$s.", name));
                    continue;
                }

                m_kits.put(name, kit);
            }
        }
    }

    /**
     * Get kit
     *
     * @param name
     * @return
     */
    public Kit getKit(String name) {
        if (name == null) {
            return null;
        }

        name = name.toLowerCase();
        synchronized (m_kits) {
            if (m_kits.containsKey(name)) {
                return m_kits.get(name);
            }
        }

        return null;
    }
    
    
    /**
     * Get all available kits
     * @return 
     */
    public String[] getKitsNames() {
        synchronized (m_kits) {
            return m_kits.keySet().toArray(new String[0]);
        }
    }
    
    
    /**
     * Get all available kits
     * @return 
     */
    public Kit[] getKits() {
        synchronized (m_kits) {
            return m_kits.values().toArray(new Kit[0]);
        }
    }
}
