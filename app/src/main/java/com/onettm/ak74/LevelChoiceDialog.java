package com.onettm.ak74;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class LevelChoiceDialog extends DialogFragment {
    View view;
    ImageView imageViewLevel1;
    ImageView imageViewLevel2;
    ImageView imageViewLevel3;
    ImageView imageViewLevel4;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */


    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int level);

    }

    /**
     * A dummy implementation of the {@link com.onettm.ak74.LevelChoiceDialog.Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int level) {
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        imageViewLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onItemSelected(0);
                dismiss();
            }
        });
        imageViewLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onItemSelected(1);
                dismiss();
            }
        });
        imageViewLevel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onItemSelected(2);
                dismiss();
            }
        });
        imageViewLevel4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onItemSelected(3);
                dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.level_choose_layout, container, false);
        imageViewLevel1 = (ImageView) view.findViewById(R.id.imageViewSelectLevel1);
        imageViewLevel2 = (ImageView) view.findViewById(R.id.imageViewSelectLevel2);
        imageViewLevel3 = (ImageView) view.findViewById(R.id.imageViewSelectLevel3);
        imageViewLevel4 = (ImageView) view.findViewById(R.id.imageViewSelectLevel4);

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }


}
