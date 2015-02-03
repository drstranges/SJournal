package com.drprog.sjournal.blank;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.drprog.sjournal.db.TableClasses;
import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.db.entity.StudyClass;
import com.drprog.sjournal.db.entity.StudyMark;
import com.drprog.sjournal.db.entity.SummaryEntry;
import com.drprog.sjournal.db.prefs.Dimensions;
import com.drprog.sjournal.db.prefs.DimensionsContainer;
import com.drprog.sjournal.preferences.PrefsManager;
import com.drprog.sjournal.R;
import com.drprog.sjournal.syncscroll.ScrollManager;
import com.drprog.sjournal.syncscroll.SyncedHorizontalScrollView;
import com.drprog.sjournal.utils.RunUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Romka on 01.02.14.
 */
public class Blank {
    public static final int KEY_TOPIC_TEXT = 1;
    public static final int KEY_NUM_TOP = 2;
    public static final int KEY_NUM_LEFT = 3;
    public static final int KEY_NAME_TOP = 4;
    public static final int KEY_NAME_LEFT = 5;
    public static final int KEY_CLASS_TOP = 6;
    public static final int KEY_CLASS_BODY = 7;
    public static final int KEY_ADD_COL = 8;
    //public static final int KEY_ADD_ROW = 9;
    public static final int KEY_TABLE = 10;
    public static final int KEY_BLANK = 11;
    public static final int KEY_ABSENT_CEIL = 12;


    private Integer bgColor_Blank;
    private Integer bgColor_Table;

    private final LinearLayout.MarginLayoutParams layoutParams_MP_WC =
            new LinearLayout.MarginLayoutParams(LinearLayout.MarginLayoutParams.MATCH_PARENT,
                                                LinearLayout.MarginLayoutParams.WRAP_CONTENT);
    private final LinearLayout.MarginLayoutParams layoutParams_WC_WC =
            new LinearLayout.MarginLayoutParams(LinearLayout.MarginLayoutParams.WRAP_CONTENT,
                                                LinearLayout.MarginLayoutParams.WRAP_CONTENT);
    private final LinearLayout.MarginLayoutParams layoutParams_WC_MP =
            new LinearLayout.MarginLayoutParams(LinearLayout.MarginLayoutParams.WRAP_CONTENT,
                                                LinearLayout.MarginLayoutParams.MATCH_PARENT);
    LinearLayout.MarginLayoutParams layoutParams_MP_MP =
            new LinearLayout.MarginLayoutParams(LinearLayout.MarginLayoutParams.MATCH_PARENT,
                                                LinearLayout.MarginLayoutParams.MATCH_PARENT);
    private LinearLayout blankLayout;
    private final View.OnClickListener generalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //if (blankClickListener != null) { blankClickListener.onBlankClick(v); }
            if (blankLayout != null) {
                blankLayout.setTag(v.getTag());
                blankLayout.performClick();
            }
        }
    };
    private final View.OnLongClickListener generalLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //if (blankClickListener != null) { blankClickListener.onBlankLongClick(v); }
            if (blankLayout != null) {
                blankLayout.setTag(v.getTag());
                blankLayout.performLongClick();
            }
            return true;
        }
    };
    //Layout
    private LinearLayout topLayout;
    private SyncedHorizontalScrollView topHScrollView;
    private LinearLayout topScrollLayout;
    private ScrollView bodyScrollView;
    private LinearLayout bodyLayout;
    private LinearLayout bodyLeftLayout;
    private SyncedHorizontalScrollView bodyHScrollView;
    private LinearLayout bodyRightLayout;
//    BlankLayoutHandler layoutHandler;

    private String topText = "";
    private Integer nameTopTextResId;
    private List<StudyClass> classList;
    private List<Student> studentList;
    private List<StudyMark> markList;
    private List<SummaryEntry> summaryEntryList;
    private List<BlankTagHandler> tagHandlerListMark;
//    List<BlankTagHandler> tagHandlerListOther;


    //OnBlankClickListener blankClickListener;
    private DimensionsContainer dimensionsContainer;

