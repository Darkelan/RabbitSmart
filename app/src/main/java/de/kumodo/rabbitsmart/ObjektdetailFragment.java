package de.kumodo.rabbitsmart;

/**
 * Created by l.schmidt on 10.03.2017.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ObjektdetailFragment extends Fragment {

    public ObjektdetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_objektdetail, container, false);

        return rootView;
    }
}