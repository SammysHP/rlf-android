package org.milderjoghurt.rlf.android.net;

import org.milderjoghurt.rlf.android.models.Model;

public abstract class ApiResponseHandler<M extends Model> {
    public abstract void onSuccess(final M model);

    public abstract void onFailure(Throwable e);
}