//    View.OnLongClickListener onLongClickListener;
    private Context mainContext;

    public Blank(Context mainContext, LinearLayout blankLayout, Integer nameTopTextResId) {
        this.mainContext = mainContext;
        this.blankLayout = blankLayout;
        if (nameTopTextResId != null) this.nameTopTextResId = nameTopTextResId;

        dimensionsContainer = new DimensionsContainer(mainContext);
        dimensionsContainer.loadCurrentDimensionProfile();

    }

    public static void setTextByPrefs(TextView markView, Integer viewWidth, Integer iPref,
            String str) {
        switch (iPref) {
            case 0:
                markView.setEllipsize(TextUtils.TruncateAt.END);
                break;
            case 1:
                if (viewWidth != null) RunUtils.placeSingleLineText(markView, str, viewWidth, 6);
                break;
            case 2:
                //TODO: Implement: Full Text (Increase Column Width)
                break;
            default:
                str = "err";
        }
        markView.setText(str);
    }

    public void setContext(Context context) {
        this.mainContext = context;
    }

    public void reloadDimensionProfile() {
        dimensionsContainer.loadCurrentDimensionProfile();
    }

    public void loadDimensionProfile(String profile) {
        dimensionsContainer.loadDimensionProfile(profile);
    }

    public void setDimensionsList(List<Dimensions> dimensionsList) {
        dimensionsContainer.setDimensionsList(dimensionsList);
    }

    public void setDimensionsList(List<Dimensions> dimensionsList, boolean refresh) {
        dimensionsContainer.setDimensionsList(dimensionsList);
        if (refresh) {
            refreshViewsStyle();
        }
    }

    public DimensionsContainer getDimensionsContainer() {
        return dimensionsContainer;
    }

    public void setDimensionsContainer(DimensionsContainer dimensionsContainer) {
        this.dimensionsContainer = dimensionsContainer;
    }

    public void setBlankLayout(LinearLayout blankLayout) {
        this.blankLayout = blankLayout;
    }

