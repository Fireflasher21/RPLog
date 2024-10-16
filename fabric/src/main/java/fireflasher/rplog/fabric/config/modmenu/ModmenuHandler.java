package fireflasher.rplog.fabric.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import fireflasher.rplog.config.screens.options.*;

public class ModmenuHandler implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return Optionsscreen::new;
    }
}
