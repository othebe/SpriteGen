package sprite_generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class ConfigOptions {
	private File config_file;
	private String log_output_dir;
	
	//Configuration options
	private HashMap<String, Object> options;
	
	//Output separator
	public static final String SEPARATOR = "\n\n---------------\n\n";
	
	//Build configuration from file
	public ConfigOptions(String conf_file) {
		this.config_file = new File(conf_file);
		this.log_output_dir = ".";
		this.options = new HashMap<String, Object>();
		read_config_file();
	}
	
	//Read config file
	private void read_config_file() {
		try {
			Scanner config_in = new Scanner(config_file);
			while(config_in.hasNextLine()) {
				String config_line = "";
				try {
					config_line = config_in.nextLine();
					//Ignore comment lines
					if (config_line.trim().length()==0 || config_line.startsWith("//")) continue;
					//Read config line
					String[] config_arr = config_line.split("=");
					set_option(config_arr[0].trim(), config_arr[1].trim());
				} catch (Exception e) {
					System.err.printf("Illegal line: %s\n", config_line);
				}
			}
			print_configuration();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
	}
	
	//Set option
	private void set_option(String option, String value) {
		switch(option) {
			//Verbosity (true/false)
			case "verbose":
				this.options.put(option, Boolean.parseBoolean(value));
				break;
			//Logger output (console/file)
			case "logger_output":
				this.options.put(option, value.toLowerCase());
				break;
			//Logger output file
			case "logger_output_file":
				this.options.put(option, value);
				break;
			//Packer type (fifo)
			case "packer":
				this.options.put(option, value.toLowerCase());
				break;
			//Compression level (0.0 - 1.0)
			case "compression":
				this.options.put(option, Float.parseFloat(value));
				break;
			//Non transparent sprite filename
			case "rgb_image_name":
				this.options.put(option, value);
				break;
			//Non transparent sprite file extension
			case "rgb_image_extension":
				this.options.put(option, value);
				break;
			//Transparent sprite filename
			case "rgba_image_name":
				this.options.put(option, value);
				break;
			//Transparent sprite file extension
			case "rgba_image_extension":
				this.options.put(option, value);
				break;
		}
	}
	
	//Print configuration
	protected void print_configuration() {
		Set<String> keys = this.options.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			this.output(String.format("%s is %s\n", key, this.options.get(key).toString()));
		}
		this.output(ConfigOptions.SEPARATOR);
	}
	
	//Print verbose
	protected void output(String s) {
		if (this.options.containsKey("verbose") && (boolean) this.options.get("verbose")) {
			//Log to console
			int y = this.getLoggerOutputName().length();
			if (this.getLoggerOutputType().equals("console")) {
				System.out.printf("%s", s);
			}
			//Log to file
			else if (this.getLoggerOutputType().equals("file") && this.getLoggerOutputName().length()>0) {
				try {
					FileOutputStream out = new FileOutputStream(new File(log_output_dir+File.separator+this.getLoggerOutputName()), true);
					out.write(s.getBytes());
					out.close();
				} catch (IOException e) {
					System.err.printf("Error writing log file: %s\n", this.getLoggerOutputName());
				}
			}
		}
	}
	
	//Set log file output dir
	public void setLogOutputDir(String dir) {
		this.log_output_dir = dir;
	}
	
	
	/* *** Getters *** */
	
	//Is verbose mode on?
	public boolean isVerbose() {
		return (this.options.containsKey("verbose")?(boolean)this.options.get("verbose"):false);
	}
	
	//Get logger output type
	public String getLoggerOutputType() {
		return (this.options.containsKey("logger_output")?(String)this.options.get("logger_output"):"console");
	}
	
	//Get logger output name
	public String getLoggerOutputName() {
		if (!this.getLoggerOutputType().equals("file")) return "";
		return (this.options.containsKey("logger_output_file")?(String)this.options.get("logger_output_file"):"results.log");
	}
	
	//Get packer type
	public String getPacker() {
		return (this.options.containsKey("packer")?(String)this.options.get("packer"):"fifo");
	}
	
	//Get compression level
	public float getCompression() {
		return (this.options.containsKey("compression")?(Float)this.options.get("compression"):0);
	}
	
	//Get non-transparent sprite filename
	public String getRGBFileName() {
		return (this.options.containsKey("rgb_image_name")?(String)this.options.get("rgb_image_name"):"sprite_rgb");
	}
	
	//Get non-transparent sprite file extension
	public String getRGBFileExtension() {
		return (this.options.containsKey("rgb_image_extension")?(String)this.options.get("rgb_image_extension"):"jpg");
	}
	
	//Get transparent sprite filename
	public String getRGBAFileName() {
		return (this.options.containsKey("rgba_image_name")?(String)this.options.get("rgba_image_name"):"sprite_rgba");
	}
	
	//Get transparent sprite file extension
	public String getRGBAFileExtension() {
		return (this.options.containsKey("rgba_image_extension")?(String)this.options.get("rgba_image_extension"):"png");
	}
}
