//this is a class for each word in a file, each word has a atring for the word itself,
// ints for how many times it was found in a spam or ham file, doubles to get the ham and spam probabilities of each word
// and a boolean for if the word has been encountered already in the file as to avoid double counting
package sample;

public class Word {
    private String word;
    private int Scount;
    private int Hcount;
    private double Sprob;
    private double Hprob;
    private boolean encountered;

    public Word(String w) {
        word = w;
        Scount = 0;
        Hcount = 0;
        Sprob = 0.0;
        Hprob = 0.0;
        encountered = false;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getScount() {
        return Scount;
    }

    public void setScount(int scount) {
        Scount = scount;
    }

    public int getHcount() {
        return Hcount;
    }

    public void setHcount(int hcount) {
        Hcount = hcount;
    }

    public double getSprob() {
        return Sprob;
    }

    public void setSprob(double sprob) {
        Sprob = sprob;
    }

    public double getHprob() {
        return Hprob;
    }

    public void setHprob(double hprob) {
        Hprob = hprob;
    }

    public boolean isEncountered() {
        return encountered;
    }

    public double getSpamFileProb() {
        //this function gives the probability if the file is spam if it has this word
        //it has been modified to better deal with rare words
        double PreSpamicity = Sprob / (Sprob + Hprob);
        return (3 * 0.5 + (getScount() + getHcount()) * PreSpamicity) / (3 + (getScount() + getHcount()));
    }

    public void setEncountered(boolean encountered) {
        this.encountered = encountered;
    }
}
