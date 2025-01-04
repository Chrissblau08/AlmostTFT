package model;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class UnitPool {

    private Map<Integer, Unit> unitData = new HashMap<>(); // Stores unit data by ID
    private Map<Integer, Integer> unitPool = new HashMap<>(); // Stores count of each unit by ID
    private Random random = new Random();

    public UnitPool(String resourceName) {
        loadUnitsFromJson(resourceName);
    }

    private void loadUnitsFromJson(String filePath) {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            Type unitListType = new TypeToken<List<Unit>>() {}.getType();
            List<Unit> units = gson.fromJson(reader, unitListType);
            if (units == null) {
                throw new IOException("Failed to parse units from JSON.");
            }
            for (Unit unit : units) {
                unitData.put(unit.getId(), unit);
                int count = getInitialCountByCost(unit.getCost());
                unitPool.put(unit.getId(), count);
            }
        } catch (IOException | JsonSyntaxException e) {
            // Use a logging framework instead
            e.printStackTrace();
        }
    }

    private int getInitialCountByCost(int cost) {
        switch (cost) {
            case 1: return 30;
            case 2: return 25;
            case 3: return 18;
            case 4: return 10;
            case 5: return 9;
            default: return 0;
        }
    }

    public Unit getUnitById(int id) {
        return unitData.get(id);
    }

    public boolean removeUnitById(int id) {
        Integer count = unitPool.get(id);
        if (count != null && count > 0) {
            unitPool.put(id, count - 1);
            if (count - 1 == 0) {
                unitPool.remove(id); // Remove from pool if count reaches 0
            }
            return true;
        }
        return false;
    }

    public List<Unit> getAllUnits() {
        List<Unit> allUnits = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : unitPool.entrySet()) {
            int id = entry.getKey();
            int count = entry.getValue();
            Unit unit = unitData.get(id);
            for (int i = 0; i < count; i++) {
                allUnits.add(unit);
            }
        }
        return allUnits;
    }

    // New Method: Get a random unit by cost
    public Unit getRandomUnitByCost(int cost) {
        List<Unit> unitsByCost = new ArrayList<>();
        for (Map.Entry<Integer, Unit> entry : unitData.entrySet()) {
            Unit unit = entry.getValue();
            if (unit.getCost() == cost && unitPool.get(unit.getId()) != null && unitPool.get(unit.getId()) > 0) {
                Unit tmp = new Unit(unit);
                tmp.setUuid(UUID.randomUUID());
                unitsByCost.add(tmp);
            }
        }
        if (unitsByCost.isEmpty()) {
            return null; // No units of that cost available
        }
        return unitsByCost.get(random.nextInt(unitsByCost.size()));
    }

    // New Method: Get a random unit from the pool
    public Unit getRandomUnit() {
        List<Unit> allAvailableUnits = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : unitPool.entrySet()) {
            int id = entry.getKey();
            int count = entry.getValue();
            Unit unit = unitData.get(id);
            for (int i = 0; i < count; i++) {
                allAvailableUnits.add(unit);
            }
        }
        if (allAvailableUnits.isEmpty()) {
            return null; // No units available
        }
        return allAvailableUnits.get(random.nextInt(allAvailableUnits.size()));
    }

}