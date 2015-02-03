package com.drprog.sjournal.utils;




/**
 * Created by Romka on 31.01.14.
 */
public class DebugUtils {

   /* public static void testMaxLines(TextView v) {
        v.setMaxLines(1);
        v.setPadding(2, 7,7,2);
        v.setIncludeFontPadding(false);

    }

    public static void testAddCol(TextView view) {

        view.setText("X");

        view.setId(R.id.blank_add_column);

    }

    public static void testStudentCols(TextView view) {
        view.setId(R.id.blank_name_body);

    }

    public static void testImport(LinearLayout view) {
        TextView targetView = (TextView) view.findViewById(R.id.blank_name_body);
        if(targetView == null) return;
        view.setVisibility(View.VISIBLE);
        //targetView.setEnabled(true);
        //targetView.setLine

        String str = "\n";
        str += "View: blank_name_body\n";
        str += "Text: " + targetView.getText() + "\n";
        str += "getLeft: " + targetView.getLeft() + "\n";
        str += "getRight: " + targetView.getRight() + "\n";
        str += "getWidth: " + targetView.getWidth() + "\n";
        str += "getHeight: " + targetView.getHeight() + "\n";
        str += "getScrollX: " + targetView.getScrollX() + "\n";
        str += "getScrollY: " + targetView.getScrollY() + "\n";
        str += "getLineSpacingExtra: " + targetView.getLineSpacingExtra() + "\n";
        str += "getLineSpacingMultiplier: " + targetView.getLineSpacingMultiplier() + "\n";
        str += "getLineHeight: " + targetView.getLineHeight() + "\n";

        Log.d("SJournal_Debug", str);

        targetView = (TextView) view.findViewById(R.id.blank_add_column);
        if(targetView == null) return;
        str = "\n";
        str += "View: blank_add_column\n";
        str += "Text: " + targetView.getText() + "\n";
        str += "getLeft: " + targetView.getLeft() + "\n";
        str += "getRight: " + targetView.getRight() + "\n";
        str += "getWidth: " + targetView.getWidth() + "\n";
        str += "getHeight: " + targetView.getHeight() + "\n";
        str += "getScrollX: " + targetView.getScrollX() + "\n";
        str += "getScrollY: " + targetView.getScrollY() + "\n";
        str += "getLineSpacingExtra: " + targetView.getLineSpacingExtra() + "\n";
        str += "getLineSpacingMultiplier: " + targetView.getLineSpacingMultiplier() + "\n";
        str += "getLineHeight: " + targetView.getLineHeight() + "\n";

        Log.d("SJournal_Debug", str);
    }*/


/*
    public static final String TAG_DEBUG = "SJournal_Debug";

    public static long getNanoTime() {
        return System.nanoTime();
    }

    public static void sleep(Integer time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void log_d(String msg) {
        Log.d(DebugUtils.TAG_DEBUG, msg);
    }

    public static void deleteJournalDatabase(Context context) {
        SQLiteJournalHelper.getInstance(context).close();
        context.deleteDatabase(SQLiteJournalHelper.DB_NAME);
    }

    public static void initDatabase(Context context) {

        SQLiteJournalHelper.getInstance(context).close();
        SQLiteProfileHelper.getInstance(context).close();
        context.deleteDatabase(SQLiteJournalHelper.DB_NAME);
        context.deleteDatabase(SQLiteProfileHelper.DB_NAME);


        DimensionsContainer dimensionsContainer = new DimensionsContainer(context);

        Dimensions dims = new Dimensions(context);
        dims.setBgColor(Color.LTGRAY);
        dims.setKey(Blank.KEY_BLANK);
        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(dims);
        dims.setBgColor(Color.BLACK);
        dims.setKey(Blank.KEY_TABLE);
        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(dims);
        dims.setTextColor(0xff000000);
        dims.setBgColor(Color.LTGRAY);
        dims.setGravity(Gravity.CENTER);
        dims.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                       LinearLayout.LayoutParams.WRAP_CONTENT);
        dims.setDimensionInDip(null, 1, 1, 1, 1);
        //dims.setMargins(5,5,5,5);
        dims.setTextSize(14);
        dims.setKey(Blank.KEY_TOPIC_TEXT);
        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(dims);
        dims.setTextColor(0xff000000);
        dims.setBgColor(Color.RED);
        dims.setGravity(Gravity.CENTER);
        dims.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);
        dims.setDimensionInDip(20, 1, 1, 1, 1);
        dims.setTextSize(10);
        dims.setKey(Blank.KEY_NUM_TOP);
        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(dims);
        dims.setTextColor(0xff000000);
        dims.setBgColor(Color.RED);
        dims.setGravity(Gravity.CENTER);
        dims.setSingleTextLine(true);
        dims.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);
        dims.setDimensionInDip(20, 1, 1, 1, 1);
        dims.setTextSize(10);
        dims.setKey(Blank.KEY_NUM_LEFT);
        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(context);
        dims.setTextColor(0xff000000);
        dims.setBgColor(Color.RED);
        dims.setGravity(Gravity.CENTER);
        dims.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);
        dims.setTextSize(16);
        dims.setSingleTextLine(true);
        dims.setDimensionInDip(300, 1, 1, 1, 1);
        dims.setKey(Blank.KEY_NAME_TOP);
        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(dims);
        dims.setTextColor(0xff000000);
        dims.setBgColor(Color.GREEN);
        dims.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        dims.setSingleTextLine(true);
        dims.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);
        dims.setDimensionInDip(300, 1, 1, 1, 1);
        dims.setTextSize(24);
        dims.setPadding(4, 0, 0, 0);
        dims.setKey(Blank.KEY_NAME_LEFT);
        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(context);
        dims.setTextColor(0xff000000);
        dims.setBgColor(Color.GREEN);
        dims.setGravity(Gravity.CENTER);
        dims.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);
        dims.setTextSize(16);
        dims.setDimensionInDip(100, 1, 1, 1, 1);
        dims.setKey(Blank.KEY_CLASS_TOP);
        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(dims);
        dims.setTextColor(0xff000000);
        dims.setBgColor(Color.GREEN);
        dims.setGravity(Gravity.CENTER);
        dims.setTextSize(24);
        dims.setSingleTextLine(true);
        dims.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);
        dims.setDimensionInDip(100, 1, 1, 1, 1);
        dims.setKey(Blank.KEY_CLASS_BODY);
        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(context);
        dims.setTextColor(0xff000000);
        dims.setBgColor(Color.GREEN);
        dims.setGravity(Gravity.CENTER);
        dims.setViewWidth(10);
        dims.setTextSize(24);
        dims.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);
        dims.setDimensionInDip(50, 1, 1, 1, 1);
        dims.setKey(Blank.KEY_ADD_COL);
        dimensionsContainer.addDimensions(dims);

//        dims = new Dimensions(context);
//        dims.setTextColor(0xff000000);
//        dims.setBgColor(Color.GREEN);
//        dims.setGravity(Gravity.CENTER);
//        dims.setSingleTextLine(true);
//        dims.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
//                       LinearLayout.LayoutParams.MATCH_PARENT);
//        dims.setDimensionInDip(300+20+2, 1, 1, 1, 1);
//        dims.setTextSize(24);
//        dims.setPadding(4,0,0,0);
//        dims.setKey(Blank.KEY_ADD_ROW);
//        dimensionsContainer.addDimensions(dims);

        dims = new Dimensions(context);
        dims.setTextColor(0xff000000);
        dims.setBgColor(Color.GREEN);
        dims.setGravity(Gravity.CENTER);
        dims.setSingleTextLine(true);
        dims.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);
        dims.setDimensionInDip(100, 1, 1, 1, 1);
        dims.setTextSize(24);
        dims.setKey(Blank.KEY_ABSENT_CEIL);
        dimensionsContainer.addDimensions(dims);

        dimensionsContainer.saveDimensionProfile(TableDimensions.STR_DEFAULT);
        //dimensionsContainer.updateDimensionProfile(TableDimensions.STR_DEFAULT);
        dimensionsContainer.loadDimensionProfile(TableDimensions.STR_DEFAULT);

        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(context, true);

        for (int i = 1; i < 10; i++) {
            dbHelper.groups.insert(new StudyGroup((long) i, "Group " + i));
        }
        for (int i = 1; i < 10; i++) {
            dbHelper.subjects
                    .insert(new StudySubject((long) i, "Title " + i, "Abbr " + i, -1));
        }

        dbHelper.classTypes.insert(new StudyClassType(1L, "Лекция", "Лк", -1));
        dbHelper.classTypes.insert(new StudyClassType(2L, "Практическое занятие", "Пз", -1));
        dbHelper.classTypes.insert(new StudyClassType(3L, "Лабораторная работа", "Лб", -1));
        dbHelper.classTypes.insert(new StudyClassType(4L, "Самостоятельная работа", "Ср", -1));

        int numClasses = 18;
        int numStudents = 40;
        for (int i = 1; i < numClasses; i++) {
            Integer color = null;//Color.rgb(i*15,i*12,i*9);
            dbHelper.classes.insert(new StudyClass((long) i, 1, 2, 2, 2,
                                                   "2014-07-" + i, "Тема номер " + i, "Тема " + i,
                                                   null, color, null, null));
        }

        for (int i = 1; i < numStudents; i++) {
            dbHelper.students.insert(new Student((long) i, "Фамилия " + i, "Имя " + i,
                                                 "Отчество " + i, null, null, null, null));
            dbHelper.students.insertStudentInGroup((long) i, 2L);
        }
        for (int i = 1; i < numClasses; i++) {
            for (int j = 3; j < numStudents; j++) {
                dbHelper.marks
                        .insert(new StudyMark((long) (j * numStudents + i), (long) j,
                                              (long) i, StudyMark.TYPE_MARK, 30 * j + i,
                                              null, null));
            }
        }
        dbHelper.marks.insert(new StudyMark(null, 1L, 2L, StudyMark.TYPE_ABSENT, null, null, null));
        dbHelper.marks
                .insert(new StudyMark(null, 1L, 3L, StudyMark.TYPE_NO_MARK, null, null, null));
        dbHelper.marks
                .insert(new StudyMark(null, 1L, 4L, StudyMark.TYPE_SYMBOL, null, "Symbol", null));
        dbHelper.marks.insert(new StudyMark(null, 1L, 5L, StudyMark.TYPE_SYMBOL, null, "Sy", null));
        dbHelper.marks
                .insert(new StudyMark(null, 2L, 3L, StudyMark.TYPE_SYMBOL, null, "Very Long String",
                                      null));
        dbHelper.marks.insert(new StudyMark(null, 2L, 4L, StudyMark.TYPE_SYMBOL, null,
                                            "Very Very Long String", null));
        dbHelper.marks.insert(new StudyMark(null, 2L, 5L, StudyMark.TYPE_SYMBOL, null,
                                            "Very Very Very Long String", null));
        dbHelper.marks.insert(new StudyMark(null, 2L, 1L, StudyMark.TYPE_SYMBOL, null,
                                            "Very Very Long String", null));
        dbHelper.marks.insert(new StudyMark(null, 2L, 2L, StudyMark.TYPE_SYMBOL, null,
                                            "Very Very Long String", null));

//
//
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//
//               // progressDialog.dismiss();
//            };
//        };
//        asyncTask.execute();
//
//
    }
*/

}

