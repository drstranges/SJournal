package com.drprog.sjournal.dialogs;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.drprog.sjournal.R;

import java.io.InputStream;

/**
 * Created by Romka on 23.08.2014.
 */
public class HelpDialog extends DialogFragment {
    private static final String ARG_HELP_ID = "ARG_HELP_ID";

    public static enum HelpId{GENERAL, IMPORT_LIST_OF_STUDENTS}

    private HelpId argHelpId = HelpId.GENERAL;

    public static HelpDialog newInstance(HelpId helpId){
        HelpDialog dialog = new HelpDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_HELP_ID, helpId.ordinal());
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_HELP_ID)) {
            argHelpId = HelpId.values()[getArguments().getInt(ARG_HELP_ID)];
        }
        setStyle(STYLE_NORMAL,R.style.Transparent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_help, container,false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ImageView imageView = (ImageView)v.findViewById(R.id.imageView);
        switch (argHelpId){
            default:
            case GENERAL:
                InputStream imageStream = getResources().openRawResource(R.raw.img_help_general);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
                break;
            case IMPORT_LIST_OF_STUDENTS:
                break;
        }



        return v;
    }
}
