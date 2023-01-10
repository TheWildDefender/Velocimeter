package com.gmail.hecarson3.velocimeter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VelocityDisplayFragment extends Fragment {

    Handler frameUpdateHandler;
    CompassInfo compassInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        frameUpdateHandler = new Handler(Looper.myLooper());
        frameUpdateHandler.post(this::onFrameUpdate);

        compassInfo = new CompassInfo(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_velocity_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VelocityDisplayView velocityDisplayView = (VelocityDisplayView)view.findViewById(R.id.velocityDisplayView);
        velocityDisplayView.setCompassInfo(compassInfo);
    }

    @Override
    public void onResume() {
        super.onResume();

        compassInfo.registerSensorListeners();
    }

    @Override
    public void onPause() {
        super.onPause();

        compassInfo.unregisterSensorListeners();
    }

    private void onFrameUpdate() {
        View velocityDisplayView = requireView().findViewById(R.id.velocityDisplayView);
        velocityDisplayView.invalidate();
        frameUpdateHandler.postDelayed(this::onFrameUpdate, 17);
    }

}