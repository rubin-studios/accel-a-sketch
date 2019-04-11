package mobiledev.unb.ca.accel_a_sketch;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.Set;

/** Initialized from Android Studio's "Simple Activity" template */
public class Draw extends AppCompatActivity implements SensorEventListener {
    /** The sensor objects - these grab accelerometer data */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    /** The main view */
    private ConstraintLayout mFrame;
    /** The template's pre-placed floating action button */
    private FloatingActionButton menuButton;

    private SketchView sketch;
    private FragmentManager fMan;
    private SettingsFragment settingsMenu;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    /** Filter variables */
    private final float mAlpha = 3.8f;
    private float[] mGravity = new float[3];
    private float[] mAccel = new float[3];
    private float[] mRaw = new float[3];
    private long mLastUpdate;
    private float speedmult = 1.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Shrink down the title bar to nothingness!
         * Note that this block *must* be *before* the setContentView
         */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**
         * Android devices tend to only take touch input to determine whether the user is active.
         * As a result, the screen will dim & then turn off if the accelerometer is the only input.
         * This line fixes that.
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /** Initialise those sensors - tell the objects what to attach to */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        setContentView(R.layout.activity_draw);
        /**
         * This block was part of the template. Shouldn't be necessary.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         */

        /** Grab hold of the layout we're stacking things onto */
        mFrame = findViewById(R.id.trueBaseLayout);
        /** This default floating action button can give us a menu */
        menuButton = findViewById(R.id.fab);

        /** Allow the main view to contain a pop-up floating menu */
        registerForContextMenu(mFrame);

