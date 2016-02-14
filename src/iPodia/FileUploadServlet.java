package iPodia;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class UploadServlet
 */
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DATA_DIRECTORY = "/iPodiaData";
   //  private static final String DATA_DIRECTORY = "/Users/AlexKoh/Desktop/workspace/iPodia/data";
    private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 2;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (!isMultipart) {
            return;
        }

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Sets the size threshold beyond which files are written directly to
        // disk.
        factory.setSizeThreshold(MAX_MEMORY_SIZE);

        // Sets the directory used to temporarily store files that are larger
        // than the configured size threshold. We use temporary directory for
        // java
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        // constructs the folder where uploaded file will be stored
       // String uploadFolder = getServletContext().getRealPath("") + DATA_DIRECTORY;
        
       // String uploadFolder = request.toString() + DATA_DIRECTORY;
       // System.out.println(uploadFolder);

        File dataFolder = new File(DATA_DIRECTORY);
        if (!dataFolder.exists() || !dataFolder.isDirectory()) {
        	dataFolder.mkdir();
        }
        
        

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Set overall request size constraint
        upload.setSizeMax(MAX_REQUEST_SIZE);
        
        System.out.println("hi");
        try {
            // Parse the request
            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (!item.isFormField()) {
                    String fileName = new File(item.getName()).getName();
                    System.out.println("filename: " +fileName);
                    String filePath = DATA_DIRECTORY + File.separator + fileName;
                    System.out.println("file path: " +filePath);
                    File uploadedFile = new File(filePath);
                    System.out.println(filePath);
                    if (!uploadedFile.exists()) {
                    	uploadedFile.createNewFile();
                    	
                    }
                   
                    item.write(uploadedFile);
                }
            }

            // displays done.jsp page after upload finished
            //getServletContext().getRequestDispatcher("/week.jsp?className=").forward(
              //      request, response);

        } catch (FileUploadException ex) {
            throw new ServletException(ex);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }

    }

}