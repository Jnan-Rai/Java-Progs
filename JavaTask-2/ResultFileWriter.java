package studentlogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
public class ResultFileWriter {

    private final String outputFilePath;
    private final PrintWriter filePrintWriter;

    public ResultFileWriter(String filePath, boolean writeHeader) throws IOException {
    	// TODO Auto-generated constructor stub
        this.outputFilePath = filePath;
        File file = new File(this.outputFilePath);

        // Check if the file is brand new or empty.
        boolean isNewFile = !file.exists() || file.length() == 0;

        // We use 'true' here to open the file in APPEND mode.
        // This means we add to the end of the file, not overwrite it.
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        this.filePrintWriter = new PrintWriter(bw);

        // If it's a new file, add the CSV header for readability.
        if (writeHeader && isNewFile) {
            this.filePrintWriter.println("StudentID,StudentName,Score");
        }
    }

   
     
    public synchronized void writeRecord(ExamRecord record) {
        filePrintWriter.println(record.asCsvRow());
    }

    
    public void close() {
        // This also automatically flushes the buffer.
        filePrintWriter.close();
    }
}

