package com.example.rxjavasamples.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rxjavasamples.databinding.FragmentMainBinding;


/**
 * A placeholder fragment containing a simple view.
 */
public class ObserverFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ObserverViewModel viewModel;
    private FragmentMainBinding binding;

    public static ObserverFragment newInstance() {
        return new ObserverFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ObserverViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.clickButton.setOnClickListener(v -> {
            viewModel.onClick(view);
        });
        binding.errorButton.setOnClickListener(v -> {
            viewModel.onClick(view);
        });
        binding.completeButton.setOnClickListener(v -> {
            viewModel.onClick(view);
        });

        initObservers();
    }

    private void initObservers() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}