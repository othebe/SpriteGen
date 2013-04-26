package sprite_generator;

import java.awt.image.BufferedImage;

//Private class: ImageTile
public class ImageTile {
	int x;
	int y;
	int width;
	int height;
	BufferedImage image;
	
	//Constructor
	protected ImageTile(int x, int y, BufferedImage image) {
		this.x = x;
		this.y = y;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.image = image;
	}
	
	//Get buffered image type
	protected int getBufferedImageType() {
		return this.image.getType();
	}
	
	//Get BufferedImage
	protected BufferedImage getImage() {
		return this.image;
	}
	
	//Get x
	protected int getX() {
		return this.x;
	}
	
	//Get y
	protected int getY() {
		return this.y;
	}
}
