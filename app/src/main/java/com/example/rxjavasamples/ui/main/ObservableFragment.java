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


public class ObservableFragment extends Fragment {

    private ObservableViewModel viewModel;
    private FragmentObservableBinding binding;
    public static ObservableFragment newInstance() {
        return new ObservableFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ObservableViewModel.class);
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
        // по нажатию кнопки on Next в Observable будет проброшено событие onNext
        binding.clickButton.setOnClickListener(v -> {
            reloadParams();
            viewModel.onClick(v);
        });
        // по нажатию кнопки Force Error в Observable будет проброшено событие onError
        binding.errorButton.setOnClickListener(v -> {
            reloadParams();

            viewModel.onClick(v);
        });
        // по нажатию кнопки On Complete в Observable будет проброшено событие onComplete
        binding.completeButton.setOnClickListener(v -> {
            reloadParams();
            viewModel.onClick(v);
        });
    }

    private void initObservers() {
        viewModel.allTicks.observe(this.getViewLifecycleOwner(),
                value -> binding.ticksCount.setText(value));
        viewModel.logStringData.observe(this.getViewLifecycleOwner(),
                value -> binding.clicksLog.setText(value));
    }


    /*
     * Загружаем параметры Observable в VM, новые параметры станут действительны после
     * пересоздания, после onComplete|onError
     */
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

    /*
     * не забываем подписываться, Flowable|Observable работают "лениво" и до вызова
     * subscribe() ничего не делают.
     */
    @Override
    public void onResume() {
        super.onResume();
        initListeners();
        initObservers();
        viewModel.subscribe();
    }

    /*
     * не забываем отписываться согласно жц и освобождать ресурсы, при передаче событий
     * в ui из Flowable|Observable андроида приложение может упасть если событие
     * к отображению придет в уничтоженный элемент ui
     */
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