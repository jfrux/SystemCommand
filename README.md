SystemCommand
=============
An alternative to cfexecute for ColdFusion Developers.

Many thanks to Kevan Stannard for originally authoring the Java class.

**NOTE: I've put this project here so I can extend development on it in some areas.**

##Description
Typically when you want to execute a system command you would use the cfexecute tag, but there are some situations when this may not be ideal such as when you want to capture both the 'standard output' and 'error output' streams of the system command.
This is a very simple to use utility that allows you to execute command line programs from within ColdFusion.
Although this was developed specifically for ColdFusion developers to use, it is written entirely in Java.

**Original details on the java class:**
http://stannard.net.au/blog/index.cfm/2008/3/26/Executing-System-Commands-with-ColdFusion

##Requirements
To use this utility you need
* To be running ColdFusion 6.1 or higher
* To be able to register Java .jar files with the ColdFusion administrator (or use a Java class loader utility).

##Installation
* Download the project zip file
* Copy the .jar file into your project wherever you desire.
* Download JavaLoader and load the jar file with JavaLoader.

-or-

* Download the project zip file
* Copy the .jar file to your ColdFusion\lib folder (for example, C:\ColdFusion8\lib)
* Restart your ColdFusion service.
* 





Typically when you want to execute a system command you would use the <cfexecute> tag, but there are some situations when this may not be ideal such as when you want to capture both the "standard output" and "error output" streams of the system command.

Standard Output Stream and Error Output Stream

Many command line programs send output to two data 'streams':
a) Standard output stream
b) Error output stream

When you manually execute a program the command line information sent to either stream is simply displayed on the screen - there is no distinction which stream is being used.

Example

For example, suppose we execute the command to determine the version of java running on your system. You may execute the following command:

java -version
Which on my machine results in:

java version "1.6.0_03"

Java(TM) SE Runtime Environment (build 1.6.0_03-b05)

Java HotSpot(TM) Client VM (build 1.6.0_03-b05, mixed mode)

The problem with <cfexecute>

Suppose you execute the command above using using:

<cfexecute name="java" arguments="-version" timeout="1" variable="output" />
<cfoutput>#output#</cfoutput>
This produces no output!

What happened? Well, cfexecute can only capture output sent to the standard output stream, but not anything sent to the error output stream.

In this example, the java -version command sends all of its output to the error output stream (which seems a bit odd, but it came in useful for this example).

So how can we capture both the standard output and the error output streams?

Using a batch file wrapper

One method of capturing both streams is to put the command inside a batch file and send the two output streams to two files, then read them back in again:

Suppose we have a file javaversion.bat with the content

java -version
Then we run the batch file (on a windows machine) as follows

javaversion.bat 1>stdout.txt 2>errout.txt
This is just the syntax for capturing the standard output (stream 1) and error output (stream 2)

This will result in the creation of two files:
stdout.txt which has no data.
errout.txt which has the version data.

The next step would be to read in the files just created to get the output data.

Using Java

The previous entry on executing system commands described a technique of using inline Java code within ColdFusion, but this technique does not work for all executables.

The problem is that the standard output and error output streams need to be read simultaneously rather than sequentially. We can use a small Java application to help us in achieving this.

Installation

1. Download SystemCommand Component. This contains the Java source code and a systemcommand.jar file.

2. Add the systemcommand.jar file to your "Java class path".

This is done by either

a) Copying the systemcommand.jar file to your C:\ColdFusion8\lib directory (or the equivalent lib directory for your version of ColdFusion).

or

b) Adding the systemcommand.jar file to your Java class path within the ColdFusion administrator,

3. Restart the ColdFusion service so the new .jar file is picked up.

Usage

Once the systemcommand.jar file has been successfully installed, it can be used from within ColdFusion.

The system command object is created using the command:

<cfset syscmd = createObject("java","au.com.webcode.util.SystemCommand").init()>
This object has one function: execute(command,timeout).

<cfset result = syscmd.execute(command,timeout)>
The parameters are:

command	The full command string to execute.
timeout	(Optional) A timeout in milliseconds. The process will be terminated when the timeout is reached and an Exception will be thrown. The default timeout is 10 seconds.
Return Value

The value returned from the execute() function is an object with the following functions:

getCommand()	The original command that was executed.
getStandardOutput()	The standard output produced by the command.
getErrorOutput()	The error output produced by the command.
getExitValue()	An integer value provided by the process that was executed. A value of zero usually indicates that all was fine. A non zero value usually indicates a problem occurred.
Example

A simple example of ColdFusion code to use the system command utility.

<cfset command = "java -version">
<cfset syscmd = createObject("java","au.com.webcode.util.SystemCommand").init()>
<cfset result = syscmd.execute(command)>
<cfoutput>
   Command: #result.getCommand()#<br />
   ExitValue: #result.getExitValue()#<br />
   Error Output: #result.getErrorOutput()#<br />
   Standard Output: #result.getStandardOutput()#<br />
</cfoutput>
