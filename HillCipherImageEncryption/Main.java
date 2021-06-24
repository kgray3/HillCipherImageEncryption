import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Color;

import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in); //For taking in user input
        BufferedImage img = null;
        System.out.println("Enter filename: "); //image to be manipulated
        String fileName = scanner.next(); //filename entered by user

           
            try {
                img = ImageIO.read(new File(fileName)); //reads image
            } catch (IOException e){
                //image failed to read
            }

            //Obtains user input on encryption/decryption choice
            System.out.println("Encrypt [e] or decrypt [d] image: ");
            String choice = scanner.next();

            //Handles method call based on whether user wants to encrypt or decrypt
            if(choice.equalsIgnoreCase("e")) {
                long before = System.currentTimeMillis(); //measures runtime in ms
                encryptImage(img);
                System.out.println("FINISHED IN " + (System.currentTimeMillis() - before) + " ms.");
            } else if (choice.equalsIgnoreCase("d")) {
                long before = System.currentTimeMillis(); //measures runtime in ms
                decryptImage(img);
                System.out.println("FINISHED IN " + (System.currentTimeMillis() - before) + " ms.");
            } else {
                System.out.println("[Error] Incorrect choice entered.");
            }
            scanner.close();

    }

    /* Method that performs the Hill Cipher encryption.
    / @params - BufferedImage image inputed by user.
    / @returns - BufferedImage image after Hill Cipher encryption/decryption.
    */
    public static BufferedImage hillCipher(BufferedImage img) {
        int numRows = img.getHeight(); //image pixel height (rows)
        int numCols = img.getWidth(); //image pixel width (columns)
        

        while (numCols % 8 != 0) {
            numCols++;
        }

        BufferedImage newImage = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_RGB);

        //arbitrarily set involutory key matrix (found online)
        int[][] keyMatrix = {{128,12,45,34,51,156,137,58}, 
                            {189,200,9,99,217,219,181,199},
                            {245,135,59,33,177,155,114,237},
                            {72,122,27,109,168,178,31,124},
                            {251,196,159,86,128,244,211,222},
                            {207,147,83,145,67,56,247,157},
                            {183,221,212,219,11,121,197,223},
                            {152,158,249,218,184,134,229,147}};

        //local variables to keep track of row and column index
        int row = 0;
        int col = 0;

        //loop until the last pixel
        while (row != numRows) {

            //initialize arrays for red, green, and blue vectors
            int[][] redArr = new int[8][1];
            int[][] blueArr = new int[8][1];
            int[][] greenArr = new int[8][1];

            //fill vectors with corresponding pixel colors
            for (int c = 0; c < 8; c++) {
                if(col >= img.getWidth()) {
                    redArr[c][0] = 0;
                    blueArr[c][0] = 0;
                    greenArr[c][0] = 0;
                } else {
                    Color currentColor = new Color(img.getRGB(col, row), true);
                    redArr[c][0] = currentColor.getRed();
                    blueArr[c][0] = currentColor.getBlue();
                    greenArr[c][0] = currentColor.getGreen();

                    
                }
                col++;
            }

            //Perform matrix multiplication of keyMatrix with each color channel array
            int[][] newRedArr = matrixMultiplication(keyMatrix, redArr);
            int[][] newBlueArr = matrixMultiplication(keyMatrix, blueArr);
            int[][] newGreenArr = matrixMultiplication(keyMatrix, greenArr);


            int colorArrRow = 0; //Index for row of [color] array
            //Change color of corresponding matrix in original image to new colors from matrix multiplication
            for(int y = col - 8; y < col; y++) {
                Color newColor = new Color(newRedArr[colorArrRow][0], newGreenArr[colorArrRow][0], newBlueArr[colorArrRow][0]);
                newImage.setRGB(y, row, newColor.getRGB());
                colorArrRow++;
                
            }

            //reset column # for new row
            if (col >= img.getWidth()) {
                col = 0;
                row++;
            }

        }
        return newImage;
    }

    /* Method to encrypt an image. Calls on Hill Cipher for math.
       Outputs new image to 'encryptedImage.png' file.
    */
    public static void encryptImage(BufferedImage img) throws IOException {
        BufferedImage newImage = hillCipher(img);
        
        //create output image
        File outputfile = new File("encryptedImage.png");
        ImageIO.write(newImage, "png", outputfile);
    }

    /* Method to decrypt an image. Calls on Hill Cipher for math.
       Outputs new image to 'decryptedImage.png' file.
    */
    public static void decryptImage(BufferedImage img) throws IOException {
        BufferedImage newImage = hillCipher(img);

        //create output image
        File outputfile = new File("decryptedImage.png");
        ImageIO.write(newImage, "png", outputfile);
    }


    /* Method for performing matrix multiplication mod 256.
       @params - Key Matrix and pixel vector
       @returns - Matrix result of multiplication mod 256.
    */
    public static int[][] matrixMultiplication(int[][] keyMatrix, int[][] pixelArr) {
        int[][] finalMatrix = new int[keyMatrix.length][pixelArr[0].length];

        for(int x = 0; x < keyMatrix.length; x++) {
            for(int y = 0; y < pixelArr[0].length; y++) {
                for(int z = 0; z < keyMatrix[0].length; z++) {
                    finalMatrix[x][y] += keyMatrix[x][z]*pixelArr[z][y];
                }
            }
        }

        //Mods new matrix by 256
        for(int r = 0; r < finalMatrix.length; r++){
            for(int c = 0; c < finalMatrix[0].length; c++) {
                finalMatrix[r][c] = finalMatrix[r][c] % 256;
            }
        }

        return finalMatrix;
    }

}
