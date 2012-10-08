/*
 * SystemCommandResult.java
 * 
 * Provides a simple object with the results of a system command execution.
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
 */

package au.com.webcode.util;

public class SystemCommandResult {
	private String command = "";
	private String stdout = "";
	private String errout = "";
	private String exitValue = "";
	public SystemCommandResult(String command, String exitValue, String stdout, String errout) {
		this.command = command;
		this.exitValue = exitValue;
		this.stdout = stdout;
		this.errout = errout;
	}
	public String getCommand() { return command; };
	public String getExitValue() { return exitValue; };
	public String getStandardOutput() { return stdout; };
	public String getErrorOutput() { return errout; };
}
