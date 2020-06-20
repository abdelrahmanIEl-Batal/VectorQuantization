import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

public class ConstructVectors {

    int height,width;
    int book;
    ArrayList<vector> blocks = new ArrayList<>();
    ArrayList<Group> avg= new ArrayList<>();
    ArrayList<Group> tempArrayList = new ArrayList<Group>();
    File file;
    BufferedImage image;
    ArrayList<Integer> codes = new ArrayList<>();
    ConstructVectors(int h,int w,int k, String path)
    {
        height = h;
        width = w;
        book = k;
        file = new File(path);
    }

    boolean load() throws IOException {
         image = ImageIO.read(file);
        int [][] image_value = new int[image.getHeight()][image.getWidth()];

        //int [][]image_value = { {1,2,7,9,4,11}, {3,4,6,6,12,12}, {4,9,15,14,9,9}, {10,10,20,18,8,8}, {4,3,17,16,1,4},{4,5,18,18,5,6 } } ;

        for(int i =0;i<image.getHeight();++i) {
            for(int j=0;j<image.getWidth();++j) {
                int color = image.getRGB(j, i);
                int p = color & 0xff;
                image_value[i][j] = p;
            }
        }

        if(image.getHeight()%height!=0 || image.getWidth()%width!=0) return false;

        for(int i=0;i<image.getHeight();i+=height)
        {
            for(int j=0;j<image.getWidth();j+=width)
            {
                vector v = new vector(height, width);
                int xx = i, yy = j;
                for(int k=0;k<height;++k)
                {
                    for(int l=0;l<width;++l) {
                        v.data[k][l] = (double) image_value[xx][yy++];
                    }
                    yy = j;
                    xx++;
                }
                blocks.add(v);
            }
        }
        return true;

//        for(int i=0;i<blocks.size();++i)
//        {
//            vector v = blocks.get(i);
//            for(int j=0;j<v.row;++j) {
//                for(int k=0;k<v.col;++k) {
//                    System.out.print(v.data[j][k]+ "");
//                }
//                System.out.println();
//            }
//        }
    }

