package com.onettm.ak74;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
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
import java.util.Collections;
import java.util.Stack;
import java.util.Timer;

public class MainActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }


    public void toGame(int level) {
        // Create a new Fragment to be placed in the activity layout
        GameFragment gameFragment = new GameFragment();
        gameFragment.setLevel(level);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.placeholderFragment, gameFragment);
        // Commit the transaction
        transaction.commit();
    }

    public void toLevelChoise() {
        // Create a new Fragment to be placed in the activity layout
        LevelChoiceFragment levelChoiceFragment = new LevelChoiceFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.placeholderFragment, levelChoiceFragment);
        // Commit the transaction
        transaction.commit();
    }


    public static class LevelChoiceFragment extends Fragment {
        View view;
        MainActivity activity;


        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.level_choose_layout, container, false);
            view.findViewById(R.id.imageViewSelectLevel1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity != null) {
                        activity.toGame(0);
                    }
                }
            });
            view.findViewById(R.id.imageViewSelectLevel2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity != null) {
                        activity.toGame(1);
                    }
                }
            });
            view.findViewById(R.id.imageViewSelectLevel3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity != null) {
                        activity.toGame(2);
                    }
                }
            });
            view.findViewById(R.id.imageViewSelectLevel4).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity != null) {
                        activity.toGame(3);
                    }
                }
            });

            return view;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.activity = (MainActivity) activity;
        }

        @Override
        public void onDetach() {
            super.onDetach();
            this.activity = null;
        }
    }


    public static class GameFragment extends Fragment implements View.OnDragListener {

        private Handler handler;
        private TextView timerText;
        //TODO check if volatile is enough
        private volatile int currentNumber;
        private ImageView ak;
        private TableLayout table;
        //TODO check if volatile is enough
        private volatile int currentTick;

        private int level = 0;
        public static int MAX_NUMBER = 6;
        private static int[] delays = {10, 20, 30, 45};

        private Timer akTimer;

        MediaPlayer mp;

        public GameFragment() {
            super();
        }

        private void startTimer() {
            akTimer = new Timer();
            if (currentTick <= 0) {
                currentTick = delays[level];
            }
            akTimer.schedule(new AKTimerTask(this), 0, 1000);
        }

        public void incrementTick() {

            currentTick--;

            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    timerText.setText(currentTick + "");
                    if (currentNumber == MAX_NUMBER) {
                        currentTick = 0;
                        akTimer.cancel();
                        showWin();
                    } else {
                        if (currentTick <= 0) {
                            akTimer.cancel();
                            showFail();
                        }
                    }
                }
            });

        }

        public void showWin() {
            showAlert(getString(R.string.winTitle), getString(R.string.winMessage));//, R.drawable.win);
        }

        public void showFail() {
            showAlert(getString(R.string.failTitle), getString(R.string.failMessage));//, R.drawable.fail);
        }

        private void showAlert(String title, String message){//, int drawable) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            //alertDialog.setIcon(drawable);

            // On pressing Settings button
            alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).toLevelChoise();
                    }

                }
            });

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).toLevelChoise();
                    }
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }

        @Override
        public void onResume() {
            super.onResume();
            startTimer();
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.game_layout, container, false);

            handler = new Handler();
            timerText = (TextView) rootView.findViewById(R.id.timerText);
            ak = (ImageView) rootView.findViewById(R.id.ak);
            table = (TableLayout) rootView.findViewById(R.id.table);

            rootView.findViewById(R.id.ak).setOnDragListener(this);
            AssetFileDescriptor afd;
            try {
                afd = getActivity().getAssets().openFd("reload2.mp3");
                mp = new MediaPlayer();
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mp.prepare();
                afd.close();
            } catch (IOException e1) {
                Log.e("play", e1.getMessage(), e1);
            }

            mix();
            return rootView;
        }

        public Handler getHandler() {
            return handler;
        }

        @Override
        public boolean onDrag(View receivingLayoutView, DragEvent dragEvent) {
            View draggedImageView = (View) dragEvent.getLocalState();

            // Handles each of the expected events
            switch (dragEvent.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED:

                    // Determines if this View can accept the dragged data
                    if (dragEvent.getClipDescription()
                            .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        // returns true to indicate that the View can accept the dragged data.
                        return true;

                    }

                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:
//                the drag point has entered the bounding box
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                /*triggered after ACTION_DRAG_ENTERED
                stops after ACTION_DRAG_EXITED*/
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
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
                                currentNumber++;
                                mp.start();
                                ak.setImageResource(part.getAkWithThisPartResource());
                                draggedImageView.setVisibility(View.INVISIBLE);
                                return true;
                            }
                        }
                    }
                    return false;

                case DragEvent.ACTION_DRAG_ENDED:


//                if the drop was not successful, set the ball to visible
                    if (!dragEvent.getResult()) {
                        draggedImageView.setVisibility(View.VISIBLE);
                        return true;
                    }

                    return false;
                // An unknown action type was received.
                default:
                    break;
            }
            return false;
        }

        public void mix() {


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
                                    switch (motionEvent.getActionMasked()) {
                                        case (MotionEvent.ACTION_DOWN):
                                            return true;
                                        case (MotionEvent.ACTION_UP):
                                            v.setVisibility(View.VISIBLE);
                                            return false;
                                        case (MotionEvent.ACTION_MOVE):
                                            try {
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
                                            } catch (Throwable t) {
                                                return false;
                                            }
                                        default:
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
            Stack<PartEnum> result = new Stack<PartEnum>();
            for (PartEnum value : PartEnum.values()) {
                result.push(value);
            }
            Collections.shuffle(result);
            return result;
        }


        @Override
        public void onPause() {
            super.onPause();
            if (akTimer != null) {
                akTimer.cancel();
            }
        }

        public void setLevel(int level) {
            this.level = level;
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
                    //.addTestDevice("173BD073D90BF3D4470C5BF99574C283")
                    //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
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
