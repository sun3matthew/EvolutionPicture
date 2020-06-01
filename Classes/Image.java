import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;

public class Image
{
  private BufferedImage picture;
  private Pixel[][] pixelArray;
  public Image(String imageName)
  {
    System.out.print(imageName);
    try{
      picture = ImageIO.read(new File(imageName));
    }catch (Exception e)
    {
      System.out.println("Fuck you");
    }
    pixelArray = new Pixel[picture.getHeight()][picture.getWidth()];
    for (int row = 0; row < pixelArray.length; row++)
     for (int col = 0; col < pixelArray[0].length; col++)
     {
       //System.out.println(row + ", " + col);
       //if(row % 100 == 0)
        //System.out.println(((double)(row)/pixelArray.length)*100);
       int value = picture.getRGB(col, row);
       int red = (value >> 16) & 0xff;
       int green = (value >>  8) & 0xff;
       int blue = value & 0xff;
       pixelArray[row][col] = new Pixel(red, green, blue);
       //System.out.println(pixelArray[row][col].r + " " + pixelArray[row][col].g + " " + pixelArray[row][col].b);
     }
  }
  public Image(Pixel[][] arrayIn)
  {
    pixelArray = arrayIn;
  }
  public Pixel[][] getData()
  {
    return pixelArray;
  }
  public void exportImage(String name)
  {
    try{
      BufferedImage exportThis = new BufferedImage(pixelArray[0].length, pixelArray.length, BufferedImage.TYPE_INT_RGB);;
      for (int row = 0; row < pixelArray.length; row++)
       for (int col = 0; col < pixelArray[0].length; col++)
       {
         //if(row % 100 == 0)
          //System.out.println(((double)(row)/pixelArray.length)*100);
        //int avg = (pixelArray[row][col].r+pixelArray[row][col].g+pixelArray[row][col].b)/3;
        exportThis.setRGB(col, row, new Color(pixelArray[row][col].r, pixelArray[row][col].g, pixelArray[row][col].b).getRGB());
        //exportThis.setRGB(col, row, new Color(avg, avg, avg).getRGB());
       }
      File outputFile = new File(name+".png");
      ImageIO.write(exportThis, "png", outputFile);
    }catch (IOException e)
    {
      System.out.println("FUCK YOU");
    }
  }
}
