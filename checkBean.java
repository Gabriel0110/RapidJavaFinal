/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project02;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author gabri
 */
@Named(value = "checkBean")
@ApplicationScoped
public class checkBean implements Serializable {

    String content;
    String textArea;
    String resultBox;
    String description;
    String chapter;
    String exercise;
    List<String> chapterList;
    List<String> exerciseList;
    Object newValue;
    boolean isOutput;
    boolean isInput;
    HashMap<String, String> inputCheck;
    final static int EXECUTION_TIME_ALLOWED = 1000;
    final static int EXECUTION_TIME_INTERVAL = 100;

    static class Output {

        public String output = "";
        public String error = "";
        public boolean isInfiniteLoop = false;
        public int timeUsed = 100;

    }

    public checkBean() throws FileNotFoundException, IOException {
        initialize();
    }

    public void initialize() throws FileNotFoundException, IOException {
        chapterList = new ArrayList<>();
        isInput = false;
        isOutput = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("C:\\Users\\gabri\\Documents\\NetBeansProjects\\Tomberlin\\web\\ChapterList.txt"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                chapterList.add(line);
            }
        } finally {
            reader.close();
        }
        //String[] array = (String[]) chapterList.toArray();

        exerciseList = new ArrayList<>();
    }

    public String getChapter() {
        if (chapter == null) {
            chapter = "Chapter 01";
        }
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public List<String> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<String> chapterList) {
        this.chapterList = chapterList;
    }

    public String getExercise() {
        if (exercise == null) {
            exercise = "Exercise01_01";
        }
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public List<String> getExerciseList() {
        return exerciseList;
    }

    public void setExerciseList(String chapter) {
        exerciseList.clear();
        File dir = new File("C:\\Users\\gabri\\Documents\\NetBeansProjects\\Tomberlin\\web\\exercisedescription");
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                // ADD THE IF STATEMENT TO SEE IF THE FILE NUMBER MATCHES CHAPTER NUMBER
                String name = child.getName();

                if (name.substring(8, 10).contains(chapter.substring(8, 10))) {
                    exerciseList.add(name);
                }
            }
        }
    }

    public void changeAreaListener(ValueChangeEvent event) {
        textArea = event.getNewValue().toString();
        setTextArea(textArea);
    }

    public void changeNameListener(ValueChangeEvent event) {
        chapter = event.getNewValue().toString();
        setExerciseList(chapter);
    }

    public void changeExerciseListener(ValueChangeEvent event) {
        if (!"Exercise01_01".equals(exercise)) {
            exercise = event.getNewValue().toString();
            setExercise(exercise);
        }

    }

    public String getDescription() {
        setExercise(exercise);
        String ch = chapter.replaceAll("[a-zA-Z\\s]", "");
        String ex = exercise.substring(exercise.length() - 2, exercise.length()).replaceAll("[a-zA-Z\\s]", "");

        if (exercise.contains("Extra")) {
            ex = exercise.substring(exercise.length() - 7, exercise.length() - 5).replaceAll("[a-zA-Z\\s]", "");
        }

        if (exercise == null || "Exercise01_01".equals(exercise)) {
            return "/* Paste your Exercise01_01 here and click Automatic Check.\n "
                    + "For all programming projects, the numbers should be double\n "
                    + "unless it is explicitly stated as integer.\n "
                    + "If you get a java.util.InputMismatchException error, check if\n "
                    + "your code used input.readInt(), but it should be input.readDouble().\n "
                    + "For integers, use int unless it is explicitly stated as long. */ ";
        }

        if (Integer.parseInt(ch) < 10) {
            if (Integer.parseInt(ex) < 10) {
                return "/* Paste your " + getExercise() + " here and click Automatic Check.\n "
                        + "For all programming projects, the numbers should be double\n "
                        + "unless it is explicitly stated as integer.\n "
                        + "If you get a java.util.InputMismatchException error, check if\n "
                        + "your code used input.readInt(), but it should be input.readDouble().\n "
                        + "For integers, use int unless it is explicitly stated as long. */ ";
            }
            return "/* Paste your " + getExercise() + " here and click Automatic Check.\n "
                    + "For all programming projects, the numbers should be double\n "
                    + "unless it is explicitly stated as integer.\n "
                    + "If you get a java.util.InputMismatchException error, check if\n "
                    + "your code used input.readInt(), but it should be input.readDouble().\n "
                    + "For integers, use int unless it is explicitly stated as long. */ ";
        }
        if (Integer.parseInt(ex) < 10) {
            return "/* Paste your " + getExercise() + " here and click Automatic Check.\n "
                    + "For all programming projects, the numbers should be double\n "
                    + "unless it is explicitly stated as integer.\n "
                    + "If you get a java.util.InputMismatchException error, check if\n "
                    + "your code used input.readInt(), but it should be input.readDouble().\n "
                    + "For integers, use int unless it is explicitly stated as long. */ ";
        }
        return "/* Paste your Exercise01_01 here and click Automatic Check.\n "
                + "For all programming projects, the numbers should be double\n "
                + "unless it is explicitly stated as integer.\n "
                + "If you get a java.util.InputMismatchException error, check if\n "
                + "your code used input.readInt(), but it should be input.readDouble().\n "
                + "For integers, use int unless it is explicitly stated as long. */ ";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Output executeProgram(String command, String program,
            String programDirectory, String inputFile, String outputFile) {
        final Output result = new Output();
        ProcessBuilder pb;

        // For Java security, added c:/etext.policy in c:\program files\jre\bin\security\java.security
        pb = new ProcessBuilder(command, "-Djava.security.manager", program);
        pb.directory(new File(programDirectory));
        pb.redirectErrorStream(true);

        if (inputFile != null) {
            pb.redirectInput(ProcessBuilder.Redirect.from(new File(inputFile)));
        }

        pb.redirectOutput(ProcessBuilder.Redirect.to(new File(outputFile)));
        long startTime = System.currentTimeMillis();
        Process proc = null;

        try {
            proc = pb.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // This separate thread destroy the process if it takes too long time
        final Process proc1 = proc;

        new Thread() {
            public void run() {
                int sleepTime = 0;
                boolean isFinished = false;

                while (sleepTime <= EXECUTION_TIME_ALLOWED && !isFinished) {
                    try {
                        try {
                            Thread.sleep(EXECUTION_TIME_INTERVAL);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
//                System.out.println("sleepTime " + sleepTime);
                        sleepTime += EXECUTION_TIME_INTERVAL;
                        int exitValue = proc1.exitValue();
                        isFinished = true;
//                System.out.println("exitValue " + exitValue);
                    } catch (IllegalThreadStateException ex) {
                    }
                }
                if (!isFinished) {
                    proc1.destroy();
                    result.isInfiniteLoop = true;
//            System.out.println("Infinite loop");
                }
            }
        }.start();

        try {
            int exitCode = proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        result.timeUsed = (int) (System.currentTimeMillis() - startTime);

        return result;
    }

    public static Output compileProgram(String command,
            String sourceDirectory, String program) {
        final Output result = new Output();
        ProcessBuilder pb;
        pb = new ProcessBuilder(command, "-classpath", ".;C:\\book",
                "-Xlint:unchecked", "-nowarn", "-XDignore.symbol.file", program);
        pb.directory(new File(sourceDirectory));
        long startTime = System.currentTimeMillis();
        Process proc = null;

        try {
            proc = pb.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // This separate thread destroy the process if it takes too long time
        final Process proc1 = proc;

        new Thread() {
            public void run() {
                Scanner scanner1 = new Scanner(proc1.getInputStream());

                while (scanner1.hasNext()) {
                    result.output += scanner1.nextLine().replaceAll(" ", "&nbsp;") + "\n";
                    //  scanner1.close(); // You could have closed it too soon
                }
            }
        }.start();

        new Thread() {
            public void run() {
                // Process output from proc
                Scanner scanner2 = new Scanner(proc1.getErrorStream());

                while (scanner2.hasNext()) {
                    result.error += scanner2.nextLine() + "\n";
                }
                // scanner2.close(); // You could have closed it too soon
            }
        }.start();

        try {
            //Wait for the external process to finish
            int exitCode = proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        result.output.replaceAll(" ", "&nbsp;");
        result.error.replaceAll(" ", "&nbsp;");

        // Ignore warnings
        if (result.error.indexOf("error") < 0) {
            result.error = "";
        }

//        if (result.error.indexOf("error") >= 0 || result.error.indexOf("Error") >= 0)
//          result.error = "";
        result.timeUsed = (int) (System.currentTimeMillis() - startTime);

        return result;
    }

    public void compile() throws IOException {
        createFile();
        File str = new File("C:\\book\\" + exercise + ".class");

        Output output = compileProgram("javac",
                "C:\\book", exercise + ".java");
        System.out.println(output.error);
        System.out.println("Result: " + output.output);

        if (str.exists()) {
            // RUN WITHOUT INPUT //
            output = executeProgram("java", exercise,
                    "C:\\book", null, "C:\\book\\" + exercise + ".output");
        } else {
            setTextArea(output.error);
        }

        // RUN WITH INPUT //
//        output = executeProgram("java", exercise,
//                "C:\\book", inputFromInputBox,
//                "C:\\book\\" + exercise + ".output");
//
//        System.out.println(output.error);
//
//        System.out.println("Result: " + output.output);
        // now you would get the contents of the output file and send it to the textarea for displaying
    }

    public void createFile() throws IOException {
        //Files.delete(C:\\book\\" + exercise + ".java);

        File file = new File("C:\\book\\" + exercise + ".java");
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(textArea);
        printWriter.close();
    }

    static String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    public boolean getIsOutput() {
        return isOutput;
    }

    public void setIsOutput(boolean isOutput) {
        this.isOutput = isOutput;
    }

    public void hasOutput() {
        isOutput = true;
    }

    public void hasNoOutput() {
        isOutput = false;
    }

    public boolean getIsInput() {
        return isInput;
    }

    public void setIsInput(boolean isInput) {
        this.isInput = isInput;
    }

    public void hasInput() {
        File dir = new File("C:\\Users\\gabri\\Documents\\NetBeansProjects\\Tomberlin\\web\\gradeexercise");
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                String name = child.getName();
                if (name.contains(exercise) && name.contains(".input")) {
                    isInput = true;
                }

            }
        }
    }

    public String getResultBox() throws IOException {
        return "<div id=\"editorForResult\" style=\"text-align: left; width:575px; height:181px; font-weight: normal; font-size: 96%; color: black; background-color: white; border: 1px solid; border-color: #f6912f;height:147px;; width:100%; margin: 0 auto;\">"
                + "command&gt;javac " + exercise + ".java\n"
                + "Compiled successful\n"
                + "\n"
                + "command&gt;java " + exercise + "\n" + textArea
                + "\n"
                + "command&gt;\n"
                + "</div>";
    }

    public String getInputBox() throws IOException {
        return "<textarea id=\"dataInputTextarea\" name=\"jsfForm:dataInputTextarea\" style=\"font-family: Consolas, Menlo, Monaco, Lucida Console, Liberation Mono, DejaVu Sans Mono, Bitstream Vera Sans Mono, Courier New, monospace, serif, monospace !important; width:575px; height:40px; background-color: white; border: 1px solid #f6912f; font-weight: normal; font-size: 96%; margin-top: 0px; width:100%; margin: 0 auto;\">" + getInputText() + "</textarea>";
    }

    public String getTextArea() {
        return textArea;
    }

    public void setTextArea(String textArea) {
        this.textArea = textArea;
    }

    public String getLabel() {
        return "<label style=\"font-weight: bold; font-family: times; width:100%; margin: 0 auto;\">Enter input data for the program (Sample data provided below. You may modify it.)</label>";
    }

    public String getInputText() throws FileNotFoundException, IOException {
        String str = readFile("C:\\Users\\gabri\\Documents\\NetBeansProjects\\Tomberlin\\web\\gradeexercise\\" + exercise + "a.input");
        return str;
    }

}
