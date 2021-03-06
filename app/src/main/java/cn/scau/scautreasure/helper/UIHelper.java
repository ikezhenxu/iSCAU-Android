package cn.scau.scautreasure.helper;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import cn.scau.scautreasure.R;

/**
 * User:  Special Leung
 * Date:  13-7-29
 * Time:  下午5:46
 * Mail:  specialcyci@gmail.com
 */
public class UIHelper {

    public static final int    QUERY_FOR_EDIT_CLASS   = 101;
    public static final int    TARGET_FOR_NOW_BORROW  = 102;
    public static final int    TARGET_FOR_PAST_BORROW = 103;
    public static final int    QUERY_FOR_EDIT_ACCOUNT = 104 ;
    public static final String CANCEL_FLAG            = "cancellable_task";
    private static ProgressDialog dialog;
    public static enum LISTVIEW_EFFECT_MODE{
        EXPANDABLE, ALPHA, SWING,
        EXPANDABLE_ALPHA,EXPANDABLE_SWING
    }

    /**
     * build a progressDialog;
     * @param ctx
     *          Context of the activity;
     * @param listener
     *          Cancel listener if can cancel,if no,set to null;
     */
    public static void buildDialog(Context ctx,DialogInterface.OnCancelListener listener){

        dialog = new ProgressDialog(ctx);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(R.string.tips);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminate(false);
        if( listener != null){
            dialog.setCancelable(true);
            dialog.setOnCancelListener(listener);
        }
    }

    public static void buildDialog(Context ctx){
        if (ctx instanceof DialogInterface.OnCancelListener){
            buildDialog(ctx,(DialogInterface.OnCancelListener)ctx);
        }else{
            buildDialog(ctx,null);
        }
    }

    /**
     * return the dialog instantce;
     * @param titleStringId
     *                  the title of the dialog to show;
     * @return
     */
    public static ProgressDialog getDialog(int titleStringId){
        String message = dialog.getContext().getString(titleStringId);
        dialog.setMessage(message);
        return getDialog();
    }

    /**
     * return the dialog instantce;
     * @return
     */
    public static ProgressDialog getDialog() {
        return dialog;
    }

    /***
     * build a adapter with effect;
     * @param originAdapter
     * @param listView
     * @param buildModel
     * @return
     */
    public static BaseAdapter buildEffectAdapter(BaseAdapter originAdapter,AbsListView listView,LISTVIEW_EFFECT_MODE buildModel){

        BaseAdapter returnAdapter = null;
        SlideExpandableListAdapter exAdapter;

        switch (buildModel){

            case EXPANDABLE:
                returnAdapter = new SlideExpandableListAdapter(
                        originAdapter,
                        R.id.expandable_toggle_button,
                        R.id.expandable);
                break;

            case ALPHA:
                returnAdapter = new AlphaInAnimationAdapter(originAdapter);
                ((AlphaInAnimationAdapter)returnAdapter).setAbsListView(listView);
                break;

            case SWING:
                returnAdapter = new SwingLeftInAnimationAdapter(originAdapter);
                ((SwingLeftInAnimationAdapter)returnAdapter).setAbsListView(listView);
                break;

            case EXPANDABLE_ALPHA:
                exAdapter = new SlideExpandableListAdapter(originAdapter,R.id.expandable_toggle_button,R.id.expandable);
                returnAdapter = new AlphaInAnimationAdapter(exAdapter);
                ((AlphaInAnimationAdapter)returnAdapter).setAbsListView(listView);
                break;

            case EXPANDABLE_SWING:
                exAdapter = new SlideExpandableListAdapter(originAdapter,R.id.expandable_toggle_button,R.id.expandable);
                returnAdapter     = new SwingLeftInAnimationAdapter(exAdapter);
                ((SwingLeftInAnimationAdapter)returnAdapter).setAbsListView(listView);
                break;

        }

        return returnAdapter;

    }


    /**
     * help to build ClassTableListView
     * @param ctx
     * @return
     */
    public static ListView buildClassListView(Context ctx){
        ListView classListView = new ListView(ctx);
        classListView.setBackgroundColor(0);
        classListView.setCacheColorHint(0);
        classListView.setDividerHeight(0);
        return classListView;
    }

    /**
     * help to build the wheel adapter;
     * @param ctx
     * @param strings
     * @return
     */
    public static ArrayWheelAdapter<String> buildWheelAdapter(Context ctx,String[] strings){

        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(ctx, strings);
        adapter.setItemTextResource(R.id.text);
        adapter.setItemResource(R.layout.classtable_editor_wheel_text);
        return adapter;

    }

    /**
     * help to build bundle;
     * @param objects
     * @return
     */
    private static Bundle buildBundle(Object... objects){
        Bundle bundle = new Bundle();
        for (int index = 0; index < objects.length; index = index + 2){
            String key   = (String) objects[index];
            Object value = objects[index + 1];
            if(value instanceof Integer){
                bundle.putInt(key, (Integer) value);
            }else if(value instanceof String){
                bundle.putString(key , (String) value);
            }else if(value instanceof Boolean){
                bundle.putBoolean(key, (Boolean) value);
            }else if(value instanceof ArrayList){
                bundle.putStringArrayList(key, (ArrayList<String>) value);
            }
        }
        return bundle;
    }

    /**
     * help to replace fragment
     * @param act
     * @param fragment
     */
    public static void startFragment(ActionBarActivity act, Fragment fragment){
        FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.main_fragment,fragment);
        ft.commit();
    }

    /**
     * help to add fragment
     * @param act
     * @param fragment
     */
    public static void startFragment(ActionBarActivity act, Fragment fragment, String tag){
        deAttachAllFragments(act);
        FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
        Fragment _fragment = act.getSupportFragmentManager().findFragmentByTag(tag);
        if (_fragment != null) {
            ft.show(_fragment);
        }else {
            ft.add(R.id.main_fragment, fragment, tag);
        }
        ft.commitAllowingStateLoss();
        act.getSupportFragmentManager().executePendingTransactions();
    }

    private static void deAttachAllFragments(ActionBarActivity act){
        FragmentManager fragmentManager = act.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) return;
        for(Fragment fragment : fragments){
            if (fragment.isVisible())
                ft.hide(fragment);
        }
        ft.commitAllowingStateLoss();
    }

    /**
     * help to replace fragment and set the arguments
     * @param act
     * @param fragment
     * @param objects   the format such as "target"(key),"1"(value),"mode","all" ....
     */
    public static void startFragment(ActionBarActivity act, Fragment fragment, Object... objects){
        fragment.setArguments(buildBundle(objects));
        startFragment(act,fragment);
    }

    /**
     * help to add fragment
     *
     * @param act
     * @param fragment
     */
    public static void addFragment(ActionBarActivity act, Fragment fragment){

        FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.main_fragment, fragment, "fragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();

    }

    /**
     * help to add fragment with params
     * @param act
     * @param fragment
     * @param objects
     */
    public static void addFragment(ActionBarActivity act, Fragment fragment, Object... objects){
        // build bundle;
        fragment.setArguments(buildBundle(objects));
        addFragment(act,fragment);
    }

    public static void showToast(int textResourceId,Context ctx){
        Toast.makeText(ctx,textResourceId,Toast.LENGTH_LONG).show();
    }

}
