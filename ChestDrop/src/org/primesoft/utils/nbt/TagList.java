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
 *    or its parts using any license terms he sees fit/*
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

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author SBPrime
 */
public class TagList extends TagBase {

    private final TagType m_valueType;
    private final List<TagBase> m_values;

    public List<TagBase> getValues() {
        return m_values;
    }
    
    public TagType getValueType() {
        return m_valueType;
    }

    public TagList(TagType valueType) {
        this(null, valueType);
    }

    public TagList(List<TagBase> values, TagType valueType) {
        super(TagType.NBT_LIST);

        m_valueType = valueType;
        if (values == null) {
            m_values = new LinkedList<TagBase>();
        } else {
            m_values = new LinkedList<TagBase>(values);
        }
    }

    @Override
    public String toJson(String head) {
        StringBuilder sb = new StringBuilder();
        sb.append(head);sb.append("{\n");
        sb.append(head);sb.append(String.format("\t\"Type\": \"%1$s\",\n", getType()));
        sb.append(head);sb.append(String.format("\t\"ValueType\": \"%1$s\",\n", getValueType()));
        sb.append(head);sb.append(String.format("\t\"Value\": [\n"));
        
        final String nHead = head + "\t\t";
        boolean first = true;
        for (TagBase tag : m_values) {
            if (!first) {
                sb.append(",\n");
            } else {
                first = false;
            }
            
            sb.append(tag.toJson(nHead));
        }
        sb.append("\n");
        
        sb.append(head);sb.append(String.format("\t]\n"));
        
        sb.append(head);sb.append("}");
        return sb.toString();
    }        
}
