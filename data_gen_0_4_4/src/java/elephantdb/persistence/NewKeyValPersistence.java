package elephantdb.persistence;

import elephantdb.document.NewKeyValDocument;

import java.io.IOException;

public interface NewKeyValPersistence extends Persistence<NewKeyValDocument> {
    byte[] get(byte[] key) throws IOException;
    void put(byte[] key, byte[] value) throws IOException;
}