//    public void setBlankClickListener(OnBlankClickListener blankClickListener) {
//        this.blankClickListener = blankClickListener;
//    }

    public void setData(String topText, List<StudyClass> classList, List<Student> studentList,
            List<StudyMark> markList) {
        this.topText = topText;
        this.classList = classList;
        this.studentList = studentList;
        this.markList = markList;

        this.summaryEntryList = null;
        this.tagHandlerListMark = null;
    }

    public void setDataSum(String topText, List<SummaryEntry> summaryEntryList,
            List<Student> studentList, List<BlankTagHandler> tagHandlerList) {
        this.topText = topText;
        this.summaryEntryList = summaryEntryList;
        this.studentList = studentList;
        this.tagHandlerListMark = tagHandlerList;

        this.classList = null;
        this.markList = null;
    }

    //TODO: Optimize
    private StudyMark findMark(long studentId, long classId) {
        if (markList == null) return new StudyMark(studentId, classId, StudyMark.TYPE_NO_MARK);
        for (StudyMark mark : markList) {
            if ((mark.getStudentId() == studentId) && (mark.getClassId() == classId)) {
                return mark;
            }
        }
        return new StudyMark(studentId, classId, StudyMark.TYPE_NO_MARK);
    }

    private BlankTagHandler findSumTag(long studentId, long entryId) {
        if (tagHandlerListMark == null) return null;
        for (BlankTagHandler tagHandler : tagHandlerListMark) {
            if ((tagHandler.getStudentId() == studentId) && (tagHandler.getClassId() == entryId)) {
                return tagHandler;
            }
        }
        return new BlankTagHandler(BlankTagHandler.CeilType.NO_MARK, entryId, studentId, "");
    }

    private String formatDate(String dateToFormat) {
        String finalDateTime = "";
        if (dateToFormat != null) {
            try {
                Date date = TableClasses.dateFormat.parse(dateToFormat);
                finalDateTime =
                        android.text.format.DateFormat.getDateFormat(mainContext).format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return finalDateTime;

    }

    private void setClickableView(View view, Integer bgColor) {//}, int normalStateRes) {
        if (bgColor != null) {
            StateListDrawable states = new StateListDrawable();
            states.addState(new int[]{android.R.attr.state_pressed},
                            mainContext.getResources()
                                    .getDrawable(R.drawable.btn_choice_bg_pressed)
            );


            GradientDrawable gd_normal = new GradientDrawable();
            gd_normal.setColor(bgColor);
            states.addState(new int[]{}, gd_normal);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                view.setBackgroundDrawable(states);
            } else {
                view.setBackground(states);
            }
        }
        view.setClickable(true);
        view.setOnClickListener(generalClickListener);
        view.setOnLongClickListener(generalLongClickListener);
    }

    //TODO: Reuse Views.
    public LinearLayout drawBlank() {
        //long startTime = DebugUtils.getNanoTime();

        assert (blankLayout != null);
//Init bgColor_Table and bgColor_Blank
        initBaseColors();
//Clear and prepare blankLayout
        initBlankLayout();
//InitBaseLayout()
        initBaseLayout();
//AddClassViews
        drawClassViews();
//Add Students
        drawStudentViews();
//AddMarks
        drawMarkViews();

       // DebugUtils.log_d("Delay of Blank.Draw() = " + (DebugUtils.getNanoTime() - startTime));

        return blankLayout;
    }

    public LinearLayout drawSummaryBlank() {
        //long startTime = DebugUtils.getNanoTime();

        assert (blankLayout != null);
//Init bgColor_Table and bgColor_Blank
        initBaseColors();
//Clear and prepare blankLayout
        initBlankLayout();
//InitBaseLayout()
        initBaseLayout();
//AddClassViews
        drawSumEntryViews();
//Add Students
        drawStudentViews();
//AddMarks
        drawSumValuesViews();
//
//        DebugUtils
//                .log_d("Delay of SummaryBlank.Draw() = " + (DebugUtils.getNanoTime() - startTime));

        return blankLayout;

    }

    private void initBaseColors() {
        bgColor_Blank = dimensionsContainer.findDimensions(KEY_BLANK).getBgColor();
        if (bgColor_Blank == null) bgColor_Blank = Color.DKGRAY;
        bgColor_Table = dimensionsContainer.findDimensions(KEY_TABLE).getBgColor();
        if (bgColor_Table == null) bgColor_Table = Color.BLACK;
    }

    private void initBlankLayout() {
        blankLayout.removeAllViews();
        blankLayout.clearDisappearingChildren(); // Clear animation.
        blankLayout.setOrientation(LinearLayout.VERTICAL);
        blankLayout.setBackgroundColor(bgColor_Blank);
    }

    private void initBaseLayout() {
//        if (layoutHandler == null) layoutHandler = new BlankLayoutHandler();

        //Add the Topic text
        if (topText != null) {
            TextView topicText = new TextView(mainContext);
            topicText.setText(topText);
            dimensionsContainer.setViewStyle(KEY_TOPIC_TEXT, topicText);
            topicText.setTag(new BlankTagHandler(BlankTagHandler.CeilType.NONE, R.id.blank_topic));
            topicText.setClickable(true);
            topicText.setOnClickListener(generalClickListener);
            blankLayout.addView(topicText);
        }
//Add topLayout
        //if (topLayout == null) {
        topLayout = new LinearLayout(mainContext);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        topLayout.setLayoutParams(layoutParams_WC_WC);
        topLayout.setBackgroundColor(bgColor_Table);
        blankLayout.addView(topLayout);
//Add numTop
        TextView numTop = new TextView(mainContext);
        numTop.setText(R.string.symbol_num);
        dimensionsContainer.setViewStyle(KEY_NUM_TOP, numTop);
        numTop.setTag(new BlankTagHandler(BlankTagHandler.CeilType.NONE, R.id.blank_num_top));
        numTop.setClickable(true);
        numTop.setOnClickListener(generalClickListener);
        topLayout.addView(numTop);
//Add nameTop
        TextView nameTop = new TextView(mainContext);
        nameTop.setText(nameTopTextResId);
        dimensionsContainer.setViewStyle(KEY_NAME_TOP, nameTop);
        nameTop.setTag(new BlankTagHandler(BlankTagHandler.CeilType.NONE, R.id.blank_name_top));
        nameTop.setClickable(true);
        nameTop.setOnClickListener(generalClickListener);
        topLayout.addView(nameTop);






//Add topHScrollView
//        if (layoutHandler.topHScrollView == null) {
        topHScrollView = new SyncedHorizontalScrollView(mainContext);
        topHScrollView.setLayoutParams(layoutParams_WC_MP);
        topHScrollView.setHorizontalScrollBarEnabled(false);
        if (dimensionsContainer.findDimensions(KEY_NAME_TOP).getBgColor() !=
                null) //OverScrollColor
        {
            topHScrollView.setBackgroundColor(
                    dimensionsContainer.findDimensions(KEY_NAME_TOP).getBgColor());
        }
        topLayout.addView(topHScrollView);

//Add topScrollLayout
        // if(layoutHandler.topScrollLayout == null) {
        topScrollLayout = new LinearLayout(mainContext);
        topScrollLayout.setBackgroundColor(bgColor_Table);
        topHScrollView.addView(topScrollLayout);

//Add bodyScrollView
        //if(layoutHandler.bodyScrollView == null) {
        bodyScrollView = new ScrollView(mainContext);
        bodyScrollView.setLayoutParams(layoutParams_MP_WC);
        bodyScrollView.setVerticalScrollBarEnabled(false);
        //bodyScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        blankLayout.addView(bodyScrollView);
//Add bodyLayout
        //if(layoutHandler.bodyLayout == null) {
        bodyLayout = new LinearLayout(mainContext);
        bodyLayout.setOrientation(LinearLayout.HORIZONTAL);
        bodyLayout.setLayoutParams(layoutParams_WC_WC);
        bodyScrollView.addView(bodyLayout);

//Add bodyLeftLayout
        //if(layoutHandler.bodyLeftLayout == null) {
        bodyLeftLayout = new LinearLayout(mainContext);
        bodyLeftLayout.setOrientation(LinearLayout.VERTICAL);
        bodyLeftLayout.setLayoutParams(layoutParams_WC_WC);
        bodyLeftLayout.setBackgroundColor(bgColor_Table);
        bodyLayout.addView(bodyLeftLayout);

//Add bodyHScrollView
        //if(layoutHandler.bodyHScrollView == null) {
        bodyHScrollView = new SyncedHorizontalScrollView(mainContext);
        bodyHScrollView.setLayoutParams(layoutParams_WC_WC);
        bodyHScrollView.setHorizontalScrollBarEnabled(false);
        if (dimensionsContainer.findDimensions(KEY_NAME_LEFT).getBgColor() !=
                null)//OverScrollColor
        {
            bodyHScrollView.setBackgroundColor(
                    dimensionsContainer.findDimensions(KEY_NAME_LEFT).getBgColor());
        }
        bodyLayout.addView(bodyHScrollView);

//Add bodyRightLayout
        //if(layoutHandler.bodyRightLayout == null) {
        bodyRightLayout = new LinearLayout(mainContext);
        bodyRightLayout.setOrientation(LinearLayout.VERTICAL);
        bodyRightLayout.setBackgroundColor(bgColor_Table);
        bodyHScrollView.addView(bodyRightLayout);

//Add Scroll Listeners

        ScrollManager scrollManager = new ScrollManager();
        scrollManager.setScrollDirection(ScrollManager.SCROLL_HORIZONTAL);
        scrollManager.addScrollClient(topHScrollView);
        scrollManager.addScrollClient(bodyHScrollView);

//        bodyScrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                bodyHScrollView.getParent().requestDisallowInterceptTouchEvent(false);
//                return false;
//            }
//        });
//        bodyHScrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });

        //ScrollManager.disallowParentScroll(bodyHScrollView, 100);
        //ScrollManager.disallowChildScroll(bodyHScrollView, bodyLayout, 100);
        //bodyHScrollView.getWidth());
        //Dimensions.dipToPixels(mainContext, 800));
//        ScrollManager
//                .disallowParentScroll(bodyHScrollView, Dimensions.dipToPixels(mainContext, 200));
        //TODO: disallowParentScroll(bodyHScrollView, Dimensions.dipToPixels(mainContext, 50));

    }

    private void drawClassViews() {
        //Add classViews
        BlankTagHandler tagHandler;
        Dimensions dims = dimensionsContainer.findDimensions(KEY_CLASS_TOP);
        Integer iPref = Integer.valueOf(PrefsManager.getInstance(mainContext).getPrefs()
                                                .getString(PrefsManager.PREFS_BLANK_CLASS_STYLE,
                                                           "0"));
        String str;
        if ((classList != null) && (!classList.isEmpty())) {
            for (StudyClass studyClass : classList) {
                TextView classView = new TextView(mainContext);
                switch (iPref) {
                    case 0:
                        str = studyClass.getAbbr() + "\n" + formatDate(studyClass.getDate());
                        break;
                    case 1:
                        str = studyClass.getAbbr();
                        break;
                    case 2:
                        str = formatDate(studyClass.getDate());
                        break;
                    default:
                        str = "err";
                }

                classView.setText(str);
                dims.setStyle(classView);
                if (studyClass.getRowColor() != null) {
                    setClickableView(classView, studyClass.getRowColor());
                } else {
                    setClickableView(classView, dims.getBgColor());
                }
                if (studyClass.getTextColor() != null) {
                    classView.setTextColor(studyClass.getTextColor());
                }
                if (studyClass.getRowWidth() != null) {
                    classView.setWidth(studyClass.getRowWidth());
                }

                //Add tagHandler
                tagHandler =
                        new BlankTagHandler(BlankTagHandler.CeilType.CLASS, R.id.blank_class_top);
                tagHandler.setClassId(studyClass.getId());
                tagHandler.setTextView(classView);
                classView.setTag(tagHandler);
                topScrollLayout.addView(classView);
            }
        }
//Add addColumnView
        TextView addColView = new TextView(mainContext);
        addColView.setText(mainContext.getString(R.string.symbol_col_add));
        //addColView.setBackgroundResource(android.R.drawable.ic_input_add);
        //TODO: Set Image to AddColumnView
        //dimensionsContainer.setViewStyle(KEY_ADD_COL, addColView);
        Dimensions dimsAddCol = dimensionsContainer.findDimensions(KEY_ADD_COL);
        dimsAddCol.setStyle(addColView);
        if (dims.getTextSize() != null) addColView.setTextSize(dims.getTextSize());
        setClickableView(addColView, dims.getBgColor());//, android.R.drawable.ic_input_add);
        //Add tagHandler
        tagHandler =
                new BlankTagHandler(BlankTagHandler.CeilType.ADD_COL_CEIL, R.id.blank_add_column);
        tagHandler.setTextView(addColView);
        addColView.setTag(tagHandler);
        topScrollLayout.addView(addColView);
        //TODO: Add Absent Col
        //TODO: Add averageMark Col
    }

    private void drawSumEntryViews() {
        //Add entriesViews
        BlankTagHandler tagHandler;
        Dimensions dims = dimensionsContainer.findDimensions(KEY_CLASS_TOP);
        if ((summaryEntryList != null) && (!summaryEntryList.isEmpty())) {
            for (SummaryEntry entry : summaryEntryList) {
                TextView entryView = new TextView(mainContext);
                entryView.setText(entry.toString());
                dims.setStyle(entryView);
                if (entry.getRowColor() != null) {
                    setClickableView(entryView, entry.getRowColor());
                } else {
                    setClickableView(entryView, dims.getBgColor());
                }
                if (entry.getTextColor() != null) {
                    entryView.setTextColor(entry.getTextColor());
                }
                if (entry.getRowWidth() != null) {
                    entryView.setWidth(entry.getRowWidth());
                }

                //Add tagHandler
                tagHandler = new BlankTagHandler(BlankTagHandler.CeilType.SUM_ENTRY,
                                                 R.id.blank_class_top);
                tagHandler.setClassId(entry.getId());
                tagHandler.setTextView(entryView);
                entryView.setTag(tagHandler);
                topScrollLayout.addView(entryView);
            }
        }
//Add addColumnView
        TextView addColView = new TextView(mainContext);
        addColView.setText(mainContext.getString(R.string.symbol_col_add));
        //addColView.setBackgroundResource(android.R.drawable.ic_input_add);
        //TODO: Set Image to AddColumnView
        //dimensionsContainer.setViewStyle(KEY_ADD_COL, addColView);
        Dimensions dimsAddCol = dimensionsContainer.findDimensions(KEY_ADD_COL);
        dimsAddCol.setStyle(addColView);
        if (dims.getTextSize() != null) addColView.setTextSize(dims.getTextSize());
        setClickableView(addColView, dims.getBgColor());//, android.R.drawable.ic_input_add);
        //Add tagHandler
        tagHandler = new BlankTagHandler(BlankTagHandler.CeilType.ADD_COL_CEIL_SUM,
                                         R.id.blank_add_column);
        tagHandler.setTextView(addColView);
        addColView.setTag(tagHandler);
        topScrollLayout.addView(addColView);
//Add resultView
        TextView resultView = new TextView(mainContext);
        resultView.setText(mainContext.getString(R.string.result));
        dims.setStyle(resultView);
        if (dims.getTextSize() != null) resultView.setTextSize(dims.getTextSize());
        setClickableView(resultView, dims.getBgColor());//, android.R.drawable.ic_input_add);
        //Add tagHandler
        tagHandler = new BlankTagHandler(BlankTagHandler.CeilType.SUM_RESULT, -1); //!!!
        tagHandler.setTextView(resultView);
        resultView.setTag(tagHandler);
        topScrollLayout.addView(resultView);
//Add resultView
        Boolean isPref = PrefsManager.getInstance(mainContext).getPrefs()
                .getBoolean(PrefsManager.PREFS_BLANK_SUMMARY_SHOW_COLS_AVG, true);
        if (isPref) {
            TextView avgResultView = new TextView(mainContext);
            avgResultView.setText(mainContext.getString(R.string.result_avg));
            dims.setStyle(avgResultView);
            if (dims.getTextSize() != null) avgResultView.setTextSize(dims.getTextSize());
            setClickableView(avgResultView, dims.getBgColor());//, android.R.drawable.ic_input_add);
            //Add tagHandler
            tagHandler = new BlankTagHandler(BlankTagHandler.CeilType.SUM_RESULT, -1); //!!!
            tagHandler.setTextView(avgResultView);
            avgResultView.setTag(tagHandler);
            topScrollLayout.addView(avgResultView);
        }
        //TODO: Add 100b | ECTS by Options
    }

    private void drawStudentViews() {
        //Add number and Students names
        Dimensions dimsNum = dimensionsContainer.findDimensions(KEY_NUM_LEFT);
        Dimensions dimsName = dimensionsContainer.findDimensions(KEY_NAME_LEFT);
        Integer iPref = Integer.valueOf(PrefsManager.getInstance(mainContext).getPrefs()
                                                .getString(PrefsManager.PREFS_BLANK_NAME_FORMAT,
                                                           "0"));
        String str;
        BlankTagHandler tagHandler;
        if ((studentList != null) && (!studentList.isEmpty())) {
            for (int i = 0; i < studentList.size(); i++) {
                //Add LayoutLeft
                LinearLayout layoutLeft = new LinearLayout(mainContext);
                layoutLeft.setOrientation(LinearLayout.HORIZONTAL);
                layoutLeft.setLayoutParams(layoutParams_WC_WC);
                bodyLeftLayout.addView(layoutLeft);
                //Add numBody
                TextView numBody = new TextView(mainContext);
                numBody.setText(String.valueOf(i + 1));
                dimsNum.setStyle(numBody);
                numBody.setTag(
                        new BlankTagHandler(BlankTagHandler.CeilType.NONE, R.id.blank_num_body));
                numBody.setClickable(true);
                numBody.setOnClickListener(generalClickListener);
                layoutLeft.addView(numBody);
                //Add nameBody
                TextView nameBody = new TextView(mainContext);
                int formatId;
                if ((studentList.get(i).getMiddleName() == null)
                        || ((studentList.get(i).getMiddleName().equals("")))) {
                    if (iPref == 0) { formatId = 4; } else formatId = 3;
                } else { formatId = iPref; }
                switch (formatId) {
                    case 0:
                        str = String.format("%s %c.%c.",
                                            studentList.get(i).getLastName(),
                                            studentList.get(i).getFirstName().charAt(0),
                                            studentList.get(i).getMiddleName().charAt(0));
                        break;
                    case 1:
                        str = String.format("%s %s %s",
                                            studentList.get(i).getLastName(),
                                            studentList.get(i).getFirstName(),
                                            studentList.get(i).getMiddleName());
                        break;
                    case 2:
                        str = String.format("%s %s %c.", studentList.get(i).getLastName(),
                                            studentList.get(i).getFirstName(),
                                            studentList.get(i).getMiddleName().charAt(0));
                        break;
                    case 3:
                        str = String.format("%s %s", studentList.get(i).getLastName(),
                                            studentList.get(i).getFirstName());
                        break;
                    case 4:
                        str = String.format("%s %c.", studentList.get(i).getLastName(),
                                            studentList.get(i).getFirstName().charAt(0));
                        break;
                    default:
                        str = "err";
                }
                nameBody.setText(str);
                dimsName.setStyle(nameBody);
                setClickableView(nameBody, dimsName.getBgColor());
                //nameBody.setMinHeight(Dimensions.dipToPixels(mainContext,48));
                //Add tagHandler
                tagHandler =
                        new BlankTagHandler(BlankTagHandler.CeilType.STUDENT, R.id.blank_name_body);
                tagHandler.setStudentId(studentList.get(i).getId());
                tagHandler.setTextView(nameBody);
                nameBody.setTag(tagHandler);
                //DebugUtils.testStudentCols(nameBody);
                layoutLeft.addView(nameBody);

            }
        }
//Add addRowCeil / Absent Row
        if (studentList == null || studentList.isEmpty() || (classList != null && !classList.isEmpty())
                || (studentList.isEmpty() && summaryEntryList != null &&
                !summaryEntryList.isEmpty())) {
            //Add LayoutLeft
            LinearLayout layoutLeft = new LinearLayout(mainContext);
            layoutLeft.setOrientation(LinearLayout.HORIZONTAL);
            layoutLeft.setLayoutParams(layoutParams_MP_WC);
            //layoutLeft.setX(dims.getViewWidth()+dims.getMarginsLeft()*2);
            layoutLeft.setGravity(Gravity.RIGHT);
            layoutLeft.setBackgroundColor(bgColor_Table);
            bodyLeftLayout.addView(layoutLeft);
            //Add nameBody
            TextView nameBody = new TextView(mainContext);
            //nameBody.setWidth(dims.getViewWidth());
            //nameBody.setGravity(Gravity.CENTER);

            Dimensions dimsAbsent = dimensionsContainer.findDimensions(KEY_ABSENT_CEIL);
            dimsAbsent.setStyle(nameBody);
            nameBody.setGravity(Gravity.CENTER);
            int width = 0;
            if (dimsName.getViewWidth() != null) width += dimsName.getViewWidth();
            if (dimsNum.getViewWidth() != null) width += dimsNum.getViewWidth();
            if (width != 0) {
                width += dimsNum.getMarginsLeft() + dimsNum.getMarginsRight();
                nameBody.setWidth(width);
            }
            if (studentList.isEmpty()) {
                nameBody.setText(R.string.symbol_row_add);
                tagHandler = new BlankTagHandler(BlankTagHandler.CeilType.ADD_ROW_CEIL,
                                                 R.id.blank_absent_row);
                setClickableView(nameBody, dimsAbsent.getBgColor());
            } else {
                nameBody.setText(R.string.blank_absent_row);
                tagHandler =
                        new BlankTagHandler(BlankTagHandler.CeilType.NONE, R.id.blank_absent_row);
                nameBody.setClickable(true);
                nameBody.setOnClickListener(generalClickListener);
            }
            tagHandler.setTextView(nameBody);
            nameBody.setTag(tagHandler);
            layoutLeft.addView(nameBody);
        }

    }

    private void drawMarkViews() {
        int[] absentInClass = new int[classList.size()];
        Arrays.fill(absentInClass, 0);
        //Add markView
        BlankTagHandler tagHandler;
        Dimensions dims = dimensionsContainer.findDimensions(KEY_CLASS_BODY);
        Dimensions dims1 = dimensionsContainer.findDimensions(KEY_ADD_COL);
        String absentSymbol = PrefsManager.getInstance(mainContext).getPrefs()
                .getString(PrefsManager.PREFS_BLANK_ABSENT_SYMBOL,
                           mainContext.getResources().getString(R.string.symbol_absent));
        Integer iPref = Integer.valueOf(PrefsManager.getInstance(mainContext).getPrefs()
                                                .getString(PrefsManager.PREFS_BLANK_SYMBOL_FORMAT,
                                                           "0"));
        if ((studentList != null) && (!studentList.isEmpty()) && (classList != null) &&
                (!classList.isEmpty())) {
            StudyClass studyClass;
            for (Student student : studentList) {
                //Add layoutRight
                LinearLayout layoutRight = new LinearLayout(mainContext);
                layoutRight.setOrientation(LinearLayout.HORIZONTAL);
                layoutRight.setLayoutParams(layoutParams_WC_WC);
                bodyRightLayout.addView(layoutRight);

                for (int num = 0; num < classList.size(); num++) {
                    studyClass = classList.get(num);
                    //Add markView
                    TextView markView = new TextView(mainContext);
                    //dims.setSingleTextLine(false);
                    dims.setStyle(markView);
                    //markView.setMaxLines(1);
                    tagHandler = new BlankTagHandler();
                    tagHandler.setStudentId(student.getId());
                    tagHandler.setClassId(studyClass.getId());
                    if (studyClass.getRowColor() != null) {
                        //markView.setBackgroundColor(studyClass.getRowColor());
                        setClickableView(markView, studyClass.getRowColor());
                    } else {
                        setClickableView(markView, dims.getBgColor());
                    }
                    if (studyClass.getTextColor() != null) {
                        markView.setTextColor(studyClass.getTextColor());
                    }
                    if (studyClass.getRowWidth() != null) {
                        markView.setWidth(studyClass.getRowWidth());

                    }
                    //Add tagHandler and setText
                    if (markList != null) {
                        StudyMark mark = findMark(student.getId(), studyClass.getId());
                        switch (mark.getType()) {
                            case StudyMark.TYPE_NO_MARK:
                                tagHandler.setCeilType(BlankTagHandler.CeilType.NO_MARK);
                                break;
                            case StudyMark.TYPE_MARK:
                                tagHandler.setCeilType(BlankTagHandler.CeilType.MARK);
                                //tagHandler.setMarkId(mark.getMarkId());
                                markView.setText(mark.toString());
                                break;
                            case StudyMark.TYPE_ABSENT:
                                absentInClass[num] += 1;
                                tagHandler.setCeilType(BlankTagHandler.CeilType.ABSENT);
                                markView.setText(absentSymbol);
                                break;
                            case StudyMark.TYPE_SYMBOL:
                                tagHandler.setCeilType(BlankTagHandler.CeilType.SYMBOL);
                                setTextByPrefs(markView, studyClass.getRowWidth() != null ?
                                                       studyClass.getRowWidth() :
                                                       dims.getViewWidth(),
                                               iPref, mark.toString()
                                );
                                break;
                        }
                    }
                    tagHandler.setTextView(markView);
                    tagHandler.setViewId(R.id.blank_class_body);
                    markView.setTag(tagHandler);
                    layoutRight.addView(markView);

                }
                //Add ClassAddView
                TextView addClassViewBody = new TextView(mainContext);
                dims1.setStyle(addClassViewBody);
                //Add tagHandler
                tagHandler =
                        new BlankTagHandler(BlankTagHandler.CeilType.NONE, R.id.blank_add_column);
                addClassViewBody.setClickable(true);
                addClassViewBody.setOnClickListener(generalClickListener);
                tagHandler.setTextView(addClassViewBody);
                addClassViewBody.setTag(tagHandler);

                //DebugUtils.testAddCol(addClassViewBody);

                layoutRight.addView(addClassViewBody);



            }

//Add Absent Row
            dims = dimensionsContainer.findDimensions(KEY_ABSENT_CEIL);
            //Add layoutRight
            LinearLayout layoutRight = new LinearLayout(mainContext);
            layoutRight.setOrientation(LinearLayout.HORIZONTAL);
            layoutRight.setLayoutParams(layoutParams_WC_WC);
            bodyRightLayout.addView(layoutRight);

            for (int num = 0; num < classList.size(); num++) {
                studyClass = classList.get(num);
                //Add markView
                TextView markView = new TextView(mainContext);
                dims.setStyle(markView);
                if (studyClass.getRowWidth() != null) {
                    markView.setWidth(studyClass.getRowWidth());
                }
                tagHandler = new BlankTagHandler();
                tagHandler.setViewId(R.id.blank_absent_row);
                tagHandler.setCeilType(BlankTagHandler.CeilType.ABSENT_ROW_CEIL);
                tagHandler.setClassId(studyClass.getId());
                tagHandler.setStudentId(-1L);
                markView.setText(String.valueOf(absentInClass[num]));
                tagHandler.setTextView(markView);
                markView.setTag(tagHandler);
                layoutRight.addView(markView);
            }
            //Add ClassAddView
            TextView addClassViewBody = new TextView(mainContext);
            dims1.setStyle(addClassViewBody);
            if (dims.getTextSize() != null) addClassViewBody.setTextSize(dims.getTextSize());
            //Add tagHandler
            tagHandler = new BlankTagHandler(BlankTagHandler.CeilType.NONE, R.id.blank_add_column);
            addClassViewBody.setClickable(true);
            addClassViewBody.setOnClickListener(generalClickListener);
            tagHandler.setTextView(addClassViewBody);
            addClassViewBody.setTag(tagHandler);
            layoutRight.addView(addClassViewBody);
        }


    }

    private void drawSumValuesViews() {
        int resultMark;
        int markNum;
        //Add sumView
        BlankTagHandler tagHandler;
        Dimensions dims = dimensionsContainer.findDimensions(KEY_CLASS_BODY);
        Dimensions dims1 = dimensionsContainer.findDimensions(KEY_ADD_COL);
        Boolean isAvgPref = PrefsManager.getInstance(mainContext).getPrefs()
                .getBoolean(PrefsManager.PREFS_BLANK_SUMMARY_SHOW_COLS_AVG, true);
        if ((studentList != null) && (!studentList.isEmpty()) && (summaryEntryList != null) &&
                (!summaryEntryList.isEmpty())) {
            for (Student student : studentList) {
                //Add layoutRight
                LinearLayout layoutRight = new LinearLayout(mainContext);
                layoutRight.setOrientation(LinearLayout.HORIZONTAL);
                layoutRight.setLayoutParams(layoutParams_WC_WC);
                bodyRightLayout.addView(layoutRight);

                resultMark = 0;
                markNum = 0;

                for (SummaryEntry entry : summaryEntryList) {
                    //Add markView
                    TextView markView = new TextView(mainContext);
                    dims.setStyle(markView);
                    if (entry.getRowColor() != null) {
                        markView.setBackgroundColor(entry.getRowColor());
                    }
                    if (entry.getTextColor() != null) {
                        markView.setTextColor(entry.getTextColor());
                    }
                    if (entry.getRowWidth() != null) {
                        markView.setWidth(entry.getRowWidth());
                    }
                    //Add tagHandler and setText
                    tagHandler = findSumTag(student.getId(), entry.getId());
                    if (entry.isCounted()) {
                        resultMark += tagHandler.getValue();
                        markNum++;
                    }
                    markView.setText(tagHandler.toString());
                    tagHandler.setTextView(markView);
                    //tagHandler.setViewId(R.id.blank_class_body);
                    markView.setClickable(true);
                    markView.setOnClickListener(generalClickListener);
                    markView.setTag(tagHandler);
                    layoutRight.addView(markView);
                }
                //Add EntryAddView
                TextView addEntryViewBody = new TextView(mainContext);
                dims1.setStyle(addEntryViewBody);
                //Add tagHandler
                tagHandler =
                        new BlankTagHandler(BlankTagHandler.CeilType.NONE, R.id.blank_add_column);
                addEntryViewBody.setClickable(true);
                addEntryViewBody.setOnClickListener(generalClickListener);
                tagHandler.setTextView(addEntryViewBody);
                addEntryViewBody.setTag(tagHandler);
                layoutRight.addView(addEntryViewBody);

                //Add ResultView
                TextView addResultViewBody = new TextView(mainContext);
                dims.setStyle(addResultViewBody);
                addResultViewBody.setText(String.valueOf(resultMark));
                //Add tagHandler
                tagHandler = new BlankTagHandler(BlankTagHandler.CeilType.NONE, -1);
                //TODO: Add id
                tagHandler.setTextView(addResultViewBody);
                addResultViewBody.setTag(tagHandler);
                layoutRight.addView(addResultViewBody);
                //Add resultView
                if (isAvgPref){
                    TextView addAvgResultViewBody = new TextView(mainContext);
                    dims.setStyle(addAvgResultViewBody);
                    float avg = 0;
                    if(markNum > 0) {avg = (float) resultMark/markNum;}
                    addAvgResultViewBody.setText(String.format("%.1f",avg));
                    //Add tagHandler
                    tagHandler = new BlankTagHandler(BlankTagHandler.CeilType.NONE, -1);
                    //TODO: Add id
                    tagHandler.setTextView(addAvgResultViewBody);
                    addAvgResultViewBody.setTag(tagHandler);
                    layoutRight.addView(addAvgResultViewBody);
                }
            }

        }
    }

    private void refreshViewsStyle() {
        //TODO implement
    }

    public interface OnBlankClickListener {
        void onBlankClick(View v);

        void onBlankLongClick(View v);
    }

//    private void addTagToList(BlankTagHandler tagHandler){
//
//    }
}

