package metadataGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import com.google.gson.Gson;  

public class GenerateMetadata {
	
	private static boolean iterationFlag = true;
	private static String destFolderPath = "";
	
	public static void main(String args[]) {
		System.out.println("Starting metadata generation");
		
		try {
			GenerateMetadata metadata = new GenerateMetadata();
			generateMetadata(metadata.getRootFolderPathFromConfig("rootPath"));
			
		}  catch (SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// This is for travel through all the sub folders from the root folder specified in the configuration file
	// and generate metadata
	private static void generateMetadata(String folderPath) throws SQLException {

		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles.length == 0) {
			System.out.println("No Image files in the " + folderPath + " directory");
		}
		
		// This one is for the recursive program
		// This recursiveCount is for identify the root folder. Because in root folder also the metadata object get created.
		// But the files array is null for root folder. To avoid that we can use this recursiveCount variable when printing the JSON.
		int recursiveCount = 0; 
		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				generateMetadata(file.getAbsolutePath());
				recursiveCount++;
			}
		}
		
		recursiveCount--;
		
		Metadata metadata = new Metadata();
		Files[] files = new Files[listOfFiles.length];
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				Files fileList = new Files();
				
				File file = listOfFiles[i];
				String fileName = file.getName();
				
				String referenceNumber = file.getParent().substring(file.getParent().lastIndexOf("\\") + 1);
				
				// Listing common details for a particular folder. Otherwise it'll loop for the number of files in the folder
				if (i == 0) {
					// Set ref_num
					metadata.setRef_num(referenceNumber);
					// Set division. By default it's NB
					metadata.setDivision("NB");
					// Set source. By default it's A
					metadata.setSource("A");
					// Set sub_type. For batch job, sub_type is O
					metadata.setSub_type("O");
					// Set sms_type. By default is's blank
					metadata.setSms_type("");
					
					Connection conn = DatabaseConnection.getConnection();
					
					// Getting pol_num and source_date
					PreparedStatement preStmt = conn.prepareStatement("SELECT POLICY_NO, SUBMITTED_DATE FROM INT_CASE_INFO WHERE " +
							"CASE_REF_NO = ?");
					preStmt.setString(1, referenceNumber);
					ResultSet rs = preStmt.executeQuery();
					String policyNumber = null;
					
					while (rs.next()) {
						policyNumber = rs.getString("POLICY_NO");
						String submittedDate = rs.getString("SUBMITTED_DATE");
						
						if (policyNumber != null) {
							metadata.setPol_num(policyNumber);
						} else {
							metadata.setPol_num("");
						}
						
						metadata.setSource_date(submittedDate);
					}
					
					// Getting the cpp_name, pro_code
					if (policyNumber != null) {
						preStmt = conn.prepareStatement("SELECT POLICY_OWNER, PRODUCT_CODE FROM " +
								"CASE_CLIENT_DETAILS_VIEW WHERE POLICY_NO = ?");
						preStmt.setString(1, policyNumber);
						rs = preStmt.executeQuery();
						
						while (rs.next()) {
							String policyOwner = rs.getString("POLICY_OWNER");
							String productCode = rs.getString("PRODUCT_CODE");
							
							metadata.setCpp_name(policyOwner);
							metadata.setPro_code(productCode);
						}
					}
					
					// Getting the ct_name
					if (policyNumber != null) {
						preStmt = conn.prepareStatement("SELECT SURNAME FROM IDMDTA.IFSDMCLNTPF WHERE CLNTNUM in " +
								"(SELECT LIFCNUM FROM IDMDTA.IFSDMLIFEPF WHERE IDMDTA.IFSDMLIFEPF.CHDRNUM = ?)");
						preStmt.setString(1, policyNumber);
						rs = preStmt.executeQuery();
						boolean flag = false;
						
						while (rs.next()) {
							String surname = rs.getString("SURNAME");
							metadata.setCt_name(surname.trim());
							flag = true;
						}
						
						if (!flag) {
							metadata.setCt_name("");
						}
						
					}
					
					// Getting fast_t & print_tab
					if (policyNumber != null) {
						preStmt = conn.prepareStatement("SELECT IS_PRINTED, FAST_TRACK FROM INT_CASE_INFO WHERE CASE_REF_NO = ?");
						preStmt.setString(1, referenceNumber);
						rs = preStmt.executeQuery();
						while (rs.next()) {
							String is_printed = rs.getString("IS_PRINTED");
							String fast_track = rs.getString("FAST_TRACK");

							if (is_printed.equalsIgnoreCase("N")) {
								metadata.setPrint_tab(false);
							} else {
								metadata.setPrint_tab(true);
							}
							
							if (fast_track != null) {
								metadata.setFast_t(fast_track);
							} else {
								metadata.setFast_t("");
							}
						}
					}
					
					// Getting ul_remarks
					if (policyNumber != null) {
						// Get the caseID
						preStmt = conn.prepareStatement("SELECT CASE_ID FROM INT_CASE_INFO WHERE CASE_REF_NO = ?");
						preStmt.setString(1, referenceNumber);
						rs = preStmt.executeQuery();
						String caseID = "";
						while (rs.next()) {
							caseID = rs.getString("CASE_ID");
						}
						
						// get the remarks from the above retrieve caseID
						preStmt = conn.prepareStatement("SELECT ERROR_CODE, ERROR_DESC FROM INT_CASE_ERROR_INFO WHERE CASE_ID = ?");
						preStmt.setString(1, caseID);
						rs = preStmt.executeQuery();
						List<String> remarks = new ArrayList<String>();
						while (rs.next()) {
							String errorCode = rs.getString("ERROR_CODE");
							String error_desc = rs.getString("ERROR_DESC");
							remarks.add(errorCode + " - " + error_desc);
						}
						
						String[] ulRemarks = new String[remarks.size()];
						remarks.toArray(ulRemarks);
						metadata.setUl_remarks(ulRemarks);
					}
					
					// Getting ul_stat
					if (policyNumber != null) {
						preStmt = conn.prepareStatement("SELECT STATUS FROM INT_CASE_RECEIVED WHERE FILE_NAME = ?");
						preStmt.setString(1, referenceNumber + ".ZIP");
						rs = preStmt.executeQuery();
						
						while (rs.next()) {
							String status = rs.getString("STATUS");
							metadata.setUl_stat(status);
						}
					}
					
					// Closing the connections
					if (rs != null) {
						rs.close();
					}
					
					if (preStmt != null) {
						preStmt.close();
					}
					
					if (conn != null) {
						conn.close();
					}
				}
				
