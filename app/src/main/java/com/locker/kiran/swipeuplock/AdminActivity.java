package com.locker.kiran.swipeuplock;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Kiran on 20-07-2015.
 */
public class AdminActivity extends DeviceAdminReceiver {

    final static String IS_ENABLED = "isEnabled";
    final static boolean DEBUG = false;

    public static class Controller extends Activity {

        DevicePolicyManager mDPM;
        ComponentName mAdminName;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            applyLock();
        }

        @Override
        protected void onResume() {
            super.onResume();
            applyLock();
        }

        private void applyLock() {
            SharedPreferences pref = getSharedPreferences(IS_ENABLED, MODE_PRIVATE);
            boolean isAdmin = pref.getBoolean(IS_ENABLED, false);
            if(DEBUG){
                Toast.makeText(this,"isAdmin:"+isAdmin,Toast.LENGTH_SHORT).show();
            }
            if (isAdmin) {
                mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                mAdminName = new ComponentName(Controller.this,
                        AdminActivity.class);

                if (mDPM != null && mDPM.isAdminActive(mAdminName)) {
                    mDPM.lockNow();
                }
                if(DEBUG){
                    Toast.makeText(this,"Finishing",Toast.LENGTH_SHORT).show();
                }
                finish();
            }else
            {
                if(DEBUG){
                    Toast.makeText(this,"Not an admin",Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this,"Please activate SwipeUpLock as an Administrator",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings"));
                startActivity(intent);
            }
        }

    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        // admin rights
        //getSharedPreferences().edit().putBoolean(App.ADMIN_ENABLED, true).commit(); //App.getPreferences() returns the sharedPreferences
        SharedPreferences.Editor editor = context.getSharedPreferences(IS_ENABLED, context.MODE_PRIVATE).edit();
        editor.putBoolean(IS_ENABLED, true);
        editor.commit();
        if(DEBUG){
            Toast.makeText(context,"onEnabled",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "SwipeUpLock will not work unless it is an Administrator!";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        // admin rights removed
        SharedPreferences.Editor editor = context.getSharedPreferences(IS_ENABLED, context.MODE_PRIVATE).edit();
        editor.putBoolean(IS_ENABLED, false);
        editor.commit();
        if(DEBUG){
            Toast.makeText(context,"onDisabled",Toast.LENGTH_SHORT).show();
        }
    }


}
