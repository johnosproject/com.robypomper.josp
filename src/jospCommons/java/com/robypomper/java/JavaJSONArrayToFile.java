package com.robypomper.java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class JavaJSONArrayToFile<T, K> {

    // Internal constants

    private Filter<T> NO_FILTER = new Filter<T>() {
        @Override
        public boolean accepted(T o) {
            return true;
        }
    };


    // Internal vars

    private final ObjectMapper jsonMapper;
    private final Class<T> typeOfT;
    private final File jsonFile;
    private int fileCount;       // count in file (for total add cacheBuffered.size())
    private T fileFirst = null;
    private T fileLast = null;
    private final List<T> cacheBuffered; // stored only on memory not on file


    // Constructor

    public JavaJSONArrayToFile(File jsonFile, Class<T> typeOfT) throws IOException {
        if (jsonFile.isDirectory())
            throw new IllegalArgumentException("File can not be a directory!");

        this.jsonMapper = JsonMapper.builder().build();
        this.typeOfT = typeOfT;
        this.jsonFile = jsonFile;

        cacheBuffered = new ArrayList<>();
        initCache();
    }


    // Memory mngm

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
                array.insertPOJO(0, v);
            fileCount += cacheBuffered.size();
            if (fileFirst == null) fileFirst = getFirstBuffered();
            fileLast = cacheBuffered.get(cacheBuffered.size() - 1);
            cacheBuffered.clear();

            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, array);
        }
    }

    public void flushCache(int count) throws IOException {
        if (cacheBuffered.size() == 0) return;
        if (count <= 0) return;

        int countAdded = 0;
        synchronized (cacheBuffered) {
            ArrayNode array = getMainNode();

            for (T v : cacheBuffered) {
                countAdded++;
                array.addPOJO(v);
                if (countAdded == count)
                    break;
            }

            fileCount += countAdded;
            if (fileFirst == null) fileFirst = getFirstBuffered();
            fileLast = cacheBuffered.get(countAdded - 1);

            cacheBuffered.subList(0, countAdded).clear();

            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, array);
        }

    }


    // Counts, firsts and lasts

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
        if (getFirstFile() != null)
            return getFirstFile();

        return getFirstBuffered();
    }

    public T getFirstBuffered() {
        if (cacheBuffered.size() > 0)
            return cacheBuffered.get(0);
        return null;
    }

    public T getFirstFile() {
        return fileFirst;
    }

    public T getLast() {
        if (getLastBuffered() != null)
            return getLastBuffered();

        return getLastFile();
    }

    public T getLastBuffered() {
        if (cacheBuffered.size() > 0)
            return cacheBuffered.get(cacheBuffered.size() - 1);
        return null;
    }

    public T getLastFile() {
        return fileLast;
    }


    // Getters and Filters

    private T findInBuffered(K id) {
        for (T v : cacheBuffered)
            if (equalsItemIds(id, getItemId(v)))
                return v;
        return null;
    }

    private T findInBuffered(Date date) {
        for (T v : cacheBuffered)
            if (equalsItemDate(date, getItemDate(v)))
                return v;
        return null;
    }

    public List<T> getAll() throws IOException {
        return filterAll(NO_FILTER);
    }

    public List<T> getLatest(long latestCount) throws IOException {
        return filterLatest(NO_FILTER, latestCount);
    }

    public List<T> getAncient(long ancientCount) throws IOException {
        return filterAncient(NO_FILTER, ancientCount);
    }

    public List<T> getById(K fromId, K toId) throws IOException {
        return filterById(NO_FILTER, fromId, toId);
    }

    public List<T> getByData(Date fromDate, Date toDate) throws IOException {
        return filterByDate(NO_FILTER, fromDate, toDate);
    }

    // Getters and Filters: All (filtered)

    public List<T> tryAll(Filter<T> filter) {
        try {
            return filterAll(filter);
        } catch (IOException e) {
            return filterAllBuffered(filter);
        }
    }

    public List<T> filterAll(Filter<T> filter) throws IOException {
        List<T> filtered = new ArrayList<>(filterAllFile(filter));
        filtered.addAll(filterAllBuffered(filter));
        return filtered;
    }

    public List<T> filterAllBuffered(Filter<T> filter) {
        List<T> filtered = new ArrayList<>();

        for (T o : cacheBuffered) {
            if (filter.accepted(o))
                filtered.add(o);
        }

        return filtered;
    }

    public List<T> filterAllFile(Filter<T> filter) throws IOException {
        List<T> filtered = new ArrayList<>();

        ArrayNode array = getMainNode();
        int count = 0;
        for (Iterator<JsonNode> i = array.elements(); i.hasNext(); ) {
            count++;
            JsonNode node = i.next();
            T o = jsonMapper.readValue(node.traverse(), typeOfT);

            if (filter.accepted(o))
                filtered.add(o);
        }

        System.out.println(String.format("\nScanning di %d elementi, %d accepted", count, filtered.size()));

        return filtered;
    }

    // Getters and Filters: Latest

    public List<T> tryLatest(Filter<T> filter, long latestCount) {
        try {
            return filterLatest(filter, latestCount);
        } catch (IOException e) {
            return filterLatestBuffered(filter, latestCount);
        }
    }

    public List<T> filterLatest(Filter<T> filter, long latestCount) throws IOException {
        List<T> filtered = new ArrayList<>(filterLatestBuffered(filter, latestCount));

        if (filtered.size() < latestCount)
            filtered.addAll(filterLatestFile(filter, latestCount - filtered.size()));

        return filtered;
    }

    public List<T> filterLatestBuffered(Filter<T> filter, long latestCount) {
        List<T> filtered = new ArrayList<>();

        for (ListIterator<T> i = cacheBuffered.listIterator(cacheBuffered.size()); i.hasPrevious(); ) {
            T o = i.previous();
            if (latestCount-- == 0)
                break;
            if (filter.accepted(o))
                filtered.add(o);
        }

        return filtered;
    }

    public List<T> filterLatestFile(Filter<T> filter, long latestCount) throws IOException {
        List<T> filtered = new ArrayList<>();

        ArrayNode array = getMainNode();
        int count = 0;
        for (Iterator<JsonNode> i = array.elements(); i.hasNext(); ) {
            count++;
            JsonNode node = i.next();
            T o = jsonMapper.readValue(node.traverse(), typeOfT);

            if (filter.accepted(o)) {
                filtered.add(o);
                if (--latestCount == 0)
                    break;
            }
        }

        System.out.println(String.format("\nScanning di %d elementi, %d accepted", count, filtered.size()));

        return filtered;
    }


    // Getters and Filters: Ancient

    public List<T> tryAncient(Filter<T> filter, long ancientCount) {
        try {
            return filterAncient(filter, ancientCount);
        } catch (IOException e) {
            return filterAncientBuffered(filter, ancientCount);
        }
    }

    public List<T> filterAncient(Filter<T> filter, long ancientCount) throws IOException {
        List<T> filtered = new ArrayList<>(filterAncientFile(filter, ancientCount));

        if (filtered.size() < ancientCount)
            filtered.addAll(filterAncientBuffered(filter, ancientCount - filtered.size()));

        return filtered;
    }

    public List<T> filterAncientBuffered(Filter<T> filter, long ancientCount) {
        List<T> filtered = new ArrayList<>();

        for (Iterator<T> i = cacheBuffered.iterator(); i.hasNext(); ) {
            T o = i.next();
            if (ancientCount-- == 0)
                break;
            if (filter.accepted(o))
                filtered.add(o);
        }

        return filtered;
    }

    public List<T> filterAncientFile(Filter<T> filter, long ancientCount) throws IOException {
        List<T> filtered = new ArrayList<>();

        ArrayNode array = getMainNode();
        int count = 0;
        for (Iterator<JsonNode> i = array.iterator(); i.hasNext(); ) {
            count++;
            JsonNode node = i.next();
            T o = jsonMapper.readValue(node.traverse(), typeOfT);
            if (filter.accepted(o))
                filtered.add(o);
        }

        System.out.println(String.format("\nScanning di %d elementi, %d accepted", count, filtered.size()));
        if (filtered.size() > ancientCount)
            filtered = filtered.subList((int) (filtered.size() - ancientCount), filtered.size());

        // ToDo Reverse array order (required ancient)  ???
        return filtered;
    }

    // Getters and Filters: ById

    public List<T> tryById(Filter<T> filter, K fromId, K toId) {
        try {
            return filterById(filter, fromId, toId);
        } catch (IOException e) {
            return filterByIdBuffered(filter, fromId, toId);
        }
    }

    public List<T> filterById(Filter<T> filter, K fromId, K toId) throws IOException {
        if (count() == 0) return new ArrayList<>();

        if (fromId == null)
            if (fileFirst != null)
                fromId = getItemId(fileFirst);
            else if (cacheBuffered.size() > 0)
                fromId = getItemId(getFirstBuffered());                     // ATTENZIONE null
            else
                assert false;
        T fromEv = findInBuffered(fromId);

        if (toId == null)
            if (cacheBuffered.size() > 0)
                toId = getItemId(cacheBuffered.get(cacheBuffered.size() - 1));
            else if (fileLast != null)
                toId = getItemId(fileLast);
            else
                assert false;
        T toEv = findInBuffered(toId);

        List<T> range = new ArrayList<>();
        if (fromEv != null && toEv != null) {
            range.addAll(filterByIdBuffered(filter, fromId, toId));

        } else if (fromEv == null && toEv != null) {
            range.addAll(filterByIdFile(filter, fromId, null));
            range.addAll(filterByIdBuffered(filter, null, toId));

        } else //noinspection ConstantConditions
            if (fromEv == null && toEv == null) {
                range.addAll(filterByIdFile(filter, fromId, toId));
            }

        return range;
    }

    public List<T> filterByIdBuffered(Filter<T> filter, K fromId, K toId) {
        boolean store = fromId == null;
        List<T> range = new ArrayList<>();

        for (T v : cacheBuffered) {
            if (fromId != null && equalsItemIds(fromId, getItemId(v)))
                store = true;
            if (store && filter.accepted(v))
                range.add(v);
            if (toId != null && equalsItemIds(toId, getItemId(v)))
                store = false;
        }

        return range;
    }

    public List<T> filterByIdFile(Filter<T> filter, K fromId, K toId) throws IOException {
        boolean store = fromId == null;
        List<T> range = new ArrayList<>();

        ArrayNode array = getMainNode();
        for (Iterator<JsonNode> i = array.elements(); i.hasNext(); ) {
            JsonNode node = i.next();
            T v = jsonMapper.readValue(node.traverse(), typeOfT);

            if (fromId != null && equalsItemIds(fromId, getItemId(v)))
                store = true;
            if (store && filter.accepted(v))
                range.add(v);
            if (toId != null && equalsItemIds(toId, getItemId(v)))
                store = false;
        }

        return range;
    }

    // Getters and Filters: ByDate

    public List<T> tryByDate(Filter<T> filter, Date fromDate, Date toDate) {
        try {
            return filterByDate(filter, fromDate, toDate);
        } catch (IOException e) {
            return filterByDateBuffered(filter, fromDate, toDate);
        }
    }

    public List<T> filterByDate(Filter<T> filter, Date fromDate, Date toDate) throws IOException {
        if (fromDate != null && getFirstBuffered() != null && compareItemDate(getItemDate(getFirstBuffered()), fromDate) < 0)
            return filterByDateBuffered(filter, fromDate, toDate);

        List<T> filtered = filterByDateFile(filter, fromDate, toDate);
        if (toDate == null || (getLastBuffered() != null && compareItemDate(getItemDate(getLastBuffered()), toDate) > 0))
            filtered.addAll(filterByDateBuffered(filter, fromDate, toDate));
        return filtered;
    }

    public List<T> filterByDateBuffered(Filter<T> filter, Date fromDate, Date toDate) {
        boolean store = fromDate == null;
        List<T> range = new ArrayList<>();

        for (T v : cacheBuffered) {
            if (fromDate != null && compareItemDate(fromDate, getItemDate(v)) < 0)
                store = true;
            if (toDate != null && compareItemDate(toDate, getItemDate(v)) < 0)
                break;
            if (store && filter.accepted(v))
                range.add(v);
        }

        return range;
    }

    public List<T> filterByDateFile(Filter<T> filter, Date fromDate, Date toDate) throws IOException {
        boolean store = toDate == null;
        List<T> range = new ArrayList<>();

        ArrayNode array = getMainNode();
        for (Iterator<JsonNode> i = array.elements(); i.hasNext(); ) {
            JsonNode node = i.next();
            T v = jsonMapper.readValue(node.traverse(), typeOfT);

            if (toDate != null && compareItemDate(toDate, getItemDate(v)) > 0)
                store = true;
            if (fromDate != null && compareItemDate(fromDate, getItemDate(v)) > 0)
                break;
            if (store && filter.accepted(v))
                range.add(v);
        }

        return range;
    }

    /**
     * @return the value {@code 0} if the argument string is equal to this string;
     * a value less than {@code 0} if first date is before than the second argument;
     * and a value greater than {@code 0} if first date is after than the second argument.
     */
    protected int compareItemDate(Date id1, Date id2) {
        return (int) (id1.getTime() - id2.getTime());
    }


    // Sub class utils

    protected abstract int compareItemIds(K id1, K id2);

    protected boolean equalsItemIds(K id1, K id2) {
        return compareItemIds(id1, id2) == 0;
    }

    protected abstract K getItemId(T value);

    protected boolean equalsItemDate(Date id1, Date id2) {
        return compareItemDate(id1, id2) == 0;
    }

    protected abstract Date getItemDate(T value);

    public interface Filter<T> {
        boolean accepted(T o);
    }

}
