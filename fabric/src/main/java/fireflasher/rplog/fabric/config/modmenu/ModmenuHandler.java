package fireflasher.rplog.fabric.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import fireflasher.rplog.fabric.config.screens.Optionsscreen_1_18_2;

public class ModmenuHandler implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        #if MC_1_18_2
        return Optionsscreen_1_18_2::new;
        #endif

        /*
        #elif MC_1_19_2
        return Optionsscreen_1_19_2::new;
        #elif MC_1_20_1
        return Optionsscreen_1_20_1::new;
        #endif


         */
    }
}
