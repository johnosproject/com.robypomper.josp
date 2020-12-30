package com.robypomper.josp.jod.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class CloudStats {

    private final File file;

    public long lastStored = -1;
    public long lastUploaded = -1;
    public long lastDelete = 0;

    public long uploaded = 0;
    public long deleted = 0;
    public long unUploaded = 0;

    @JsonCreator
    private CloudStats(@JsonProperty("file") File file,
                       @JsonProperty("lastStored") long lastStored,
                       @JsonProperty("lastUploaded") long lastUploaded,
                       @JsonProperty("lastDelete") long lastDelete,
                       @JsonProperty("uploaded") long uploaded,
                       @JsonProperty("deleted") long deleted,
                       @JsonProperty("unUploaded") long unUploaded) throws IOException {
        this.file = file;
        this.lastStored = lastStored;
        this.lastUploaded = lastUploaded;
        this.lastDelete = lastDelete;
        this.uploaded = uploaded;
        this.deleted = deleted;
        this.unUploaded = unUploaded;
    }

    public CloudStats(File file) throws IOException {
        this.file = file;

        if (file.exists()) {
            CloudStats s = read();
            this.lastStored = s.lastStored;
            this.lastUploaded = s.lastUploaded;
            this.lastDelete = s.lastDelete;
            this.uploaded = s.uploaded;

            this.deleted = s.deleted;
            this.unUploaded = s.unUploaded;
        }
    }

    private CloudStats read() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, CloudStats.class);
    }

    public void store() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, this);
    }

    public void storeIgnoreExceptions() {
        try {
            store();
        } catch (IOException ignore) {
        }
    }

}
