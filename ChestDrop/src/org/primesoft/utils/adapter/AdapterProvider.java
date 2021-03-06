/*
 * SBPrime plugin utils
 * Copyright (c) 2015, SBPrime <https://github.com/SBPrime/>
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
 * 5. Usage in closed source projects is not allowed
 * 6. Any derived work based on or containing parts of this software must reproduce 
 *    the above copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided with the 
 *    derived work.
 * 7. The original author of the software is allowed to change the license 
 *    terms or the entire license of the software as he sees fit.
 * 8. The original author of the software is allowed to sublicense the software 
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
package org.primesoft.utils.adapter;

import java.util.List;
import org.bukkit.Server;
import org.bukkit.World;
import static org.primesoft.chestDrop.ChestDropMain.log;

/**
 *
 * @author SBPrime
 */
public class AdapterProvider {
    /**
     * Initialise the direct chunk API
     * @param server
     * @return 
     */
    public static IUtilsAdapter get(Server server) {
        List<World> worlds = server.getWorlds();
        if (worlds == null || worlds.isEmpty()) {
            log("No worlds found, native API disabled");
            return null;
        }

        World w = worlds.get(0);
        Class<?> cls = w.getClass();
        String className = cls.getCanonicalName();
        className = className.replace("org.bukkit.craftbukkit.", "");
        int idx = className.indexOf(".");

        if (idx >= 0) {
            className = className.substring(0, idx);
        }

        for (UtilsAdapters adapter : UtilsAdapters.values()) {
            if (adapter.getAPIVersion().equals(className)) {
                return adapter.createInstance();
            }
        }

        log(String.format("No matching adapter found for %s, DirectChunkAPI disabled", className));
        return null;
    }
}
