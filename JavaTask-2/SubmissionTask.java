package studentlogger;

import java.util.List;
import java.util.Random;


public class SubmissionTask implements Runnable {

    // A reference to the ONE shared file writer instance
    private final ResultFileWriter writer;
    
    // The specific list of records this task is responsible for
    private final List<ExamRecord> recordsToProcess;
    
    private final String taskName;
    private final Random random = new Random();

    public SubmissionTask(String taskName, ResultFileWriter writer, List<ExamRecord> records) {
    	// TODO Auto-generated constructor stub
        this.taskName = taskName;
        this.writer = writer;
        this.recordsToProcess = records;
    }

    @Override
    public void run() {
        System.out.printf("Task '%s' starting, processing %d records.\n", taskName, recordsToProcess.size());

        for (ExamRecord record : recordsToProcess) {
            try {
                int delay = random.nextInt(15) + 5; // Sleep 5-19 ms
                Thread.sleep(delay);

                // Call the synchronized method to safely write the record
                writer.writeRecord(record);

            } catch (InterruptedException e) {
                // This happens if another thread interrupts this one.
                System.err.printf("Task '%s' was interrupted.\n", taskName);
                // Restore the interrupted status
                Thread.currentThread().interrupt();
                break; // Exit the loop
            }
        }
        
        System.out.printf("Task '%s' finished.\n", taskName);
    }
}

