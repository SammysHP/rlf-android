package org.milderjoghurt.rlf.android.net;

import org.milderjoghurt.rlf.android.models.Model;

public abstract class ApiResponseHandler<M extends Model> {
    public void onSuccess(final M model) {
    }

    public void onFailure(Throwable e) {
    }
}
