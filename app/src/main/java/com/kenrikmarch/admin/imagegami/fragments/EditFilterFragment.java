package com.kenrikmarch.admin.imagegami.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kenrikmarch.admin.imagegami.R;
import com.kenrikmarch.admin.imagegami.models.FilterSettings;

public class EditFilterFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private Spinner sSize;
    private Spinner sType;
    private Spinner sColor;
    private Button btnSave;
    private EditText etWebsite;
    private FilterSettings filters;

    public interface EditFilterDialogListener {
        void onFinishEditDialog(FilterSettings settings);
    }

    public EditFilterFragment() { }

    public static EditFilterFragment newInstance(String title) {
        EditFilterFragment frag = new EditFilterFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_filters, container);

        Bundle bundle = this.getArguments();
        filters = bundle.getParcelable("filters");

        return setupView(view);
    }

    private View setupView(final View view) {

        sSize = (Spinner) view.findViewById(R.id.sSize);
        String[] sizeItems = new String[]{"none", "small", "medium", "large", "extra-large"};
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, sizeItems);
        sSize.setAdapter(sizeAdapter);
        sSize.setSelection(sizeAdapter.getPosition(filters.size));

        sType = (Spinner) view.findViewById(R.id.sType);
        String[] typeItems = new String[]{"none", "faces", "photo", "clip art", "line art"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, typeItems);
        sType.setAdapter(typeAdapter);
        sType.setSelection(typeAdapter.getPosition(filters.type));

        sColor = (Spinner) view.findViewById(R.id.sColor);
        String[] colorItems = new String[]{"none","black","blue","brown","gray","green","orange",
                "pink","purple","red","teal","white","yellow"};
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, colorItems);
        sColor.setAdapter(colorAdapter);
        sColor.setSelection(colorAdapter.getPosition(filters.color));

        String title = getArguments().getString("title", getString(R.string.filter_search_results));
        getDialog().setTitle(title);

        etWebsite = (EditText) view.findViewById(R.id.etWebsite);
        etWebsite.setOnEditorActionListener(this);
        etWebsite.setText(filters.site);
        etWebsite.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etWebsite.getText().toString();
                if (!url.isEmpty()) {
                    if (!url.startsWith("http://")|| !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                }
                if (Patterns.WEB_URL.matcher(url).matches() || url.isEmpty()) {
                    EditFilterDialogListener listener = (EditFilterDialogListener) getActivity();
                    listener.onFinishEditDialog(getFilterValues());
                    dismiss();
                } else {
                    Toast.makeText(view.getContext(), "Please enter a valid URL or none at all", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private FilterSettings getFilterValues() {
        filters.site  = etWebsite.getText().toString();
        filters.size  = sSize.getSelectedItem().toString();
        filters.color = sColor.getSelectedItem().toString();
        filters.type  = sType.getSelectedItem().toString();
        return filters;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            EditFilterDialogListener listener = (EditFilterDialogListener) getActivity();
            listener.onFinishEditDialog(getFilterValues());
            dismiss();
            return true;
        }
        return false;
    }
}