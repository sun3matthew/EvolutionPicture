import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.Color;
/*

java EvoPicture filename

The images (in .jpg format) are added into a folder in the Input_Images,
the two images should be named One.jpg and Two.jpg, the "Two" image acts
as the world and the "One" image acts as the sample population that will
be selected to populate the world.

Random pixels from the the "One" image are selected and put into the world.
For the first 15 generation, each pixel will look at the 8 pixels around it
to find the "Best" home, this goes on for about 40 frames. Then after it the
40 frames, the pixels who are too diffrent from the current world pixel they
are currently on are deleted. The remaining pixels have create children who
are slightly mutated versions of themselves(colorwise). This process goes on
for 15 more generations, then after the 15 generations the pixels will no
longer look/move and just produce more child pixels. Even the majority of
the pixels on the screen is covered, the program will end, exporting the images
into the Output_Images.

By: Matthew Sun
Since: May 31 2020
*/

/*
The main class to setup java GUI
*/
public class EvoPicture
{
 JFrame frame;
 AiProgram canvas;
 public static void main (String[] args)
 {
   String folderName = "Lofi";
   if(args.length > 0)
     folderName = args[0];
  EvoPicture kt = new EvoPicture();
  kt.Run(folderName);
 }

 public void Run(String name) {
  frame = new JFrame("EvoPicture");
  frame.setSize(1050, 1050);
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  frame.setResizable(true);
  canvas = new AiProgram(name);
  frame.getContentPane().add(canvas);

  frame.setVisible(true);
 }
}

/*
The the class the runs the program
*/
class AiProgram extends JPanel{

Pixel[][] currentImage;
Pixel[][] currentAlive;
Pixel[][] colorImage;
Boolean[][] doneOnes;
private Timer balltimer;
private int counter;
int genCounter;
String exportPath;
final int startWith = 16;
final int numberOfGens = 15;
final int survival = 100;
final int vision = 3;
final int stopAt = 1500;
/*
Constructor to get the data from the two images using a image class
*/
 public AiProgram(String folderName) {
   String dir = System.getProperty("user.dir");
   String mainPath = dir.substring(0, dir.length()-7);
   String newFolder = mainPath+"Output_Images/"+folderName+"Output";
   File folder = new File(mainPath+"Input_Images/"+folderName);
   new File(newFolder).mkdirs();
   exportPath = newFolder+"/";
   File[] listOfFiles = folder.listFiles();
   int counter = 0;
   for(int i = 0; i < listOfFiles.length; i++)
   {
     if(listOfFiles[i].getName().indexOf("jpg") != -1)
     {
       String fileName = listOfFiles[i].getPath();
       Image picture = new Image(fileName);
       if(listOfFiles[i].getName().indexOf("One") != -1)
       {
         colorImage = picture.getData();
       }else
       {
         currentImage = picture.getData();
       }
     }
   }
   currentAlive = new Pixel[currentImage.length][currentImage[0].length];
   genCounter = 0;
   startGen();
  BallMover ballmover = new BallMover();
  balltimer = new Timer(20, ballmover);
  balltimer.start();
 }

 /*
 This method randomly selects pixels in image One and populates the world with them
 */
 public void startGen()
 {
   int numPix = (currentImage.length*currentImage[0].length)/startWith;
   for(int i = 0; i < numPix; i++)
   {
     int row = randomWithRange(0, currentImage.length-1);
     int col = randomWithRange(0, currentImage[0].length-1);
     int rowColor = randomWithRange(0, colorImage.length-1);
     int colColor = randomWithRange(0, colorImage[0].length-1);
     currentAlive[row][col] = new Pixel(colorImage[rowColor][colColor].getColor());
   }
 }

 /*
 Helper method
 */
  public int randomWithRange(int min, int max)
  {
     int range = (max - min) + 1;
     return (int)(Math.random() * range) + min;
  }

