package fileOwner;

public class ChunkObj  implements java.io.Serializable {
    public byte[] chunk;
    public ChunkObj(byte[] arr){
        this.chunk = arr;
    }
}
