public class MetaFile {
    String fileName;
    String fileExtension;
    int nChunks;
    int size;
    public MetaFile(String fileName, String fileExtension, int nChunks,int size){
        this.fileExtension = fileExtension;
        this.fileName = fileName;
        this.nChunks = nChunks;
        this.size = size;
    }

}
