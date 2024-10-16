package fireflasher.rplog.fabric.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import fireflasher.rplog.config.screens.options.*;

public class ModmenuHandler implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        #if MC_1_18_2 || MC_1_19_2
        return Optionsscreen_1_18_2::new;
        #elif MC_1_20_4
        return Optionsscreen_1_20_4::new;
        #endif
    }
}
