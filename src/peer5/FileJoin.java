package peer5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileJoin {
    boolean is_false;
    FileJoin(){

    }
    FileJoin(boolean flag){
        this.is_false = flag;
    }
    public static void main(String path) throws IOException {


        //It will store the bytes of file in it and then we will break it
        byte[] b = new byte[1000000];
        // TODO Remove hardcoded path

        String current_dir = new File(".").getCanonicalPath();


        String newFileName = "test_new";
        String extension = ".pdf";
        String new_path = current_dir + path + newFileName + extension;


        try {
            FileOutputStream fos = new FileOutputStream(new_path);
            int x = 1;
            int read_bytes;
            String parts_name_path = "";
            while (true) {
                parts_name_path = current_dir + path + x + ".bin";

                File f = new File(parts_name_path);
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
