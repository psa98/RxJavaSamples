package com.example.rxjavasamples.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rxjavasamples.databinding.FragmentObservableBinding;


/**
 * A placeholder fragment containing a simple view.
 */
public class ObserverFragment extends Fragment {


    int clickCounter = 0;

    private ObserverViewModel viewModel;
    private FragmentObservableBinding binding;

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

        binding = FragmentObservableBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void initListeners() {
        binding.clickButton.setOnClickListener(v -> {
            clickCounter++;
            viewModel.onClick(v, clickCounter);
        });
        binding.errorButton.setOnClickListener(v -> {
            clickCounter = 0;
            viewModel.onClick(v, clickCounter);
        });
        binding.completeButton.setOnClickListener(v -> {
            clickCounter = 0;
            viewModel.onClick(v, clickCounter);
        });
    }

    private void initObservers() {
        viewModel.allTicks.observe(this.getViewLifecycleOwner(), value -> {
            binding.ticksCount.setText(value);
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        initListeners();
        initObservers();
        viewModel.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (viewModel.dispose != null) viewModel.dispose.dispose();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}