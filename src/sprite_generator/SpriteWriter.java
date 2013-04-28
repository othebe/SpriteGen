package sprite_generator;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;


/* Convert a sprite to a BufferedImage, and real file */
public class SpriteWriter {
	private ConfigOptions config;
	
	//Constructor
	protected SpriteWriter(ConfigOptions config) {
		this.config = config;
	}
	
	//Create BufferedImage from sprite
	protected void write_to_memory(Sprite sprite) {
		this.config.output(String.format("Writing sprite %s to memory...\n", sprite.getName()));
		sprite.image = this.write(sprite.image_grid);
	}
	
	//Convert an ImageGrid to a BufferedImage
	private BufferedImage write(ImageGrid grid) {
		LinkedList<ImageTile> image_tiles = grid.getGridImages();

		BufferedImage image = new BufferedImage(grid.getWidth(), grid.getHeight(), image_tiles.getFirst().getBufferedImageType());
		Iterator<ImageTile> iterator = image_tiles.iterator();
		while (iterator.hasNext()) {
			ImageTile image_tile = iterator.next();
			Graphics g = image.getGraphics();
			g.drawImage(image_tile.getImage(), image_tile.getX(), image_tile.getY(), null);
		}
		
		this.config.output(String.format("Image written to memory. (%d x %d, args)\n %s", image.getWidth(), image.getHeight(), ConfigOptions.SEPARATOR));
		
		return image;
	}
	
	//Write to file
	protected void write_to_file(Sprite sprite) {
		String format = "";
		String filename = "";
		
		//Read transparent sprite config
		if (Sprite.isTransparent(sprite.image)) {
			format = this.config.getRGBAFileExtension();
			filename = this.config.getRGBAFileName();
		}
		//Read non-transparent sprite config
		else {
			format = this.config.getRGBFileExtension();
			filename = this.config.getRGBFileName();
		}
		
		this.config.output(String.format("Writing sprite %s to file: %s.%s...\n", sprite.getName(), filename, format));
		
		//Check if writer is supported
		Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(format);
		if (!writers.hasNext()) {
			System.err.printf("Error handling format: %s\n", format);
			return;
		}
		
		//Set image writer and compression
		ImageWriter img_writer = writers.next();
		ImageWriteParam img_write_params = img_writer.getDefaultWriteParam();
		if (img_write_params.canWriteCompressed() && (this.config.getCompression()>0)) {
			img_write_params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			img_write_params.setCompressionQuality(1-this.config.getCompression());
		}
		
		try {
			img_writer.setOutput(ImageIO.createImageOutputStream(new FileOutputStream(new File(filename+"."+format))));
			img_writer.write(null, new IIOImage(sprite.image, null, null), img_write_params);
			this.config.output(String.format("%s.%s written.%s", filename, format, ConfigOptions.SEPARATOR));
		} catch (IOException e) {
			System.err.printf("Error writing file: %s.%s", filename, format);
		}
	}
}