        /** Set our draw options to change when the user settings are updated */
        prefs = getSharedPreferences("preferences", 0);
        prefsEditor = prefs.edit();
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                sketch.setDrawWidth();
                sketch.setDrawColour();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPause();
                showPopup(view);
                /**
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                 */
            }
        });


        //Here goes nothing!
        sketch = new SketchView(mFrame.getContext());
        mFrame.addView(sketch);
        mLastUpdate = 0;
    }

    /**
     * These two methods are largely for resource control.
     * App's closed/in the background? Phone screen is off? Stop grabbing accelerometer input.
     * App's back in focus? Start grabbing that input again.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onResume();
                switch (item.getItemId()) {
                    case R.id.colourRed:
                        prefsEditor.putString("pref_colour", "r");
                        sketch.setDrawColourManual(Color.RED);
                        return true;
                    case R.id.colourGreen:
                        prefsEditor.putString("pref_colour", "g");
                        sketch.setDrawColourManual(Color.GREEN);
                        return true;
                    case R.id.colourBlue:
                        prefsEditor.putString("pref_colour", "b");
                        sketch.setDrawColourManual(Color.BLUE);
                        return true;
                    case R.id.colourCyan:
                        prefsEditor.putString("pref_colour", "c");
                        sketch.setDrawColourManual(Color.CYAN);
                        return true;
                    case R.id.colourMagenta:
                        prefsEditor.putString("pref_colour", "m");
                        sketch.setDrawColourManual(Color.MAGENTA);
                        return true;
                    case R.id.colourYellow:
                        prefsEditor.putString("pref_colour", "y");
                        sketch.setDrawColourManual(Color.YELLOW);
                        return true;
                    case R.id.colourBlack:
                        prefsEditor.putString("pref_colour", "b");
                        sketch.setDrawColourManual(Color.BLACK);
                        return true;
                    case R.id.width05:
                        prefsEditor.putString("prefs_width", "05");
                        sketch.setDrawWidthManual(5);
                        return true;
                    case R.id.width15:
                        prefsEditor.putString("prefs_width", "15");
                        sketch.setDrawWidthManual(15);
                        return true;
                    case R.id.width25:
                        prefsEditor.putString("prefs_width", "25");
                        sketch.setDrawWidthManual(25);
                        return true;
                    case R.id.width35:
                        prefsEditor.putString("prefs_width", "35");
                        sketch.setDrawWidthManual(35);
                        return true;
                    case R.id.width45:
                        prefsEditor.putString("prefs_width", "45");
                        sketch.setDrawWidthManual(45);
                        return true;
                    case R.id.speedHalf:
                        speedmult = .5f;
                        return true;
                    case R.id.speedStd:
                        speedmult = 1f;
                        return true;
                    case R.id.speed1_5:
                        speedmult = 1.5f;
                        return true;
                    case R.id.speedDouble:
                        speedmult = 2f;
                        return true;
                    case R.id.resume:
                        return true;
                    default:
                        onPause();
                        return false;
                }
            }
        });
        popup.inflate(R.menu.menu_draw);
        popup.show();
    }

    /** Necessary for the SensorEventListener implementation, but we don't use it */
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) { }

    /** Idea and particulars of implementation borrowed from the last lab. Wanted to change speed. */
    private float lowPass(float current, float gravity) {
        return gravity * mAlpha + current * (1 - mAlpha);
    }
    private float highPass(float current, float gravity) {
        return current - gravity;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Make sure to respond specifically to accelerometer input - as opposed to, say, gyroscope
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long actualTime = System.currentTimeMillis();
            /** Again, borrowed from lab - it was a liiittle bit too sensitive by default */
            //if (actualTime - mLastUpdate > 10) {
                mLastUpdate = actualTime;
                // Store unfiltered values
                mRaw = event.values;
                // Apply low-pass filter
                mGravity[0] = lowPass(mRaw[0], mGravity[0]);
                mGravity[1] = lowPass(mRaw[1], mGravity[1]);
                mGravity[2] = lowPass(mRaw[2], mGravity[2]);
                // Apply high-pass filter
                mAccel[0] = highPass(mRaw[0], mGravity[0]);
                mAccel[1] = highPass(mRaw[1], mGravity[1]);
                mAccel[2] = highPass(mRaw[2], mGravity[2]);
                sketch.onSensorEvent(event);
            //}
        }
    }

    /** Learned this directly from the Android Studio documentation. */
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    public class SketchView extends View {
        public int drawWidth;
        public int cursorRadius;

        /**
         * This block is essentially just for device localisation.
         * It's needed to determine the draw boundaries.
         */
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        private int viewWidth;
        private int viewHeight;
        private int x = metrics.widthPixels;
        private int xPrev = x;
        private int y = metrics.heightPixels;
        private int yPrev = y;

        private Paint cursorPaint;
        private Path cursorPath;
        private Path linePath;
        private Paint linePaint;

        public SketchView(Context context) {
            super(context);

            /** Set initial draw width and associated cursor radius */
            drawWidth = 15;
            cursorRadius = drawWidth + 10;

            /** Initialise the cursor */
            cursorPaint = new Paint();
            cursorPaint.setColor(Color.BLACK);
            cursorPath = new Path();

            /** Initialise the draw line */
            linePaint = new Paint();
            linePaint.setColor(Color.BLACK);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeJoin(Paint.Join.MITER);
            linePaint.setStrokeWidth(drawWidth);
            linePath = new Path();

            /** Initialise the bitmap that actually displays the draw line */
            //bitPaint = new Paint(Paint.DITHER_FLAG);
        }

        /**
         * Actually performs the screen setup. Implemented as a screen size change, but
         * no actual change is implemented - it's just used for setup.
         * @param w     The width of the 'new' screen
         * @param h     The height of the 'new' screen
         * @param oldw  The width of the 'old' screen
         * @param oldh  The height of the 'old' screen
         */
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            /** Grab the full width & height of the screen */
            viewWidth = w;
            viewHeight = h;
            /** Set the initial cursor position to the centre of the screen */
            x = w/2;
            y = h/2;

            linePath.reset();
            linePath.moveTo(x,y);
        }

        /**
         * The first "big one," this method takes care of the app's response to the accelerometer.
         * @param event     Any detected accelerometer motion 'event'
         */
        public void onSensorEvent (SensorEvent event) {
            /**
             * Separate the new cursor location from the last frame's location.
             * As a consequence of hard-setting the orientation to landscape, we need to do some
             * funky swap changes to the x,y values, or gravity gets weird.
             */
            xPrev = x;
            x = x - (int)(-1 * (int) event.values[1] * speedmult);
            //x = x - (-1 * (int) mAccel[1]);
            yPrev = y;
            y = y + (int)((int) event.values[0] * speedmult);
            //y = y + (int) mAccel[0];

            /** Keep the cursor within the screen boundaries. */
            if (x <= 0 + cursorRadius) {
                x = 0 + cursorRadius;
            }
            if (x >= viewWidth - cursorRadius) {
                x = viewWidth - cursorRadius;
            }
            if (y <= 0 + cursorRadius) {
                y = 0 + cursorRadius;
            }
            if (y >= viewHeight - cursorRadius) {
                y = viewHeight - cursorRadius;
            }

            /** 'Draw' a line between the two points - this isn't something the user sees (yet). */
            linePath.quadTo(xPrev, yPrev, (x + xPrev)/2, (y + yPrev)/2);
            //linePath.quadTo(yPrev, xPrev, (y + yPrev)/2, (x + xPrev)/2);

            /** Since the cursor isn't actually part of the drawing, we can just remake it. */
            cursorPath.reset();
            cursorPath.addCircle(xPrev, yPrev, cursorRadius, Path.Direction.CW);
            //cursorPath.addCircle(yPrev, xPrev, cursorRadius, Path.Direction.CW);
        }

        /**
         * The second "big one," this method actually draws stuff on the screen!
         * @param canvas    The virtual canvas space we're drawing to
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            /** Remember that line we "drew" in onSensorEvent? Yeah, let's put it on the screen. */
            canvas.drawPath(linePath, linePaint);

            /** Redraw the cursor, etc. etc., nothing new here */
            canvas.drawCircle(x, y, cursorRadius, cursorPaint);
            //We need to call invalidate each time, so that the view continuously draws
            postInvalidate();
            //previously used just invalidate();
        }

        /** New feature! Change the draw width! */
        public void setDrawWidth() {
            String newWid = prefs.getString("pref_width", "15");
            int newWidth = Integer.getInteger(newWid);
            drawWidth = newWidth;
            cursorRadius = drawWidth + 10;
            linePaint.setStrokeWidth(drawWidth);
        }

        public void setDrawWidthManual(int newWidth) {
            drawWidth = newWidth;
            cursorRadius = drawWidth + 10;
            linePaint.setStrokeWidth(drawWidth);
        }

        /** New feature! Change the draw colour! */
        public void setDrawColour() {
            String newCol = prefs.getString("pref_colour", "k");
            int newColour;
            if(newCol.equals("c")) {
                newColour = Color.CYAN;
            } else if(newCol.equals("m")) {
                newColour = Color.MAGENTA;
            } else if(newCol.equals("y")) {
                newColour = Color.YELLOW;
            } else {
                newColour = Color.BLACK;
            }
            linePaint.setColor(newColour);
            cursorPaint.setColor(newColour);
        }

        public void setDrawColourManual(int newColour) {
            linePaint.setColor(newColour);
            cursorPaint.setColor(newColour);
        }
    }

    /**
     * These methods were included in the template. I'm leaving them here just in case.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
     */
}
