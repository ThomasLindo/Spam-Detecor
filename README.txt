Hi there to whoever may be reading this.

So here's the thing... My spam detector has only been tested in Intellij and I doubt it will run
on command line or gradle so please use Intellij for testing.

That being said, here are the run configurations you will need:
(hit edit configurations in the dropdown list next to the run button)
- give the run configuration a name (I used "Main" for default)
- the Main Class should be set to sample.Main
- the folder that conatins the Test and Train folders should be in the same folder as the src folder
then type the name of that folder in the Program Arguments area (just the name of the folder, nothing else)
the "files" folder included contains the data supplied to us for the assignment and you can use that for testing
- the use classpath of module should be set to SpamDetector
- JRE should be 9.0 - SDK or the defualt setting
- leave the other areas blank

Once you've done all that hit apply, set the dropdown to the name of the run configuration 
and then the run button to run the program

Hopefuly everything runs smoothly and I hope it meets your expectations.

Thomas Lindo (100587671)