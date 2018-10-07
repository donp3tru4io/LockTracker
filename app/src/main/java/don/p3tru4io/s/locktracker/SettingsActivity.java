package don.p3tru4io.s.locktracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {

    CheckBoxPreference cnScreenOn,cnUserPresents,cnFailedPassword,
                        ctScreenOn,ctUserPresents,ctFailedPassword,
                        cTakePhoto;

    /*public static final String APP_PREFERENCES = "don.p3tru4io.s.locktracker_preferences";
    public static final String CN_SCREEN_ON = "cnScreenOn";
    public static final String CN_USER_PRESENTS = "cnUserPresents";
    public static final String CN_FAILED_PASSWORD = "cnFailedPassword";
    public static final String CT_SCREEN_ON = "ctScreenOn";
    public static final String CT_USER_PRESENTS = "ctUserPresents";
    public static final String CT_FAILED_PASSWORD = "ctFailedPassword";
    public static final String C_TAKE_PHOTO = "cTakePhoto";

    public static final String CN_SCREEN_ON = "screen_on_notify";
    public static final String CN_USER_PRESENTS = "user_presents_notify";
    public static final String CN_FAILED_PASSWORD = "failed_password_notify";
    public static final String CT_SCREEN_ON = "screen_on_track";
    public static final String CT_USER_PRESENTS = "user_presents_track";
    public static final String CT_FAILED_PASSWORD = "failed_password_track";
    public static final String C_TAKE_PHOTO = "take_photo";*/

    //SharedPreferences mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar)LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
        bar.setTitle(getApplicationContext().getResources().getString(R.string.title_activity_settings));
        bar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        root.addView(bar, 0); // insert at top
        addPreferencesFromResource(R.xml.pref_general);

        /*mSettings = getSharedPreferences(APP_PREFERENCES, getApplicationContext().MODE_PRIVATE);

        cnScreenOn = (CheckBoxPreference) findPreference("screen_on_notify");
        cnUserPresents = (CheckBoxPreference) findPreference("user_presents_notify");
        cnFailedPassword =(CheckBoxPreference) findPreference("failed_password_notify");
        ctScreenOn = (CheckBoxPreference) findPreference("screen_on_track");
        ctUserPresents = (CheckBoxPreference) findPreference("user_presents_track");
        ctFailedPassword = (CheckBoxPreference) findPreference("failed_password_track");
        cTakePhoto = (CheckBoxPreference) findPreference("take_photo");*/

        /*cnScreenOn.setChecked(mSettings.getBoolean(CN_SCREEN_ON, true));
        cnUserPresents.setChecked(mSettings.getBoolean(CN_USER_PRESENTS, true));
        cnFailedPassword.setChecked(mSettings.getBoolean(CN_FAILED_PASSWORD, true));
        ctScreenOn.setChecked(mSettings.getBoolean(CT_SCREEN_ON, true));
        ctUserPresents.setChecked(mSettings.getBoolean(CT_USER_PRESENTS, true));
        ctFailedPassword.setChecked(mSettings.getBoolean(CT_FAILED_PASSWORD, true));
        cTakePhoto.setChecked(mSettings.getBoolean(C_TAKE_PHOTO,true));*/


        /*cnScreenOn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (cnScreenOn.isChecked())
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CN_SCREEN_ON, true);
                    editor.apply();
                }
                else
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CN_SCREEN_ON, false);
                    editor.apply();
                }
                return true;
            }
        });

        cnUserPresents.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (cnUserPresents.isChecked())
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CN_USER_PRESENTS, true);
                    editor.apply();
                }
                else
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CN_USER_PRESENTS, false);
                    editor.apply();
                }
                return true;
            }
        });

        cnFailedPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (cnFailedPassword.isChecked())
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CN_FAILED_PASSWORD, true);
                    editor.apply();
                }
                else
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CN_FAILED_PASSWORD, false);
                    editor.apply();
                }
                return true;
            }
        });

        ctScreenOn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (ctScreenOn.isChecked())
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CT_SCREEN_ON, true);
                    editor.apply();
                }
                else
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CT_SCREEN_ON, false);
                    editor.apply();
                }
                return true;
            }
        });

        ctUserPresents.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (ctUserPresents.isChecked())
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CT_USER_PRESENTS, true);
                    editor.apply();
                }
                else
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CT_USER_PRESENTS, false);
                    editor.apply();
                }
                return true;
            }
        });

        ctFailedPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (ctFailedPassword.isChecked())
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CT_FAILED_PASSWORD, true);
                    editor.apply();
                }
                else
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(CT_FAILED_PASSWORD, false);
                    editor.apply();
                }
                return true;
            }
        });

        cTakePhoto.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (cTakePhoto.isChecked())
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(C_TAKE_PHOTO, true);
                    editor.apply();
                }
                else
                {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(C_TAKE_PHOTO, false);
                    editor.apply();
                }
                return true;
            }
        });*/
    }

}
