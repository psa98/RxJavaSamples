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
            reloadParams();
            viewModel.onClick(v, 1);
        });
        binding.errorButton.setOnClickListener(v -> {
            reloadParams();
            clickCounter = 0;
            viewModel.onClick(v, 0);
        });
        binding.completeButton.setOnClickListener(v -> {
            reloadParams();
            clickCounter = 0;
            viewModel.onClick(v, 0);
        });
    }

    private void initObservers() {
        viewModel.allTicks.observe(this.getViewLifecycleOwner(),
                value -> binding.ticksCount.setText(value));
        viewModel.logStringData.observe(this.getViewLifecycleOwner(),
                value -> binding.clicksLog.setText(value));
    }



    @SuppressWarnings("ConstantConditions")
    private void reloadParams() {
        try {
            viewModel.skipParam = Integer.parseInt(binding.howManySkipped.getText().toString());
            viewModel.debounceParam = Integer.parseInt(binding.debounce.getText().toString());
            viewModel.takeParam = Integer.parseInt(binding.howManyClicks.getText().toString());
        } catch (Exception e){
            e.printStackTrace();
        }
        if (viewModel.takeParam<=0) {
            viewModel.takeParam = 2; //min number
            binding.howManyClicks.setText("2");
        }

        if (viewModel.debounceParam<=0) {
            viewModel.debounceParam = 0;
            binding.debounce.setText("0");
        }
        if (viewModel.skipParam<=0) {
            viewModel.skipParam = 0;
            binding.howManySkipped.setText("0");
        }
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