package sprite_generator.Packers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import sprite_generator.ConfigOptions;
import sprite_generator.ImageGrid;
import sprite_generator.ImageTile;
import sprite_generator.Packer;

public class GapFill_Packer extends Packer {
	private ImageGrid grid;
	private ArrayList<BufferedImage> sorted_images;
	private ListIterator<BufferedImage> small;
	private ListIterator<BufferedImage> big;
	private boolean[] added_images;
	private int width_pointer;
	private int height_pointer;
	
	public GapFill_Packer(ConfigOptions config) {
		super(config);
	}

	@Override
	protected ImageGrid pack(ArrayList<BufferedImage> images) {
		this.added_images = new boolean[images.size()];
		this.sorted_images = sortImagesBySide(images);
		this.small = this.sorted_images.listIterator(sorted_images.size());
		this.big = this.sorted_images.listIterator();
		this.grid = new ImageGrid();
		
		//Add the big blocks
		while (big.hasNext()) {
			BufferedImage big_img = big.next();
			if (added_images[big.previousIndex()]) continue;
			
			//Start adding from x=0
			big.previous();	
			int current_grid_width = this.grid.getWidth();
			int current_grid_height = this.grid.getHeight();
			this.width_pointer = 0;
			this.height_pointer = current_grid_height;
			
			add_to_grid(this.width_pointer, this.height_pointer, Math.max(Math.max(big_img.getWidth(), big_img.getHeight()), current_grid_width), current_grid_height+Math.min(big_img.getHeight(), big_img.getWidth()), false);
			
			//Add the smaller blocks
			add_to_grid(this.width_pointer, this.height_pointer, Math.max(Math.max(big_img.getWidth(), big_img.getHeight()), current_grid_width), current_grid_height+Math.min(big_img.getHeight(), big_img.getWidth()), true);
		}
		
		//Create a linked list
		return grid;
	}
	
	//Title:		sortImagesBySide
	//Description:	Sort an arraylist of RotatedBufferedImages by descending side size
	private ArrayList<BufferedImage> sortImagesBySide(ArrayList<BufferedImage> images) {
		//Sort by minimum
		for(int i=0; i<images.size(); i++) {
			BufferedImage bi = images.get(i);
			int swap_location = i;
			int current_max = Math.max(bi.getWidth(), bi.getHeight());
			int current_max_area = (bi.getWidth() * bi.getHeight());
			
			for(int j=i+1; j<images.size(); j++) {
				BufferedImage bj = images.get(j);
				int max_compare = Math.max(bj.getWidth(), bj.getHeight());
				int max_compare_area = bj.getWidth() * bj.getHeight();
				//Measure longest side
				if (max_compare > current_max) {
					swap_location = j;
					current_max = max_compare;
					current_max_area = bj.getWidth() * bj.getHeight();
				}
				//Measure total
				else if (max_compare == current_max) {
					if (max_compare_area > current_max_area) {
						swap_location = j;
						current_max = max_compare;
						current_max_area = max_compare_area;
					}
				}
			}
			//Swap elements
			if (swap_location != i) {
				BufferedImage tmp = images.get(i);
				images.set(i, images.get(swap_location));
				images.set(swap_location, tmp);
			}
			
		}
		
		return images;
	}
	
	//Title:		add_to_grid
	//Description:	Tries to add to a grid based on boundaries
	private void add_to_grid(int x1, int y1, int x2, int y2, boolean isReverse) {
		boolean within_boundary = true;
		BufferedImage img;
		int ndx;
		int last_img_height = 0;
		
		while (within_boundary) {
			//Fetch appropriate image
			if (isReverse) {
				if (!this.small.hasPrevious()) return;
				ndx = this.small.previousIndex();
				img = this.small.previous();
			} else {
				if (!this.big.hasNext()) return;
				ndx = this.big.nextIndex();
				img = this.big.next();
			}
			
			//Check if image has been seen
			if (this.added_images[ndx]) break;
				
			//Check boundaries
			int img_width = img.getWidth();
			int img_height = img.getHeight();
			
			//Check if we need to rotate
			boolean rotate = false;
			if (!isReverse) rotate = (img_height > img_width);
			if (isReverse) rotate = (img_width > img_height);
			
			//Flip dimensions if rotated
			if (rotate) {
				int temp_width = img_width;
				img_width = img_height;
				img_height = temp_width;
			}
			
			//Can we add it on the same line?
			within_boundary = ((this.width_pointer+img_width) <= x2) && ((this.height_pointer+img_height) <= y2);
			if (within_boundary) {
				ImageTile tile = new ImageTile(this.width_pointer, this.height_pointer, img, rotate);
				this.grid.insert(tile);
				this.added_images[ndx] = true;
				this.width_pointer = (this.width_pointer+img_width);
				last_img_height = img_height;
				continue;
			}
			
			//Can we add in to a different line?
			within_boundary = ((x1+img_width) <= x2) && ((this.height_pointer+last_img_height+img_height) <= y2);
			if (within_boundary) {
				this.height_pointer += last_img_height;
				ImageTile tile = new ImageTile(x1, this.height_pointer, img, rotate);
				this.grid.insert(tile);
				this.added_images[ndx] = true;
				this.width_pointer = (x1+img_width);
				last_img_height = img_height;
				continue;
			}
		}
		
		//Reset iterator
		if (isReverse) {
			this.small.next();
		} else this.big.previous();
		return;
	}
}
