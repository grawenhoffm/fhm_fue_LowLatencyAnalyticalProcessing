package de.mk.elasticsearch.csvpreparation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import de.mk.elasticsearch.App;

public class CSVPreparation {


	public void prepareAll() throws IOException {
//		Settings s = null;
		final File folderWithUnpreparedFiles = new File(App.class.getResource("/csv_data_to_prepare").getPath());
	    for (final File fileEntry : folderWithUnpreparedFiles.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            // Do nothing
	        } else {
	        	final StringBuffer buf = new StringBuffer();
	            System.out.println("Update: "+fileEntry.getName());
	            try (BufferedReader br = new BufferedReader(new FileReader(fileEntry))) {
	    		    String line;
	    		    while ((line = br.readLine()) != null) {
	    		    	if (!line.trim().isEmpty() && !"".equals(line.trim())) {
	    		    		while (line.contains("||")) {
	    		    			line = line.replaceFirst("\\|\\|", "|null|");
	    		    		}
	    		    		buf.append(line + "\n");
	    		    	}
	    		    }
	    		}
	            FileUtils.writeStringToFile(new File("prepared_csv/"+fileEntry.getName()), buf.toString(), Charset.forName("UTF-8"));
	        }
	    }


//		final File folder = new File("/home/you/Desktop");
	}
}
