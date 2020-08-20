An Implementation of LineSets in Processing/Java
=========

![inaction](https://github.com/dtwelch/Line-Sets/tree/master/images/linesets.png)

This small project is an implementation of a set based visualization technique known as LineSets. For more 
information on LineSets, see:

    http://research.microsoft.com/en-us/um/people/nath/docs/linesets_infovis2011.pdf
    
Running the Visualization
=========

The easiest way to get this running at the moment is the following:

1. Download [IntelliJ Community Edition](https://www.jetbrains.com/idea/download/#section=mac)

2. Clone the this repo to a directory of your choosing (I'm using `~/Documents`)

3. Fire up IntelliJ and click 'open' when the intro screen arrives:
![inaction](https://github.com/dtwelch/Line-Sets/blob/master/images/01.png)

4. Navigate to the directory where the repository was cloned and select the `Line-Sets` folder:
![inaction](https://github.com/dtwelch/Line-Sets/blob/master/images/02.png)

5. Once opened, IntelliJ will ask if you want to make this a maven project; do it, then IntelliJ
should start downloading dependencies

6. Once this is done, you'll notice a number of errors persist. To resolve these, we'll need to manually add
some library `.jar` files that are not available on the maven central repo. To do this, first click the 
project structure button (identified with the arrow):
![inaction](https://github.com/dtwelch/Line-Sets/blob/master/images/03.png)

7. Next, go down to area named 'global libraries' and press the plus button (after pressing, select Java)
![inaction](https://github.com/dtwelch/Line-Sets/blob/master/images/04.png)



8. Once you've clicked 'Java' a file browser will open. Navigate to the `libs` folder (within the repo) and select one of 
the `.jar` files, then press OK (and you may be prompted again, so just click ok). 
You should now see the jar you selected in the global libraries right hand pane (now follow the same process for the other jar). 
When you're done, it should look like this:

![inaction](https://github.com/dtwelch/Line-Sets/blob/master/images/05.png)

9. After you've clicked ok and libraries window has closed, the IDE shouldn no longer be reporting errors (i.e.: no red-squiggly lines) 
in the `src/main/java/setvis/LineSets.java` file; make sure this is the case before proceeding.

10. Finally, to run it, we must create a 'run-configuration'. To do this, at the top of the 
IDE's main pane you should see a box titled: "Add Configuration". Click it and then follow the steps numbered:

![inaction](https://github.com/dtwelch/Line-Sets/blob/master/images/06.png)

11. We now have an empty run configuration (you can name it if you like), before closing we need to identify the 
class which will execute our program (i.e., the one titled Main.java):

![inaction](https://github.com/dtwelch/Line-Sets/blob/master/images/07.png)

After this you can run the program by pressing the green play button at the top of the IDE's pane. 

Some Points to be Aware of
=========

* This project is far from done (and I'm not actively working on it). That said, feel free to clone or fork. 
It's pretty much a prototype/proof-of-concept that uses a lineset visualization technique for  
easily grouping and identifying downtown seattle restaurants based on some number of characteristics (such as rating).

* Don't use the trackpad on your laptop to navigate the map; use the arrow keys to pan and the 
+, - keys to zoom in and out (respectively) 
 
* The button UI at the top may appear pixelated if you have a high resolution display 
(this may be due to the age of the library being used, but I could be wrong)

* If you do work on this, try to eliminate the `.jar`s that have to be manually installed. 
It's far preferable to have maven take care of that stuff for you. This change alone would eliminate steps 6-9 above.

 
