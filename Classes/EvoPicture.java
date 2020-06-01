import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.Color;
//as counter goes up, detail goes up
//as vision go up, delatil also gose up
//
public class EvoPicture
{
 JFrame frame;
 AiProgram canvas;
 public static void main (String[] args)
 {
   String folderName = "Test";
   if(args.length > 0)
     folderName = args[0];
  EvoPicture kt = new EvoPicture();
  kt.Run(folderName);
 } // end main

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
 public AiProgram(String folderName) {
   String dir = System.getProperty("user.dir");
   //System.out.println(dir.substring(0, dir.length()-7));
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
         /*
         for(int row = 0; row < currentImage.length; row++)
           for(int col = 0; col < currentImage[0].length; col++)
           {
             int average = (currentImage[row][col].r+currentImage[row][col].g+currentImage[row][col].b)/3;
             currentImage[row][col].setColor(average, average, average);
           }
           */
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

 public void startGen()
 {
   int numPix = (currentImage.length*currentImage[0].length)/startWith;
   for(int i = 0; i < numPix; i++)
   {
     int row = randomWithRange(0, currentImage.length-1);
     int col = randomWithRange(0, currentImage[0].length-1);
     int rowColor = randomWithRange(0, colorImage.length-1);
     int colColor = randomWithRange(0, colorImage[0].length-1);
     //while(currentAlive[row][col] != null)
     //{
      // row = randomWithRange(0, currentImage.length-1);
    //   col = randomWithRange(0, currentImage[0].length-1);
    // }
     //currentAlive[row][col] = new Pixel(randomWithRange(0, 255), randomWithRange(0, 255), randomWithRange(0, 255));
     currentAlive[row][col] = new Pixel(colorImage[rowColor][colColor].getColor());
     //System.out.println(i + "     " + numPix);
   }
 }
public int randomWithRange(int min, int max)
{
   int range = (max - min) + 1;
   return (int)(Math.random() * range) + min;
}
 class BallMover implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    counter++;
    //System.out.println(genCounter);
    /*
    for(int i = 0; i < 1000; i++)
    {
      updateShit();
      System.out.println(i);
    }
    */
    //int genCounter = 0;
    if(genCounter >= numberOfGens)
    {
      counter = -1;
    }
    if(count() <= 300)// || genCounter >= stopAt
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
    if(counter != -1 && counter < 150)//counter < 150
    {
      updateShit();
      System.out.println(counter);
    }else
    {
      for(int row = 0; row < currentAlive.length; row++)
        for(int col = 0; col < currentAlive[0].length; col++)
          if(currentAlive[row][col] != null && isDead(currentImage[row][col].comparePixel(currentAlive[row][col])))
            currentAlive[row][col] = null;

      System.out.println("yep "+count()+"\t"+genCounter);
      counter = 0;
      genCounter++;
      for(int randTest = 0; randTest < (currentAlive.length*currentAlive[0].length)/2; randTest++)
      {
        int row = randomWithRange(0, currentAlive.length-1);
        int col = randomWithRange(0, currentAlive[0].length-1);
          if(currentAlive[row][col] != null)
          {
          //  if(isDead(currentImage[row][col].comparePixel(currentAlive[row][col])))
          //  {
          //    currentAlive[row][col] = null;
          //  }else
          //  {
              int numBabies = ((80 - currentImage[row][col].comparePixel(currentAlive[row][col]))/10)+(genCounter/100);
              //System.out.println("test?  " + randomWithRange(-1,1));
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
                  //System.out.println("test?");
                  babyCounter++;
                  currentAlive[checkRow][checkCol] = geiWoBaby(currentAlive[row][col], babyCounter);
                }
              }
            //}
           }
          }
        }
        //repaint();
    }
   //grabFocus();
  }
  public int count()
  {
    int counter = 0;
    for(int row = 0; row < currentAlive.length; row++)
      for(int col = 0; col < currentAlive[0].length; col++)
        if(currentAlive[row][col] == null)
          counter++;
    return counter;
  }
 public void updateShit()
 {
   //Pixel[][] oldArray = cloneArray(currentAlive);
   //int vision = 3;
   //for(int row = currentAlive.length-1; row >= 0; row--)
     //for(int col = currentAlive[0].length-1; col >= 0; col--)
     for(int i = 0; i < (currentAlive.length*currentAlive[0].length)/2; i++)
     {
       int row = randomWithRange(0, currentAlive.length-1);
       int col = randomWithRange(0, currentAlive[0].length-1);
       if(currentAlive[row][col] != null)// && currentAlive[row][col].foundHome == false
       {
         int highRow = row;
         int highCol = col;
         int highestFitness = 765;
         for(int checkRow = row - vision; checkRow <= row + vision; checkRow++)
          for(int checkCol = col - vision; checkCol <= col + vision; checkCol++)
            if(checkCol >= 0 && checkCol < currentAlive[0].length && checkRow >= 0 && checkRow < currentAlive.length)
            {
              int currentFitness = currentImage[checkRow][checkCol].comparePixel(currentAlive[row][col]);
              //System.out.print(currentFitness + " ");
              if(currentFitness < highestFitness && currentAlive[checkRow][checkCol] == null)
              {
                //System.out.print(currentFitness + " ");
                highestFitness = currentFitness;
                highRow = checkRow;
                highCol = checkCol;
              }
            }
            //System.out.println("     "+highestFitness+"    "+highRow+" "+highCol);
            if(highRow == row && highCol == col)
            {
              //currentAlive[row][col].foundHome = true;
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
 public Pixel geiWoBaby(Pixel parent, int mutationAmount)
 {
   mutationAmount = (int)Math.pow(2,mutationAmount);
   return new Pixel(parent.r+randomWithRange(-1*mutationAmount,mutationAmount), parent.g+randomWithRange(-1*mutationAmount,mutationAmount), parent.b+randomWithRange(-1*mutationAmount,mutationAmount));
 }
 public boolean isDead(int fitness)
 {
   return !(fitness < survival+(genCounter/10));
 }
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
 } // end paintComponent
 public Pixel[][] cloneArray(Pixel[][] cloneThis)
 {
   Pixel[][] newArray = new Pixel[cloneThis.length][cloneThis[0].length];
   for (int row = 0; row < newArray.length; row++)
    for (int col = 0; col < newArray[0].length; col++)
     newArray[row][col] = cloneThis[row][col];
   return newArray;
 }
}
