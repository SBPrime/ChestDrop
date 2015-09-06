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
package org.primesoft.utils.nbt;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author SBPrime
 */
public class TagCompound extends TagBase {

    private final HashMap<String, TagBase> m_tags = new LinkedHashMap<String, TagBase>();

    public HashMap<String, TagBase> Tags() {
        return m_tags;
    }

    
    /**
     * Get tag value
     * @param name
     * @return 
     */
    public TagBase get(String name) {
        return m_tags.get(name);
    }
    
    /**
     * Set tag value
     *
     * @param name
     * @param value
     */
    public void set(String name, TagBase value) {
        if (value == null) {
            m_tags.remove(name);
        } else {
            m_tags.put(name, value);
        }
    }

    public TagCompound() {
        super(TagType.NBT_COMPOUND);
    }

    @Override
    public String toJson(String head) {
        StringBuilder sb = new StringBuilder();
        sb.append(head);
        sb.append("{\n");
        final String nHead = head + "\t";
        boolean first = true;
        for (Map.Entry<String, TagBase> entrySet : m_tags.entrySet()) {
            if (!first) {
                sb.append(",\n");
            } else {
                first = false;
            }
            String key = entrySet.getKey();
            TagBase value = entrySet.getValue();

            sb.append(nHead);
            sb.append(String.format("\"%1$s\":\n", key));
            sb.append(value.toJson(nHead));
        }
        sb.append("\n");
        sb.append(head);
        sb.append("}");

        return sb.toString();
    }
}
