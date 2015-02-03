package com.drprog.sjournal.db.prefs;

import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 02.02.14.
 */
public class DimensionsContainer {

    private Context mainContext;
    private List<Dimensions> dimensionsList;
    //int[] keyList;

    public DimensionsContainer() {
    }

    public DimensionsContainer(Context mainContext) {
        this.mainContext = mainContext;
    }

    public static List<String> getProfileNames(Context mainContext) {
        SQLiteProfileHelper sqlProfile = SQLiteProfileHelper.getInstance(mainContext, true);

        return sqlProfile.dimensions.getProfileNames();
    }

    public static String getCurrentProfile(Context mainContext) {
        SQLiteProfileHelper sqlProfile = SQLiteProfileHelper.getInstance(mainContext, true);

        return sqlProfile.dimensions.getCurrentProfile();
    }

//    public DimensionsContainer(Context mainContext, int[] keyList) {
//        this.mainContext = mainContext;
//        this.keyList = keyList;
//    }

//    public void setKeyList(int[] keyList) {
//        this.keyList = keyList;
//    }

    public static boolean setCurrentProfile(Context mainContext, String profileName) {
        long res = -1;
        SQLiteProfileHelper sqlProfile = SQLiteProfileHelper.getInstance(mainContext, true);
        //     try {
        if (sqlProfile.dimensions.getProfileNames().contains(profileName)) {
            res = sqlProfile.dimensions.setCurrentProfile(profileName);
        }
//        } finally {
//            sqlProfile.close();
//        }
        return res >= 0;
    }

    public static boolean deleteProfile(Context context, String profileName) {
        if (profileName == null || profileName.equals(TableDimensions.STR_DEFAULT)) return false;
        SQLiteProfileHelper sqlProfile = SQLiteProfileHelper.getInstance(context, true);
        String currentProfileName = sqlProfile.dimensions.getCurrentProfile();
        if (profileName.equals(currentProfileName)) {
            sqlProfile.dimensions.setCurrentProfile(TableDimensions.STR_DEFAULT);
        }
        long res = sqlProfile.dimensions.delete(profileName);
        return res > 0;
    }

    public void setContext(Context context) {
        this.mainContext = context;
    }

    public List<Dimensions> getDimensionsList() {
        return dimensionsList;
    }

    public void setDimensionsList(List<Dimensions> dimensionsList) {
        this.dimensionsList = dimensionsList;
    }

    public void addDimensions(Dimensions dims) {
        if (dimensionsList == null) dimensionsList = new ArrayList<Dimensions>();
        if (dimensionsList.contains(dims)) dimensionsList.remove(dims);
        dimensionsList.add(dims);
    }

    public Dimensions findDimensions(int key) {
        for (Dimensions dims : dimensionsList) {
            if (dims.getKey() == key) return dims;
        }
        return new Dimensions(mainContext);
    }

    public void updateDimensions(Dimensions newDims) {
        for (Dimensions dims : dimensionsList) {
            if (dims.getKey().equals(newDims.getKey())) {
                dimensionsList.remove(dims);
                dimensionsList.add(newDims);
                break;
            }
        }
    }

    public List<Dimensions> loadCurrentDimensionProfile() {
        return loadDimensionProfile(null);
    }

    public List<Dimensions> loadDimensionProfile(String profile) {
        if (dimensionsList == null) { dimensionsList = new ArrayList<Dimensions>(); } else {
            dimensionsList.clear();
        }
        if (mainContext == null) return dimensionsList;
        //if (keyList == null || keyList.length <= 0) return dimensionsList;
        SQLiteProfileHelper sqlProfile = SQLiteProfileHelper.getInstance(mainContext, true);
//        try {
//            Dimensions dimsPrototype = new Dimensions(mainContext);
        if (profile == null) {
            profile = sqlProfile.dimensions.getCurrentProfile();
            if (profile == null) profile = TableDimensions.STR_DEFAULT;
        }

        dimensionsList = sqlProfile.dimensions.getProfile(profile);
//            Dimensions dimsPrototype = new Dimensions(mainContext);
//            Dimensions dims;
//            for (int key : keyList) {
//                dims = sqlProfile.dimensions.get(profile, key, dimsPrototype);
//                dimensionsList.add(dims);
//            }
//        } finally {
//            sqlProfile.close();
//        }
        return dimensionsList; //new ArrayList<Dimensions>(dimensionsList);
    }

    public long saveDimensionProfile(String profileName) {
        long res = 0;
        if ((profileName == null) || (profileName.isEmpty()) || (profileName.matches("[ ]+"))) {
            return -1;
        }
        SQLiteProfileHelper sqlProfile = SQLiteProfileHelper.getInstance(mainContext, true);
        //sqlProfile.getWritableDatabase();
//        try {
        for (Dimensions dims : dimensionsList) {
            dims.setProfileName(profileName);
            if (sqlProfile.dimensions.insert(dims) >= 0) res++;
        }
        if (res > 0) sqlProfile.dimensions.setCurrentProfile(profileName);
//        } finally {
//            sqlProfile.close();
//        }
        return res;
    }

    public long updateDimensionProfile(String profileName) {
        long res = 0;
        if ((profileName == null) || (profileName.equals("")) || (profileName.matches("[ ]+"))) {
            return -1;
        }
        SQLiteProfileHelper sqlProfile = SQLiteProfileHelper.getInstance(mainContext, true);
        //sqlProfile.getWritableDatabase();
//        try {
        for (Dimensions dims : dimensionsList) {
            dims.setProfileName(profileName);
            if (sqlProfile.dimensions.update(dims) >= 0) res++;
        }
        if (res > 0) sqlProfile.dimensions.setCurrentProfile(profileName);
//        } finally {
//            sqlProfile.close();
//        }
        return res;
    }

    public void setViewStyle(int key, TextView view) {
        Dimensions dims = findDimensions(key);
        if (dims != null) dims.setStyle(view);
    }

}
