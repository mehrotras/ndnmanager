package edu.ucla.ndn.ndnui;

/**
 * Created by SMehrotra on 1/11/2015.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class CommonUtil {

    private static int nfd_process_id;
    public final static String NiceCMD = "/data/data/edu.ucla.ndn.ndnui/nice";
    public final static String NFDStartCMD1 = "export HOME=/data/local/nfd-android; $HOME/bin/nfd --config $HOME/etc/ndn/nfd.conf &";
    public final static String NFDStopCMD = "export pid=`ps | grep nfd | awk 'NR==1{print $1}' | cut -d' ' -f1`;kill $pid";
    public static Random RandomGen = new Random();
    private int BUFFER = 80;
    private static final String CLASS_TAG = "CommonUtil";


    public static int getNfd_process_id() {
        return nfd_process_id;
    }


    public static boolean checkExtraStore(PreferenceActivity activity) {
        boolean flag = false;
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            // use Reflection to avoid errors (for cupcake 1.5)
            Method MethodList[] = activity.getClass().getMethods();
            for (int checkMethod = 0; checkMethod < MethodList.length; checkMethod++) {
                if (MethodList[checkMethod].getName().indexOf("ApplicationInfo") != -1) {
                    try {
                        if ((((ApplicationInfo) MethodList[checkMethod].invoke(activity, new Object[]{})).flags & 0x40000 /* ApplicationInfo.FLAG_EXTERNAL_STORAGE*/) != 0)
                            flag = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return flag;
    }

    public static int getSDKVersion() {
        return Integer.parseInt(Build.VERSION.SDK);
    }


    // Screen Size
    private static int ScreenSize = 1; /* 0 == Small, 1 == Normal, 2 == Large */

    public static void detectScreen(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int lanscapeHight = 0;
        if (activity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT)
            lanscapeHight = metrics.heightPixels;
        else
            lanscapeHight = metrics.widthPixels;

        if (lanscapeHight >= 800)
            ScreenSize = 2;
        else if (lanscapeHight <= 320)
            ScreenSize = 0;
        else
            ScreenSize = 1;
    }

    public static int getScreenSize() {
        return ScreenSize;
    }

    // Gesture Threshold
    public static final int SWIPE_MIN_DISTANCE = 120;
    public static final int SWIPE_MAX_OFF_PATH = 250;
    public static final int SWIPE_THRESHOLD_VELOCITY = 200;


    public static String execCommand(String command) throws Exception {
        Process shProc = null;
        try {
            shProc = Runtime.getRuntime().exec("su");
            DataOutputStream InputCmd = new DataOutputStream(shProc.getOutputStream());
            Log.d(CLASS_TAG, " su process SU returned " + shProc.toString() + " - " + command);
            InputCmd.writeBytes(command);
            Log.d(CLASS_TAG, " su process WB returned " + InputCmd.toString());
            // Close the terminal
            InputCmd.writeBytes("exit\n");
            InputCmd.flush();
            InputCmd.close();

            try {
                shProc.waitFor();
            } catch (InterruptedException e) {
                throw new Exception(e);
            } finally {
                Log.d(CLASS_TAG, " su process WF returned " + shProc.exitValue());
            }
        } catch (IOException e) {
            throw new Exception(e);
        } finally {

            Log.d(CLASS_TAG, " su process finally returned " + shProc.toString());
        }
        nfd_process_id = Integer.parseInt(shProc.toString().substring(shProc.toString().indexOf("pid") + 4, shProc.toString().length() - 1));
        return shProc.toString();
    }

    private static void exeCmd() throws IOException, InterruptedException {
        //Build command
        List<String> commands = new ArrayList<String>();
        commands.add("su");
        //commands.add("-c");
        //Add arguments
        commands.add(NFDStartCMD1);
        // commands.add(NFDStartCMD2);
        Log.d(CLASS_TAG, commands.toString());

        //Run macro on target
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File("."));
        pb.redirectErrorStream(true);
        Process process = null;
        try {
            process = pb.start();
        } catch (Exception e) {
            // e.printStackTrace();
            Log.e(CLASS_TAG, e.getMessage());
        }
        //Read output
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null, previous = null;
        while ((line = br.readLine()) != null)
            if (!line.equals(previous)) {
                previous = line;
                out.append(line).append('\n');
                Log.d(CLASS_TAG, line);
            }

        //Check result
        if (process.waitFor() == 0) {
            Log.d(CLASS_TAG, "Success!");
            //System.exit(0);
        }

        //Abnormal termination: Log command parameters and output and throw ExecutionException
        Log.e(CLASS_TAG, commands.toString());
        Log.e(CLASS_TAG, out.toString());
        //System.exit(1);

    }

    public static ArrayList<String> command(final String cmdline,
                                            final String directory) {
        try {
            Process process =
                    new ProcessBuilder(new String[]{"su", "-c" + cmdline})
                            .redirectErrorStream(true)
                            .directory(new File(directory))
                            .start();

            ArrayList<String> output = new ArrayList<String>();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null)
                output.add(line);

            //There should really be a timeout here.
            // if (0 != process.waitFor())
            //  return null;

            return output;

        } catch (Exception e) {
            //Warning: doing this is no good in high quality applications.
            //Instead, present appropriate error messages to the user.
            //But it's perfectly fine for prototyping.

            Log.e(CLASS_TAG, e.getMessage());
            return null;
        }
    }

    private static String executeCommand(String command) throws Exception {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            Log.d(CLASS_TAG, p.toString() + " - " + output);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return output.toString();

    }

    private static Handler EndHelper = new Handler() {
        public void handleMessage(Message msg) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };

    public static void kill(Context target, String packageName) {
        if (CommonUtil.getSDKVersion() <= 7) {
            ((ActivityManager) target.getSystemService(Context.ACTIVITY_SERVICE))
                    .killBackgroundProcesses(packageName);
        } else {
            EndHelper.sendEmptyMessageDelayed(0, 500);
        }

    }

    public static int getAppId(Context ctx, String pName) {
        int pId = 0;
        ActivityManager am = (ActivityManager) ctx.getSystemService(ctx.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = ctx.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            Log.d("Process", "Id: " + info.pid + " ProcessName: " + info.processName + "  Label: ");
            try {
                if (info.processName == pName) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    Log.d("Process", "Id: " + info.pid + " ProcessName: " + info.processName + "  Label: " + c.toString());
                    //processName = c.toString();
                    pId = info.pid;
                }
            } catch (Exception e) {
                Log.e("Process", "Error>> :" + e.toString());
            }
        }
        return pId;
    }

    public static void CheckNice(AssetManager Asset) throws Exception {
        try {
            InputStream bNiceIn = Asset.open("nice");
            OutputStream bNiceOut = new FileOutputStream(NiceCMD);


            // Transfer bytes from in to out
            byte[] bTransfer = new byte[1024];
            int bTransferLen = 0;
            while ((bTransferLen = bNiceIn.read(bTransfer)) > 0) {
                bNiceOut.write(bTransfer, 0, bTransferLen);
            }

            bNiceIn.close();
            bNiceOut.close();

            CommonUtil.execCommand("chmod 755 " + NiceCMD + "\n");

        } catch (IOException e) {
            throw e;
        }
    }

    static void test(String cmdline) {
        ArrayList<String> output = command(cmdline, ".");
        if (null == output)
            System.out.println("\n\n\t\tCOMMAND FAILED: " + cmdline);
        else
            for (String line : output)
                System.out.println(line);

    }


    public void zip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unzip(String _zipFile, String _targetLocation) {

        //create target location folder if not exist
        //dirChecker(_targetLocatioan);

        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    //dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String executeCommand(String command, boolean waitForResponse) {

        String response = "";

        ProcessBuilder pb = new ProcessBuilder("su", "-c", command);
        pb.redirectErrorStream(true);
        pb.directory(new File("."));
        System.out.println("Linux command: " + command);

        try {
            Process shell = pb.start();

            if (waitForResponse) {

// To capture output from the shell
                InputStream shellIn = shell.getInputStream();

// Wait for the shell to finish and get the return code
                int shellExitStatus = shell.waitFor();
                System.out.println("Exit status" + shellExitStatus);

                response = convertStreamToStr(shellIn);

                shellIn.close();
            }

        } catch (IOException e) {
            System.out.println("Error occured while executing Linux command. Error Description: "
                    + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Error occured while executing Linux command. Error Description: "
                    + e.getMessage());
        }

        return response;
    }

/*
* To convert the InputStream to String we use the Reader.read(char[]
* buffer) method. We iterate until the Reader return -1 which means
* there's no more data to read. We use the StringWriter class to
* produce the string.
*/

    public static String convertStreamToStr(InputStream is) throws IOException {

        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is,
                        "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}