package sprite_generator;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import sprite_generator.Packers.*;

public abstract class Packer {
	
	private ConfigOptions config;
	
	//Constructor
	protected Packer(ConfigOptions config) {
		this.config = config;
	}
	
	//Pack a ArrayList of BufferedImages and return an ImageGrid
	protected abstract ImageGrid pack(ArrayList<BufferedImage> images);
	
	//Create a Packer from a configuration object
	protected static Packer createFromConfig(ConfigOptions config) {
		switch (config.getPacker()) {
			case "fifo":
				return new FIFO_Packer(config);
			case "gapfill":
				return new GapFill_Packer(config);
		}	
		//Default
		return new FIFO_Packer(config);
	}
	
	//Start the packing process
	protected void pack(Sprite sprite) {
		this.config.output(String.format("Packing sprite %s\n", sprite.getName()));
		sprite.image_grid = pack(sprite.getBufferedImages());
		
		//Analytics
		this.print_analytics(sprite.image_grid);
	}
	
	//Get analytics about the packing
	private void print_analytics(ImageGrid grid) {
		this.config.output(String.format("Images added: %d\n", grid.getGridImages().size()));
		this.config.output(String.format("Grid size: %d x %d\n", grid.getWidth(), grid.getHeight()));
		this.config.output(String.format("Image surface area: %d\n", grid.getUsedSurfaceArea()));
		this.config.output(String.format("Efficiency: %d/%d [%f%%]\n", grid.getUsedSurfaceArea(), grid.getSurfaceArea(), (grid.getUsedSurfaceArea()*100.0/grid.getSurfaceArea())));
		this.config.output(ConfigOptions.SEPARATOR);
	}
	

}
