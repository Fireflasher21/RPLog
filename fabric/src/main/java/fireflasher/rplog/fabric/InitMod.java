package fireflasher.rplog.fabric;

import com.terraformersmc.modmenu.util.TranslationUtil;
import fireflasher.rplog.RPLog;
import net.fabricmc.api.ModInitializer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.io.InputStream;
import java.io.InputStreamReader;

public class InitMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // Code here will only run on the physical client.
        // So here you can use net.minecraft.client.
        loadLanguage();
    }

    private void loadLanguage() {
        String languageFilePath = "/assets/rplog/lang/en_us.json";
        try (InputStream reader = getClass().getResourceAsStream(languageFilePath)) {
            // Parse JSON and register translations
            assert reader != null;
            Language.loadFromJson(reader,(s, s2) -> {
                RPLog.translateAbleStrings.put(s,null);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
