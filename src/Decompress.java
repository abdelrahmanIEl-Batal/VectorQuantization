
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;




public class Decompress {
    BufferedReader reader;
    int height, width, row, col;

    ArrayList<vector> averages = new ArrayList<vector>();
    ArrayList<Integer> codes = new ArrayList<Integer>();

    Decompress(String path) throws FileNotFoundException{
        reader = new BufferedReader(new FileReader(path));
    }

    void constructImage() throws IOException {

        String line;
        line = reader.readLine();
        String[] splits = line.split(" ");
        row = Integer.parseInt(splits[0]);
        col = Integer.parseInt(splits[1]);
        height = Integer.parseInt(splits[2]);
        width = Integer.parseInt(splits[3]);
        line = reader.readLine();
        splits = line.split(" ");
        for(int i=0;i<splits.length;++i) {
            codes.add(Integer.parseInt(splits[i]));

        }

        int c = 0;
        vector vec = new vector(row, col);
        while(line!=null) {
            line = reader.readLine();
            if(line==null) break;
            splits = line.split(" ");
            for(int i=0;i<splits.length;++i) {
                vec.data[c][i] = Double.parseDouble(splits[i]);
            }
            if(c==row-1) {
                averages.add(vec);
                vec = new vector(row, col);
                c = 0;
            }
            else c++;
        }
        reader.close();
//        for(vector v: averages) {
//            for(int i=0;i<row;++i) {
//                for(int j=0;j<col;++j) {
//                    System.out.print(v.data[i][j]+ " ");
//                }
//                System.out.println();
//            }
//        }

        int [][] image_value = new int [height][width];

        int idx = 0;

        for(int i=0;i<height;i+=row) {
            for(int j=0;j<width;j+=col) {
                vector v= averages.get(codes.get(idx++));
                int r = i, cc = j;
                for (int k = 0; k < row; ++k) {
                    for (int l = 0; l < col; ++l) {
                        image_value[r][cc++] =(v.data[k][l]).intValue();
                    }
                    cc = j;
                    r++;
                }
            }
        }
		/*
		for(int i=0;i<height;++i) {
			for(int j=0;j<width;++j) {
				System.out.print(image_value[i][j]+" ");
			}
			System.out.println();
		}
		*/
        BufferedImage out = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<width;++i){
            for(int j=0;j<height;++j){
                int p = image_value[j][i];

                out.setRGB(i,j, (p<<16) | (p<<8) | p);
            }
        }
        System.out.println("Done!");
        ImageIO.write(out,"JPG", new File("res.jpg"));
    }
}

