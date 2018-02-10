package de.baumann.browser.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import de.baumann.browser.Activity.JavascriptActivity;
import de.baumann.browser.Activity.WhitelistActivity;
import de.baumann.browser.Ninja.R;
import de.baumann.browser.Task.ExportBookmarksTask;
import de.baumann.browser.Task.ExportWhitelistJSTask;
import de.baumann.browser.Task.ExportWhitelistTask;
import de.baumann.browser.Task.ImportBookmarksTask;
import de.baumann.browser.Task.ImportWhitelistTask;
import de.baumann.browser.Task.ImportWhitelistTaskJS;
import de.baumann.browser.View.NinjaToast;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Fragment_settings_data extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_data);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getTitleRes()) {
            case R.string.setting_title_whitelist:
                Intent toWhitelist = new Intent(getActivity(), WhitelistActivity.class);
                getActivity().startActivity(toWhitelist);
                break;
            case R.string.setting_title_whitelistJS:
                Intent toJavascript = new Intent(getActivity(), JavascriptActivity.class);
                getActivity().startActivity(toJavascript);
                break;
            case R.string.setting_title_export_whitelist:
                new ExportWhitelistTask(getActivity()).execute();
                break;
            case R.string.setting_title_import_whitelist:
                new ImportWhitelistTask(getActivity()).execute();
                break;

            case R.string.setting_title_export_whitelistJS:
                new ExportWhitelistJSTask(getActivity()).execute();
                break;
            case R.string.setting_title_import_whitelistJS:
                new ImportWhitelistTaskJS(getActivity()).execute();
                break;
            case R.string.setting_title_export_bookmarks:
                new ExportBookmarksTask(getActivity()).execute();
                break;
            case R.string.setting_title_import_bookmarks:
                new ImportBookmarksTask(getActivity()).execute();
                break;

            case R.string.setting_title_export_database:
                try {

                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();

                    if (sd.canWrite()) {
                        String currentDBPath = "//data//de.baumann.browser//databases//Ninja4.db";
                        String backupDBPath = "Browser.db";

                        File currentDB = new File(data, currentDBPath);
                        File backupDB = new File(sd, backupDBPath);

                        Log.d("backupDB path", "" + backupDB.getAbsolutePath());

                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                            NinjaToast.show(getActivity(), getActivity().getString(R.string.toast_export_successful) + "Browser.db");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.string.setting_title_import_database:

                final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
                View dialogView = View.inflate(getActivity(), R.layout.dialog_action, null);
                TextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.hint_database);
                Button action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        try {

                            File sd = Environment.getExternalStorageDirectory();
                            File data = Environment.getDataDirectory();

                            if (sd.canWrite()) {

                                String backupDBPath = "//data//de.baumann.browser//databases//Ninja4.db";
                                String currentDBPath = "Browser.db";

                                File currentDB = new File(sd, currentDBPath);
                                File backupDB = new File(data, backupDBPath);

                                Log.d("backupDB path", "" + backupDB.getAbsolutePath());

                                if (currentDB.exists()) {
                                    FileChannel src = new FileInputStream(currentDB).getChannel();
                                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                    dst.transferFrom(src, 0, src.size());
                                    src.close();
                                    dst.close();

                                    final SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
                                    final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
                                    View dialogView = View.inflate(getActivity(), R.layout.dialog_action, null);
                                    TextView textView = dialogView.findViewById(R.id.dialog_text);
                                    textView.setText(R.string.toast_restart);
                                    Button action_ok = dialogView.findViewById(R.id.action_ok);
                                    action_ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sp.edit().putInt("restart_changed", 1).apply();
                                            getActivity().finish();
                                        }
                                    });
                                    Button action_cancel = dialogView.findViewById(R.id.action_cancel);
                                    action_cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.cancel();
                                        }
                                    });
                                    dialog.setContentView(dialogView);
                                    dialog.show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                Button action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.setContentView(dialogView);
                dialog.show();

                break;

            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
