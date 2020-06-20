/// Multimedia course Project
/// Author : Abdelrahman Ibrahim

import java.io.IOException;
import java.security.CodeSigner;

public class Main {
	public static void  main(String [] args) throws IOException {
		/// make sure that dimensions of image is divisible by dimensions of vector
		ConstructVectors constructVectors = new ConstructVectors(2, 2, 8, "image2.png");
		boolean check = constructVectors.load();
		if(!check) System.out.println("Image Height, Width must be divisible by height and width of vector, Please enter a valid image");
		else {
			constructVectors.process();
			constructVectors.writeToFile();
			Decompress decompress = new Decompress("codes.txt");
			decompress.constructImage();

		}
	}
}
