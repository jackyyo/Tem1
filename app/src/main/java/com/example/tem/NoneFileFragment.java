package com.example.tem;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class NoneFileFragment extends Fragment {
    private MainActivity mainActivity;

    public NoneFileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View noneFileFragment = inflater.inflate(R.layout.fragment_none_file, container, false);
        noneFileFragment.findViewById(R.id.none_file_fragment_text_view).setOnClickListener(((MainActivity) getActivity()));

        return noneFileFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity.fragmentState=3;
        mainActivity=(MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
