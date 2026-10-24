package net.scottishfrogg.cutenames.client.jem;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class JemLayout {
    public static final JemLayout CAT = new JemLayout(List.of(
            "head", "body", "tail1", "tail2",
            "left_hind_leg", "right_hind_leg", "left_front_leg", "right_front_leg"))
            .alias("tail", "tail1")
            .alias("back_left_leg", "left_hind_leg")
            .alias("back_right_leg", "right_hind_leg")
            .alias("front_left_leg", "left_front_leg")
            .alias("front_right_leg", "right_front_leg");
    public static final JemLayout WOLF = new JemLayout(List.of(
            "head", "body", "upper_body",
            "right_hind_leg", "left_hind_leg", "right_front_leg", "left_front_leg", "tail"))
            .alias("mane", "upper_body")
            .alias("leg1", "right_hind_leg")
            .alias("leg2", "left_hind_leg")
            .alias("leg3", "right_front_leg")
            .alias("leg4", "left_front_leg")
            .alias("real_head", "head")
            .alias("real_tail", "tail")
            .wrap("head", "real_head")
            .wrap("tail", "real_tail");
    public static final JemLayout PIG = new JemLayout(List.of(
            "head", "body",
            "right_hind_leg", "left_hind_leg", "right_front_leg", "left_front_leg"))
            .alias("leg1", "right_hind_leg")
            .alias("leg2", "left_hind_leg")
            .alias("leg3", "right_front_leg")
            .alias("leg4", "left_front_leg");
    private final List<String> parts;
    private final Map<String, String> aliases = new HashMap<>();
    private final Map<String, String> wrappers = new HashMap<>();
    private JemLayout(List<String> parts) {
        this.parts = parts;
    }
    private JemLayout alias(String jemName, String vanillaPart) {
        aliases.put(jemName.toLowerCase(Locale.ROOT), vanillaPart);
        return this;
    }
    private JemLayout wrap(String vanillaPart, String innerChild) {
        wrappers.put(vanillaPart, innerChild);
        return this;
    }
    public List<String> parts() {
        return parts;
    }
    public String wrapper(String vanillaPart) {
        return wrappers.get(vanillaPart);
    }
    public String resolve(String jemName) {
        if (jemName == null || jemName.isEmpty()) {
            return null;
        }
        String key = jemName.toLowerCase(Locale.ROOT);
        String alias = aliases.get(key);
        if (alias != null) {
            return alias;
        }
        return parts.contains(key) ? key : null;
    }
}
