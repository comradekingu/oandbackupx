/*
 * OAndBackupX: open-source apps backup and restore app.
 * Copyright (C) 2020  Antonios Hazim
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.machiav3lli.backup.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.machiav3lli.backup.databinding.SheetSortFilterBinding;
import com.machiav3lli.backup.handler.SortFilterManager;
import com.machiav3lli.backup.items.SortFilterModel;

public class SortFilterSheet extends BottomSheetDialogFragment {
    SortFilterModel sortFilterModel;
    private SheetSortFilterBinding binding;

    public SortFilterSheet() {
        this.sortFilterModel = new SortFilterModel();
    }

    public SortFilterSheet(SortFilterModel sortFilterModel) {
        this.sortFilterModel = sortFilterModel;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog sheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        sheet.setOnShowListener(d -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) d;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null)
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SheetSortFilterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setupOnClicks();
        setupChips();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sortFilterModel = SortFilterManager.getFilterPreferences(requireContext());
    }

    private void setupOnClicks() {
        binding.dismiss.setOnClickListener(v -> dismissAllowingStateLoss());
        binding.reset.setOnClickListener(v -> {
            SortFilterManager.saveFilterPreferences(requireContext(), new SortFilterModel("0000"));
            dismissAllowingStateLoss();
        });
        binding.apply.setOnClickListener(v -> {
            SortFilterManager.saveFilterPreferences(requireContext(), sortFilterModel);
            dismissAllowingStateLoss();
        });
    }

    private void setupChips() {
        binding.sortBy.check(sortFilterModel.getSortById());
        binding.sortBy.setOnCheckedChangeListener((group, checkedId) -> sortFilterModel.putSortBy(checkedId));
        binding.filters.check(sortFilterModel.getFilterId());
        binding.filters.setOnCheckedChangeListener((group, checkedId) -> sortFilterModel.putFilter(checkedId));
        binding.backupFilters.check(sortFilterModel.getBackupFilterId());
        binding.backupFilters.setOnCheckedChangeListener((group, checkedId) -> sortFilterModel.putBackupFilter(checkedId));
        binding.specialFilters.check(sortFilterModel.getSpecialFilterId());
        binding.specialFilters.setOnCheckedChangeListener((group, checkedId) -> sortFilterModel.putSpecialFilter(checkedId));
    }
}
