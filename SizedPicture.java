package generateProfilePicture;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

/**<h1>Speedy's Image Generation Toolkit</h1>
 * <p>Used to generate the profile picture I use</p>
 * @author Speedy2025
 */
public class SizedPicture {
	BufferedImage picture;
	
	public SizedPicture(File imageDirectory) throws IOException {
		picture = ImageIO.read(imageDirectory);
	}
	public SizedPicture(BufferedImage picture) {
		this.picture = picture;
	}
	
	//https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
	//Original Author: MadProgrammer
	//Modified: Speedy2025
	public BufferedImage rotate(float angle) throws Exception{
		 double rads = Math.toRadians(angle);
         
         int w = picture.getWidth();
         int h = picture.getHeight();

         BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g2d = rotated.createGraphics();
         
         AffineTransform at = new AffineTransform();
         at.translate(0, 0);

         int x = w / 2;
         int y = h / 2;

         at.rotate(rads, x, y);
         g2d.setTransform(at);
         g2d.drawImage(picture, 0, 0, null);
         g2d.dispose();
         return rotated;
	}
	
	//Expects 4 floats. Discolors an image based on a given pattern using RescaleOp.
	public BufferedImage discolor(float[] newColor) {
		Graphics g = picture.getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		float[] offsets = new float[4];
		RescaleOp rop = new RescaleOp(newColor, offsets , null);
		g2d.drawImage(picture, rop, 0, 0);
		return picture;
	}
	
