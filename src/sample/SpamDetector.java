package sample;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class SpamDetector {
    private Map<String, Word> wordDatabase;
    private List<TestFile> TestFiles;
    private boolean SpamFolder;

    int SpamFiles = 0;
    int HamFiles = 0;

    public SpamDetector() {
        wordDatabase = new TreeMap<>();
    }

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
            //reset Encountered booleans
            Set<String> keys = wordDatabase.keySet();
            Iterator<String> keyIterator = keys.iterator();
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

            // count the words in this file
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isWord(word.toLowerCase())) {
                    UpdateWordDatabase(word.toLowerCase());
                }
            }
        }
    }

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
                        //if word is in database
                        mu = mu + (Math.log(1 - wordDatabase.get(word).getSpamFileProb()) - Math.log(wordDatabase.get(word).getSpamFileProb()));
                    }
                }
            }
            //all words (possible) have been scanned
            if (SpamFolder) {
                TestFiles.add(new TestFile(file.getName(), 1 / (1 + Math.pow(Math.E, mu)), "Spam"));
            } else {
                TestFiles.add(new TestFile(file.getName(), 1 / (1 + Math.pow(Math.E, mu)), "Ham"));
            }
        }
    }

    public ObservableList<TestFile> getResults() {
        ObservableList<TestFile> Results = FXCollections.observableArrayList();
        for (TestFile entry : TestFiles) {
            Results.add(entry);
        }
        return Results;
    }

    private boolean isWord(String word) {
        String pattern = "^[a-zA-Z']+$";
        if (word.matches(pattern)) {
            return true;
        } else {
            return false;

        }

        // also fine:
        //return word.matches(pattern);
    }

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

    public void calculatePercentages() {
        DecimalFormat df = new DecimalFormat("#.##################################");
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

    public void outputWordCounts(int minCount) {
        System.out.println("# of words: " + wordDatabase.keySet().size());


        Set<String> keys = wordDatabase.keySet();
        Iterator<String> keyIterator = keys.iterator();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            int count = wordDatabase.get(key).getScount();
            int Hcount = wordDatabase.get(key).getHcount();
            double prob = wordDatabase.get(key).getSprob(); //probability this word appears in a spam file
            double Hprob = wordDatabase.get(key).getHprob(); // probability this word appears in a ham file
            double Fprob = wordDatabase.get(key).getSpamFileProb(); // probability a file is spam if it has this word

            if (count >= minCount || Hcount >= minCount) {
                System.out.println(key + ": " + count + " " + Hcount + " " + prob + " " + Hprob + " " + Fprob);
            }
        }


    }

    public static void main(String[] args) {


        SpamDetector spamDetector = new SpamDetector();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        directoryChooser.setTitle("Select a directory with test and train folders");
        File file = directoryChooser.showDialog(null);

            if (file.isDirectory()) {
                File[] contents = file.listFiles();
                for (File current : contents) {
                    String name = current.getName().toLowerCase();
                    if (name.equals("train")) {
                        try {
                            spamDetector.processTrainFiles(current);
                            spamDetector.calculatePercentages();

                        } catch (FileNotFoundException e) {
                            System.err.println("Invalid input dir: " + current.getAbsolutePath());
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (name.equals("test")) {
                        try {
                            spamDetector.processTestFiles(current);
                        } catch (FileNotFoundException e) {
                            System.err.println("Invalid input dir: " + current.getAbsolutePath());
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        }





    }
