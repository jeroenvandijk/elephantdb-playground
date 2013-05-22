package elephantdb.document;

import java.util.Arrays;

public class NewKeyValDocument {
    public byte[] key;
    public byte[] value;

    public NewKeyValDocument() {
    }

    public NewKeyValDocument(byte[] key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        NewKeyValDocument other = (NewKeyValDocument) obj;
        return (Arrays.equals(this.key, other.key) && Arrays.equals(this.value, other.value));
    }
}