package sample;

public class Word {
    private String word;
    private int Scount;
    private int Hcount;
    private double Sprob;
    private double Hprob;
    private boolean encountered;

    public Word(String w){
        word = w;
        Scount=0;
        Hcount=0;
        Sprob=0.0;
        Hprob=0.0;
        encountered=false;
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

    public double getSpamFileProb(){
        return Sprob/(Sprob+Hprob);
    }

    public void setEncountered(boolean encountered) {
        this.encountered = encountered;
    }
}
