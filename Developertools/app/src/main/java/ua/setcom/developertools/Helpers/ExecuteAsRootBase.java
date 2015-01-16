package ua.setcom.developertools.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

/**
 * Created by daniil on 10/23/14.
 */
public abstract class ExecuteAsRootBase {
    public static boolean canRunRootCommands(){
        boolean retval = false;
        Process suProcess;

        try
        {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

            if (null != os && null != osRes)
            {
                // Getting the id of the current user to check if this is root
                os.writeBytes("id\n");
                os.flush();

                String currUid = osRes.readLine();
                boolean exitSu = false;
                if (null == currUid)
                {
                    retval = false;
                    exitSu = false;
                    Log.d("ROOT", "Can't get root access or denied by user");
                }
                else if (true == currUid.contains("uid=0"))
                {
                    retval = true;
                    exitSu = true;
                    Log.d("ROOT", "Root access granted");
                }
                else
                {
                    retval = false;
                    exitSu = true;
                    Log.d("ROOT", "Root access rejected: " + currUid);
                }

                if (exitSu)
                {
                    os.writeBytes("exit\n");
                    os.flush();
                }
            }
        }
        catch (Exception e)
        {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted

            retval = false;
            Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }

        return retval;
    }

    public final boolean execute()
    {
        boolean retval = false;

        try
        {
            ArrayList<String> commands = getCommandsToExecute();
            if (null != commands && commands.size() > 0)
            {
                Process suProcess = Runtime.getRuntime().exec("su");

                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());

                // Execute commands that require root access
                for (String currCommand : commands)
                {
                    os.writeBytes(currCommand + "\n");
                    os.flush();
                }

                os.writeBytes("exit\n");
                os.flush();

                try
                {
                    int suProcessRetval = suProcess.waitFor();
                    if (255 != suProcessRetval)
                    {
                        // Root access granted
                        retval = true;
                    }
                    else
                    {
                        // Root access denied
                        retval = false;
                    }
                }
                catch (Exception ex)
                {
                    Log.e("ROOT", "Error executing root action", ex);
                }
            }
        }
        catch (IOException ex)
        {
            Log.w("ROOT", "Can't get root access", ex);
        }
        catch (SecurityException ex)
        {
            Log.w("ROOT", "Can't get root access", ex);
        }
        catch (Exception ex)
        {
            Log.w("ROOT", "Error executing internal operation", ex);
        }

        return retval;
    }
    protected abstract ArrayList<String> getCommandsToExecute();

    private static int configPort = 5555;

    // adb wifi port, default 5555
    public static int getPort() {
        return configPort;
    }

    // detect if wifi is connected
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (state == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    // detect if adbd is running
    public static boolean getAdbdStatus() {
        int lineCount = 0;
        try {
            Process process = Runtime.getRuntime().exec("ps | grep adbd");
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String str = input.readLine();
            while (str != null) {
                lineCount++;
                str = input.readLine();
            }
            if (lineCount >= 2) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // set adb wifi service
    public static boolean setAdbWifiStatus(boolean status) {
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("setprop service.adb.tcp.port " + String.valueOf(getPort()) + "\n");
            os.writeBytes("stop adbd\n");

            if (status) {
                os.writeBytes("start adbd\n");
            }
            os.writeBytes("exit\n");
            os.flush();

            p.waitFor();
            if (p.exitValue() != 255) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}