import org.apache.log4j.Logger;
import java.io.*;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by tharindu on 7/17/17.
 */
public class StudentControl {
    HashMap<Integer, Student> studentdetails = new HashMap<Integer, Student>();
    Logger logger = Logger.getLogger(StudentControl.class);


    public void studentDetails(Student student) {

        studentdetails.put(student.getId(), student);

    }

    public boolean checkStudent(int id) {
        if (studentdetails.containsKey(id)) {
            return true;
        } else {
            return false;
        }
    }

    public Student getStudent(int id) {
        return studentdetails.get(id);
    }

    public void printStudentDetails() {
        try (OutputStream os = new FileOutputStream("test.txt", true)) {


            for (Object objectname : studentdetails.keySet()) {

                int[] marks = new int[2];

                marks = studentdetails.get(objectname).getMarks();
                os.write((objectname + "").getBytes());
                os.write(("\t").getBytes());
                os.write((studentdetails.get(objectname).getName()).getBytes());
                os.write(("\t").getBytes());
                os.write((marks[0] + "").getBytes());
                os.write(("\t").getBytes());
                os.write((Exam.grade(marks[0])).getBytes());
                os.write(("\t").getBytes());
                os.write((marks[1] + "").getBytes());
                os.write(("\t").getBytes());
                os.write((Exam.grade(marks[1])).getBytes());
                os.write(("\n").getBytes());
            }
        } catch (Exception e) {
            System.out.println("File error...");
            logger.error("File error...");
        }
    }

