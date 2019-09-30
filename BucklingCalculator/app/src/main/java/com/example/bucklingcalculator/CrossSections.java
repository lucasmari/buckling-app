package com.example.bucklingcalculator;

import java.util.ArrayList;
import java.util.List;

import static com.example.bucklingcalculator.MainActivity.crossSections;
import static com.example.bucklingcalculator.MainActivity.crossSectionsProperties;

public class CrossSections {

    public static final List<CrossSection> ITEMS = new ArrayList<>();

    static  {
        for (int i = 0; i < crossSections[0].size(); i++) {
            addItem(createCrossSection(i));
        }
    }

    private static void addItem(CrossSection item) {
        ITEMS.add(item);
    }

    public static CrossSection createCrossSection(int position) {
        return new CrossSection(position, crossSections[0].get(position).toString(), makeDetails(position));
    }

    public static CrossSection editCrossSection(int position) {
        return new CrossSection(position, crossSections[0].get(position).toString(), makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append(crossSectionsProperties.get(0) + ": " + crossSections[1].get(position).toString());
        builder.append("\n" + crossSectionsProperties.get(1) + ": " + crossSections[2].get(position).toString());
        builder.append("\n" + crossSectionsProperties.get(2) + ": " + crossSections[3].get(position).toString());
        builder.append("\n" + crossSectionsProperties.get(3) + ": " + crossSections[4].get(position).toString());
        return builder.toString();
    }

    public static class CrossSection {
        public final int id;
        public final String content;
        public final String details;

        public CrossSection(int id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }
    }
}
