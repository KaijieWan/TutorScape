package com.example.tutorscape.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.example.tutorscape.R;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class SettingsFragment extends Fragment {
    private NavController navController;
    private NavHostFragment navHostFragment;
    ChipNavigationBar menu;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment = null;
    ImageView expandButton;
    RelativeLayout containerMain;

    ChangeBounds changeBounds = new ChangeBounds();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.settings_fragment, container, false);
        Log.d("SettingsFragment", "Called");

        menu = root.findViewById(R.id.settings_menu);
        containerMain = root.findViewById(R.id.container_main);
        expandButton = root.findViewById(R.id.expand_button);
        menu.setItemSelected(R.id.item_account, true);

        /*if(fragment == null){
            fragmentManager = getActivity().getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, new AccountFragment());
            fragmentTransaction.commit();
        }*/

        navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.frame_container);
        navController = navHostFragment.getNavController();

        menu.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if(i==R.id.item_account){
                    navController.navigate(R.id.AccountFragment);
                }
                else if(i==R.id.item_notifications){
                    navController.navigate(R.id.NotificationsFragment);
                }
                else if(i==R.id.item_security){
                    navController.navigate(R.id.SecurityFragment);
                }
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menu.isExpanded()){
                    TransitionManager.beginDelayedTransition(containerMain, changeBounds);
                    menu.collapse();
                    expandButton.setImageResource(R.drawable.ic_back);
                }
                else{
                    TransitionManager.beginDelayedTransition(containerMain, changeBounds);
                    menu.expand();
                    expandButton.setImageResource(R.drawable.ic_forward);
                }
            }
        });

        return root;
    }
}
