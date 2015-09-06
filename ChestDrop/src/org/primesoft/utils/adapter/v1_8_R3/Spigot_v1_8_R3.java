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
package org.primesoft.utils.adapter.v1_8_R3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagByteArray;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagEnd;
import net.minecraft.server.v1_8_R3.NBTTagFloat;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagIntArray;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagLong;
import net.minecraft.server.v1_8_R3.NBTTagShort;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import static org.primesoft.chestDrop.ChestDropMain.log;
import org.primesoft.utils.adapter.IUtilsAdapter;
import org.primesoft.utils.nbt.TagBase;
import org.primesoft.utils.nbt.TagArrayByte;
import org.primesoft.utils.nbt.TagArrayInt;
import org.primesoft.utils.nbt.TagByte;
import org.primesoft.utils.nbt.TagCompound;
import org.primesoft.utils.nbt.TagDouble;
import org.primesoft.utils.nbt.TagEnd;
import org.primesoft.utils.nbt.TagFloat;
import org.primesoft.utils.nbt.TagInt;
import org.primesoft.utils.nbt.TagList;
import org.primesoft.utils.nbt.TagLong;
import org.primesoft.utils.nbt.TagShort;
import org.primesoft.utils.nbt.TagString;
import org.primesoft.utils.nbt.TagType;
import org.primesoft.utils.Reflection;

/**
 *
 * @author SBPrime
 */
public class Spigot_v1_8_R3 implements IUtilsAdapter {

    private static final IUtilsAdapter s_instance = new Spigot_v1_8_R3();

    private static final Constructor<NBTTagEnd> s_tagEnd;
    private static final Field s_itemStackHandle;
    private static final boolean s_isInitialised;

    static {
        s_itemStackHandle = Reflection.findTypedField(CraftItemStack.class, ItemStack.class, "handle", "Unable to get native handle from ItemStack");
        s_tagEnd = (Constructor<NBTTagEnd>) Reflection.findConstructor(NBTTagEnd.class, "Unable to get NBTTagEnd constructor");
        
        
        s_isInitialised = s_itemStackHandle != null && s_tagEnd != null;
    }

    public static IUtilsAdapter getInstance() {
        return s_instance;
    }

    @Override
    public TagBase getNbt(org.bukkit.inventory.ItemStack itemStack) {
        CraftItemStack cItemStack = itemStack instanceof CraftItemStack ? (CraftItemStack) itemStack : null;

        if (cItemStack == null || !s_isInitialised) {
            return null;
        }

        ItemStack nStack = Reflection.get(cItemStack, ItemStack.class, s_itemStackHandle, "Unable to get ItemStack");

        if (nStack == null) {
            return null;
        }

        NBTTagCompound nTag = nStack.getTag();
        return fromNative(nTag);
    }
        

    @Override
    public org.bukkit.inventory.ItemStack setNbt(org.bukkit.inventory.ItemStack itemStack, TagBase nbt) {        
        if (!s_isInitialised || itemStack == null) {
            return itemStack;
        }
        
        
        ItemStack nStack = CraftItemStack.asNMSCopy(itemStack);       

        if (nStack == null) {
            return itemStack;
        }

        NBTBase nTag = nbt != null ? toNative(nbt) : null;
        nStack.setTag(nTag instanceof NBTTagCompound ? (NBTTagCompound) nTag : null);
        
        return CraftItemStack.asBukkitCopy(nStack);
    }

