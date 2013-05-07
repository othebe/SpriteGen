package sprite_generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class SpriteGenerator {
	private ConfigOptions config;
	private Packer packer;
	private SpriteWriter sprite_writer;
	private CSSWriter css_writer;
	
	private String tmp_dir;
	private File[] images;
	private Sprite sprite_rgb;
	private Sprite sprite_rgba;
	
	
	private static final String TMP_DIR_NAME = "tmp";
	
	//Constructor
	public SpriteGenerator(File[] images) {
		initialize();
		this.images = images;
	}
	
	//Constructor: Convert URLs to files
	public SpriteGenerator(String[] urls) {
		initialize();
		ArrayList<File> images = new ArrayList<File>();
		for(int i=0; i<urls.length; i++) {
			try {
				URL url = new URL(urls[i]);
				images.add(get_url_data(url));
			} catch (MalformedURLException e) {
				System.err.printf("Invalid URL.\n");
			}
		}
		this.config.output(ConfigOptions.SEPARATOR);
		
		this.images = (images.toArray(new File[0]));
	}
	
	//Initialize general members
	private void initialize() {
		this.config = new ConfigOptions("spritegen.conf");
		this.packer = Packer.createFromConfig(this.config);
		this.sprite_writer = new SpriteWriter(this.config);
		this.sprite_rgb = new Sprite("RGB", this.config);
		this.sprite_rgba = new Sprite("RGBA", this.config);
		
		//Check if tmp directory exists, else create
		File tmp_dir = new File(SpriteGenerator.TMP_DIR_NAME);
		if (!tmp_dir.exists()) {
			tmp_dir.mkdir();
		} else if (!tmp_dir.isDirectory()) {
			System.err.printf("tmp directory required. File currently exists with that name.\n");
			System.exit(0);
		}
		
		//Create temporary directory for storing image files
		boolean tmp_dir_created = false;
		do {
			this.tmp_dir = String.valueOf((int) (Math.random() * 1000) + 1);
			File local_tmp_dir = new File(SpriteGenerator.TMP_DIR_NAME+File.separator+this.tmp_dir);
			tmp_dir_created = local_tmp_dir.mkdir();
		} while (!tmp_dir_created);
		this.config.setLogOutputDir(SpriteGenerator.TMP_DIR_NAME+File.separator+this.tmp_dir);
	}
	
	//Save URL data to the local temp directory
	private File get_url_data(URL url) {
		String full_path = url.getFile();
		
		this.config.output(String.format("Fetching file: %s\n", full_path));

		//Extract filename
		String filename = full_path.substring(full_path.lastIndexOf('/')+1);
		//Only allow valid filenames
		if (filename.indexOf('?') >= 0) filename = filename.substring(0, filename.indexOf('?'));
		
		//Create file
		File new_file = new File(SpriteGenerator.TMP_DIR_NAME+File.separator+this.tmp_dir+File.separator+filename);
		new_file.deleteOnExit();
		try {
			new_file.createNewFile();
		} catch (IOException e1) {
			System.err.printf("Could not create file: %s\n", new_file.toString());
		}
		
		//Copy URL to file
		try {
			InputStream in = url.openStream();
			FileOutputStream out = new FileOutputStream(new_file);
			byte[] b = new byte[1024];
			int bytesRead = 0;
			
			while ((bytesRead = in.read(b)) != -1) {
				out.write(b, 0, bytesRead);
		    }
			
			in.close();
			out.close();
		} catch (IOException e) {
			System.err.printf("Could not fetch file: %s\n", full_path);
		}
		
		return new_file;
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
		
		/* *** Read images as local files *** */
		//SpriteGenerator sprite_gen = new SpriteGenerator(image_root_file.listFiles());
		
		/* *** Read images as URIs *** */
		ArrayList<String> urls = new ArrayList<String>();
		File[] files = image_root_file.listFiles();
		for(int i=0; i<files.length; i++) {
			urls.add(files[i].toURI().toString());
		}
		SpriteGenerator sprite_gen = new SpriteGenerator((urls.toArray(new String[0])));
		
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
		String location = SpriteGenerator.TMP_DIR_NAME+File.separator+this.tmp_dir+File.separator;
		
		if (this.sprite_rgb.getBufferedImages().size() > 0) {
			this.sprite_writer.write_to_file(this.sprite_rgb, location);
			this.sprite_rgb.print_analytics(location);
		}
		
		if (this.sprite_rgba.getBufferedImages().size() > 0) {
			this.sprite_writer.write_to_file(this.sprite_rgba, location);
			this.sprite_rgba.print_analytics(location);
		}
	}
}
