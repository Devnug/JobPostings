package com.nugdev.jobpostings;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.etsy.android.grid.StaggeredGridView;

/**
 * Created by Nug on 5/13/2015.
 */
public class JobPosting extends android.support.v4.app.Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

    public final String DEBUG_TAG = "JobPosting";

    TextView school;
    TextView opening;
    TextView desc;
    TextView link;
    static View v;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.posting, null);

        return v;
    }

}