  /*
  The "update function"
  */
 class BallMover implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    counter++;
    if(genCounter >= numberOfGens)
    {
      counter = -1;
    }
    if(count() <= 300)
    {
      for(int row = 0; row < currentAlive.length; row++)
        for(int col = 0; col < currentAlive[0].length; col++)
          if(currentAlive[row][col] == null)
            currentAlive[row][col] = new Pixel();
      Image newPicture = new Image(currentAlive);
      newPicture.exportImage(exportPath+"OutputImg");
      System.exit(0);
    }
    if(genCounter > 150)
    {
      genCounter += 5;
    }
    if(counter != -1 && counter < 150)
    {
      updatePixel();
    }else
    {
      for(int row = 0; row < currentAlive.length; row++)
        for(int col = 0; col < currentAlive[0].length; col++)
          if(currentAlive[row][col] != null && isDead(currentImage[row][col].comparePixel(currentAlive[row][col])))
            currentAlive[row][col] = null;

      System.out.println("Number Of Empty Pixels "+count());
      counter = 0;
      genCounter++;
      for(int randTest = 0; randTest < (currentAlive.length*currentAlive[0].length)/2; randTest++)
      {
        int row = randomWithRange(0, currentAlive.length-1);
        int col = randomWithRange(0, currentAlive[0].length-1);
          if(currentAlive[row][col] != null)
          {
              int numBabies = ((80 - currentImage[row][col].comparePixel(currentAlive[row][col]))/10)+(genCounter/100);
              if(numBabies < 0)
                numBabies = 0;
              int babyCounter = 0;
              for(int i = 1; i <= numBabies; i++)
              {
                int babyRange = 1;
                int checkCol = randomWithRange(-1*babyRange+col, babyRange+col);
                int checkRow = randomWithRange(-1*babyRange+row, babyRange+row);
                if(checkCol >= 0 && checkCol < currentAlive[0].length && checkRow >= 0 && checkRow < currentAlive.length &&
                checkCol != col && checkRow != row && currentAlive[checkRow][checkCol] == null)
                {
                  babyCounter++;
                  currentAlive[checkRow][checkCol] = geiWoBaby(currentAlive[row][col], babyCounter);
                }
              }
           }
          }
        }
        //repaint();
    }
  }

  /*
  Counts the number of empty Pixels
  */
  public int count()
  {
    int counter = 0;
    for(int row = 0; row < currentAlive.length; row++)
      for(int col = 0; col < currentAlive[0].length; col++)
        if(currentAlive[row][col] == null)
          counter++;
    return counter;
  }

  /*
  This method moves all the pixels
  */
   public void updatePixel()
   {
       for(int i = 0; i < (currentAlive.length*currentAlive[0].length)/2; i++)
       {
         int row = randomWithRange(0, currentAlive.length-1);
         int col = randomWithRange(0, currentAlive[0].length-1);
         if(currentAlive[row][col] != null)
         {
           int highRow = row;
           int highCol = col;
           int highestFitness = 765;
           for(int checkRow = row - vision; checkRow <= row + vision; checkRow++)
            for(int checkCol = col - vision; checkCol <= col + vision; checkCol++)
              if(checkCol >= 0 && checkCol < currentAlive[0].length && checkRow >= 0 && checkRow < currentAlive.length)
              {
                int currentFitness = currentImage[checkRow][checkCol].comparePixel(currentAlive[row][col]);
                if(currentFitness < highestFitness && currentAlive[checkRow][checkCol] == null)
                {
                  highestFitness = currentFitness;
                  highRow = checkRow;
                  highCol = checkCol;
                }
              }
              if(highRow == row && highCol == col)
              {
                if(isDead(currentImage[row][col].comparePixel(currentAlive[row][col])))
                  currentAlive[row][col] = null;
              }else
              {
                currentAlive[highRow][highCol] = currentAlive[row][col];
                currentAlive[row][col] = null;
              }
         }
       }
   }

   /*
   This method returns a slightly mutated child of the parent
   */
   public Pixel geiWoBaby(Pixel parent, int mutationAmount)
   {
     mutationAmount = (int)Math.pow(2,mutationAmount);
     return new Pixel(parent.r+randomWithRange(-1*mutationAmount,mutationAmount), parent.g+randomWithRange(-1*mutationAmount,mutationAmount), parent.b+randomWithRange(-1*mutationAmount,mutationAmount));
   }

   /*
   This method returns if a fitness level would result in death
   */
   public boolean isDead(int fitness)
   {
     return !(fitness < survival+(genCounter/10));
   }

   /*
   This method draws the pixels on screen
   */
   public void paintComponent(Graphics g) {
    super.paintComponent(g);
    setBackground(Color.BLACK);
    for(int row = 0; row < currentAlive.length; row++)
      for(int col = 0; col < currentAlive[0].length; col++)
        if(currentAlive[row][col] != null)
        {
          g.setColor(currentAlive[row][col].getColor());
          g.drawLine(col, row, col, row);
        }
   }
   public Pixel[][] cloneArray(Pixel[][] cloneThis)
   {
     Pixel[][] newArray = new Pixel[cloneThis.length][cloneThis[0].length];
     for (int row = 0; row < newArray.length; row++)
      for (int col = 0; col < newArray[0].length; col++)
       newArray[row][col] = cloneThis[row][col];
     return newArray;
   }
}
