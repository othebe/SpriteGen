package sprite_generator;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class ImageGrid {
	private LinkedList<ImageTile> grid_images;
	private int width;
	private int height;
	private int used_surface_area;
	
	//Constructor
	public ImageGrid() {
		this.grid_images = new LinkedList<ImageTile>();
		this.width = 0;
		this.height = 0;
		this.used_surface_area = 0;
	}
	
	//Insert a tile into the image grid
	public void insert(ImageTile tile) {
		grid_images.add(tile);
		
		BufferedImage image = tile.getImage();
		
		//Update width
		int x = tile.getX();
		if ((x+image.getWidth()) > this.width) {
			this.width = x+image.getWidth();
		}
		
		//Update height
		int y = tile.getY();
		if ((y+image.getHeight()) > this.height) {
			this.height = y+image.getHeight();
		}
		
		//Update used surface area
		this.used_surface_area += (image.getWidth() * image.getHeight());
	}
	
	//Get ImageGrid height
	public int getHeight() {
		return this.height;
	}
	
	//Get ImageGrid width
	public int getWidth() {
		return this.width;
	}
	
	//Get grid surface area
	public int getSurfaceArea() {
		return this.width * this.height;
	}
	
	//Get the used surface area
	public int getUsedSurfaceArea() {
		return this.used_surface_area;
	}
	
	//Get grid images
	public LinkedList<ImageTile> getGridImages() {
		return this.grid_images;
	}
}

