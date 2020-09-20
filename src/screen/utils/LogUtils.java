package screen.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import static org.junit.Assert.assertNotNull;

public class LogUtils {
    private String TAG = "";
    private String ERRORMESSAGE = "An error has occured";

    public LogUtils(Object tag) {
        setTag(tag);
    }

    public LogUtils(String tag) {
        setTag(tag);
    }

    public LogUtils(Object tag, String errorMessage) {
        setTag(tag);
        setErrorMessage(errorMessage);
    }

    public LogUtils(String tag, String errorMessage) {
        setTag(tag);
        setErrorMessage(errorMessage);
    }

    public void setTag(Object object) {
        setTag(object.getClass().getName());
    }

    public void setTag(String tag) {
        TAG = tag;
    }

    public void setErrorMessage(String errorMessage) {
        ERRORMESSAGE = errorMessage;
    }

    public final void log(String message) {
        Log.i("LogUtils", TAG + ": " + message);
    }

    public void logWithClassName(Object object, String message) {
        Log.i("LogUtils", TAG + ": " + object.getClass().getName() + ": " + message);
    }

    public final Throwable error() {
        return error(ERRORMESSAGE);
    }

    public final AssertionError error(String message) {
        AssertionError t = new AssertionError(message);
        Log.e("LogUtils", TAG + ": " + Log.getStackTraceString(t));
        return t;
    }

    public void errorWithClassName(Object object, Exception exception) {
        AssertionError t = new AssertionError(Log.getStackTraceString(exception));
        Log.e("LogUtils", TAG + ": " + object.getClass().getName() + ": " + Log.getStackTraceString(t));
    }

    public void errorWithClassName(Object object, String message) {
        AssertionError t = new AssertionError(message);
        Log.e("LogUtils", TAG + ": " + object.getClass().getName() + ": " + Log.getStackTraceString(t));
    }

    public final void errorNoStackTrace() {
        errorNoStackTrace(ERRORMESSAGE);
    }

    public final void errorNoStackTraceWithClassName(Object object) {
        errorNoStackTraceWithClassName(object, ERRORMESSAGE);
    }

    public final void errorNoStackTrace(String message) {
        Log.e("LogUtils", TAG + ": " + message);
    }

    public final void errorNoStackTraceWithClassName(Object object, String message) {
        Log.e("LogUtils", TAG + ": " + object.getClass().getName() + ": " + message);
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorIfNull(@Nullable T object) {
        return errorIfNull(object, ERRORMESSAGE);
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorIfNull(@Nullable T object, String message) {
        if (object == null) error(message);
        return object;
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorIfNullNoStackTrace(@Nullable T object) {
        return errorIfNullNoStackTrace(object, ERRORMESSAGE);
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorIfNullNoStackTrace(@Nullable T object, String message) {
        if (object == null) errorNoStackTrace(message);
        return object;
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorAndThrowIfNull(@Nullable T object) {
        return errorAndThrowIfNull(object, ERRORMESSAGE);
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorAndThrowIfNull(@Nullable T object, String message) {
        assertNotNull(message, object);
        return object;
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorAndThrowIfNullWithClass(Object object_, @Nullable T object, String message) {
        assertNotNull(object_.getClass().getName() + ": " + message, object);
        return object;
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final void errorAndThrow(String message) {
        assertNotNull(message, null);
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final void errorAndThrowWithClass(Object object, String message) {
        assertNotNull(object.getClass().getName() + ": " + message, null);
    }

    public void logMethodName() {
        Log.i("LogUtils", TAG + ": " + Thread.currentThread().getStackTrace()[3].getMethodName() + "() called");
    }

    public void logParentMethodName() {
        Log.i("LogUtils", TAG + ": " + Thread.currentThread().getStackTrace()[4].getMethodName() + "() called");
    }

    public void logMethodNameWithClassName(Object object) {
        Log.i("LogUtils",
                TAG + ": " + object.getClass().getName() + ": " +
                        Thread.currentThread().getStackTrace()[3].getMethodName() + "() called");
    }

    public String getMethodName() {
        return getMethodName(1);
    }

    public String getMethodName(int methodDepthOffset) {
        return Thread.currentThread().getStackTrace()[3+methodDepthOffset].getMethodName();
    }

    public String getParentMethodName() {
        return getParentMethodName(1);
    }

    public String getParentMethodName(int methodDepthOffset) {
        return Thread.currentThread().getStackTrace()[4+methodDepthOffset].getMethodName();
    }
}