package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaTransform;

import java.util.HashMap;
import java.util.Map;

public final class PlacedUmbrellaDefaults {
    private static final Map<String, UmbrellaTransform> MAP = new HashMap<>();
    static {
        MAP.put("telescopic|true|false|false|false|cardinal", new UmbrellaTransform(1.0F, -0.39149177F, 0.05167651F, -0.12851274F, 0.0F, 0.0F, 0.0F));
        MAP.put("telescopic|true|false|false|false|diagonal", new UmbrellaTransform(1.0F, 0.066286564F, 0.05654645F, 0.40231514F, 90.00925F, 0.0F, 0.0F));
        MAP.put("straight|true|false|false|false|cardinal", new UmbrellaTransform(1.0F, -0.3525319F, 0.027326584F, -0.18208253F, 0.0F, 0.0F, 0.0F));
        MAP.put("straight|true|false|false|false|diagonal", new UmbrellaTransform(1.0F, 0.05167651F, 0.041936636F, 0.35361552F, 90.44754F, 0.0F, 0.0F));
        MAP.put("oil_paper|true|false|false|false|cardinal", new UmbrellaTransform(1.0F, -0.28922212F, 0.0F, -0.31357205F, 0.0F, 0.0F, 0.0F));
        MAP.put("oil_paper|true|false|false|false|diagonal", new UmbrellaTransform(1.0F, -0.1236428F, 0.0F, 0.28543568F, 89.57092F, 0.0F, 0.0F));
        MAP.put("telescopic|false|false|false|false|cardinal", new UmbrellaTransform(1.0F, -0.5132413F, 0.2903056F, -0.1236428F, 0.0F, 0.15808105F, -30.084518F));
        MAP.put("telescopic|false|false|false|false|diagonal", new UmbrellaTransform(1.0F, -0.07494295F, 0.32439542F, 0.49971485F, 90.44754F, 0.0F, -31.399414F));
        MAP.put("telescopic|false|false|false|true|diagonal", new UmbrellaTransform(1.0F, -0.51811135F, 0.8600936F, -0.14799273F, 9.800644F, -3.3483124F, -92.76118F));
        MAP.put("telescopic|false|false|false|true|cardinal", new UmbrellaTransform(1.0F, -0.13825274F, 0.41205525F, -0.79083025F, 92.200714F, 0.0F, -157.19104F));
        MAP.put("straight|false|false|false|false|cardinal", new UmbrellaTransform(1.0F, 0.39257526F, 0.34874535F, 0.31952548F, 95.26883F, -21.756851F, -19.565353F));
        MAP.put("straight|false|false|false|false|diagonal", new UmbrellaTransform(1.0F, 0.31465554F, 0.24647593F, -0.43045163F, -172.53148F, -19.12706F, -15.182358F));
        MAP.put("straight|false|false|false|true|cardinal", new UmbrellaTransform(1.0F, 0.29517555F, 1.1522925F, -0.35740197F, 17.690002F, -87.0633F, -180.0F));
        MAP.put("straight|false|false|false|true|diagonal", new UmbrellaTransform(1.0F, -0.33792198F, 1.0354128F, -0.39636183F, -94.51437F, -80.05053F, 4.1027527F));
        MAP.put("oil_paper|false|false|false|false|cardinal", new UmbrellaTransform(1.0F, -0.9369297F, 0.20264602F, -0.29409218F, -2.4717255F, -4.663208F, -34.905792F));
        MAP.put("oil_paper|false|false|false|false|diagonal", new UmbrellaTransform(1.0F, -0.2648723F, 0.2562158F, 0.95749307F, 89.13263F, 0.0F, -34.02919F));
        MAP.put("oil_paper|false|false|false|true|cardinal", new UmbrellaTransform(1.0F, 0.17829609F, 1.5613708F, -0.49863136F, -91.00799F, 4.1027527F, -96.26756F));
        MAP.put("oil_paper|false|false|false|true|diagonal", new UmbrellaTransform(1.0F, -0.60577095F, 1.4883211F, -0.18695259F, 0.0F, 0.0F, -91.88458F));
        MAP.put("telescopic|true|true|true|false|inverted", new UmbrellaTransform(1.0F, 0.007846832F, 0.061416626F, -0.011633158F, 0.0F, 0.0F, 0.0F));
        MAP.put("telescopic|true|true|false|false|inverted", new UmbrellaTransform(1.0F, -0.47428143F, -1.0051095F, -0.4791515F, 0.0F, 0.0F, 0.0F));
        MAP.put("straight|true|true|false|false|inverted", new UmbrellaTransform(1.0F, -0.5229813F, -1.0781592F, -0.5570712F, 0.0F, 0.0F, 0.0F));
        MAP.put("straight|true|true|true|false|inverted", new UmbrellaTransform(1.0F, -0.46454155F, -1.1463389F, -0.5132413F, 0.0F, 0.0F, 0.0F));
        MAP.put("oil_paper|true|true|false|false|inverted", new UmbrellaTransform(1.0472628F, -0.65934074F, -0.9710196F, -0.66908073F, 0.0F, 0.0F, 0.0F));
        MAP.put("oil_paper|true|true|true|false|inverted", new UmbrellaTransform(1.0543243F, -0.59603107F, -1.0878992F, -0.57168114F, 0.0F, 0.0F, 0.0F));
    }
    private PlacedUmbrellaDefaults() {}
    static UmbrellaTransform get(String key) {
        return MAP.getOrDefault(key, UmbrellaTransform.IDENTITY);
    }
}
