package com.example.rxjavasamples.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rxjavasamples.databinding.FragmentFlowableBinding;

import kotlin.random.Random;



public class FlowableFragment extends Fragment {
    public static final int MAX_RANDOM = 37;
    private FlowableViewModel viewModel;
    private FragmentFlowableBinding binding;
    public static FlowableFragment newInstance() {
        return new FlowableFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FlowableViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentFlowableBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void initListeners() {
        // по нажатию кнопки on Next во Flowable будет проброшено событие onNext
        binding.clickButton.setOnClickListener(v -> {
            int random = Random.Default.nextInt(1, MAX_RANDOM);
            reloadParams();
            viewModel.onClick(v, random);
        });
        // по нажатию кнопки Force Error во Flowable будет проброшено событие onError
        binding.errorButton.setOnClickListener(v -> {
            reloadParams();
            viewModel.onClick(v, 0);
        });
        // по нажатию кнопки On Complete во Flowable будет проброшено событие onComplete
        binding.completeButton.setOnClickListener(v -> {
            reloadParams();
            viewModel.onClick(v, 0);
        });
    }

    private void initObservers() {
        viewModel.allTicks.observe(this.getViewLifecycleOwner(),
                value -> binding.ticksCount.setText(value));
        viewModel.logStringData.observe(this.getViewLifecycleOwner(),
                value -> binding.clicksLog.setText(value));
    }

    /*
    * Загружаем параметры Flowable в VM, новые параметры станут действительны после
    * пересоздания, после onComplete|onError
    */
    @SuppressWarnings("ConstantConditions")
    private void reloadParams() {
        try {
            viewModel.delayParam = Integer.parseInt(binding.delay.getText().toString());
            viewModel.takeParam = Integer.parseInt(binding.howManyClicks.getText().toString());
        } catch (Exception e){
            e.printStackTrace();
        }
        if (viewModel.takeParam<=0||viewModel.takeParam>=MAX_RANDOM/2) {
            viewModel.takeParam = 6; //min number
            binding.howManyClicks.setText("6");
        }
        if (viewModel.delayParam <=0) {
            viewModel.delayParam = 0;
            binding.delay.setText("0");
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
        viewModel.startFlow();
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