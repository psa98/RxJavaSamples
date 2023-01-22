package com.example.rxjavasamples.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rxjavasamples.R;
import com.example.rxjavasamples.databinding.FragmentRetrofitBinding;


/**
 * A placeholder fragment containing a simple view.
 */
public class RetrofitFragment extends Fragment {


    private RetrofitViewModel viewModel;
    private FragmentRetrofitBinding binding;
    public static RetrofitFragment newInstance() {
        return new RetrofitFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RetrofitViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentRetrofitBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initListeners() {
        /* По нажатию кнопки запрашивается случайное количество названий постов с
        * https://jsonplaceholder.typicode.com
        *
        * */
        binding.clickButton.setOnClickListener(v ->{
                binding.randomPosts.setText(R.string.loading);
                viewModel.reloadPost();
        });

        /* По нажатию кнопки специально запрашивается ошибочный адрес API
         *
         * */
        binding.errorButton.setOnClickListener(v -> {
            binding.randomPosts.setText(R.string.loading);
            viewModel.forceErrorRequest();});

    }

    private void initObservers() {
        viewModel.liveDataWithPostText.observe(this.getViewLifecycleOwner(),
                value -> binding.randomPosts.setText(value));

    }



    @Override
    public void onResume() {
        super.onResume();
        initListeners();
        initObservers();
        //viewModel.subscribe();
    }

    /*
     * не забываем отписываться согласно жц и освобождать ресурсы, при передаче событий
     * в ui из Flowable|Observable андроида приложение может упасть если событие
     * к отображению придет в уничтоженный элемент ui.
     * Вызов dispose() в данном случае так же остановит загрузку данных и дальнейшую
     *  обработку, передавшись вверх по цепочке
     */
    @Override
    public void onPause() {
        super.onPause();
        if (viewModel.currentSubscription != null && !viewModel.currentSubscription.isDisposed())
            viewModel.currentSubscription.dispose();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}