	/**<h1>Vertical Stacking</h1>
	 * <p>Takes a BufferedImage b and puts it onto a BufferedImage a.</p>
	 * @param a Bottom Buffered Image
	 * @param b BufferedImage to put on top.
	 * @return Resulting BufferedImage
	 */
	public static BufferedImage verticalStack(BufferedImage a, BufferedImage b) {
		if(a.getHeight() == b.getHeight() && a.getWidth() == b.getWidth()) {

			BufferedImage c = new BufferedImage(a.getWidth(), a.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = c.getGraphics();
			g.drawImage(a, 0, 0, null);
			g.drawImage(b, 0, 0, null);
			g.dispose();
			return c;
		}
		return null;
	}
	
	/**<h1>Vertical Stacking</h1>
	 * <p>Takes a series of buffered images and places them on to the first one by one.</p>
	 * @param a Bottom Buffered Image
	 * @param imgs BufferedImages to put on top, in order.
	 * @return Resulting BufferedImage
	 */
	public static BufferedImage verticalStack(BufferedImage a, BufferedImage...imgs) {
		BufferedImage stacked = null;
		for(BufferedImage i : imgs) {
			stacked = verticalStack(stacked, i);
		}
		
		return stacked;
	}
	
	/**<h1>Generate Circular Backdrop</h1>
	 * <p> Generates a backdrop circle using arcs of size degreeBetween with an offset of degreeVariance. 
	 *     Assigns a color to each arc. 
	 *     Intended for use with Merge By Degree (color)
	 * </p>
	 * @param degreeBetween Size of each arc
	 * @param degreeVariance Offset of each arc.
	 * @return Generated Backdrop as BufferedImage
	 */
	public static BufferedImage generateBackdrop(int degreeBetween, int degreeVariance) {
		File inLine = new File("C:\\Users\\speed\\ECU\\EclipseWorkspace\\ImageGeneration\\src\\generateProfilePicture","white.png");
		BufferedImage outputImage = null;
		try {
			SizedPicture sp = new SizedPicture(inLine);
			outputImage = sp.rotate(degreeVariance);
			int maxCycles = 360 / degreeBetween;
			
			for(int b = 0; b*degreeBetween < 360; b++) {
				Color color = Color.getHSBColor((float)(b+1)/maxCycles, 1.0f, 1.0f);
				float val1 = color.getRed()/255.0f;
				float val2 = color.getGreen()/255.0f;
				float val3 = color.getBlue()/255.0f;
				
				for(int a = 0; a < degreeBetween; a++) {
					sp = new SizedPicture(inLine);
					float colors[] = {val1, val2, val3, 1.0f};
					
					sp.picture = sp.discolor(colors);
					outputImage = verticalStack(outputImage, sp.rotate(a+ degreeVariance + (b*degreeBetween)));
				}
			}
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return outputImage;
	}
	
	/**<h1>Single Arc</h1>
	 * <p>Draws a single arc. You'd typically use this for mergyByArc!</p>
	 * @param degreeBetween Size of the Arc (Whole Degrees)
	 * @param degreeVariance Offset of the Arc (Whole Degrees)
	 * @param arcNum Which arc you are referencing in a circle.
	 * @return Non-colored single arc. Color depends on degreeBetween and arcNum.
	 */
	public static BufferedImage singleArc(int degreeBetween, int degreeVariance, int arcNum) {
		File inLine = new File("C:\\Users\\speed\\ECU\\EclipseWorkspace\\ImageGeneration\\src\\generateProfilePicture","white.png");
		BufferedImage outputImage = null;
		try {
			SizedPicture sp = new SizedPicture(inLine);
			outputImage = sp.rotate(degreeVariance);
			
			for(int a = 0; a < degreeBetween; a++) {
				sp = new SizedPicture(inLine);
				outputImage = verticalStack(outputImage, sp.rotate(a+ degreeVariance + (arcNum*degreeBetween)));
			}
			
			/*Color color = Color.getHSBColor((float)(arcNum+1)/maxCycles, 1.0f, 1.0f);
			float val1 = color.getRed()/255.0f;
			float val2 = color.getGreen()/255.0f;
			float val3 = color.getBlue()/255.0f;
			float colors[] = {val1, val2, val3, 1.0f};
			outputImage = new SizedPicture(outputImage).discolor(colors);*/
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return outputImage;
	}
	
	/**<h1>Colored Single Arc</h1>
	 * <p>Draws a single arc. You'd typically use this for mergyByArc! This just makes it look nicer if you don't.</p>
	 * @param degreeBetween Size of the Arc (Whole Degrees)
	 * @param degreeVariance Offset of the Arc (Whole Degrees)
	 * @param arcNum Which arc you are referencing in a circle.
	 * @return Auto-colored single arc. Color depends on degreeBetween and arcNum.
	 */
	
	public static BufferedImage singleArcColored(int degreeBetween, int degreeVariance, int arcNum) {
		File inLine = new File("C:\\Users\\speed\\ECU\\EclipseWorkspace\\ImageGeneration\\src\\generateProfilePicture","white.png");
		BufferedImage outputImage = null;
		try {
			SizedPicture sp = new SizedPicture(inLine);
			outputImage = sp.rotate(degreeVariance);
			int maxCycles = 360 / degreeBetween;
			
			for(int a = 0; a < degreeBetween; a++) {
				sp = new SizedPicture(inLine);
				outputImage = verticalStack(outputImage, sp.rotate(a+ degreeVariance + (arcNum*degreeBetween)));
			}
			
			Color color = Color.getHSBColor((float)(arcNum+1)/maxCycles, 1.0f, 1.0f);
			float val1 = color.getRed()/255.0f;
			float val2 = color.getGreen()/255.0f;
			float val3 = color.getBlue()/255.0f;
			float colors[] = {val1, val2, val3, 1.0f};
			outputImage = new SizedPicture(outputImage).discolor(colors);
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return outputImage;
	}
	
	/**<h1>Merge By Arc</h1>
	 * <p>Shapes an image to be that of an arc with the given parameters. Draws a new arc every single time.</p>
	 * @param replacement Image to shape.
	 * @param degreeBetween Size of the Arc (In Whole Degrees)
	 * @param degreeVariance Offset of the Arc (In Whole Degrees)
	 * @param segment Which Arc you're referring to.
	 * @return
	 */
	public static BufferedImage mergeByArc(BufferedImage replacement, int degreeBetween, int degreeVariance, int segment) {
		BufferedImage template = singleArc(degreeBetween, degreeVariance, segment);

		int width = template.getWidth(),
			height = template.getHeight();

		BufferedImage newImage = new BufferedImage(width, height, template.getType());
	    
		//Override the image with a new one using the center pixel as its target.
		//This allows one to theoretically build an outside and only affect the inside.
		//You should make an overlay for that, though.
		int targetColor = template.getRGB(width / 2, height / 2);
		for (int i = 0; i < width; i++) {
	        for (int j = 0; j < height; j++) {
	            if (template.getRGB(i, j) == targetColor) {
	                newImage.setRGB(i, j, replacement.getRGB(i, j));
	            }
	        }
	    }
		return newImage;
	}
	
	public static BufferedImage mergyByBufferedImage(BufferedImage template, BufferedImage replacement) {
		int width = template.getWidth(),
			height = template.getHeight();
		
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < width - 1; i++) {
	        for (int j = 0; j < height - 1; j++) {
	            if (template.getRGB(i,j) == Color.black.getRGB()) {
	                newImage.setRGB(i, j, replacement.getRGB(i, j));
	            }
	        }
	    }
		return newImage;
	}
	
	/**<h1>Generate Lines</h1>
	 * <p>Generates black lines seperating each arc.</p>
	 * @param degreeBetween Size of each arc to border.
	 * @param degreeVariance Offset of each arc.
	 * @return BufferedImage containing just the black lines.
	 */
	public static BufferedImage generateLines(int degreeBetween, int degreeVariance) {
		File inLine = new File("C:\\Users\\speed\\ECU\\EclipseWorkspace\\ImageGeneration\\src\\generateProfilePicture","black.png");
		BufferedImage outputImage = null;
		try {
			SizedPicture sp = new SizedPicture(inLine);
			outputImage = sp.rotate(degreeVariance);
			for(int a = 0; a*degreeBetween < 360; a++) {
				outputImage = verticalStack(outputImage, sp.rotate(a*degreeBetween + degreeVariance));
			}
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return outputImage;
	}
	
	/**<h1>Print Image</h1>
	 * <p>Prints resulting image</p>;
	 * @param a Image to Send to Storage
	 * @param name Name of Image to Print
	 * @return
	 */
	public static boolean printImage(BufferedImage a, String dir, String name) {
		try {
			return ImageIO.write(a, "png", new File(dir,(name + ".png")));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}
	public static boolean printImage(SizedPicture a, String dir, String name) {
		return printImage(a.picture, dir, name);
	}
	
	/**<h1>SPIN!</h1>
	 * <p>Generates the spinning image from a set of files</p>
	 * @param numFrames Number of frames it takes to complete one spin.
	 * @param imageFiles The files used to input. Should be >=2.
	 */
	public static void spin(int numFrames, File...imageFiles) {
		try {
			SizedPicture sp = null;
			int numImages = imageFiles.length;
			int degree = 360 / numImages;
			
			//We generate each frame independently.
			for(int i = 0; i < 360; i += 360/numFrames) {
				BufferedImage backdrop = generateBackdrop(degree,i);
				BufferedImage topDrop = backdrop; //Forces a colored background.
				
				//This handles each arc's image independently of one-another.
				for(int k = 0; k < imageFiles.length; k++) {
					sp = new SizedPicture(imageFiles[k]);
					topDrop = verticalStack(topDrop,mergeByArc(sp.picture, degree, i, k));
				}
				BufferedImage lines = generateLines(degree,i);
				
				/*BufferedImage shadow = lines;
				
				Graphics2D g2d = shadow.createGraphics();

				AffineTransform at = new AffineTransform();
	         	at.translate(5, 5);
	         	g2d.setTransform(at);
	            g2d.drawImage(shadow, 0, 0, null);
	            g2d.dispose();
	            float f[] = {0.0f, 0.0f, 0.0f, 0.3f};
	            shadow = new SizedPicture(shadow).discolor(f);*/
		        
				BufferedImage finalImage = verticalStack(topDrop,/*shadow,*/lines);
				String dir = "C:\\Users\\speed\\ECU\\EclipseWorkspace\\ImageGeneration\\src\\generateProfilePicture";
				
				
				//Hijacked Code...
				File f12 = new File(dir, "dice_template.png");
				File f13 = new File(dir, "outerdice_template.png");
				SizedPicture spCheck = new SizedPicture(f12);
				SizedPicture spOutCheck = new SizedPicture(f13);
				float f[] = {0.0f, 0.0f, 0.0f, 1.0f};
				spCheck.picture = spCheck.discolor(f);
				finalImage = mergyByBufferedImage(spCheck.picture,finalImage);
				finalImage = verticalStack(finalImage, spOutCheck.picture);
				printImage(finalImage,dir,("\\frames\\spin" + i));
			}
		} catch (Exception ex) {
			System.out.println("FAILED SPIN");
			System.out.println(ex.getMessage());
		}
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[]) {
		String dir = "C:\\Users\\speed\\ECU\\EclipseWorkspace\\ImageGeneration\\src\\generateProfilePicture";
		File f1 = new File(dir, "speedy_happy.jpg");
		File f2 = new File(dir, "speedy_angry.jpg");
		File f3 = new File(dir, "speedy_sad.jpg");
		File f4 = new File(dir, "sus1.png");
		File f5 = new File(dir, "sus2.png");
		File f6 = new File(dir, "sus3.png");
		File f7 = new File(dir, "chicken.jpg");
		File f8 = new File(dir, "burger.jpg");
		File f9 = new File(dir, "fries.jpg");
		File f10 = new File(dir, "hotdog.jpg");
		File f11 = new File(dir, "pizza.jpg");
		File f12 = new File(dir, "template_check");
		spin(50, f2, f1, f3);
		
	}
}
