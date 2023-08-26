package com.drprog.sjournal.blank;

import static com.drprog.sjournal.blank.BlankFragment.KEY_FRAGMENT_BLANK_STYLE;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.drprog.sjournal.db.prefs.Dimensions;
import com.drprog.sjournal.db.prefs.DimensionsContainer;
import com.drprog.sjournal.db.prefs.TableDimensions;
import com.drprog.sjournal.dialogs.DimensionSetDialog;
import com.drprog.sjournal.dialogs.utils.ColorAdapter;
import com.drprog.sjournal.dialogs.utils.DialogClickListener;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.RunUtils;

import java.util.List;

/**
 * Created by Romka on 15.08.2014.
 */
public class BlankStyleFragment extends Fragment implements DialogClickListener {
    public static final String TAG = "BLANK_STYLE_FRAGMENT";

    private static final int CODE_DIMENSION_SET = 1;
    public final View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer key = null;
            int id;
            Object tag = v.getTag();
            if (tag != null && tag instanceof BlankTagHandler) {
                id = ((BlankTagHandler) tag).getViewId();
            } else {

                id = v.getId();
            }
            if (id == R.id.blank_topic) {
                key = Blank.KEY_TOPIC_TEXT;
            } else if (id == R.id.blank_num_top) {
                key = Blank.KEY_NUM_TOP;
            } else if (id == R.id.blank_num_body) {
                key = Blank.KEY_NUM_LEFT;
            } else if (id == R.id.blank_name_top) {
                key = Blank.KEY_NAME_TOP;
            } else if (id == R.id.blank_name_body) {
                key = Blank.KEY_NAME_LEFT;
            } else if (id == R.id.blank_class_top) {
                key = Blank.KEY_CLASS_TOP;
            } else if (id == R.id.blank_class_body) {
                key = Blank.KEY_CLASS_BODY;
            } else if (id == R.id.blank_add_column) {
                key = Blank.KEY_ADD_COL;
            } else if (id == R.id.blank_absent_row) {
                key = Blank.KEY_ABSENT_CEIL;
            }
            showDimensionSetDialog(key);
        }
    };
    private final AdapterView.OnItemSelectedListener spinnerColorSelectedListener =
            new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position,
                        long id) {
                    if (position < 0) return;
                    Integer key = null;
                    int parentId = parent.getId();
                    if (parentId == R.id.spinner1) {
                        key = Blank.KEY_BLANK;
                    } else if (parentId == R.id.spinner2) {
                        key = Blank.KEY_TABLE;
                    }
                    if (key == null) return;
                    int color = (int) id;
                    Dimensions dims = dimensionsContainer.findDimensions(key);
                    if (color != dims.getBgColor()) {
                        dims.setBgColor(color);
                        dimensionsContainer.updateDimensions(dims);
                        refresh();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };
    private Button btnTopText;
    private Button btnNumTop;
    private Button btnNumBody;
    private Button btnNameTop;
    private Button btnNameBody;
    private Button btnClassTop;
    private Button btnClassBody;
    private Button btnAddCol;
    private Button btnAbsent;
    private Spinner spinnerBgBlank;
    private Spinner spinnerBgTable;
    private Spinner spinnerProfiles;
    private TextView viewCurrentProfile;
    //private Button buttonLoad;
    private Button buttonSave;
    private Button buttonDelete;
    private String currentProfile;
    private DimensionsContainer dimensionsContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_blank_style, container, false);
        btnTopText = (Button) v.findViewById(R.id.blank_topic);
        btnNumTop = (Button) v.findViewById(R.id.blank_num_top);
        btnNumBody = (Button) v.findViewById(R.id.blank_num_body);
        btnNameTop = (Button) v.findViewById(R.id.blank_name_top);
        btnNameBody = (Button) v.findViewById(R.id.blank_name_body);
        btnClassTop = (Button) v.findViewById(R.id.blank_class_top);
        btnClassBody = (Button) v.findViewById(R.id.blank_class_body);
        btnAddCol = (Button) v.findViewById(R.id.blank_add_column);
        btnAbsent = (Button) v.findViewById(R.id.blank_absent_row);
        spinnerBgBlank = (Spinner) v.findViewById(R.id.spinner1);
        spinnerBgTable = (Spinner) v.findViewById(R.id.spinner2);
        //viewCurrentProfile = (TextView) v.findViewById(R.id.view_current_profile);
        buttonSave = (Button) v.findViewById(R.id.button_save);
        buttonDelete = (Button) v.findViewById(R.id.button_delete);
        //buttonLoad = (Button) v.findViewById(R.id.button_load);
        spinnerProfiles = (Spinner) v.findViewById(R.id.spinner3);
        v.findViewById(R.id.button_close).setOnClickListener(v1 -> getFragmentManager()
                .popBackStackImmediate(KEY_FRAGMENT_BLANK_STYLE,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE));
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dimensionsContainer = new DimensionsContainer(getActivity());
        dimensionsContainer.loadCurrentDimensionProfile();

        final ColorAdapter colorAdapter =
                new ColorAdapter(getActivity(), android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBgBlank.setAdapter(colorAdapter);
        spinnerBgTable.setAdapter(colorAdapter);
        initColorSpinnerPos(colorAdapter, spinnerBgBlank, Blank.KEY_BLANK);
        initColorSpinnerPos(colorAdapter, spinnerBgTable, Blank.KEY_TABLE);

        initCurrentProfileSpinner();

        spinnerBgBlank.setOnItemSelectedListener(spinnerColorSelectedListener);
        spinnerBgTable.setOnItemSelectedListener(spinnerColorSelectedListener);


        AdapterView.OnItemSelectedListener profileSelectedListener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                            long id) {
                        String newProfile = (String) parent.getSelectedItem();
                        if (newProfile != null && !newProfile
                                .equals(DimensionsContainer.getCurrentProfile(getActivity()))) {
                            DimensionsContainer.setCurrentProfile(getActivity(), newProfile);
                            dimensionsContainer.loadCurrentDimensionProfile();
                            refresh();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                };
        spinnerProfiles.setOnItemSelectedListener(profileSelectedListener);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener okClickListener =
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (DimensionsContainer
                                        .deleteProfile(getActivity(),
                                                       spinnerProfiles.getSelectedItem()
                                                               .toString())) {
                                    initCurrentProfileSpinner();
                                    dimensionsContainer.loadCurrentDimensionProfile();
                                    refresh();
                                    RunUtils.showToast(getActivity(),
                                                       R.string.dialog_profile_delete_success);
                                }
                            }
                        };

                AlertDialog dialog = RunUtils.getAlertDialog(getActivity(),
                                                             android.R.drawable.ic_dialog_alert,
                                                             R.string.dialog_delete_profile_title,
                                                             null,
                                                             true, true, okClickListener, null,
                                                             null, null);
                dialog.show();
            }
        });


        btnTopText.setOnClickListener(btnClickListener);
        btnNumTop.setOnClickListener(btnClickListener);
        btnNumBody.setOnClickListener(btnClickListener);
        btnNameTop.setOnClickListener(btnClickListener);
        btnNameBody.setOnClickListener(btnClickListener);
        btnAddCol.setOnClickListener(btnClickListener);
        btnClassTop.setOnClickListener(btnClickListener);
        btnClassBody.setOnClickListener(btnClickListener);
        btnAbsent.setOnClickListener(btnClickListener);
