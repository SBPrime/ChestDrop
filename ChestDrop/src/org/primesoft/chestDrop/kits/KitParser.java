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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import static org.primesoft.chestDrop.ChestDropMain.log;
import org.primesoft.utils.nbt.TagArrayByte;
import org.primesoft.utils.nbt.TagArrayInt;
import org.primesoft.utils.nbt.TagBase;
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
import org.primesoft.utils.ArrayHelper;
import org.primesoft.utils.ExceptionHelper;
import org.primesoft.utils.InOutParam;

/**
 *
 * @author SBPrime
 */
public class KitParser {

    /**
     * The kits file name
     */
    private final static String KIT_FILE = "kits.json";

    /**
     * Save kit.json to plugins folder
     *
     * @param plugin
     * @return
     */
    public static boolean saveDefault(JavaPlugin plugin) {
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        if (!pluginFolder.canRead()) {
            return false;
        }

        File kits = new File(pluginFolder, KIT_FILE);

        if (kits.exists()) {
            return true;
        }

        if (!pluginFolder.canWrite()) {
            return false;
        }

        InputStream input = null;
        FileOutputStream output = null;

        try {
            if (!kits.createNewFile()) {
                return false;
            }

            input = plugin.getClass().getResourceAsStream("/" + KIT_FILE);
            if (input == null) {
                return false;
            }

            output = new FileOutputStream(kits.getAbsoluteFile());

            byte[] buf = new byte[4096];
            int readBytes = 0;

            while ((readBytes = input.read(buf)) > 0) {
                output.write(buf, 0, readBytes);
            }
        } catch (IOException ex) {
            ExceptionHelper.printException(ex, "Unable to extract default kit file.");
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    //Ignore close error
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    //Ignore close error
                }
            }
        }
        return true;
    }

    /**
     * Load kits from kits.json
     *
     * @param plugin
     * @return
     */
    public static KitProvider load(JavaPlugin plugin) {
        Object o = readKitFile(plugin);

        Kit[] kits = parseKits(o);

        if (kits == null || kits.length == 0) {
            return null;
        }

        return new KitProvider(kits);
    }

    /**
     * Read the kit file content
     *
     * @param plugin
     * @return
     */
    private static Object readKitFile(JavaPlugin plugin) {
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        if (!pluginFolder.canRead()) {
            return null;
        }
        File kits = new File(pluginFolder, KIT_FILE);
        if (!kits.exists()) {
            return null;
        }
        FileInputStream input = null;
        try {
            input = new FileInputStream(kits.getAbsoluteFile());

            InputStreamReader is = new InputStreamReader(input);

            return JSONValue.parseWithException(is);
        } catch (IOException ex) {
            ExceptionHelper.printException(ex, "Unable to load kit file.");
            return null;
        } catch (ParseException ex) {
            ExceptionHelper.printException(ex, "Unable to load kit file, JSON parser error.");
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    //Ignore close error
                }
            }
        }
    }

    /**
     * Parse the kit file content
     *
     * @param content
     * @return
     */
    private static Kit[] parseKits(Object content) {
        if (content == null) {
            log("Unable to parse kits file");
            return null;
        }
        if (!(content instanceof JSONArray)) {
            log(String.format("Unable to parse kits file. Expected JSONArray got %1$s.", content.getClass().getCanonicalName()));
            return null;
        }

        final JSONArray jKits = (JSONArray) content;
        final List<Kit> result = new ArrayList<Kit>();
        for (Object oKit : jKits) {
            if (oKit == null) {
                continue;
            }
            if (!(oKit instanceof JSONObject)) {
                log(String.format("Unable to parse kits entry. Expected JSONObject got %1$s.", oKit.getClass().getCanonicalName()));
                continue;
            }

            JSONObject jKit = (JSONObject) oKit;
            Kit kit = parseKit(jKit);

            if (kit == null) {
                continue;
            }

            result.add(kit);
        }

        return result.toArray(new Kit[0]);
    }

    /**
     * Parse the Kit entry
     *
     * @param jKit
     * @return
     */
    private static Kit parseKit(JSONObject jKit) {
        if (jKit == null) {
            return null;
        }

        String name = getString(jKit, "name");       
        if (name == null || name.isEmpty()) {
            log("No kit name defined");
            return null;
        }

        name = name.toLowerCase();
        
        String kitGroup = getString(jKit, "group");
        if (kitGroup == null || kitGroup.isEmpty()) {
            kitGroup = name;
        }
        String displayName = getString(jKit, "displayName");
        if (displayName == null || displayName.isEmpty()) {
            displayName = name;
        }
        
        
        InOutParam<Integer> tmp = InOutParam.Out();
        int chestCount = -1;
        int interva = -1;
        int live = -1;
        int minItems = 1;
        int maxItems = 4;
                
        if (getInt(jKit, "interval", tmp)) {
            interva = tmp.getValue();
        }
        if (getInt(jKit, "live", tmp)) {
            live = tmp.getValue();
        }
        if (getInt(jKit, "chests", tmp)) {
            chestCount = tmp.getValue();
        }
        
        if (getInt(jKit, "minItems", tmp)) {
            minItems = tmp.getValue();
        }
        if (getInt(jKit, "maxItems", tmp)) {
            maxItems = tmp.getValue();
        }
        
        if (minItems < 0) {
            minItems = 0;
        }
        if (maxItems < 1) {
            log(String.format("Kit %1$s, invalid number of items < 1", name));
            return null;
        }
        if (maxItems < minItems) {
            int t = maxItems;
            maxItems = minItems;
            minItems = t;
        }
        
        if (interva < 1) {
            log(String.format("Kit %1$s, interval < 1", name));
            return null;
        }
        
        if (chestCount < 1) {
            log(String.format("Kit %1$s, Chest count < 1", name));
            return null;
        }
        
        if (live < 1) {
            log(String.format("Kit %1$s, Chest live < 1", name));
            return null;
        }
        
        Server server = Bukkit.getServer();
        
        
        String worldName = getString(jKit, "world");
        World world = server != null && worldName != null ? server.getWorld(worldName) : null;
        
        if (world == null) {
            log(String.format("World %1$s not found.", worldName));
            return null;
        }
        
        int[] region = parseRegion(getArray(jKit, "region"));
        if (region == null || region.length != 4) {
            log("Invalid region. You need to provide 4 coordinates (minX, minY, maxX, maxY)");
            return null;
        }

        Vector from = new Vector(region[0], 0, region[1]);
        Vector to = new Vector(region[2], 255, region[3]);
        
        
        JSONArray items = getArray(jKit, "items");
        if (items == null) {
            log(String.format("No items defined for kit %1$s.", name));
            return null;
        }

        final List<KitItem> kItems = new ArrayList<KitItem>();
        for (Object oItem : items) {
            if (oItem == null) {
                continue;
            }
            if (!(oItem instanceof JSONObject)) {
                log(String.format("Unable to parse item entry. Expected JSONObject got %1$s.", oItem.getClass().getCanonicalName()));
                continue;
            }

            KitItem kItem = parseItem((JSONObject) oItem);
            if (kItem == null) {
                log("Unable to parse item entry.");
                continue;
            }
            kItems.add(kItem);
        }

        if (kItems.isEmpty()) {
            log(String.format("No items defined for kit %1$s.", name));
            return null;
        }

        return new Kit(name, displayName, kitGroup, kItems.toArray(new KitItem[0]),
            chestCount, minItems, maxItems,
                interva, live,
                from, to, world);
    }

    /**
     * Parse the item
     *
     * @param jItem
     * @return
     */
    private static KitItem parseItem(JSONObject jItem) {
        if (jItem == null) {
            return null;
        }

        String customName = getString(jItem, "customName");
        Material m = getMaterial(jItem, "id");
        int chance, amount;
        short data;

        InOutParam<Integer> tmp = InOutParam.Out();

        if (!getInt(jItem, "data", tmp)) {
            log("Invalid item data");
            return null;
        }
        data = tmp.getValue().shortValue();

        if (!getInt(jItem, "chance", tmp)) {
            log("Invalid item chance");
            return null;
        }
        chance = tmp.getValue();

        if (!getInt(jItem, "amount", tmp)) {
            log("Invalid item amount");
            return null;
        }
        amount = tmp.getValue();
        TagBase nbt = parseNbt(getObject(jItem, "nbt"));

        if (m == null) {
            log("Invalid item data. No material");
            return null;
        }

        if (chance < 1/* || chance > 100*/) {
            log("Invalid item data. Chance must be in range <0, 100>");
            return null;
        }
        if (data < 0) {
            log("Invalid item data");
            return null;
        }
        if (amount < 1) {
            log("Invalid amount");
            return null;
        }

        return new KitItem(customName, m, data, amount, chance, nbt);
    }

    private static JSONObject getObject(JSONObject o, String entry) {
        Object r = get(o, entry);

        if (!(r instanceof JSONObject)) {
            return null;
        }

        return (JSONObject) r;
    }

    private static JSONArray getArray(JSONObject o, String entry) {
        Object r = get(o, entry);

        if (!(r instanceof JSONArray)) {
            return null;
        }

        return (JSONArray) r;
    }

    private static boolean getInt(JSONObject o, String entry, InOutParam<Integer> result) {
        Object r = get(o, entry);

        if (!(r instanceof Long)) {
            return false;
        }

        result.setValue(((Long) r).intValue());
        return true;
    }

    private static boolean getLong(JSONObject o, String entry, InOutParam<Long> result) {
        Object r = get(o, entry);

        if (!(r instanceof Long)) {
            return false;
        }

        result.setValue((Long) r);
        return true;
    }

    private static boolean getDouble(JSONObject o, String entry, InOutParam<Double> result) {
        Object r = get(o, entry);

        if (!(r instanceof Double)) {
            return false;
        }

        result.setValue((Double) r);
        return true;
    }

    private static String getString(JSONObject o, String entry) {
        Object r = get(o, entry);

        if (!(r instanceof String)) {
            return null;
        }

        return (String) r;
    }

    private static Object get(JSONObject o, String entry) {
        if (o == null || entry == null || !o.containsKey(entry)) {
            return null;
        }

        return o.get(entry);
    }

    private static Material getMaterial(JSONObject jItem, String id) {
        InOutParam<Integer> tmp = InOutParam.Out();

        if (getInt(jItem, "id", tmp)) {
            return Material.getMaterial(tmp.getValue());
        }

        String s = getString(jItem, "id");
        if (s == null) {
            return null;
        }

        return Material.getMaterial(s);
    }

    private static TagBase parseNbt(JSONObject o) {
        if (o == null) {
            return null;
        }

        String sType = getString(o, "Type");
        TagType type = sType == null ? null : TagType.valueOf(sType);

        if (type == null) {
            type = TagType.NBT_COMPOUND;
        }

        switch (type) {
            case NBT_COMPOUND:
                return parseCompound(o);

            case NBT_BYTE:
            case NBT_INT:
            case NBT_SHORT:
            case NBT_LONG:
                return parseIntNbt(o, type);

            case NBT_FLOAT:
            case NBT_DOUBLE:
                return parseDoubleNbt(o, type);

            case NBT_STRING:
                return parseStringNbt(o);

            case NBT_LIST:
                return parseList(o);

            case NBT_ARRAY_BYTE:
            case NBT_ARRAY_INT:
                return parseArray(o, type);

            case NBT_END:
                return new TagEnd();
        }
        
        return null;
    }

    private static TagBase parseCompound(JSONObject o) {
        if (o == null) {
            return null;
        }

        TagCompound result = new TagCompound();        
        HashMap<String, TagBase> entries = result.Tags();

        for (Object k : o.keySet()) {
            String key = k.toString();
            Object value = o.get(k);

            if (!(value instanceof JSONObject)) {
                continue;
            }

            TagBase childTag = parseNbt((JSONObject) value);
            if (childTag == null) {
                continue;
            }
            
            entries.put(key, childTag);
        }

        return result;
    }

    private static TagBase parseList(JSONObject o) {
        List<TagBase> values = new ArrayList<TagBase>();
        String sValueType = getString(o, "ValueType");
        JSONArray jValues = getArray(o, "Value");

        if (sValueType == null || jValues == null) {
            return null;
        }

        TagType type = TagType.valueOf(sValueType);
        if (type == null) {
            return null;
        }

        for (Object e : jValues) {
            JSONObject je = e instanceof JSONObject ? (JSONObject) e : null;
            TagBase t = parseNbt(je);

            if (t == null || t.getType() != type) {
                continue;
            }

            values.add(t);
        }

        return new TagList(values, type);

    }

    private static TagBase parseStringNbt(JSONObject o) {
        String v = getString(o, "Value");

        if (v == null) {
            return null;
        }

        return new TagString(v);
    }

    private static TagBase parseDoubleNbt(JSONObject o, TagType type) {
        InOutParam<Double> lOut = InOutParam.Out();
        if (!getDouble(o, "Value", lOut)) {
            return null;
        }

        double v = (double) lOut.getValue();
        switch (type) {
            case NBT_FLOAT:
                return new TagFloat((float) v);
            case NBT_DOUBLE:
                return new TagDouble(v);
        }

        return null;
    }

    private static TagBase parseIntNbt(JSONObject o, TagType type) {
        InOutParam<Long> lOut = InOutParam.Out();
        if (!getLong(o, "Value", lOut)) {
            return null;
        }

        long v = (long) lOut.getValue();
        switch (type) {
            case NBT_BYTE:
                return new TagByte((byte) v);
            case NBT_INT:
                return new TagInt((int) v);
            case NBT_SHORT:
                return new TagShort((short) v);
            case NBT_LONG:
                return new TagLong(v);
        }

        return null;
    }

    private static TagBase parseArray(JSONObject o, TagType type) {
        JSONArray array = getArray(o, "Value");
        if (array == null) {
            return null;
        }

        List<Byte> vByte = new ArrayList<Byte>();
        List<Integer> vInteger = new ArrayList<Integer>();
        for (Object a : array) {
            if (!(a instanceof Long)) {
                continue;
            }

            Long v = (Long) a;

            switch (type) {
                case NBT_ARRAY_BYTE:
                    vByte.add((byte) v.byteValue());
                    break;
                case NBT_ARRAY_INT:
                    vInteger.add(v.intValue());
                    break;
            }
        }

        switch (type) {
            case NBT_ARRAY_BYTE: {
                if (vByte.isEmpty()) {
                    return null;
                }

                return new TagArrayByte(ArrayHelper.toPrimitives(vByte.toArray(new Byte[0])));
            }
            case NBT_ARRAY_INT:
                if (vInteger.isEmpty()) {
                    return null;
                }

                return new TagArrayInt(ArrayHelper.toPrimitives(vInteger.toArray(new Integer[0])));
            default:
                return null;
        }
    }

    
    /**
     * Parse the region entry
     * @param array
     * @return 
     */
    private static int[] parseRegion(JSONArray array) {
        if (array == null) {
            return null;
        }
        
        List<Integer> result = new ArrayList<Integer>();
        for (Object jObj : array) {
            if (!(jObj instanceof Long)) {
                continue;
            }
            
            result.add(((Long)jObj).intValue());
        }
        
        return ArrayHelper.toPrimitives(result.toArray(new Integer[0]));
    }
}
