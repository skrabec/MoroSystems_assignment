package model;

public enum ScreenResolution {
    MOBILE_S("Mobile S (375x667)",   375,  667),
    MOBILE_L("Mobile L (425x896)",   425,  896),
    TABLET  ("Tablet (768x1024)",    768, 1024),
    DESKTOP ("Desktop (1440x900)",  1440,  900),
    DESKTOP_4K("Desktop 4K (2560x1440)", 2560, 1440);

    public final String label;
    public final int width;
    public final int height;

    ScreenResolution(String label, int width, int height) {
        this.label = label;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return label;
    }
}
