package sprite_generator.Packers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import sprite_generator.ConfigOptions;
import sprite_generator.ImageGrid;
import sprite_generator.Packer;
import sprite_generator.Sprite;

//Pack files one at time vertically
public class FIFO_Packer extends Packer {
	public FIFO_Packer(ConfigOptions config) {
		super(config);
	}

	@Override
	protected ImageGrid pack(ArrayList<BufferedImage> images) {
		ImageGrid grid = new ImageGrid();
		
		Iterator<BufferedImage> iterator = images.iterator();
		while (iterator.hasNext()) {
			grid.insert(0, grid.getHeight(), iterator.next());
		}
		return grid;
	}
}
