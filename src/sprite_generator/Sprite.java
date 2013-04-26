package sprite_generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import sprite_generator.Packers.*;

public class Sprite {
	private String name;
	private long original_size;
	private ArrayList<BufferedImage> buffered_images;
	protected ImageGrid image_grid;
	protected BufferedImage image;
	private ConfigOptions config;
	
	//Constructor
	protected Sprite(String name, ConfigOptions config) {
		this.name = name;
		this.original_size = 0;
		this.buffered_images = new ArrayList<BufferedImage>();
		this.config = config;
	}
	
	//Add images to buffered image list
	protected void add(BufferedImage b) {
		this.buffered_images.add(b);
	}
	
	//Add images to buffered image list, individual image size
	protected void add(BufferedImage b, long image_size) {
		this.buffered_images.add(b);
		this.original_size += image_size;
	}
	
	//Get sprite name
	protected String getName() {
		return this.name;
	}
	
	//Get buffered_images
	protected ArrayList<BufferedImage> getBufferedImages() {
		return this.buffered_images;
	}
	
	//Print analytics
	protected void print_analytics() {
		//Get new filesize
		String sprite_filename = "";
		if (Sprite.isTransparent(image)) {
			sprite_filename = this.config.getRGBAFileName()+"."+this.config.getRGBAFileExtension();
		} else sprite_filename = this.config.getRGBFileName()+"."+this.config.getRGBFileExtension();
		
		long new_size = new File(sprite_filename).length();
		
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
