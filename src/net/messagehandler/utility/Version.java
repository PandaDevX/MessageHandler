package net.messagehandler.utility;

import org.bukkit.Bukkit;

public enum Version {
    TOO_OLD(-1),
    v1_7_R1(171),
    v1_7_R2(172),
    v1_7_R3(173),
    v1_7_R4(174),
    v1_8_R1(181),
    v1_8_R2(182),
    v1_8_R3(183),
    v1_9_R1(191),
    v1_9_R2(192),
    v1_10_R1(1101),
    v1_11_R1(1111),
    v1_12_R1(1121),
    v1_13_R2(1132),
    v1_14_R1(1141),
    v1_15_R1(1151),
    v1_15_R2(1152),
    v1_16_R1(1161),
    v1_16_R2(1162),
    TOO_NEW(-2);

    public static Version currentVersion;

    private static Version latest;

    private final Integer versionInteger;

    Version(int versionInteger) {
        this.versionInteger = versionInteger;
    }

    public static Version getCurrentVersion() {
        if (currentVersion == null) {
            String ver = Bukkit.getServer().getClass().getPackage().getName();
            int v = Integer.parseInt(ver.substring(ver.lastIndexOf('.') + 1).replaceAll("_", "").replaceAll("R", "")
                    .replaceAll("v", ""));
            byte b;
            int i;
            Version[] arrayOfVersion;
            for (i = (arrayOfVersion = values()).length, b = 0; b < i; ) {
                Version version = arrayOfVersion[b];
                if (version.getVersionInteger() == v) {
                    currentVersion = version;
                    break;
                }
                b++;
            }
            if (v > getLatestVersion().getVersionInteger())
                currentVersion = getLatestVersion();
            if (currentVersion == null)
                currentVersion = TOO_NEW;
        }
        return currentVersion;
    }

    public static Version getLatestVersion() {
        if (latest == null) {
            Version v = TOO_OLD;
            byte b;
            int i;
            Version[] arrayOfVersion;
            for (i = (arrayOfVersion = values()).length, b = 0; b < i; ) {
                Version version = arrayOfVersion[b];
                if (version.comparedTo(v) == 1)
                    v = version;
                b++;
            }
            return v;
        }
        return latest;
    }

    public Integer getVersionInteger() {
        return this.versionInteger;
    }

    public Integer comparedTo(Version version) {
        int resault = -1;
        int current = getVersionInteger();
        int check = version.getVersionInteger();
        if (current > check || check == -2) {
            resault = 1;
        } else if (current == check) {
            resault = 0;
        } else if (current < check || check == -1) {
            resault = -1;
        }
        return resault;
    }

    public boolean isNewer(Version version) {
        return !(this.versionInteger <= version.versionInteger && this.versionInteger != -2);
    }

    public boolean isSame(Version version) {
        return this.versionInteger.equals(version.versionInteger);
    }

    public boolean isOlder(Version version) {
        return !(this.versionInteger >= version.versionInteger && this.versionInteger != -1);
    }
}

