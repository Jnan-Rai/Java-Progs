package studentlogger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * JUnit 4 test class for our ResultFileWriter.
 * This checks if the file writing is correct, both in a single-threaded
 * and a multi-threaded context.
 */
public class ResultFileWriterTest {

    private static final String TEST_OUTPUT_FILE = "test_results.csv";
    private ResultFileWriter testWriter;
    private Path testFilePath;

    @Before
    public void setUp() throws IOException {
        // This method runs before each @Test
        testFilePath = Paths.get(TEST_OUTPUT_FILE);
        
        // Start with a clean slate for every test
        Files.deleteIfExists(testFilePath);

        // We use 'false' for no header to make line counting simpler
        testWriter = new ResultFileWriter(TEST_OUTPUT_FILE, false);
    }

    @After
    public void tearDown() throws IOException {
        // This method runs after each @Test
        if (testWriter != null) {
            testWriter.close();
        }
        Files.deleteIfExists(testFilePath);
    }

    @Test
    public void testSingleRecordWrite() throws IOException {
        // 1. Arrange
        ExamRecord record = new ExamRecord("Test Student", 101, 95);
        String expectedCsv = "101,Test Student,95";

        // 2. Act
        testWriter.writeRecord(record);
        testWriter.close(); // Flush data to disk

        // 3. Assert
        assertTrue("File was not created.", Files.exists(testFilePath));
        List<String> lines = Files.readAllLines(testFilePath);
        assertEquals("File should only have one line.", 1, lines.size());
        assertEquals("File content is incorrect.", expectedCsv, lines.get(0));
    }

    @Test
    public void testConcurrentWritesFromMultipleThreads() throws IOException, InterruptedException {
        // 1. Arrange
        int recordsPerThread = 150;
        List<ExamRecord> batch1 = new ArrayList<>();
        List<ExamRecord> batch2 = new ArrayList<>();
        List<ExamRecord> batch3 = new ArrayList<>();
        Set<String> expectedCsvRows = new HashSet<>();

        for (int i = 0; i < recordsPerThread; i++) {
            ExamRecord r1 = new ExamRecord("Thread1_S" + i, 1000 + i, 80);
            batch1.add(r1);
            expectedCsvRows.add(r1.asCsvRow());

            ExamRecord r2 = new ExamRecord("Thread2_S" + i, 2000 + i, 85);
            batch2.add(r2);
            expectedCsvRows.add(r2.asCsvRow());
            
            ExamRecord r3 = new ExamRecord("Thread3_S" + i, 3000 + i, 90);
            batch3.add(r3);
            expectedCsvRows.add(r3.asCsvRow());
        }

        SubmissionTask task1 = new SubmissionTask("Test-1", testWriter, batch1);
        SubmissionTask task2 = new SubmissionTask("Test-2", testWriter, batch2);
        SubmissionTask task3 = new SubmissionTask("Test-3", testWriter, batch3);

        Thread worker1 = new Thread(task1);
        Thread worker2 = new Thread(task2);
        Thread worker3 = new Thread(task3);
        
        int totalExpectedRecords = recordsPerThread * 3;

        // 2. Act
        worker1.start();
        worker2.start();
        worker3.start();

        // Wait for all test threads to finish
        worker1.join();
        worker2.join();
        worker3.join();

        testWriter.close(); 

        // 3. Assert
        assertTrue("Test file was not created by threads.", Files.exists(testFilePath));

        List<String> actualLines = Files.readAllLines(testFilePath);
        
        assertEquals(
                "Incorrect number of lines in file. Expected: " + totalExpectedRecords + ", Got: " + actualLines.size(),
                totalExpectedRecords, 
                actualLines.size()
        );

        
        Set<String> actualLinesAsSet = new HashSet<>(actualLines);
        assertEquals(
                "Duplicate lines found in file, indicating a race condition.",
                totalExpectedRecords, 
                actualLinesAsSet.size()
        );


        assertEquals(
                "The set of records in the file does not match the set of expected records.",
                expectedCsvRows, 
                actualLinesAsSet
        );
    }
}
