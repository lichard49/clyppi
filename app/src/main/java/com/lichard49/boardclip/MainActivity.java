package com.lichard49.boardclip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView appList = (ListView) findViewById(R.id.app_list);
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        String[] packageNames = new String[packages.size()];
        int i = 0;
        Log.d("hiii", "Size: " + packages.size());
        for(ApplicationInfo p: packages)
        {
            Log.d("hiii", p.toString());
            try {
                packageNames[i] = pm.getApplicationLabel(pm.getApplicationInfo(p.processName,
                        PackageManager.GET_META_DATA)).toString();
                //Log.d("hiii", i + ". " + packageNames[i]);
                i++;
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        AppListAdapter adapter = new AppListAdapter(this, packageNames);
        //appList.setAdapter(adapter);
        //String[] codeLearnChapters = new String[] { "Android Introduction","Android Setup/Installation","Android Hello World","Android Layouts/Viewgroups","Android Activity & Lifecycle","Intents in Android"};
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, packageNames);
        appList.setAdapter(adapter);

        startService(new Intent(MainActivity.this, ChatHeadService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class AppListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public AppListAdapter(Context context, String[] values) {
            super(context, R.layout.app_list_row, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.app_list_row, parent, false);
            CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.app_checkbox);
            checkBox.setOnClickListener(onAppSelectedListener);
            checkBox.setText(values[position]);
            for(String badProgram: ChatHeadService.badPrograms) {
                if (values[position] != null && values[position].equals(badProgram))
                {
                    checkBox.setChecked(true);
                    break;
                }
            }
            return rowView;
        }

        private View.OnClickListener onAppSelectedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox c = (CheckBox) v;

                // update something
                String[] newBadPrograms = new String[ChatHeadService.badPrograms.length+1];
                int i = 0;
                for(String s: ChatHeadService.badPrograms)
                {
                    newBadPrograms[i] = s;
                    i++;
                }
                newBadPrograms[ChatHeadService.badPrograms.length] = c.getText().toString();
                ChatHeadService.badPrograms = newBadPrograms;
            }
        };
    }
}
