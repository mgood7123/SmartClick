package smallville7123.smartclick;

import android.os.Build;
import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1}, manifest=Config.NONE)
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        Bundle b = new Bundle();
        b.putInt("k", 5);
        assertEquals(b.getInt("k"), 5);
    }
}