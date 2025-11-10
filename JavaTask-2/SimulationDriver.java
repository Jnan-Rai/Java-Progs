package studentlogger;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulationDriver {

    // The name of the output file
    private static final String OUTPUT_FILE = "exam_results.csv";

    public static void main(String[] args) {
    	// TODO Auto-generated method stub
        System.out.println("Starting submission simulation...");

        try {
            // 1. Setup the shared file writer (this is the key part)
            ResultFileWriter fileWriter = new ResultFileWriter(OUTPUT_FILE, true);

            // 2. Make some dummy data for the threads
            // We'll make slightly different sized batches
            List<ExamRecord> batch1 = new ArrayList<>();
            for (int i = 1; i <= 30; i++) {
                batch1.add(new ExamRecord("Student_A_" + i, 1000 + i, (int) (Math.random() * 60 + 40)));
            }

            List<ExamRecord> batch2 = new ArrayList<>();
            for (int i = 1; i <= 35; i++) {
                batch2.add(new ExamRecord("Student_B_" + i, 2000 + i, (int) (Math.random() * 60 + 40)));
            }
            
            List<ExamRecord> batch3 = new ArrayList<>();
            for (int i = 1; i <= 40; i++) {
                batch3.add(new ExamRecord("Student_C_" + i, 3000 + i, (int) (Math.random() * 60 + 40)));
            }

            // 3. Create the "jobs" for each thread
            SubmissionTask task1 = new SubmissionTask("Batch-1", fileWriter, batch1);
            SubmissionTask task2 = new SubmissionTask("Batch-2", fileWriter, batch2);
            SubmissionTask task3 = new SubmissionTask("Batch-3", fileWriter, batch3);

            // 4. Create the threads themselves
            Thread thread1 = new Thread(task1);
            Thread thread2 = new Thread(task2);
            Thread thread3 = new Thread(task3);

            // 5. Start all threads!
            long startTime = System.currentTimeMillis();
            thread1.start();
            thread2.start();
            thread3.start();
            thread1.join();
            thread2.join();
            thread3.join();

            long endTime = System.currentTimeMillis();

            // 7. All done, safe to close the file now.
            fileWriter.close();
            
            int total = batch1.size() + batch2.size() + batch3.size();
            
            System.out.println("\nAll batches complete.");
            System.out.println("Total scores logged: " + total);
            System.out.println("Time taken: " + (endTime - startTime) + " ms");
            System.out.println("File saved to " + OUTPUT_FILE);

        } catch (IOException e) {
            System.err.println("Error: Couldn't open the file writer.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}