    public void printStudentDetailsBfWr() {
        Scanner in = new Scanner(System.in);
        String name;
        boolean reprint=false;
        boolean isAppend;
        System.out.println("Enter filename...");
        logger.info("Enter filename...");
        name = in.next();
        String fname = name + ".txt";
        File file = new File(fname);


        isAppend=fileExist(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, isAppend))) {
            HashMap<Integer, Student> sdprint = studentdetails;
            if (isAppend) {
                addTimestamp(bw);
            }
            writefromHashmap(in, reprint, file, bw, sdprint);
            bw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writefromHashmap(Scanner in, boolean reprint, File file, BufferedWriter bw, HashMap<Integer, Student> sdprint) throws IOException {
        for (int studentID : sdprint.keySet()) {
            reprint = isReprint(reprint, file, studentID);
            if (reprint) {
                System.out.println("Already printed... Do you want to print again...");
                logger.info("Already printed... Do you want to print again...");
                System.out.println("If yes press 1 otherwise press 2...");
                logger.info("If yes press 1 otherwise press 2...");

                switch (in.nextInt()) {
                    case 1:
                        writetoFile(bw, sdprint, studentID);
                        break;
                    case 2:
                        break;
                }
            } else {
                writetoFile(bw, sdprint, studentID);
            }
        }
    }

    public void printStudentDetailsNIO() {
        Scanner in = new Scanner(System.in);
        String name;
        System.out.println("Enter filename...");
        logger.info("Enter filename...");
        name = in.next();
        boolean isAppend;
        boolean reprint=false;
        Path path = Paths.get("/home/tharindu/IdeaProjects/studentmanagement/"+name+".txt");
        isAppend=fileExist(path.toFile());

        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            HashMap<Integer, Student> sdprint = studentdetails;
            if (isAppend) {
                addTimestamp(writer);
            }
            writefromHashmap(in,reprint,path.toFile(),writer,sdprint);
            writer.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean fileExist(File file){
        Scanner in = new Scanner(System.in);
        if (file.exists()) {
            System.out.println("File exist with same name...");
            logger.info("File exist with same name...");
            System.out.println("Pres 1 to append Press 2 to overwrite Press 3 to do nothing.");
            logger.info("Pres 1 to append Press 2 to overwrite Press 3 to do nothing.");

            switch (in.nextInt()) {
                case 1:
                    return true;

                case 2:
                    return false;

            }
        }
        return true;
    }

    public void addTimestamp(BufferedWriter bw) throws IOException {
        Date date = new Date();
        bw.write(new Timestamp(date.getTime()).toString());
        bw.newLine();
    }

    public boolean isReprint(boolean reprint, File file, int studentID) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String lineFromFile = scanner.nextLine();
            if (lineFromFile.contains(studentID + "")) {

                reprint =true;
                break;
            }
        }
        return reprint;
    }

    public void writetoFile(BufferedWriter bw, HashMap<Integer, Student> sdprint, int objectname) throws IOException {
        int[] marks;
        marks = sdprint.get(objectname).getMarks();
        String content = objectname + "\t" + (sdprint.get(objectname).getName()) + "\t" + (marks[0]) + "\t" + (Exam.grade(marks[0])) + "\t" + (marks[1]) + "\t" + (Exam.grade(marks[0]) + "\n");

        bw.write(content);
    }

    public void insertfromFile(String filename) {

        File file = new File(filename);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            fileread(br);


        } catch (IOException e) {
            System.out.println("file error");
            logger.info("File error...");
        }

    }
    public void insertfromFileNIO(String filename) {

        //File file = new File(filename);
        Path path = Paths.get("/home/tharindu/IdeaProjects/studentreport/"+filename);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            fileread(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fileread(BufferedReader reader) {
        int[] marks = new int[2];
        String line;
        try {
            line = reader.readLine();

            while (line != null) {

                StringTokenizer st = new StringTokenizer(line, "\t");
                if (st.countTokens() == 6) {

                    Student newstudent = new Student();
                    newstudent.setId(Integer.parseInt(st.nextToken()));

                    newstudent.setName(st.nextToken());

                    marks[0] = Integer.parseInt(st.nextToken());
                    st.nextToken();
                    marks[1] = Integer.parseInt(st.nextToken());

                    newstudent.setMarks(marks);
                    studentdetails.put(newstudent.getId(), newstudent);

                }
                line = reader.readLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void watchDir(){
        try {
            //Create a new watch service for the file system
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get("/home/tharindu/IdeaProjects/studentmanagement");
            //Register for events to be monitored
            WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Watch service registered dir: " + path.toString());
            logger.info("Watch service registered dir: " + path.toString());
            //Wait for key to be signaled
            Scanner in =new Scanner(System.in);
            String status="";
            while(!(status.equalsIgnoreCase("return"))){

                WatchKey key;

                try {
                    System.out.println("Waiting for key to be signalled...");
                    logger.info("Waiting for key to be signalled...");
                    key = watchService.take();
                }
                catch (InterruptedException ex) {
                    System.out.println("Interrupted Exception");
                    logger.error("Interrupted Exception");
                    return;
                }
                //Process the pending events for the key
                List<WatchEvent<?>> eventList = key.pollEvents();
                System.out.println("Process the pending events for the key: " + eventList.size());
                logger.info("Process the pending events for the key: " + eventList.size());

                for (WatchEvent<?> genericEvent: eventList) {
                    //Retrieve the type of event
                    WatchEvent.Kind<?> eventKind = genericEvent.kind();
                    System.out.println("Event kind: " + eventKind);
                    logger.info("Event kind: " + eventKind);

                  /*if (eventKind == ) {

                      continue; // pending events for loop
                  }*/
                    //Retrieve the file path associated with the event
                    WatchEvent pathEvent =genericEvent;
                    Path file = (Path) pathEvent.context();
                    System.out.println("File name: " + file.toString());
                    logger.info("File name: " + file.toString());
                    insertfromFile(file.toString());

                }
                //Reset the key
                boolean validKey = key.reset();
                System.out.println("Key reset");
                logger.info("Key reset");
                System.out.println("");

                if (! validKey) {
                    System.out.println("Invalid key");
                    logger.info("Invalid key");
                    break; // infinite for loop
                }
                System.out.println("Type return to go back to main menu..");
                logger.info("Type return to go back to main menu..");
                status=in.next();
            } // end infinite for loop
            //Close the service
            watchService.close();
            System.out.println("Watch service closed.");
            logger.info("Watch service closed.");


        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IO exception.");
        }
    }

}
