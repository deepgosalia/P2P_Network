package v1;

import java.io.*;
import java.util.Map;

public class Splitter {

    public MetaFile main(Map<Integer,Integer> map) throws IOException {

        // Default values:
        String fileName = "test";
        String fileExtension = "pdf";


        MetaFile metaFile = new MetaFile(fileName,fileExtension,0,0);
        byte[] binaryData = new byte[1000000];
        int read_bytes;
        int x = 1, j;
        String s;


        String current_dir = new File(".").getCanonicalPath();
        String path = current_dir + "\\src\\" + fileName + "." + fileExtension; // TODO remove src while submitting code \\ is for windows

        String extension = ".bin";  // To store as binary data

        FileInputStream fis = new FileInputStream(path);

        while (fis.available() != 0) {
            j = 0;
            s = current_dir + "\\src\\" + x + extension;
            FileOutputStream fos = new FileOutputStream(s);
            while (j <= 100000 && fis.available() != 0) {  // TODO 100000 ?????
                read_bytes = fis.read(binaryData, 0, 102400); // 100KB chunks
                //save details in meta
                map.put(x,read_bytes);
                metaFile.nChunks = x;
                metaFile.size = metaFile.size + read_bytes;
                j = j + read_bytes;
                fos.write(binaryData, 0, read_bytes);
            }

            x++; // file naming
        }
        System.out.println("File has been splitted to chunks");
        fis.close();

        return metaFile;
    }

}