    void process() {

        vector new_avg = new vector(height, width);
        for(int i=0;i<height;++i)
        {
            for(int j=0;j<width;++j)
            {
                Double sumDouble = 0.0;
                for(int l=0;l<blocks.size();++l) {
                    vector currVector = blocks.get(l);
                    sumDouble+= currVector.data[i][j];
                }
                if(blocks.size()>0) new_avg.data[i][j] = sumDouble/ (1.0*blocks.size());
                else new_avg.data[i][j] = 0.0;
               // System.out.println(new_avg.data[i][j]);
            }
        }
        Group firstAverage  = new Group(new_avg);
        //firstAverage.nearestVectors.addAll(blocks);
        avg.add(firstAverage);

        // -------------------------------------- //

        while(tempArrayList.size() < book) { 

            tempArrayList.clear();
            for(int i=0;i<avg.size();++i) {
                Group currGroup = avg.get(i);
                split(currGroup);
                 }

            // distance
            //System.out.print(blocks.size());
            for(int i=0;i<blocks.size();i++) {

                Double maxDouble = 10000000000.0;
                int index = -1;
                vector currVector = blocks.get(i);
                //System.out.println("Temp size " + tempArrayList.size());
                for(int j=0;j<tempArrayList.size();j++)
                {
                    //System.out.println("j= "+ j);
                    Double distanceDouble = 0.0;
                    Group currGroup = tempArrayList.get(j);
                    vector currAverageVector = currGroup.v;
                    for(int k=0;k<height;k++) {
                        for(int l=0;l<width;l++) {
                            distanceDouble+= Math.abs(currVector.data[k][l]- currAverageVector.data[k][l]);
                        }
                    }
                    if(distanceDouble < maxDouble) {
                        maxDouble = distanceDouble;
                        index = j;
                    }
                }
                //System.out.println("This index "+ index);
                tempArrayList.get(index).nearestVectors.add(currVector);
            }

            avg = new ArrayList<Group>(tempArrayList);
            //System.out.println(avg.size());
            //tempArrayList.clear();
        }

		/*
		for(Group group : avg) {
			vector currVector = group.v;
			for(int i=0;i<currVector.row;++i) {
				for(int j=0;j<currVector.col;++j) {
					System.out.print(currVector.data[i][j] + " ");
				}
				System.out.println();
			}

			System.out.println(group.nearestVectors.size());
			for(int i=0;i<group.nearestVectors.size();++i) {
				vector vector = group.nearestVectors.get(i);
				for(int j=0;j<height;++j) {
					for(int k = 0;k<width;++k) {
						System.out.print(vector.data[j][k]+" ");
					}
					System.out.println();
					//System.out.println();
				}
			}
		}
		*/


        for(int k=0;k<avg.size();++k) {
            Group curGroup = avg.get(k);
            new_avg = new vector(height, width);
            for(int i=0;i<height;++i)
            {
                for(int j=0;j<width;++j)
                {
                    Double sumDouble = 0.0;
                    for(int l=0;l<curGroup.nearestVectors.size();++l) {
                        vector currVector = curGroup.nearestVectors.get(l);
                        sumDouble+= currVector.data[i][j];
                    }
                    if(curGroup.nearestVectors.size()>0) new_avg.data[i][j] = sumDouble/ (1.0*curGroup.nearestVectors.size());
                    else new_avg.data[i][j] = 0.0;

                    //System.out.println(new_avg.data[i][j]);
                }
            }
            avg.get(k).v= new_avg;
        }

        //

        int finish = 10;
        while(finish > 0) {
            for(int i=0;i<avg.size();++i) {
                avg.get(i).nearestVectors.clear();
            }
            for(int i=0;i<blocks.size();i++) {
                Double maxDouble = 10000000000.0;
                int index = -1;
                vector currVector = blocks.get(i);
                //System.out.println("Temp size " + tempArrayList.size());
                for(int j=0;j<avg.size();j++)
                {
                    //System.out.println("j= "+ j);
                    Double distanceDouble = 0.0;
                    Group currGroup = avg.get(j);
                    vector currAverageVector = currGroup.v;
                    for(int k=0;k<height;k++) {
                        for(int l=0;l<width;l++) {
                            distanceDouble+= Math.abs(currVector.data[k][l]- currAverageVector.data[k][l]);
                        }
                    }
                    if(distanceDouble < maxDouble) {
                        maxDouble = distanceDouble;
                        index = j;
                    }
                }
                //System.out.println("This index "+ index);
                avg.get(index).nearestVectors.add(currVector);
            }
            for(int k=0;k<avg.size();++k) {
                Group curGroup = avg.get(k);
                new_avg = new vector(height, width);
                for(int i=0;i<height;++i)
                {
                    for(int j=0;j<width;++j)
                    {
                        Double sumDouble = 0.0;
                        for(int l=0;l<curGroup.nearestVectors.size();++l) {
                            vector currVector = curGroup.nearestVectors.get(l);
                            sumDouble+= currVector.data[i][j];
                        }
                        if(curGroup.nearestVectors.size()>0) new_avg.data[i][j] = sumDouble/ (1.0*curGroup.nearestVectors.size());
                        else new_avg.data[i][j] = 0.0;

                        //System.out.println(new_avg.data[i][j]);
                    }
                }
                avg.get(k).v= new_avg;
            }
            finish--;
        }
		/*
		for(Group group : avg) {
			vector currVector= group.v;
			for(int i=0;i<height;++i) {
				for(int j=0;j<width;++j) {

					System.out.print(currVector.data[i][j]+ " ");
				}
				System.out.println();
			}

		}
		*/
        // get codes of vectors ( index of the average that represents it in codeBook)

        for(int i=0;i<blocks.size();++i) {
            vector curVector = blocks.get(i);
            Boolean exit = false;
            for(int j=0;j<avg.size();++j) {
                Group curGroup = avg.get(j);
                for(int k=0;k<curGroup.nearestVectors.size();++k) {

                    if(equal(curVector.data, curGroup.nearestVectors.get(k).data)){
                        codes.add(j);
                        exit = true;
                        break;
                    }
                }
                if(exit) break;
            }
        }
        System.out.println();
        // for(Integer i : codes) System.out.print(i + " ");

    }


    void writeToFile() throws IOException {
        FileWriter writer = new FileWriter("codes.txt");
        // first we write the dimensions of vector and the dimensions of original image
        writer.write(height + " " + width + " " + image.getHeight() + " " + image.getWidth());   // add image height and with instead of 500 and 500
        writer.write(System.getProperty( "line.separator" ));
        // then we write codes of blocks
        for(Integer integer : codes) {
            writer.write(integer + " ");
        }
        writer.write(System.getProperty( "line.separator" ));

        for(Group group : avg) {
            vector cuVector = group.v;
            for(int i=0;i<height;++i) {
                for(int j=0;j<width;++j) {
                    writer.write(cuVector.data[i][j] + " ");
                }
                writer.write(System.getProperty( "line.separator" ));
            }
        }
        //writer.write(System.getProperty( "line.separator" ));
        writer.close();
        
    }

    void split(Group curr_average) {

        vector vector1 = new vector(height, width);
        vector vector2 = new vector(height, width);

        vector currVector = curr_average.v;
        for(int i=0;i<height;++i) {
            for(int j=0;j<width;++j) {
                vector1.data[i][j] = Math.floor(currVector.data[i][j]);
                vector2.data[i][j] = Math.floor(currVector.data[i][j]) + 1.0;
            }
        }

        Group group1 = new Group(vector1);
        Group group2 = new Group(vector2);
        //System.out.print("Added");
        tempArrayList.add(group1);
        tempArrayList.add(group2);
        //System.out.println(tempArrayList.size());
    }

    Boolean equal (Double[][] data, Double[][] data2) {
        for(int i=0;i<data.length;++i) {
            for(int j=0;j<data[0].length;++j) {
                if(data[i][j]!=data2[i][j]) return false;
            }
        }
        return true;
    }
}
