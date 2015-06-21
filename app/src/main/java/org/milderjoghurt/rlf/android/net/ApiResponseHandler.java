package org.milderjoghurt.rlf.android.net;


public abstract class ApiResponseHandler<T> {
    public abstract void onSuccess(final T model);

    public abstract void onFailure(Throwable e);
}
