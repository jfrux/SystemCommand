SystemCommand
=============
An alternative to cfexecute for ColdFusion Developers.

Originally written by Kevan Stannard

**NOTE: I've put this project here so I can extend development on it in some areas.**

###Description:
Typically when you want to execute a system command you would use the cfexecute tag, but there are some situations when this may not be ideal such as when you want to capture both the 'standard output' and 'error output' streams of the system command.
This is a very simple to use utility that allows you to execute command line programs from within ColdFusion.
Although this was developed specifically for ColdFusion developers to use, it is written entirely in Java.

**More details over here:**
http://stannard.net.au/blog/index.cfm/2008/3/26/Executing-System-Commands-with-ColdFusion

###Requirements:
To use this utility you need
* To be running ColdFusion 6.1 or higher
* To be able to register Java .jar files with the ColdFusion administrator (or use a Java class loader utility).

###INSTALLATION
* Download the project zip file
* Copy the .jar file into your project wherever you desire.
* Download JavaLoader and load the jar file with JavaLoader.

-or-

* Download the project zip file
* Copy the .jar file to your ColdFusion\lib folder (for example, C:\ColdFusion8\lib)
* Restart your ColdFusion service.

###USAGE

