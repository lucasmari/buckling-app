package com.example.bucklingcalculator;

import java.util.ArrayList;
import java.util.List;

import static com.example.bucklingcalculator.MainActivity.materials;
import static com.example.bucklingcalculator.MainActivity.materialsProperties;

public class Materials {

    public static final List<Material> ITEMS = new ArrayList<>();

    static  {
        for (int i = 0; i < materials[0].size(); i++) {
            addItem(createMaterial(i));
        }
    }

    private static void addItem(Material item) {
        ITEMS.add(item);
    }

    public static Material createMaterial(int position) {
        return new Material(position, materials[0].get(position).toString(), makeDetails(position));
    }

    public static Material editMaterial(int position) {
        return new Material(position, materials[0].get(position).toString(), makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append(materialsProperties.get(0) + ": " + materials[1].get(position).toString());
        builder.append("\n" + materialsProperties.get(1) + ": " + materials[2].get(position).toString());
        return builder.toString();
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
