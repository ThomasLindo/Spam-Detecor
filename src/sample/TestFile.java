//this is a class to keep track of each test file covered.
//this class is identical to the one provided in the assignment doc
package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.DecimalFormat;

public class TestFile {
    //Strings for the file name and actual class, as well as a double for spam probability
    private SimpleStringProperty filename;
    private SimpleDoubleProperty spamProbability;
    private SimpleStringProperty actualClass;

    public TestFile(String filename,
                    double spamProbability,
                    String actualClass) {
        this.filename = new SimpleStringProperty(filename);
        this.spamProbability = new SimpleDoubleProperty(spamProbability);
        this.actualClass = new SimpleStringProperty(actualClass);
    }

    public String getFilename() {
        return this.filename.get();
    }

    public double getSpamProbability() {
        return this.spamProbability.get();
    }

    public String getActualClass() {
        return this.actualClass.get();
    }

    public void setFilename(String value) {
        this.filename.set(value);
    }

    public void setSpamProbability(double val) {
        this.spamProbability.set(val);
    }

    public void setActualClass(String value) {
        this.actualClass.set(value);
    }
}

