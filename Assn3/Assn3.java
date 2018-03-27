
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.util.TrustManagerUtils;

/**
* FTP client program created using Apache Commons Net.
*
* @author Ryan Riehl <ryanpriehl@gmail.com>
*/
public class Assn3 {

    public static void main(String[] args) throws IOException {

        // Parsing info for connecting to server
        String ip = args[0];
        String id = args[1].substring(0, args[1].indexOf(":"));
        String password = args[1].substring(args[1].indexOf(":") + 1);
        int argNum = args.length - 2;

        // Creating FTP client and connecting to FTP server. Ends program if connecting fails.
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(ip, 21);
        } catch(SocketException e) {
            System.out.println("Socket Exception. Exiting program.");
            return;
        }
        System.out.println("Connected to: " +ip);

        // Logging in to FTP server. Ends program if login fails.
        if(ftp.login(id, password)) {
            System.out.println("Successfully logged in to account: " +id);
        } else {
            System.out.println("Login failed. Exiting program.");
            ftp.disconnect();
            return;
        }

        // Keeps track of the argument command currently being executed.
        int current = 2;

        // Continues while there are command remaining.
        while(argNum - current + 2 > 0) {
            String command = args[current];

            // Checks for ls first since it's the only command without arguments, and so doesn't require additional parsing
            if(command.equals("ls")) {
                System.out.println("\nls:");
                FTPFile[] files = ftp.listFiles();
                for(FTPFile f : files) {
                    System.out.println("\t" +f.getName());
                }
            }

            // Checking if the command was a typo: if it doesn't contain any spaces, and wasn't ls, then it must have been a typo
            else if(command.indexOf(" ") == -1) {
                System.out.println(command +":");
                System.out.println("\tError, unrecognized command: " +command);
            }

            // All the remaining commands: ones that require an extra argument
            else {

                // Splitting the input into its command and parameter parts
                String parameter = command.substring(command.indexOf(" ") + 1, command.length());
                command = command.substring(0, command.indexOf(" "));
                System.out.println("\n" +command +" " +parameter +":");

                switch(command) {

                    // Changes directory
                    case "cd":
                        if(parameter.equals("..")) {
                            if(ftp.changeToParentDirectory()) {
                                System.out.print("\tDirectory changed, ");
                            } else {
                                System.out.print("\tError changing directories, ");
                            }
                        } else {
                            if(ftp.changeWorkingDirectory(parameter)) {
                                System.out.print("\tDirectory changed, ");
                            } else {
                                System.out.print("\tError changing directories, ");
                            }
                        }
                        System.out.println("current directory: " +ftp.printWorkingDirectory());
                        break;

                    // Deletes a file
                    case "delete":
                        if(ftp.deleteFile(parameter)) {
                            System.out.println("\tFile \"" +parameter +"\" deleted.");
                        } else {
                            System.out.println("\tError deleting file \"" +parameter +"\".");
                        }
                        break;

                    // Gets a file or directory.
                    case "get":
                        // Tries changing to the parameter to check if it's a directory.
                        // If it is, gets it recursively with getDir.
                        if(ftp.changeWorkingDirectory(parameter)) {
                            ftp.changeToParentDirectory();
                            if(getDir(ftp, parameter)){
                                System.out.println("\tGot directory \"" +parameter +"\".");
                            } else {
                                System.out.println("\tError getting directory \"" +parameter +"\".");
                            }
                        }
                        // If it's not a directory, then it must be a file
                        else {
                            OutputStream output = new FileOutputStream(parameter);
                            if(ftp.retrieveFile(parameter, output)) {
                                System.out.println("\tGot file \"" +parameter +"\".");
                            } else {
                                System.out.println("\tError getting file \"" +parameter +"\".");
                            }
                            output.close();
                        }
                        break;

                    // Puts a file or directory
                    case "put":
                        File file = new File(parameter);
                        if(!file.exists()){
                            System.out.println("\tThat parameter doesn't exist.");
                        }
                        else {
                            // Putting a file
                            if(file.isFile()) {
                                InputStream input = new FileInputStream(parameter);
                                if(ftp.storeFile(parameter, input)) {
                                    System.out.println("\tPut file \"" +parameter +"\".");
                                } else {
                                    System.out.println("\tError putting file \"" +parameter +"\".");
                                }
                                input.close();
                            }
                            // Putting a directory
                            else {
                                if(putDir(ftp, parameter)) {
                                    System.out.println("\tPut directory \"" +parameter +"\".");
                                } else {
                                    System.out.println("\tError putting directory \"" +parameter +"\".");
                                }
                            }
                        }
                        break;

                    // Makes a new directory
                    case "mkdir":
                        if(ftp.makeDirectory(parameter)) {
                            System.out.println("\tDirectory \"" +parameter +"\" created.");
                        } else {
                            System.out.println("\tError creating directory \"" +parameter +"\".");
                        }
                        break;

                    // Recursively removes a directory
                    case "rmdir":
                        if(removeDir(ftp, parameter)) {
                            System.out.println("\tDirectory \"" +parameter +"\" removed.");
                        }
                        break;

                    default:
                        System.out.println("\tError, unrecognized command: " +command);
                }

            }

            // Increments to move to next command.
            current++;
        }

