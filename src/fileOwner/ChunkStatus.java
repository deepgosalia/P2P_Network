package fileOwner;

public class ChunkStatus implements java.io.Serializable {
    public int size;
    public boolean received;

    public ChunkStatus(int size, boolean received) {
        this.size = size;
        this.received = received;
    }
}
