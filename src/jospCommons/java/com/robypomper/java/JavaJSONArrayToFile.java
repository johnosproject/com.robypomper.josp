package com.robypomper.java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class JavaJSONArrayToFile<T, K> {

    private final ObjectMapper jsonMapper;
    private final Class<T> typeOfT;
    private final File jsonFile;
    private int fileCount;       // count in file (for total add cacheBuffered.size())
    private T fileFirst = null;
    private T fileLast = null;
    private final List<T> cacheBuffered; // stored only on memory not on file


    public JavaJSONArrayToFile(File jsonFile, Class<T> typeOfT) throws IOException {
        if (jsonFile.isDirectory())
            throw new IllegalArgumentException("File can not be a directory!");

        this.jsonMapper = JsonMapper.builder().build();
        this.typeOfT = typeOfT;
        this.jsonFile = jsonFile;

        cacheBuffered = new ArrayList<>();
        initCache();
    }


    private void initCache() throws IOException {
        ArrayNode array = getMainNode();
        fileCount = array.size();
        if (array.size() > 0) {
            fileFirst = jsonMapper.readValue(array.get(0).traverse(), typeOfT);
            fileLast = jsonMapper.readValue(array.get(array.size() - 1).traverse(), typeOfT);
        }
    }

    private ArrayNode getMainNode() throws IOException {
        JsonNode node = null;

        if (jsonFile.exists() && jsonFile.length() > 0)
            node = jsonMapper.readTree(jsonFile);

        if (node == null)
            node = jsonMapper.createArrayNode();

        if (!node.isArray())
            throw new IOException(String.format("File '%s' is not a JSON array", jsonFile.getPath()));

        return (ArrayNode) node;
    }


    public void append(T value) {
        synchronized (cacheBuffered) {
            cacheBuffered.add(value);
        }
    }

    public void storeCache() throws IOException {
        if (cacheBuffered.size() == 0) return;

        synchronized (cacheBuffered) {
            ArrayNode array = getMainNode();

            for (T v : cacheBuffered)
                array.addPOJO(v);
            fileCount += cacheBuffered.size();
            if (fileFirst == null) fileFirst = cacheBuffered.get(0);
            fileLast = cacheBuffered.get(cacheBuffered.size() - 1);
            cacheBuffered.clear();

            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, array);
        }
    }

    public void flushCache(int count) throws IOException {
        if (cacheBuffered.size() == 0) return;
        if (count <= 0) return;

        int countAdded=0;
        synchronized (cacheBuffered) {
            ArrayNode array = getMainNode();

            for (T v : cacheBuffered) {
                countAdded++;
                array.addPOJO(v);
                if (countAdded==count)
                    break;
            }

            fileCount += countAdded;
            if (fileFirst == null) fileFirst = cacheBuffered.get(0);
            fileLast = cacheBuffered.get(countAdded-1);

            cacheBuffered.subList(0, countAdded).clear();

            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, array);
        }

    }


    public int count() {
        return fileCount + cacheBuffered.size();
    }

    public int countBuffered() {
        return cacheBuffered.size();
    }

    public int countFile() {
        return fileCount;
    }

    public T getFirst() {
        if (fileFirst != null)
            return fileFirst;
        if (cacheBuffered.size() > 0)
            return cacheBuffered.get(0);
        return null;
    }

    public T getLast() {
        if (cacheBuffered.size() > 0)
            return cacheBuffered.get(cacheBuffered.size() - 1);

        return fileLast;
    }


    private T findInBuffered(K id) {
        for (T v : cacheBuffered)
            if (equalsItemIds(id, getItemId(v)))
                return v;
        return null;
    }

    public List<T> getAllNoFile() {
        return getRangeNoFile(null, null);
    }

    public List<T> getRangeNoFile(K from) {
        return getRangeNoFile(from, null);
    }

    public List<T> getRangeNoFile(K from, K to) {
        try {
            return getRange(from, to, true);
        } catch (IOException e) {
            assert false;
            return null;
        }
    }

    public List<T> getAll() throws IOException {
        return getRange(null,null,false);
    }

    public List<T> getAll(boolean noFile) throws IOException {
        return getRange(null,null,noFile);
    }

    public List<T> getRange(K from) throws IOException {
        return getRange(from,null,false);
    }

    public List<T> getRange(K from, boolean noFile) throws IOException {
        return getRange(from,null,noFile);
    }

    public List<T> getRange(K from, K to) throws IOException {
        return getRange(from,to,false);
    }

    public List<T> getRange(K from, K to, boolean noFile) throws IOException {
        if (count() == 0) return new ArrayList<>();

        if (from == null)
            if (!noFile && fileFirst != null)
                from = getItemId(fileFirst);
            else if (cacheBuffered.size() > 0)
                from = getItemId(cacheBuffered.get(0));                     // ATTENZIONE null
            else
                assert false;
        T fromEv = findInBuffered(from);

        if (to == null)
            if (cacheBuffered.size() > 0)
                to = getItemId(cacheBuffered.get(cacheBuffered.size() - 1));
            else if (fileLast != null)
                to = getItemId(fileLast);
            else
                assert false;
        T toEv = findInBuffered(to);

        List<T> range = new ArrayList<>();
        if (fromEv != null && toEv != null) {
            range.addAll(getRangeBuffered(from, to));

        } else if (fromEv == null && toEv != null) {
            if (!noFile) range.addAll(getRangeFile(from));
            range.addAll(getRangeBuffered(null, to));

        } else //noinspection ConstantConditions
            if (fromEv == null && toEv == null) {
            if (!noFile) range.addAll(getRangeFile(from, to));
        }

        return range;
    }

    private List<T> getRangeBuffered(K from) {
        return getRangeBuffered(from, null);
    }

    private List<T> getRangeBuffered(K from, K to) {
        boolean store = from == null;
        List<T> range = new ArrayList<>();

        for (T v : cacheBuffered) {
            if (from != null && equalsItemIds(from, getItemId(v)))
                store = true;
            if (store)
                range.add(v);
            if (to != null && equalsItemIds(to, getItemId(v)))
                store = false;
        }

        return range;
    }

    private List<T> getRangeFile(K from) throws IOException {
        return getRangeFile(from, null);
    }

    private List<T> getRangeFile(K from, K to) throws IOException {
        boolean store = from == null;
        List<T> range = new ArrayList<>();

        ArrayNode array = getMainNode();
        for (Iterator<JsonNode> i = array.elements(); i.hasNext(); ) {
            JsonNode node = i.next();
            T v = jsonMapper.readValue(node.traverse(), typeOfT);

            if (from != null && equalsItemIds(from, getItemId(v)))
                store = true;
            if (store)
                range.add(v);
            if (to != null && equalsItemIds(to, getItemId(v)))
                store = false;
        }

        return range;
    }


    protected abstract int compareItemIds(K id1, K id2);

    protected boolean equalsItemIds(K id1, K id2) {
        return compareItemIds(id1, id2) == 0;
    }

    protected abstract K getItemId(T value);

}