//        buttonLoad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showLoadDialog();
//            }
//        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });
    }

    private void initColorSpinnerPos(ColorAdapter colorAdapter, Spinner spinner, int dimsKey) {
        Integer bgColor = dimensionsContainer.findDimensions(dimsKey).getBgColor();
        if (bgColor != null) {
            int pos = colorAdapter.getItemPos(bgColor);
            if (pos != -1) spinner.setSelection(pos);
        }
    }

    private void showSaveDialog() {

        final List<String> profileList = DimensionsContainer.getProfileNames(getActivity());
        final EditText editText = new EditText(getActivity());
        editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        editText.setHint(R.string.dialog_save_dimensions_hint);
        editText.setText(currentProfile);

        DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String profileName = editText.getText().toString();
                long res = -1;
                if (profileName == null || profileName.isEmpty() || profileName.matches("[ ]+")) {
                    RunUtils.showToast(getActivity(), R.string.dialog_profile_not_saved);
                } else if (profileList.contains(profileName)) {
                    if (TableDimensions.STR_DEFAULT.equals(profileName)) {
                        RunUtils.showToast(getActivity(),
                                           R.string.dialog_not_try_update_default_profile);
                    } else {
                        res = dimensionsContainer.updateDimensionProfile(profileName);
                    }
                } else {
                    res = dimensionsContainer.saveDimensionProfile(profileName);
                }
                if (res > 0) {
                    RunUtils.showToast(getActivity(), R.string.dialog_profile_save_success);
                    initCurrentProfileSpinner();
                }
            }
        };

        AlertDialog dialog = RunUtils.getAlertDialog(getActivity(),
                                                     android.R.drawable.ic_dialog_info,
                                                     R.string.dialog_save_dimensions_title, null,
                                                     true, true, okClickListener, null, null,
                                                     editText);
        dialog.show();
    }

    private void showLoadDialog() {
        final List<String> profileList = DimensionsContainer.getProfileNames(getActivity());
        ArrayAdapter<String> listAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.item_list,
                                         profileList);
        DialogInterface.OnClickListener listClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DimensionsContainer
                                .setCurrentProfile(getActivity(), profileList.get(which));
                        dimensionsContainer.loadCurrentDimensionProfile();
                        initCurrentProfileSpinner();
                        refresh();
                    }
                };
        AlertDialog dialog = RunUtils.getAlertDialog(getActivity(),
                                                     android.R.drawable.ic_dialog_info,
                                                     R.string.dialog_load_dimensions_title, null,
                                                     false, false, null, listAdapter,
                                                     listClickListener, null);
        dialog.show();
    }

    private void initCurrentProfileSpinner() {
        currentProfile = DimensionsContainer.getCurrentProfile(getActivity());
//        if (currentProfile != null) viewCurrentProfile.setText(currentProfile);
        final List<String> profileList = DimensionsContainer.getProfileNames(getActivity());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                                         profileList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfiles.setAdapter(adapter);
        spinnerProfiles.setSelection(profileList.indexOf(currentProfile));

    }

    private void showDimensionSetDialog(Integer key) {
        if (key == null) return;
        Dimensions dims = new Dimensions(dimensionsContainer.findDimensions(key));
        DimensionSetDialog.newInstance(this, CODE_DIMENSION_SET, dims)
                .show(getActivity().getFragmentManager(), "dialog");
    }


    private void refresh() {
        BlankFragment blankFragment =
                (BlankFragment) getFragmentManager().findFragmentByTag(BlankFragment.TAG);
        if (blankFragment != null) {
            blankFragment.refreshBlank(dimensionsContainer.getDimensionsList());
        }
    }

    @Override
    public void onDialogResult(int requestCode, DialogResultCode resultCode,
            Bundle bundle) {
        if (requestCode == CODE_DIMENSION_SET && resultCode == DialogResultCode.RESULT_OK) {
            if (bundle != null && bundle.containsKey(DialogClickListener.KEY_CALLBACK_BUNDLE_TAG)) {
                Dimensions dims = bundle.getParcelable(DialogClickListener.KEY_CALLBACK_BUNDLE_TAG);
                if (dims != null) {
                    dims.setMainContext(getActivity());
                    updateDimensions(dims);
                    refresh();
                }
            }
        }
    }

    private void updateDimensions(Dimensions dims) {

        dimensionsContainer.updateDimensions(dims);

        int[] textSizeKeys = {};
        int[] viewWidthKeys = {};
        RewriteMode textSizeRewriteMode = RewriteMode.FOR_ALL;
        RewriteMode viewWidthRewriteMode = RewriteMode.FOR_ALL;

        switch (dims.getKey()) {
            case Blank.KEY_TOPIC_TEXT:
                break;
            case Blank.KEY_NUM_TOP:
//                textSizeKeys = new int[]{};
//                textSizeRewriteMode = RewriteMode.FOR_ALL;
                viewWidthKeys = new int[]{Blank.KEY_NUM_LEFT};
                viewWidthRewriteMode = RewriteMode.FOR_ALL;
                break;
            case Blank.KEY_NUM_LEFT:
                textSizeKeys =
                        new int[]{Blank.KEY_NAME_LEFT, Blank.KEY_CLASS_BODY, Blank.KEY_ADD_COL};
                textSizeRewriteMode = RewriteMode.FOR_LOWER;
                viewWidthKeys = new int[]{Blank.KEY_NUM_TOP};
                viewWidthRewriteMode = RewriteMode.FOR_ALL;
                break;
            case Blank.KEY_NAME_TOP:
//                textSizeKeys = new int[]{};
//                textSizeRewriteMode = RewriteMode.FOR_ALL;
                viewWidthKeys = new int[]{Blank.KEY_NAME_LEFT};
                viewWidthRewriteMode = RewriteMode.FOR_ALL;
                break;
            case Blank.KEY_NAME_LEFT:
                textSizeKeys = new int[]{Blank.KEY_ADD_COL};
                textSizeRewriteMode = RewriteMode.FOR_ALL;
                setTextStyle(dims.getTextStyle(), new int[]{Blank.KEY_ADD_COL});
                viewWidthKeys = new int[]{Blank.KEY_NAME_TOP};
                viewWidthRewriteMode = RewriteMode.FOR_ALL;
                setTextSize(dims.getTextSize(), new int[]{Blank.KEY_NUM_LEFT, Blank.KEY_CLASS_BODY},
                            RewriteMode.FOR_GREATER);
                break;
            case Blank.KEY_CLASS_TOP:
//                textSizeKeys = new int[]{};
//                textSizeRewriteMode = RewriteMode.FOR_ALL;
                viewWidthKeys = new int[]{Blank.KEY_CLASS_BODY, Blank.KEY_ABSENT_CEIL};
                viewWidthRewriteMode = RewriteMode.FOR_ALL;
                break;
            case Blank.KEY_CLASS_BODY:
                textSizeKeys =
                        new int[]{Blank.KEY_NUM_LEFT, Blank.KEY_NAME_LEFT, Blank.KEY_ADD_COL};
                textSizeRewriteMode = RewriteMode.FOR_LOWER;
                viewWidthKeys = new int[]{Blank.KEY_CLASS_TOP, Blank.KEY_ABSENT_CEIL};
                viewWidthRewriteMode = RewriteMode.FOR_ALL;
                break;
            case Blank.KEY_ADD_COL:
                textSizeKeys =
                        new int[]{Blank.KEY_NUM_LEFT, Blank.KEY_NAME_LEFT, Blank.KEY_CLASS_BODY,
                                Blank.KEY_ABSENT_CEIL};
                textSizeRewriteMode = RewriteMode.FOR_ALL;
                setTextStyle(dims.getTextStyle(), textSizeKeys);
//                viewWidthKeys = new int[]{};
//                viewWidthRewriteMode =  RewriteMode.FOR_ALL;
                break;
            case Blank.KEY_ABSENT_CEIL:
                viewWidthKeys = new int[]{Blank.KEY_CLASS_BODY, Blank.KEY_CLASS_TOP};
                viewWidthRewriteMode = RewriteMode.FOR_ALL;
                break;
            default:
                textSizeKeys = new int[]{};
                viewWidthKeys = new int[]{};
                textSizeRewriteMode = RewriteMode.FOR_ALL;
                viewWidthRewriteMode = RewriteMode.FOR_ALL;
        }
        setTextSize(dims.getTextSize(), textSizeKeys, textSizeRewriteMode);
        setViewWidth(dims.getViewWidth(), viewWidthKeys, viewWidthRewriteMode);
    }

    private void setTextSize(Integer textSize, int[] keys, RewriteMode rewriteMode) {
        if (textSize == null) return;
        switch (rewriteMode) {
            case FOR_ALL:
                for (int key : keys) {
                    dimensionsContainer.findDimensions(key).setTextSize(textSize);
                }
                break;
            case FOR_GREATER:
                for (int key : keys) {
                    Dimensions dims = dimensionsContainer.findDimensions(key);
                    if (dims.getTextSize() == null || dims.getTextSize() > textSize) {
                        dims.setTextSize(textSize);
                    }
                }
                break;
            case FOR_LOWER:
                for (int key : keys) {
                    Dimensions dims = dimensionsContainer.findDimensions(key);
                    if (dims.getTextSize() == null || dims.getTextSize() < textSize) {
                        dims.setTextSize(textSize);
                    }
                }
                break;
        }
    }

    private void setViewWidth(Integer viewWidth, int[] keys, RewriteMode rewriteMode) {
        if (viewWidth == null) return;
        switch (rewriteMode) {
            case FOR_ALL:
                for (int key : keys) {
                    dimensionsContainer.findDimensions(key).setViewWidth(viewWidth);
                }
                break;
            case FOR_GREATER:
                for (int key : keys) {
                    Dimensions dims = dimensionsContainer.findDimensions(key);
                    if (dims.getViewWidth() == null || dims.getViewWidth() > viewWidth) {
                        dims.setViewWidth(viewWidth);
                    }
                }
                break;
            case FOR_LOWER:
                for (int key : keys) {
                    Dimensions dims = dimensionsContainer.findDimensions(key);
                    if (dims.getViewWidth() == null || dims.getViewWidth() < viewWidth) {
                        dims.setViewWidth(viewWidth);
                    }
                }
                break;
        }
    }

    private void setTextStyle(Integer textStyle, int[] keys) {
        if (textStyle == null) return;
        for (int key : keys) {
            dimensionsContainer.findDimensions(key).setTextStyle(textStyle);
        }
    }

    public static enum RewriteMode {FOR_ALL, FOR_GREATER, FOR_LOWER}
}
