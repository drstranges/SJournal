package com.drprog.sjournal.dialogs;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.drprog.sjournal.db.prefs.Dimensions;
import com.drprog.sjournal.dialogs.utils.BaseDialogFragment;
import com.drprog.sjournal.dialogs.utils.ColorAdapter;
import com.drprog.sjournal.R;

import java.util.Arrays;

/**
 * Created by Romka on 16.08.2014.
 */
public class DimensionSetDialog extends BaseDialogFragment {

    public static final String ARG_DIMENSIONS = "ARG_DIMENSIONS";

    public static final Integer[] textSizes =
            {4, 6, 8, 10, 12, 14, 16, 18, 19, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44,
                    46, 48};
    public static final Integer[] viewWidths =
            {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190,
                    200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300, 310, 320, 330, 340, 350,
                    360, 370, 380, 390, 400, 410, 420, 430, 450, 460, 470, 480, 490, 500};
    private final AdapterView.OnItemSelectedListener spinnerColorSelectedListener =
            new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position,
                        long id) {
                    if (position < 0) return;
                    int color = (int) id;
                    int parentId = parent.getId();
                    if (parentId == R.id.spinner1) {
                        arg_dims.setBgColor(color);
//                    int pos = ((ColorAdapter) parent.getAdapter()).getItemPos(RunUtils.getContrastColor(color));
//                    if (pos != -1) spinnerTextColor.setSelection(pos);
//                    spinnerTextColor.setSelection(pos);
                    } else if (parentId == R.id.spinner2) {
                        arg_dims.setTextColor(color);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };
    private Dimensions arg_dims;
    private Spinner spinnerBgColor;
    private Spinner spinnerTextColor;
    private Spinner spinnerTypeface;
    private Spinner spinnerTextSize;
    private Spinner spinnerViewWidth;
    private Button buttonOk;
    private Button buttonCancel;

    public static DimensionSetDialog newInstance(Fragment clickListener, int requestCode,
            Dimensions dims) {
        DimensionSetDialog dialog = new DimensionSetDialog();
        if (clickListener != null) {
            dialog.setTargetFragment(clickListener, requestCode);
        }

        if (dims != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ARG_DIMENSIONS, dims);
            dialog.setArguments(bundle);
        }
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_DIMENSIONS)) {
                arg_dims = getArguments().getParcelable(ARG_DIMENSIONS);
            }
        }
        //setStyle(DialogFragment.STYLE_NORMAL,R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_dimensions_set, container, false);
        spinnerBgColor = (Spinner) v.findViewById(R.id.spinner1);
        spinnerTextColor = (Spinner) v.findViewById(R.id.spinner2);
        spinnerTypeface = (Spinner) v.findViewById(R.id.spinner3);
        spinnerTextSize = (Spinner) v.findViewById(R.id.spinner4);
        spinnerViewWidth = (Spinner) v.findViewById(R.id.spinner5);
        buttonOk = (Button) v.findViewById(R.id.button_ok);
        buttonCancel = (Button) v.findViewById(R.id.button_cancel);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_dimension_style_set_title);
        }
        final ColorAdapter colorAdapter =
                new ColorAdapter(getActivity(), android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBgColor.setAdapter(colorAdapter);
        spinnerTextColor.setAdapter(colorAdapter);
        Integer color = arg_dims.getBgColor();
        if (color != null) {
            int pos = colorAdapter.getItemPos(color);
            if (pos != -1) spinnerBgColor.setSelection(pos);
        }
        color = arg_dims.getTextColor();
        if (color != null) {
            int pos = colorAdapter.getItemPos(color);
            if (pos != -1) spinnerTextColor.setSelection(pos);
        }

        String[] textStyles = getResources().getStringArray(R.array.text_style);
        ArrayAdapter<String> textStyleAdapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                                         textStyles);
        textStyleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeface.setAdapter(textStyleAdapter);

        Integer textStyle = arg_dims.getTextStyle();
        if (textStyle == null) textStyle = Typeface.NORMAL;
        spinnerTypeface.setSelection(textStyle);

        ArrayAdapter<Integer> textSizeAdapter =
                new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item,
                                          textSizes);
        textSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTextSize.setAdapter(textSizeAdapter);

        ArrayAdapter<Integer> viewWidthAdapter =
                new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item,
                                          viewWidths);
        viewWidthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerViewWidth.setAdapter(viewWidthAdapter);

        Integer textSize = arg_dims.getTextSize();
        if (textSize == null) textSize = 14;
        spinnerTextSize.setSelection(Arrays.binarySearch(textSizes, textSize));

        Integer viewWidth = arg_dims.getViewWidth();
        if (viewWidth == null) viewWidth = 100;
        spinnerViewWidth.setSelection(Arrays.binarySearch(viewWidths, viewWidth));


        spinnerBgColor.setOnItemSelectedListener(spinnerColorSelectedListener);
        spinnerTextColor.setOnItemSelectedListener(spinnerColorSelectedListener);
        spinnerTypeface.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) arg_dims.setTextStyle(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTextSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) arg_dims.setTextSize(textSizes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerViewWidth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    //Integer width = viewWidths[position];
                    //if (width > 0)
                    arg_dims.setViewWidth(viewWidths[position]);//width);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCallbackOk(arg_dims);
            }
        });
    }
}
