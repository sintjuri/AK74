package com.onettm.ak74;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.Stack;
import java.util.Timer;

import static com.onettm.ak74.LevelChoiceDialog.*;


public class MainActivity extends Activity implements Callbacks {

    private int level = 0;
    public static int MAX_NUMBER = 6;
    private static int[] delays = {5, 7, 10, 25, 600};
    private int lastTick;
    private Timer akTimer;

    public Integer getCurrentNumber() {
        PlaceholderFragment fragment = (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.placeholderFragment);
        return fragment.getCurrentNumber();
    }

    public int getLastTick() {
        return lastTick;
    }

    public void setLastTick(int lastTick) {
        this.lastTick = lastTick;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onItemSelected(int level) {
        this.level = level;
        PlaceholderFragment fragment = (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.placeholderFragment);
        fragment.mix();
        startTimer();
    }

    private void startTimer() {
        PlaceholderFragment fragment = (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.placeholderFragment);
        akTimer = new Timer();
        if (lastTick <= 0) {
            lastTick = delays[level];
        }
        akTimer.schedule(new AKTimerTask(this, fragment), 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (akTimer != null) {
            akTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((akTimer != null) && (lastTick > 0)) {
            startTimer();
        }


    }

    private void showAlert(String title, String message, int drawable) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(drawable);

        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Create new fragment and transaction
                PlaceholderFragment newFragment = new PlaceholderFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.placeholderFragment, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    protected void openDialog() {
        LevelChoiceDialog lvlDialog = new LevelChoiceDialog();
        FragmentManager fm = getFragmentManager();
        lvlDialog.show(fm, "selectLevelLayout");
    }

    public void showWin() {
        showAlert(getString(R.string.winTitle), getString(R.string.winMessage), R.drawable.win);
    }

    public void showFail() {
        showAlert(getString(R.string.failTitle), getString(R.string.failMessage), R.drawable.fail);
    }

    public static class PlaceholderFragment extends Fragment implements View.OnDragListener {

        private String TAG = "DRAG";

        private View rootView;
        private Handler handler;
        private TextView timerText;
        private Integer currentNumber;
        final MediaPlayer mp = new MediaPlayer();

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_my, container, false);
            ((MainActivity) getActivity()).openDialog();

            handler = new Handler();
            timerText = (TextView) rootView.findViewById(R.id.timerText);

            rootView.findViewById(R.id.ak).setOnDragListener(this);
            AssetFileDescriptor afd;
            try {
                afd = getActivity().getAssets().openFd("reload2.mp3");
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mp.prepare();
            } catch (IOException e1) {
                Log.e("", e1.getMessage(), e1);
            }

            return rootView;
        }

        public Handler getHandler() {
            return handler;
        }

        public TextView getTextView() {
            return timerText;
        }

        @Override
        public boolean onDrag(View receivingLayoutView, DragEvent dragEvent) {
            View draggedImageView = (View) dragEvent.getLocalState();

            // Handles each of the expected events
            switch (dragEvent.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED:
                    Log.i(TAG, "drag action started");

                    // Determines if this View can accept the dragged data
                    if (dragEvent.getClipDescription()
                            .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        Log.i(TAG, "Can accept this data");

                        // returns true to indicate that the View can accept the dragged data.
                        return true;

                    } else {
                        Log.i(TAG, "Can not accept this data");

                    }

                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.i(TAG, "drag action entered");
//                the drag point has entered the bounding box
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    Log.i(TAG, "drag action location");
                /*triggered after ACTION_DRAG_ENTERED
                stops after ACTION_DRAG_EXITED*/
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    Log.i(TAG, "drag action exited");
//                the drag shadow has left the bounding box
                    return true;

                case DragEvent.ACTION_DROP:
                  /* the listener receives this action type when
                  drag shadow released over the target view
            the action only sent here if ACTION_DRAG_STARTED returned true
            return true if successfully handled the drop else false*/
                    if (draggedImageView.getTag() != null) {
                        String tag = (String) draggedImageView.getTag();
                        PartEnum part = PartEnum.getPartByTag(tag);
                        if (part != null) {
                            if (part.getOrder() == currentNumber) {
                                Log.i(TAG, "part = " + part.getTag() + " currentNumber = " + currentNumber);
                                currentNumber++;
                                mp.start();
                                ((ImageView) rootView.findViewById(R.id.ak)).setImageResource(part.getAkWithThisPartResource());
                                Log.i(TAG, "dropping " + draggedImageView.getTag());
                                draggedImageView.setVisibility(View.INVISIBLE);
                                return true;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENDED:

                    Log.i(TAG, "drag action ended");
                    Log.i(TAG, "getResult: " + dragEvent.getResult());


//                if the drop was not successful, set the ball to visible
                    if (!dragEvent.getResult()) {
                        Log.i(TAG, "setting visible");
                        draggedImageView.setVisibility(View.VISIBLE);
                    }

                    return true;
                // An unknown action type was received.
                default:
                    Log.i(TAG, "Unknown action type received by OnDragListener.");
                    break;
            }
            return false;
        }

        public void mix() {

            TableLayout table = (TableLayout) rootView.findViewById(R.id.table);
            Stack<PartEnum> parts = createRandomList();

            for (int i = 0; i < table.getChildCount(); i++) {
                final TableRow tableRow = (TableRow) table.getChildAt(i);
                if (tableRow.getId() != R.id.akRow) {
                    for (int j = 0; j < tableRow.getChildCount(); j++) {
                        final View child = tableRow.getChildAt(j);
                        if (child instanceof ImageView) {
                            PartEnum part = parts.pop();
                            child.setTag(part.getTag());
                            ((ImageView) child).setImageResource(part.getResource());
                            child.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                        ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());

                                        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                                        ClipData dragData = new ClipData("",
                                                mimeTypes, item);

                                        // Instantiates the drag shadow builder.
                                        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(child);

                                        // Starts the drag
                                        v.startDrag(dragData,  // the data to be dragged
                                                myShadow,  // the drag shadow builder
                                                child,
                                                0          // flags (not currently used, set to 0)
                                        );

                                        v.setVisibility(View.INVISIBLE);
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }

                            });
                        }
                    }
                }
                currentNumber = 0;

            }
        }

        private Stack<PartEnum> createRandomList() {
            //TODO add real implementation
            Stack<PartEnum> result = new Stack<PartEnum>();
            for (PartEnum value : PartEnum.values()) {
                result.push(value);
            }
            return result;
        }

        public Integer getCurrentNumber() {
            return currentNumber;
        }
    }

    /**
     * This class makes the ad request and loads the ad.
     */
    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .addTestDevice("173BD073D90BF3D4470C5BF99574C283")
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        /**
         * Called when leaving the activity
         */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /**
         * Called when returning to the activity
         */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /**
         * Called before the activity is destroyed
         */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }

}