        // Finished executing all commands. Closing everything.
        System.out.println("\nNo arguments remaining; closing program.");
        ftp.logout();
        System.out.println("\tLogged out of server.");
        ftp.disconnect();
        System.out.println("\tDisconnected from server.");
    }

    // Recursively gets a directory.
    // 1. Checks if the directory already exists locally, if it doesn't it
    // creates the directory, but if it already exits stops the get request.
    // 2. Enter the directory to be gotten and get a list of all the items in it.
    // 3. For each item: if it's a directory call getDir on that directory. If
    // it's a file get the file.
    private static boolean getDir(FTPClient ftp, String parameter) throws IOException {
        // Creating the directory locally if it doesn't already exist.
        File local = new File(parameter);
        if(!local.exists()) {
            local.mkdir();
        } else {
            // Exits routine if the directory already exists, so the command does
            // nothing. Should only happen for the parent directory.
            System.out.println("\tDirectory \"" +parameter +"\" already exists locally.");
            return false;
        }
        // Entering the directory to be gotten.
        ftp.changeWorkingDirectory(parameter.substring(parameter.lastIndexOf("/") + 1, parameter.length()));
        FTPFile[] files = ftp.listFiles();
        for(FTPFile f : files) {
            // Calls getDir on any subdirectories to get them first, otherwise
            // downloads the files normally.
            if(f.isDirectory()) {
                getDir(ftp, parameter +"/" +f.getName());
            } else {
                OutputStream output = new FileOutputStream(local.getAbsolutePath() +"/" +f.getName());
                if(!ftp.retrieveFile(f.getName(), output)) {
                    System.out.println("\tError getting file \"" +f.getName() +"\".");
                }
                output.close();
            }
        }
        return true;
    }

    // Recursively puts a directory.
    // 1. Makes the directory remotely if it doesn't already exist. If it already
    // exists, then it exits and does nothing.
    // 2. Gets all the items in the directory being put.
    // 3. If the item is a directory calls putDir on it, otherwise puts it like
    // a regular file.
    private static boolean putDir(FTPClient ftp, String parameter) throws IOException {
        if(ftp.makeDirectory(parameter)){
            File local = new File(parameter);
            File[] files = local.listFiles();
            for(File f : files){
                if(f.isDirectory()){
                    putDir(ftp, parameter +"/" +f.getName());
                } else {
                    InputStream input = new FileInputStream(parameter +"/" +f.getName());
                    if(!ftp.storeFile(parameter +"/" +f.getName(), input)) {
                        System.out.println("\tError putting file \"" +f.getName() +"\".");
                    }
                    input.close();
                }
            }
        } else {
            System.out.println("\tDirectory \"" +parameter +"\" already exists remotely.");
            return false;
        }
        return true;
    }

    // Recursively removes a directory.
    // 1. Enters the directory to be removed and starts removing the files in it.
    // 2. If it encounters a directory, it calls removeDir on that directory to
    // remove it before continuing.
    // 3. Once all files and directories in the directory are gone, it changes
    // to the parent directory and removes the now empty diectory.
    private static boolean removeDir(FTPClient ftp, String parameter) throws IOException {
        if(ftp.changeWorkingDirectory(parameter)){
            FTPFile[] files = ftp.listFiles();
            for(FTPFile f : files) {
                if(f.isDirectory()) {
                    removeDir(ftp, f.getName());
                } else {
                    ftp.deleteFile(f.getName());
                }
            }
            ftp.changeToParentDirectory();
            ftp.removeDirectory(parameter);
        } else {
            System.out.println("\tCouldn't find directory \"" +parameter +"\".");
            return false;
        }
        return true;
    }

}
