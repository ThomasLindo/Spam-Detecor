//this class deals with actaully reading the files and creating the data
package sample;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;


public class SpamDetector {
    DecimalFormat df = new DecimalFormat("#.#####");
    //decimals used in spam probability calculation
    double alpha;
    double omega;
    //the map is used to keep track of words encountered in the training phase
    private Map<String, Word> wordDatabase;
    //this list keeps track of data for each file in the testing phase
    private List<TestFile> TestFiles = new ArrayList<TestFile>();
    //this bool is used to tell if we are dealing with spam files or not
    private boolean SpamFolder;

    int SpamFiles = 0;
    int HamFiles = 0;

    public SpamDetector() {
        wordDatabase = new TreeMap<>();
    }

    //process all training files and generate spam and ham proabailities for each
    public void processTrainFiles(File file) throws IOException {
        System.out.println("Processing " + file.getAbsolutePath() + "...");
        if (file.isDirectory()) {
            String name = file.getName();
            if (name.equals("spam")) {
                SpamFolder = true;
            } else if (name.equals("ham") || name.equals("ham2")) {
                SpamFolder = false;
            }
            // process all the files in that directory
            File[] contents = file.listFiles();
            for (File current : contents) {
                processTrainFiles(current);
            }
        } else if (file.exists()) {

            Set<String> keys = wordDatabase.keySet();
            Iterator<String> keyIterator = keys.iterator();
            //reset encountered booleans
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                Word current = wordDatabase.get(key);
                current.setEncountered(false);
                wordDatabase.put(key, current);
            }

            if (SpamFolder) {
                //add 1 to spam counter
                SpamFiles++;
            } else {
                //add 1 to ham counter
                HamFiles++;
            }

            // update word database for each word encountered
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\s");
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isWord(word.toLowerCase())) {
                    UpdateWordDatabase(word.toLowerCase());
                }
            }
        }
    }

    //processes all the files in the test folder
    public void processTestFiles(File file) throws IOException {
        System.out.println("Processing " + file.getAbsolutePath() + "...");
        if (file.isDirectory()) {
            String name = file.getName();
            if (name.equals("spam")) {
                SpamFolder = true;
            } else if (name.equals("ham") || name.equals("ham2")) {
                SpamFolder = false;
            }
            // process all the files in that directory
            File[] contents = file.listFiles();
            for (File current : contents) {
                processTestFiles(current);
            }
        } else if (file.exists()) {
            //calculate spam prob
            double mu = 0.0;
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isWord(word.toLowerCase())) {
                    if (wordDatabase.containsKey(word)) {
                        //if word is in database, calculate power used in spam prob calculation

                        alpha = Math.log(1 - wordDatabase.get(word).getSpamFileProb());
                        omega = Math.log(wordDatabase.get(word).getSpamFileProb());

                        mu = mu + (alpha - omega);


                    }
                }
            }
            //when all words (possible) are processed, calculate final spam probability
            double finalPer = 1 / (1 + Math.pow(Math.E, mu));
            if (finalPer < 0.001) {
                finalPer = 0.0;
            }


// add this file to the list
            if (SpamFolder) {
                TestFiles.add(new TestFile(file.getName(), finalPer, "Spam"));
            } else {
                TestFiles.add(new TestFile(file.getName(), finalPer, "Ham"));
            }
        }
    }

    // returns an observable list of all test files
    public ObservableList<TestFile> getResults() {
        ObservableList<TestFile> Results = FXCollections.observableArrayList();
        for (TestFile entry : TestFiles) {
            Results.add(entry);
        }
        return Results;
    }

    //determines if the string is actually a word
    private boolean isWord(String word) {
        String pattern = "^[a-zA-Z']+$";
        return word.matches(pattern);

        // also fine:
        //return word.matches(pattern);
    }

    //updates word database
    private void UpdateWordDatabase(String word) {
        Word current;
        if (wordDatabase.containsKey(word)) {
            //map contains word
            current = wordDatabase.get(word);
            if (current.isEncountered() == false) {
                //word has not been encountered in file yet
                if (SpamFolder) {
                    //this is a spam file
                    current.setScount(current.getScount() + 1);
                } else {
                    //this is a ham file
                    current.setHcount(current.getHcount() + 1);
                }
                current.setEncountered(true);
                wordDatabase.put(word, current);
            }
        } else {
            //word is completely new
            current = new Word(word);
            if (SpamFolder) {
                //this is a spam file
                current.setScount(current.getScount() + 1);
            } else {
                //this is a ham file
                current.setHcount(current.getHcount() + 1);
            }
            current.setEncountered(true);
            wordDatabase.put(word, current);
        }
    }

    //caulculates the spam and ham probabilities for each word in the database
    public void calculatePercentages() {
        Set<String> keys = wordDatabase.keySet();
        Iterator<String> keyIterator = keys.iterator();
        Word current;
        while (keyIterator.hasNext()) {
            //for each word get ham and spam %
            String key = keyIterator.next();
            current = wordDatabase.get(key);
            current.setSprob(Double.valueOf(df.format((double) current.getScount() / (double) SpamFiles)));
            current.setHprob(Double.valueOf(df.format((double) current.getHcount() / (double) HamFiles)));
            wordDatabase.put(key, current);
        }
    }
}
