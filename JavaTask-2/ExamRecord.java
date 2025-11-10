package studentlogger;

//This class is a simple "data bucket" or POJO (Plain Old Java Object).
//Its only job is to hold the information for one student's exam result.
public class ExamRecord {
 private String studentName;
 private int studentId; // Was 'rollNumber'
 private int score;     // Was 'marks'

 public ExamRecord(String studentName, int studentId, int score) {
		// TODO Auto-generated constructor stub
     this.studentName = studentName;
     this.studentId = studentId;
     this.score = score;
 }



 public String getStudentName() {
     return studentName;
 }

 public int getStudentId() {
     return studentId;
 }

 public int getScore() {
     return score;
 }

 
 public String asCsvRow() {
     return studentId + "," + studentName + "," + score;
 }

 // It's good practice to override toString() for easy debugging.
 @Override
 public String toString() {
     return "ExamRecord[ID=" + studentId + ", Name=" + studentName + ", Score=" + score + "]";
 }

 // Overriding equals/hashCode isn't strictly needed for this project,
 // but it's essential if you were to store these in a Set or as Map keys.
 @Override
 public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;
     ExamRecord that = (ExamRecord) o;
     return studentId == that.studentId && score == that.score && studentName.equals(that.studentName);
 }

 @Override
 public int hashCode() {
     return java.util.Objects.hash(studentName, studentId, score);
 }
}

