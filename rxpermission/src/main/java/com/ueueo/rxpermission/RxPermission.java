/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ueueo.rxpermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class RxPermission {

    public static final String TAG = "RxPermission";

    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private static final ConcurrentHashMap<String, PublishSubject<Permission>> mSubjects = new ConcurrentHashMap<>();
    private static boolean DEBUG = false;

    public void setDebug(boolean debug) {
        DEBUG = debug;
    }

    private static void log(String message) {
        if (DEBUG) {
            Log.d(TAG, message);
        }
    }

    /**
     * Map emitted items from the source observable into {@code true} if permissions in parameters
     * are granted, or {@code false} if not.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    public Observable.Transformer<Object, Boolean> ensure(final Context context,final Permission... permissions) {
        return new Observable.Transformer<Object, Boolean>() {
            @Override
            public Observable<Boolean> call(Observable<Object> o) {
                return request(context,o, permissions)
                        // Transform Observable<Permission> to Observable<Boolean>
                        .buffer(permissions.length)
                        .flatMap(new Func1<List<Permission>, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(List<Permission> permissions) {
                                if (permissions.isEmpty()) {
                                    // Occurs during orientation change, when the subject receives onComplete.
                                    // In that case we don't want to propagate that empty list to the
                                    // subscriber, only the onComplete.
                                    return Observable.empty();
                                }
                                // Return true if all permissions are granted.
                                for (Permission p : permissions) {
                                    if (!p.granted) {
                                        return Observable.just(false);
                                    }
                                }
                                return Observable.just(true);
                            }
                        });
            }
        };
    }

    /**
     * Map emitted items from the source observable into {@link Permission} objects for each
     * permission in parameters.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    public Observable.Transformer<Object, Permission> ensureEach(final Context context,final Permission... permissions) {
        return new Observable.Transformer<Object, Permission>() {
            @Override
            public Observable<Permission> call(Observable<Object> o) {
                return request(context,o, permissions);
            }
        };
    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
    public Observable<Boolean> request(Context context,final Permission... permissions) {
        return Observable.just(null).compose(ensure(context,permissions));
    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
    public Observable<Permission> requestEach(Context context,final Permission... permissions) {
        return Observable.just(null).compose(ensureEach(context,permissions));
    }

    private Observable<Permission> request(final Context context,final Observable<?> trigger, final Permission... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("RxPermissions.request/requestEach requires at least one input permission");
        }
        return oneOf(trigger, pending(permissions))
                .flatMap(new Func1<Object, Observable<Permission>>() {
                    @Override
                    public Observable<Permission> call(Object o) {
                        return request_(context,permissions);
                    }
                });
    }

    private Observable<?> pending(final Permission... permissions) {
        for (Permission p : permissions) {
            if (!mSubjects.containsKey(p)) {
                return Observable.empty();
            }
        }
        return Observable.just(null);
    }

    private Observable<?> oneOf(Observable<?> trigger, Observable<?> pending) {
        if (trigger == null) {
            return Observable.just(null);
        }
        return Observable.merge(trigger, pending);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Observable<Permission> request_(Context context,final Permission... permissions) {

        List<Observable<Permission>> list = new ArrayList<>(permissions.length);
        List<Permission> unrequestedPermissions = new ArrayList<>();

        // In case of multiple permissions, we create a observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (Permission permission : permissions) {
            log("Requesting permission " + permission);
            if (isGranted(context,permission)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                list.add(Observable.just(permission.setGranted(true)));
                continue;
            }

            if (isRevoked(context,permission)) {
                // Revoked by a policy, return a denied Permission object.
                list.add(Observable.just(permission.setGranted(false)));
                continue;
            }

            PublishSubject<Permission> subject = mSubjects.get(permission);
            // Create a new subject if not exists
            if (subject == null) {
                unrequestedPermissions.add(permission);
                subject = PublishSubject.create();
                mSubjects.put(permission.name, subject);
            }

            list.add(subject);
        }

        if (!unrequestedPermissions.isEmpty()) {
            startShadowActivity(context,unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]));
        }
        return Observable.concat(Observable.from(list));
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     * <p>
     * In case of multiple permissions, only emits true if
     * Activity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     * <p>
     * You shouldn't call this method if all permissions have been granted.
     * <p>
     * For SDK &lt; 23, the observable will always emit false.
     */
    public static Observable<Boolean> shouldShowRequestPermissionRationale(final Activity activity,
                                                                    final Permission... permissions) {
        if (!isMarshmallow()) {
            return Observable.just(false);
        }
        return Observable.just(shouldShowRequestPermissionRationale_(activity, permissions));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean shouldShowRequestPermissionRationale_(final Activity activity,
                                                          final Permission... permissions) {
        for (Permission p : permissions) {
            if (!isGranted(activity,p) && !activity.shouldShowRequestPermissionRationale(p.name)) {
                return false;
            }
        }
        return true;
    }

    void startShadowActivity(Context context,String[] permissions) {
        log("startShadowActivity " + TextUtils.join(", ", permissions));
        Intent intent = new Intent(context, ShadowActivity.class);
        intent.putExtra("permissions", permissions);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    public static boolean isGranted(Context context,Permission permission) {
        return !isMarshmallow() || isGranted_(context,permission);
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    public static boolean isRevoked(Context context,Permission permission) {
        return isMarshmallow() && isRevoked_(context,permission);
    }

    static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean isGranted_(Context context,Permission permission) {
        return context.checkSelfPermission(permission.name) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean isRevoked_(Context context,Permission permission) {
        return context.getPackageManager().isPermissionRevokedByPolicy(permission.name, context.getPackageName());
    }

    static void onRequestPermissionsResult(int requestCode,
                                    String permissions[], int[] grantResults) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            log("onRequestPermissionsResult  " + permissions[i]);
            // Find the corresponding subject
            PublishSubject<Permission> subject = mSubjects.get(permissions[i]);
            if (subject == null) {
                // No subject found
                throw new IllegalStateException("RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
            }
            mSubjects.remove(permissions[i]);
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

            subject.onNext(Permission.getPermission(permissions[i]).setGranted(granted));
            subject.onCompleted();
        }
    }
}
