package sprite_generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class SpriteGenerator {
	private ConfigOptions config;
	private Packer packer;
	private SpriteWriter sprite_writer;
	private Compressor compressor;
	private CSSWriter css_writer;
	
	private File[] images;
	private Sprite sprite_rgb;
	private Sprite sprite_rgba;
	
	//Constructor
	public SpriteGenerator(File[] images) {
		this.config = new ConfigOptions("spritegen.conf");
		this.packer = Packer.createFromConfig(this.config);
		this.sprite_writer = new SpriteWriter(this.config);
		this.sprite_rgb = new Sprite("RGB", this.config);
		this.sprite_rgba = new Sprite("RGBA", this.config);
		this.images = images;
	}
	
	//Main method
	public static void main(String[] args) {
		Scanner stdin = new Scanner(System.in);
		System.out.printf("Image directory path: ");
		String image_root_path = stdin.nextLine();
		stdin.close();
		
		File image_root_file = new File(image_root_path);
		
		//Require directory
		if (!image_root_file.isDirectory()) {
			System.out.printf("Please enter a valid directory path.\n");
			return;
		}
		
		SpriteGenerator sprite_gen = new SpriteGenerator(image_root_file.listFiles());
		
		sprite_gen.read_images();
		sprite_gen.pack_images();
		sprite_gen.create_buffered_image();
		sprite_gen.write_to_file();
	}
	
	//Read file and convert to Buffered Images
	public void read_images() {
		for(int image_ndx=0; image_ndx<images.length; image_ndx++) {
			this.config.output(String.format("Reading file: %s\n", images[image_ndx].toString()));
			try {
				BufferedImage buffered_image = ImageIO.read(images[image_ndx]);
				
				/* Images with transparency */
				if (Sprite.isTransparent(buffered_image)) {
					sprite_rgba.add(buffered_image, images[image_ndx].length());
				}
				/* Images without transparency */
				else {
					sprite_rgb.add(buffered_image, images[image_ndx].length());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.config.output(ConfigOptions.SEPARATOR);
	}
	
	//Pack images into grid
	public void pack_images() {
		/* Pack RGB sprite */
		this.packer.pack(this.sprite_rgb);
		
		/* Pack RGBA sprite */
		this.packer.pack(this.sprite_rgba);
	}
	
	//Write image to memory
	public void create_buffered_image() {
		this.sprite_writer.write_to_memory(this.sprite_rgb);
		this.sprite_writer.write_to_memory(this.sprite_rgba);
	}
	
	//Write image to file
	public void write_to_file() {
		this.sprite_writer.write_to_file(this.sprite_rgb);
		this.sprite_rgb.print_analytics();
		
		this.sprite_writer.write_to_file(this.sprite_rgba);
		this.sprite_rgba.print_analytics();
	}
}
