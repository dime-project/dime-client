package eu.dime.mobile.datamining;

import eu.dime.mobile.service.DataMiningService;
import eu.dime.mobile.utility.Settings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Settings.readPreferences(context);
        if (Settings.isStartCrawlingServiceOnStartup()) {
            Intent startServiceIntent = new Intent(context, DataMiningService.class);
            context.startService(startServiceIntent);
        }

    }
}
