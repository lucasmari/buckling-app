package com.example.bucklingcalculator.models;

import java.util.ArrayList;
import java.util.List;

import static com.example.bucklingcalculator.activities.MainActivity.materials;
import static com.example.bucklingcalculator.activities.MainActivity.materialsProperties;

public class Materials {

    public static final List<Material> ITEMS = new ArrayList<>();

    static  {
        for (int i = 0; i < materials[0].size(); i++) {
            addItem(addMaterial(i));
        }
    }

    private static void addItem(Material item) {
        ITEMS.add(item);
    }

    public static Material addMaterial(int position) {
        return new Material(position, materials[0].get(position), makeDetails(position));
    }

    private static String makeDetails(int position) {
        return materialsProperties.get(0) + ": " + materials[1].get(position) + " Pa" +
                "\n" + materialsProperties.get(1) + ": " + materials[2].get(position) + " Pa";
    }

    public static class Material {
        public final int id;
        public final String content;
        public final String details;

        public Material(int id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }
    }
}
