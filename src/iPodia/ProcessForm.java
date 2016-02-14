package iPodia;
import org.apache.commons.fileupload.FileItem;

public class ProcessForm {
	
	public static void processTextElements(FileItem item) {
		if (item != null) {
			if (item.getName().equals("addQuestion")) {
				System.out.println("question asked");
			} else {
				System.out.println(":" + item.getName() + ":");
				System.out.println(":" +item.getString() + ":");
			}
		}
		
	}
			

}
