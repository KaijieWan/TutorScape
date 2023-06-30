package com.example.tutorscape.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.ChangeBounds;

import com.example.tutorscape.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class SettingsFragment extends Fragment {
    ChipNavigationBar menu;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment = null;
    ImageView expandButton;
    LinearLayout containerMain;

    ChangeBounds changeBounds = new ChangeBounds();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.settings_fragment, container, false);
        Log.d("SettingsFragment", "Called");

        menu = root.findViewById(R.id.settings_menu);
        containerMain = root.findViewById(R.id.container_main);
        expandButton = root.findViewById(R.id.expand_button);

        if(fragment == null){
            fragmentManager = getActivity().getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new AccountFragment());
            fragmentTransaction.commit();
        }
        return root;
    }
}
