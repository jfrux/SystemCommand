/**
 * SystemCommand.java
 * 
 * Allows execution of system commands and provides access to both
 * the standard output stream and error output stream.
 * 
 * Copyright (c) 2008, Kevan Stannard
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Example usage:
 * 
 *   <cfset cmd = createObject("java","webcode.util.SystemCommand").init()>
 *   <cfset command = "d:\bin\dosomething.exe -x -j -verbose">
 *   <cfset result = cmd.execute(command)>
 *   <cfoutput>
 *       Command: #result.getCommand()#<br />
 *       ExitValue: #result.getExitValue()#<br />
 *       Error Output: #result.getErrorOutput()#<br />
 *       Standard Output: #result.getStandardOutput()#<br />
 *   </cfoutput>
 * 
 * Return value:
 * 
 *   The result is a struct which always contains the following elements
 * 
 *   command         - The original command that was executed.
 *   standardOutput  - The data sent to standard output.
 *   errorOutput     - The data sent to error output. 
 *   exitValue       - The exit value returnd by the execution process.
 *                   A value of 0 typically indicates success,
 *                   a non zero value typically indicates an error. 
 */

package au.com.webcode.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.System;
import java.io.File;
import java.nio.channels.Channels;
import java.util.Timer;
import java.util.TimerTask;

public class SystemCommand {
	private String defaultTimeout = "10000"; // 10 seconds, must be a string

	/**
	 * Simply notifies the calling Interrupter object when
	 * the timeout has expired.
	 */
	private class TimeoutTask extends TimerTask {
		private Interrupter interrupter;
		public TimeoutTask(Interrupter interrupter) {
			this.interrupter = interrupter;
		}
		public void run() {
		    interrupter.timeout();
		}
	}

	/**
	 * Stops a process after a delay period.
	 */
	private class Interrupter {
		Timer timer = null;
		boolean timeout = false;
		Process proc = null;
		public Interrupter(Process proc, int delay) {
			this.proc = proc;
		    this.timer = new Timer();
		    this.timer.schedule(new TimeoutTask(this), delay);
		}
		public void timeout() {
			timeout = true;
			proc.destroy();
		}
		public boolean timeoutOccurred() {
			return timeout;
		}
		public void cancel() {
			timer.cancel();
		}
	}

	/**
	 * Reads an text input stream in a separate thread.
	 */
	private class StreamReader extends Thread {
		private InputStream is = null;
		private InputStreamReader isr = null;
		private StringBuffer sb = new StringBuffer();
		StreamReader(InputStream is) {
			this.is = is;
		}
		public String getOutput() {
			return sb.toString();
		}
		public void close() throws IOException {
			isr.close();
		}
		public void run() {
			try {
				isr = new InputStreamReader(Channels.newInputStream(Channels.newChannel(is)));
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while (true) {
					line = br.readLine();
					if (line == null) {
						break;
					}
					sb.append(line);
					sb.append(System.getProperty("line.separator"));
				}
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Executes the specified command using the default timeout.
	 * 
	 * @param cmd				The command to execute
	 * @return					A SystemCommandResult object
	 */
	public SystemCommandResult execute(String cmd) throws Exception {
		return execute(cmd,defaultTimeout,null);
	}

	/**
	 * Executes the specified command using the specified timeout.
	 * 
	 * @param cmd				The command to execute
	 * @param timeoutAsString	The timeout in milliseconds specified as a string. We use a string here to better suit caling this method from with ColdFusion.
	 * @param cwd 				The current working directory to execute the command in.
	 * @return					A SystemCommandResult object
	 */
	public SystemCommandResult execute(String cmd, String timeoutAsString, String cwd) throws InterruptedException, IOException {
		int timeout = Integer.parseInt(timeoutAsString);
		
		Process proc = Runtime.getRuntime().exec(cmd,null,new File(cwd));
		
		StreamReader errorReader = new StreamReader(proc.getErrorStream());
		StreamReader outputReader = new StreamReader(proc.getInputStream());
		Interrupter errorInterrupter = new Interrupter(proc,timeout);
		Interrupter outputInterrupter = new Interrupter(proc,timeout);
		errorReader.start();
		outputReader.start();
		int exitValue = proc.waitFor();
		errorInterrupter.cancel();
		outputInterrupter.cancel();
		if (errorInterrupter.timeoutOccurred() || outputInterrupter.timeoutOccurred()) {
			throw new InterruptedException("Timeout occurred waiting for the process to finish.");
		}
		SystemCommandResult result = new SystemCommandResult(cmd, String.valueOf(exitValue), outputReader.getOutput(), errorReader.getOutput());
		return result;
	}

}

