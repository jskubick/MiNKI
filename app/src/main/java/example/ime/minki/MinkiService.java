package example.ime.minki;

import static android.view.KeyEvent.ACTION_UP;

import android.inputmethodservice.InputMethodService;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MinkiService extends InputMethodService implements View.OnTouchListener {
    private static final String TAG = "MinkiService";
    private static int counter = 0;
    private InputMethodManager inputMethodManager;

    private int downAtX = 0;
    private int downAtY = 0;

    private boolean isShifted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateInputView() {
        Log.i(TAG, "about to inflate and return R.layout.minki");
        View v = getLayoutInflater().inflate(R.layout.minki, null);
        MinkiView mv = v.findViewById(R.id.minkiView);
        v.setOnTouchListener(this);
        return v;
    }

    @Override
    public void onStartInput(EditorInfo info, boolean restarting) {
        super.onStartInput(info, restarting);
        Log.i(TAG, "onStartInput() called, counter=" + ++counter);
        isShifted = false;
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
        Log.i(TAG, "onFinishInput() called, counter=" + counter);
    }

    /**
     * Literally EVERYTHING in this method will be replaced by your own code.
     * The stroke-recognition logic is deliberately naive and simplistic
     * to avoid muddying up the lesson it's trying to teach you.
     *
     * I deliberately omitted all the code necessary to render "finger-paint" style path previews.
     * If you want a more complete example of a Graffiti-like non-keyboard-based IME,
     * keep watching for "Graphiite".
     *
     * The key points to take away from it:
     *      It's a View.OnTouchListener. You add it to the IME's view in onCreateInputView().
     *      There's nothing magic about implementing it in your InputMethodService, and in
     *      fact many would probably regard it as a close-coupling code smell and frown upon it.
     *      For this example, I didn't want to obscure the minimalist logic by burying things under
     *      5 layers of decoupled abstraction.
     *
     *      You use getCurrentInputConnection().sendKeyEvent(...) to virtually press keys like Enter
     *
     *      You use getCurrentInputConnection().deleteSurroundingText(1,0) to backspace.
     *      If the cursor were in the middle of an EditText & you wanted to do "delete", you'd call the same method with (0,1)
     *
     *      To send arbitrary characters, call getCurrentInputConnection.commitText(stringValue)
     *      stringValue can be a single character, or it can be a whole sequence of characters. However,
     *      including control characters like backspace (0x08), tab (0x09), linefeed (0x0a), carriage return (0x0d), etc
     *      might not produce the results you'd expect. For the most part, Android EditText fields are *not* termcap!
     *
     * @param view
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked()==MotionEvent.ACTION_DOWN) {
            downAtX = Math.round(motionEvent.getX());
            downAtY = Math.round(motionEvent.getY());
            Log.i(TAG, "ACTION_DOWN! (" + downAtX + "," + downAtY + ")");
        }
        else if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
            Log.i(TAG, "ACTION_UP (" + motionEvent.getX() + "," + motionEvent.getY() + ")" );

            int xDiff =Math.round(motionEvent.getX()) - downAtX ;
            int yDiff = Math.round(motionEvent.getY()) - downAtY;
            int slope = (xDiff == 0) ? Integer.MAX_VALUE : (int) ((yDiff / (float)xDiff) * 100);

            Log.i(TAG, "xDiff=" + xDiff + ", yDiff=" + yDiff + ", slope=" + slope);


            if (Math.abs(xDiff) < 50) {
                if (yDiff < -100) {
                    isShifted = !isShifted;
                    Toast.makeText(this, isShifted ? "shifted" : "unshifted", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (yDiff > 100) {
                    commitText(isShifted ? "CAT" : "taco");
                    return true;
                }
            }

            // horizontal stroke
            if ((Math.abs(slope) < 30) && (Math.abs(xDiff) > 100)){

                if (xDiff < -100)
                    getCurrentInputConnection().deleteSurroundingText(1,0); // backspace
                else if (xDiff > 100) {
                    getCurrentInputConnection().commitText( isShifted ? "X" : "x", 1);
                    isShifted = false;
                }
            }

            else if ((slope > 50) && (slope < 300)) {
                getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                Log.i(TAG, "hit return");
            }

            downAtX = 0;
            downAtY = 0;
        }
        return true;
    }

    private void commitText(CharSequence text) {
        Log.i(TAG, "committing '" + text + "'");
        Log.i(TAG, String.valueOf(getCurrentInputConnection().commitText(text, 1)));
    }
}