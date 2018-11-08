package com.example.cpu11268.musicapp.Music.Presenter;

public abstract class BasePresenter<T> {
    protected T mView;
    public void attachView(T view) {
        this.mView = view;
    }

    public void dettachView() {
        this.mView = null;
    }
}