				// Getting the file paths
				fileList.setPath(referenceNumber + "\\" + fileName);
				
				// Getting the doc_id
				Connection conn = DatabaseConnection.getConnection();
				PreparedStatement preStmt = conn.prepareStatement("SELECT ATTACHMENT_ID, ATTACHMENT_IDENTIFIER FROM " +
						"INT_CASE_ATTACHMENT WHERE ATTACHMENT_NAME = ?");
				preStmt.setString(1, fileName);
				ResultSet rs = preStmt.executeQuery();
				while (rs.next()) {
					String docID = rs.getString("ATTACHMENT_ID");
					String docClass = rs.getString("ATTACHMENT_IDENTIFIER");
					fileList.setDoc_id(docID);
					fileList.setDoc_class(docClass);
				}
				
				files[i] = fileList;
				
				// Closing the connections
				if (rs != null) {
					rs.close();
				}
				
				if (preStmt != null) {
					preStmt.close();
				}
				
				if (conn != null) {
					conn.close();
				}
			}
		}
		
		metadata.setFiles(files);
		
		// The recursiveCount should be < 0 for child folders. But it's > 0 for the root folder
		if (metadata != null && recursiveCount < 0) {
			Gson gson = new Gson();  
			System.out.println("\n" + gson.toJson(metadata));
			movefile(metadata);
		} 
		
	}
	
	// This is to get the file extension
	private static String getFileExtension(String fileName) {
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}
	
	// Reading the root folder path from the configuration file
	private String getRootFolderPathFromConfig(String propertyName) throws IOException {
		InputStream inputStream;
		Properties prop = new Properties();
		String propFileName = "config.properties";
		
		inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		 
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
		}
		
		String propertyValue = prop.getProperty(propertyName);
		return propertyValue;
	}
	
	// This method is for copy the original files to another location
	private static void movefile(Metadata metadata) {

		GenerateMetadata metadataObj = new GenerateMetadata();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmssms");
		Calendar cal = Calendar.getInstance();
		
		try {
			if (iterationFlag) {
				destFolderPath = metadataObj.getRootFolderPathFromConfig("destFolderPath") + "System_" + dateFormat.format(cal.getTime());
				
				File file = new File(destFolderPath);
				if (!file.exists()) {
		            if (file.mkdir()) {
		                System.out.println("\nRoot directory is created!");
		            } else {
		                System.out.println("Failed to create directory!");
		            }
		        }
				
				iterationFlag = false;
			}
			
			String srcPath = metadataObj.getRootFolderPathFromConfig("rootPath");
			File srcFolder = new File(srcPath + metadata.getRef_num());
			File destFolder = new File(destFolderPath + "\\" + metadata.getRef_num());
			copyFolder(srcFolder, destFolder);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// Copy method
	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// If directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("\nDirectory copied from " + src + "  to " + dest);
			}

			// List all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// If file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// Copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}

}
