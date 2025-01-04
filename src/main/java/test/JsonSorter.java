package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JsonSorter {

    public static void main(String[] args) {
        String inputFilePath = "C:\\Users\\Florian Studium\\Documents\\GitHub\\AlmostTFT\\src\\main\\resources\\characters\\balanced_fantasy_auto_battler_characters.json";
        String outputFilePath = "C:\\Users\\Florian Studium\\Documents\\GitHub\\AlmostTFT\\src\\main\\resources\\characters\\characters.json";

        try {
            // Load units from the JSON file
            List<Unit> units = loadUnitsFromJson(inputFilePath);

            // Sort units by cost in ascending order
            Collections.sort(units, Comparator.comparingInt(Unit::getCost));

            // Update IDs to match the new order
            updateUnitIds(units);

            // Write the sorted units to a new JSON file
            writeUnitsToJson(units, outputFilePath);

            System.out.println("Units have been sorted by cost, IDs updated, and saved to " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Unit> loadUnitsFromJson(String jsonFilePath) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(jsonFilePath)) {
            Type unitListType = new TypeToken<List<Unit>>() {}.getType();
            return gson.fromJson(reader, unitListType);
        }
    }

    private static void writeUnitsToJson(List<Unit> units, String jsonFilePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(jsonFilePath)) {
            gson.toJson(units, writer);
        }
    }

    private static void updateUnitIds(List<Unit> units) {
        for (int i = 0; i < units.size(); i++) {
            units.get(i).setId(i + 1);
        }
    }
}