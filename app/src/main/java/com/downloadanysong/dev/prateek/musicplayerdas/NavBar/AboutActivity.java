package com.downloadanysong.dev.prateek.musicplayerdas.NavBar;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.downloadanysong.dev.prateek.musicplayerdas.R;


/**
 * Created by prateek on 01-06-2017.
 */

public class AboutActivity extends Fragment{


    public static Fragment newInstance() {
        AboutActivity fragment = new AboutActivity();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        Button reachus,reachdev;
        reachus= (Button) rootView.findViewById(R.id.reach_mail);
        reachdev = (Button) rootView.findViewById(R.id.dev_mail);

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Barrio_Regular.ttf");
        Typeface custom_font2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/BubblerOne.ttf");

        TextView main_head = (TextView) rootView.findViewById(R.id.heading);
        TextView para_one = (TextView) rootView.findViewById(R.id.para1);
        //TextView head_para = (TextView) rootView.findViewById(R.id.contact_para);

        main_head.setTypeface(custom_font);
        para_one.setTypeface(custom_font2);
        //head_para.setTypeface(custom_font2);
        reachus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailto = "mailto:downloadanysong@gmail.com" +
                        "?cc=" + "pratekbatra54@gmail.com" +
                        "&subject=" + Uri.encode("Review/Suggestion/Bug Report") +
                        "&body=" + Uri.encode("");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));
                startActivity(emailIntent);
            }
        });


        return rootView;
    }

}
