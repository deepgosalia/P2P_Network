package fileOwner;

public class ChunkStatus implements java.io.Serializable {
    int size;
    boolean received;

    public ChunkStatus(int size, boolean received) {
        this.size = size;
        this.received = received;
    }
}
