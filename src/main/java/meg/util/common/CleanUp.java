package meg.util.common;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class CleanUp extends HttpServlet {
	public String deletepath="c:\\tomcat\\webapps\\bank\\images\\";
	
	
	
	public String getDeletepath() {
		return deletepath;
	}

	public void setDeletepath(String deletepath) {
		this.deletepath = deletepath;
	}

	public void init() {
		// open path
		File dir = new File(this.deletepath);
		
		// read all files in path ending in .png
		if (dir!=null && dir.isDirectory()) {
			String[] files = dir.list();
			// delete all files in path
			for (int i=0; i<files.length; i++) {
				File file= new File(this.deletepath,files[i]);
	            file.delete();
	        }
		}

	}
	


  public String getServletInfo() {
    return "A servlet which deletes all images in the image directory upon startup";
  }
}