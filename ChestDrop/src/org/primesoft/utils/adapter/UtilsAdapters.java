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

import org.primesoft.utils.adapter.v1_8_R3.Spigot_v1_8_R3;
import org.primesoft.utils.Func;
import org.primesoft.utils.adapter.v1_10_R1.Spigot_v1_10_R1;
import org.primesoft.utils.adapter.v1_11_R1.Spigot_v1_11_R1;
import org.primesoft.utils.adapter.v1_9_R1.Spigot_v1_9_R1;
import org.primesoft.utils.adapter.v1_9_R2.Spigot_v1_9_R2;

/**
 *
 * @author SBPrime
 */
enum UtilsAdapters {
    v1_8_R1("v1_8_R1", null),
    v1_8_R2("v1_8_R2", null),
    v1_8_R3("v1_8_R3", new Func<IUtilsAdapter>() { 
        @Override
        public IUtilsAdapter execute() {
            return Spigot_v1_8_R3.getInstance();
        }
    }),
    v1_9_R1("v1_9_R1", new Func<IUtilsAdapter>() { 
        @Override
        public IUtilsAdapter execute() {
            return Spigot_v1_9_R1.getInstance();
        }
    }),
    v1_9_R2("v1_9_R2", new Func<IUtilsAdapter>() { 
        @Override
        public IUtilsAdapter execute() {
            return Spigot_v1_9_R2.getInstance();
        }
    }),
    v1_10_R1("v1_10_R1", new Func<IUtilsAdapter>() { 
        @Override
        public IUtilsAdapter execute() {
            return Spigot_v1_10_R1.getInstance();
        }
    }),
    v1_11_R1("v1_11_R1", new Func<IUtilsAdapter>() {
        @Override
        public IUtilsAdapter execute() {
            return Spigot_v1_11_R1.getInstance();
        }
    })
    ;
    
    
    private final String m_apiString;
    private final Func<IUtilsAdapter> m_adapterCreate;
    
    public IUtilsAdapter createInstance() {
        if (m_adapterCreate == null) {
            return null;
        }
        return m_adapterCreate.execute();
    }
    
    public String getAPIVersion() {
        return m_apiString;
    }
    
    private UtilsAdapters(String apiString, Func<IUtilsAdapter> adapterCreate) {
        m_apiString = apiString;
        m_adapterCreate = adapterCreate;
    }
}
