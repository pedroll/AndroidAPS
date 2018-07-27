package info.nightscout.androidaps.queue.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.nightscout.androidaps.Config;
import info.nightscout.androidaps.Constants;
import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.data.PumpEnactResult;
import info.nightscout.androidaps.db.ProfileSwitch;
import info.nightscout.androidaps.db.Source;
import info.nightscout.androidaps.interfaces.PluginType;
import info.nightscout.androidaps.plugins.ConfigBuilder.ConfigBuilderPlugin;
import info.nightscout.androidaps.plugins.SmsCommunicator.SmsCommunicatorPlugin;
import info.nightscout.androidaps.plugins.Treatments.TreatmentsPlugin;
import info.nightscout.androidaps.queue.Callback;

/**
 * Created by mike on 09.11.2017.
 */

public class CommandSetProfile extends Command {
    private Logger log = LoggerFactory.getLogger(Constants.QUEUE);

    private Profile profile;

    public CommandSetProfile(Profile profile, Callback callback) {
        commandType = CommandType.BASALPROFILE;
        this.profile = profile;
        this.callback = callback;
    }

    @Override
    public void execute() {
        if (ConfigBuilderPlugin.getCommandQueue().isThisProfileSet(profile)) {
            if (Config.logQueue)
                log.debug("Correct profile already set. profile: " + profile.toString());
            if (callback != null)
                callback.result(new PumpEnactResult().success(true).enacted(false)).run();
            return;
        }

        PumpEnactResult r = ConfigBuilderPlugin.getActivePump().setNewBasalProfile(profile);
        if (Config.logQueue)
            log.debug("Result success: " + r.success + " enacted: " + r.enacted + " profile: " + profile.toString());
        if (callback != null)
            callback.result(r).run();

        // Send SMS notification if ProfileSwitch is comming from NS
        ProfileSwitch profileSwitch = TreatmentsPlugin.getPlugin().getProfileSwitchFromHistory(System.currentTimeMillis());
        if (r.enacted && profileSwitch.source == Source.NIGHTSCOUT) {
            SmsCommunicatorPlugin smsCommunicatorPlugin = MainApp.getSpecificPlugin(SmsCommunicatorPlugin.class);
            if (smsCommunicatorPlugin != null && smsCommunicatorPlugin.isEnabled(PluginType.GENERAL)) {
                smsCommunicatorPlugin.sendNotificationToAllNumbers(MainApp.gs(R.string.profile_set_ok));
            }
        }
    }

    @Override
    public String status() {
        return "SETPROFILE";
    }
}