    /**
     * Convert native NBT to internal NBT
     *
     * @param nTag
     * @return
     */
    private TagBase fromNative(NBTBase nbt) {
        if (nbt == null || !s_isInitialised) {
            return null;
        }

        /**
         * Convert the complex tag
         */
        if ((nbt instanceof NBTTagCompound)) {
            TagCompound tc = new TagCompound();
            HashMap<String, TagBase> childTags = tc.Tags();
            for (String key : ((NBTTagCompound) nbt).c()) {
                TagBase subTag = fromNative(((NBTTagCompound) nbt).get(key));
                if (subTag != null) {
                    childTags.put(key, subTag);
                }
            }
            return tc;
        }

        /**
         * Convert primitives
         */
        if ((nbt instanceof NBTTagByte)) {
            return new TagByte(((NBTTagByte) nbt).f());
        }
        if ((nbt instanceof NBTTagShort)) {
            return new TagShort(((NBTTagShort) nbt).e());
        }
        if ((nbt instanceof NBTTagInt)) {
            return new TagInt(((NBTTagInt) nbt).d());
        }
        if ((nbt instanceof NBTTagLong)) {
            return new TagLong(((NBTTagLong) nbt).c());
        }
        if ((nbt instanceof NBTTagFloat)) {
            return new TagFloat(((NBTTagFloat) nbt).h());
        }
        if ((nbt instanceof NBTTagDouble)) {
            return new TagDouble(((NBTTagDouble) nbt).g());
        }
        if ((nbt instanceof NBTTagString)) {
            return new TagString(((NBTTagString) nbt).a_());
        }

        /**
         * Convert arrays
         */
        if ((nbt instanceof NBTTagByteArray)) {
            return new TagArrayByte(((NBTTagByteArray) nbt).c());
        }

        if ((nbt instanceof NBTTagIntArray)) {
            return new TagArrayInt(((NBTTagIntArray) nbt).c());
        }
        if ((nbt instanceof NBTTagList)) {
            NBTTagList nbtList = (NBTTagList) nbt;
            List<TagBase> values = new ArrayList<TagBase>();
            TagType type = null;

            for (int idx = 0; idx < nbtList.size(); idx++) {
                NBTBase nbtEntry = nbtList.g(idx);
                if (nbtEntry instanceof NBTTagEnd) {
                    continue;
                }

                TagBase entry = fromNative(nbtEntry);

                if (entry == null) {
                    continue;
                }

                if (type == null) {
                    type = entry.getType();
                } else if (type != entry.getType()) {
                    log(String.format("NBTTagList contains multiple types of NBTTgs. Current: %1$s New: %2$s",
                            type, entry.getClass().getCanonicalName()));
                    continue;
                }

                values.add(entry);
            }

            //Class cls = NBTConstants.getClassFromType(nbtList.f());
            return new TagList(values, type);
        }

        if ((nbt instanceof NBTTagEnd)) {
            return new TagEnd();
        }

        log(String.format("Unknown NMS %1$s... skipping.", nbt.getClass().getCanonicalName()));
        return null;
    }

    /**
     * Convert built in NBT to native NBT
     *
     * @param nbt
     * @return
     */
    private NBTBase toNative(TagBase nbt) {
        if (nbt == null || !s_isInitialised) {
            return null;
        }

        /**
         * Convert the complex tag
         */
        if ((nbt instanceof TagCompound)) {
            NBTTagCompound tc = new NBTTagCompound();
            HashMap<String, TagBase> childTags = ((TagCompound) nbt).Tags();
            
            for (String key : childTags.keySet()) {
                NBTBase subTag = toNative(childTags.get(key));
                
                if (subTag != null) {
                    tc.set(key, subTag);
                }
            }
            return tc;
        }

        /**
         * Convert primitives
         */
        if ((nbt instanceof TagByte)) {
            return new NBTTagByte(((TagByte) nbt).getValue());
        }
        if ((nbt instanceof TagShort)) {
            return new NBTTagShort(((TagShort) nbt).getValue());
        }
        if ((nbt instanceof TagInt)) {
            return new NBTTagInt(((TagInt) nbt).getValue());
        }
        if ((nbt instanceof TagLong)) {
            return new NBTTagLong(((TagLong) nbt).getValue());
        }
        if ((nbt instanceof TagFloat)) {
            return new NBTTagFloat(((TagFloat) nbt).getValue());
        }
        if ((nbt instanceof TagDouble)) {
            return new NBTTagDouble(((TagDouble) nbt).getValue());
        }
        if ((nbt instanceof TagString)) {
            return new NBTTagString(((TagString) nbt).getValue());
        }

        /**
         * Convert arrays
         */
        if ((nbt instanceof TagArrayByte)) {
            return new NBTTagByteArray(((TagArrayByte) nbt).getValue());
        }

        if ((nbt instanceof TagArrayInt)) {
            return new NBTTagIntArray(((TagArrayInt) nbt).getValue());
        }
        if ((nbt instanceof TagList)) {
            TagList nbtList = (TagList) nbt;
            NBTTagList result = new NBTTagList();

            for (TagBase child : nbtList.getValues()) {
                if (child.getType() == TagType.NBT_END) {
                    continue;
                }
                
                NBTBase entry = toNative(child);

                if (entry == null) {
                    continue;
                }

                result.add(entry);
            }

            //Class cls = NBTConstants.getClassFromType(nbtList.f());
            return result;
        }

        if ((nbt instanceof TagEnd)) {
            return Reflection.create(NBTTagEnd.class, s_tagEnd, "Unable to create NBTTagEnd");
        }

        log(String.format("Unknown NMS %1$s... skipping.", nbt.getClass().getCanonicalName()));
        return null;
    }
}
