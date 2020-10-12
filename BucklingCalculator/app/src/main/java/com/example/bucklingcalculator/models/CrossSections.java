package com.example.bucklingcalculator.models;

import java.util.ArrayList;
import java.util.List;

import static com.example.bucklingcalculator.activities.MainActivity.crossSections;
import static com.example.bucklingcalculator.activities.MainActivity.crossSectionsProperties;

public class CrossSections {

    public static final List<CrossSection> ITEMS = new ArrayList<>();

    static  {
        for (int i = 0; i < crossSections[0].size(); i++) {
            addItem(addCrossSection(i));
        }
    }

    private static void addItem(CrossSection item) {
        ITEMS.add(item);
    }

    public static CrossSection addCrossSection(int position) {
        return new CrossSection(position, crossSections[0].get(position), makeDetails(position));
    }


    private static String makeDetails(int position) {
        return crossSectionsProperties.get(0) + ": " + crossSections[1].get(position) + " m²" +
                "\n" + crossSectionsProperties.get(1) + ": " + crossSections[2].get(position) + " m" +
                "\n" + crossSectionsProperties.get(2) + ": " + crossSections[3].get(position) + " m" +
                "\n" + crossSectionsProperties.get(3) + ": " + crossSections[4].get(position) +
                " kg m²";
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
