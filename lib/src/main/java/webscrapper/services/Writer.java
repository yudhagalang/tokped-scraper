package webscrapper.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Writer{
	private List<String> listLinks;
	private FileWriter file;
	
	public void init() throws IOException{
		this.setFile(new FileWriter("result.csv",true));
		this.file.write("id,name,description,imagelink,price,rating,merchant\n");
	}
	
	public void close() throws IOException {
		this.file.close();
	}
	
	public void add(String str) throws IOException {
		this.file.write("\n");
	}

	public List<String> getListLinks() {
		return listLinks;
	}

	public void setListLinks(List<String> listLinks) {
		this.listLinks = listLinks;
	}

	public FileWriter getFile() {
		return file;
	}

	public void setFile(FileWriter file) {
		this.file = file;
	}
}
