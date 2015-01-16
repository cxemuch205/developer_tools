package ua.setcom.developertools.Helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import ua.setcom.developertools.Constants.App;
import ua.setcom.developertools.Models.Operation;
import ua.setcom.developertools.R;

/**
 * Created by daniil on 10/23/14.
 */
public class ControlOperation {

    private static final String TAG = "ControlOperation";
    private static ArrayList<Operation> listOperation = new ArrayList<Operation>();

    public static void operation(final Context context, final ControlButtons controlButtons, Object data) {
        updateThisOperationFromList(controlButtons.id, data);
        boolean oper, curstateB;
        int oper1, curState1;
        switch (controlButtons.id) {
            case R.id.tb_show_layout_bounds:
                Log.d(TAG, "Click show layout bounds: " + data);
                oper = (Boolean) data;
                curstateB = SystemPropertiesProxy.getBoolean(context, App.NamePref.SHOW_LAYOUT_BOUNDLS, false);
                if(oper == curstateB)
                    break;
                else
                    SystemPropertiesProxy.set(context, App.NamePref.SHOW_LAYOUT_BOUNDLS, String.valueOf(oper));

                break;
            case R.id.tb_show_touchebles:
                Log.d(TAG, "Click show touchable: " + data);
                oper = (Boolean) data;
                oper1 = oper ? 1 : 0;
                curState1 = SystemPropertiesProxy.getInt(context, App.NamePref.SHOW_TOUCHES, 0);
                curstateB = curState1 == 0 ? false : true;
                if(oper == curstateB)
                    break;
                else
                    SystemPropertiesProxy.set(context, App.NamePref.SHOW_LAYOUT_BOUNDLS, String.valueOf(oper1));

                break;
            case R.id.btn_open_dev_tool:
                Log.d(TAG, "Click open dev tools: " + data);
                Intent startDevTools = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                startDevTools.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startDevTools);
                break;
            case R.id.tb_enable_adb_wifi:
                Log.d(TAG, "Click ENABLE ADB WiFi: " + data);
                final boolean enableADBWiFi = (Boolean) data;
                if(controlButtons.object != null)
                    ((View)controlButtons.object).setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (ExecuteAsRootBase.isWifiConnected(context)) {
                            // try switch
                            try {
                                boolean ret = ExecuteAsRootBase.setAdbWifiStatus(enableADBWiFi);
                                Log.d(TAG, "RESPONSE ON REQUEST PERMISSION: " + ret);
                                if (ret) {
                                    // switch successfully
                                    if (enableADBWiFi) {
                                        Toast.makeText(context, "adb wifi service started", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "adb wifi service stopped", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // failed
                                    Toast.makeText(context, "something wrong", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {}
                        }
                    }
                }).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(controlButtons.object != null)
                            ((View)controlButtons.object).setEnabled(true);
                        if(controlButtons.tvInfo != null && enableADBWiFi)
                            controlButtons.tvInfo.setText(Tools.getLocalIpv4Address());
                    }
                }, 1000);
                if (!enableADBWiFi) {
                    if(controlButtons.tvInfo != null)
                        controlButtons.tvInfo.setText(context.getString(R.string.no_connected));
                }
                break;
        }
    }

    private static void updateThisOperationFromList(int id, Object data) {
        Operation operation = new Operation();
        operation.id = id;
        operation.data = data;
        boolean isUniq = false;
        for (int i = 0; i < listOperation.size(); i++) {
            Operation item = listOperation.get(i);
            if (item.id == operation.id) {
                item.data = operation.data;
                isUniq = true;
            }
        }
        if (isUniq) {
            listOperation.add(operation);
        }
    }
}
