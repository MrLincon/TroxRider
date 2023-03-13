package com.netro.troxrider.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.netro.troxrider.R;

public class BottomSheetLanguage extends BottomSheetDialogFragment {

    CoordinatorLayout bangla, english, hindi;
    ImageView close;
    ImageView banglaSelected, englishSelected, hindiSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_language, container, false);


        close = view.findViewById(R.id.close);
        bangla = view.findViewById(R.id.bangla);
        english = view.findViewById(R.id.english);
        hindi = view.findViewById(R.id.hindi);
        banglaSelected = view.findViewById(R.id.bangla_selected);
        englishSelected = view.findViewById(R.id.english_selected);
        hindiSelected = view.findViewById(R.id.hindi_selected);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        bangla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banglaSelected.setVisibility(View.VISIBLE);
                englishSelected.setVisibility(View.GONE);
                hindiSelected.setVisibility(View.GONE);
            }
        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banglaSelected.setVisibility(View.GONE);
                englishSelected.setVisibility(View.VISIBLE);
                hindiSelected.setVisibility(View.GONE);
            }
        });

        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banglaSelected.setVisibility(View.GONE);
                englishSelected.setVisibility(View.GONE);
                hindiSelected.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }
}
