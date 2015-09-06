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
package org.primesoft.chestDrop.strings;

/**
 *
 * @author SBPrime
 */
public enum MessageType {
    PLAYER_REQUIRED("PLAYER_REQUIRED"),
    NO_PERMS("NO_PERMS"),
    //The reload command
    CMD_RELOAD_ERROR("CMD_RELOAD_ERROR"),
    CMD_RELOAD_DONE("CMD_RELOAD_DONE"),
    //The dump command
    CMD_DUMP_NO_ITEM("CMD_DUMP_NO_ITEM"),
    CMD_DUMP_NBT("CMD_DUMP_NBT"),
    CMD_DUMP_NO_NBT("CMD_DUMP_NO_NBT"),
    //The give kit command
    CMD_KIT_NOT_FOUND("CMD_KIT_NOT_FOUND"),
    CMD_KIT_NO_ITEM("CMD_KIT_NO_ITEM"),
    //The drop command
    CMD_DROP_NOT_FOUND("CMD_DROP_NOT_FOUND"),
    CMD_DROP_NO_ITEM("CMD_DROP_NO_ITEM"),
    CMD_DROP("CMD_DROP")
    
;
    private final String m_key;

    MessageType(String key) {
        m_key = key;
    }

    /**
     * Format the message
     *
     * @param params
     * @return
     */
    public String format(Object... params) {
        return MessageProvider.formatMessage(this, params);
    }

    /**
     * Get message entry key
     *
     * @return
     */
    public String getKey() {
        return m_key;
    }

    @Override
    public String toString() {
        return format();
    }
}
