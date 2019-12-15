package peer1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import fileOwner.Splitter;

public class FileJoin {
    boolean is_false;
    public FileJoin(boolean flag){
        this.is_false = flag;
    }

    public void main(String path) throws IOException {


        //It will store the bytes of file in it and then we will break it
        byte[] b = new byte[1000000];
        // TODO Remove hardcoded path
        Splitter s = new Splitter();
        //boolean is_false = true;
        String current_dir = new File(".").getCanonicalPath();
        String newFileName = "test_new";
        String extension = ".pdf";
        String new_path = current_dir + path + newFileName + extension;
        File f = new File(new_path);
        System.out.println(new_path);
        Files.write(f.toPath(),s.chunksRange());  // create empty file
        if(!is_false) {
            try {

                FileOutputStream fos = new FileOutputStream(new_path);
                fos.write(b);
                int x = 1;
                int read_bytes;
                String parts_name_path = "";
                while (true) {
                    parts_name_path = current_dir + path + x + ".bin";
                    if (f.exists()) {
                        FileInputStream fis = new FileInputStream(parts_name_path);
                        while (fis.available() != 0) {
                            read_bytes = fis.read(b, 0, 102400);
                            fos.write(b, 0, read_bytes);
                        }
                        fis.close();
                        x++;
                    } else {
                        System.out.println("All files merged");
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }

}
