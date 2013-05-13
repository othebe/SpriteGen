package sprite_generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class Sprite {
	private String name;
	private long original_size;
	private ArrayList<ImageTile> images;
	protected ImageGrid image_grid;
	protected BufferedImage image;
	private ConfigOptions config;
	
	//Constructor
	protected Sprite(String name, ConfigOptions config) {
		this.name = name;
		this.original_size = 0;
		this.images = new ArrayList<ImageTile>();
		this.config = config;
	}
	
	//Add images to buffered image list, individual image size
	protected void add(ImageTile t, long image_size) {
		this.images.add(t);
		this.original_size += image_size;
	}
	
	//Get sprite name
	protected String getName() {
		return this.name;
	}
	
	//Get images
	protected ArrayList<ImageTile> getImages() {
		return this.images;
	}
	
	//Print analytics
	protected void print_analytics(String location) {
		//Get new filesize
		String sprite_filename = "";
		if (Sprite.isTransparent(image)) {
			sprite_filename = this.config.getRGBAFileName()+"."+this.config.getRGBAFileExtension();
		} else sprite_filename = this.config.getRGBFileName()+"."+this.config.getRGBFileExtension();
		
		long new_size = new File(location+sprite_filename).length();
		
		this.config.output(String.format("Results for sprite %s\n", this.name));
		this.config.output(String.format("Original file(s) size: %s\n", Sprite.translateSize(this.original_size)));
		this.config.output(String.format("New filesize: %s\n", Sprite.translateSize(new_size)));
		this.config.output(String.format("Filesize reduced by %.3f %%\n", (this.original_size - new_size)*100.0/this.original_size));
		this.config.output(ConfigOptions.SEPARATOR);
	}
	
	//Determine if an image is transparent
	public static boolean isTransparent(BufferedImage img) {
		return (img.getType() == BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	//Translate size into bytes, KB, MB etc.
	public static String translateSize(long size) {
		if (size < Math.pow(1024,  1))
			return String.format("%d bytes", size);
		if (size < Math.pow(1024, 2))
			return String.format("%.3f KB", size*1.0/Math.pow(1024,  1));
		if (size < Math.pow(1024,  3))
			return String.format("%.3f MB", size*1.0/Math.pow(1024, 2));
		else return String.format("%.3f GB", size*1.0/Math.pow(1024, 3));
	}
}
