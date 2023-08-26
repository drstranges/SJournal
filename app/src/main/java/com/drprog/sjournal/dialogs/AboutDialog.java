package com.drprog.sjournal.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.drprog.sjournal.BuildConfig;
import com.drprog.sjournal.R;


/**
 * Created by Romka on 23.08.2014.
 */
public class AboutDialog extends DialogFragment {

    public static AboutDialog newInstance(){
        return new AboutDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
//        if (getDialog() != null){
//            getDialog().setTitle(R.string.menu_about);
//        }
        View v = inflater.inflate(R.layout.dialog_about, container, false);
        TextView title = (TextView) v.findViewById(R.id.textView1);
        TextView message = (TextView) v.findViewById(R.id.textView2);
        title.setText(Html.fromHtml(getString(R.string.dialog_about_message_title,
                                              BuildConfig.VERSION_NAME,
                                              BuildConfig.IS_VERSION_FREE ? "FREE":"PRO")));
        //message.setText(Html.fromHtml(getString(R.string.dialog_about_message)));
        View buttonAppStore = v.findViewById(R.id.button1);
        Button buttonForum = (Button) v.findViewById(R.id.button2);
        ImageButton buttonEmailMe = (ImageButton) v.findViewById(R.id.button3);
        buttonAppStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                            .parse(BuildConfig.URI_MARKET));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        buttonForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.GITHUB_URL));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonEmailMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",getString(R.string.app_dev_email), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SJournal");
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
