package ftn.eventfinder.tools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import ftn.eventfinder.R;


public class FragmentTransition
{
	public static void to(Fragment newFragment, FragmentActivity activity, String tag)
	{
		to(newFragment, activity, true, tag);
	}

	public static void to(Fragment newFragment, FragmentActivity activity, boolean addToBackstack, String tag)
	{
		FragmentManager fm = activity.getSupportFragmentManager();

		Fragment existingFragment = fm.findFragmentByTag(tag);
		if (existingFragment == null) {
			FragmentTransaction transaction = fm
					.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.replace(R.id.mainContent, newFragment, tag);
			if(addToBackstack) transaction.addToBackStack(null);
			transaction.commit();
		} else {
				FragmentTransaction transaction = fm
						.beginTransaction()
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.replace(R.id.mainContent, existingFragment, tag);
				if(addToBackstack) transaction.addToBackStack(null);
				transaction.commit();


			// ништа
		}



	}

	public static void remove(Fragment fragment, FragmentActivity activity) // TODO izbaciti fragment parametar
	{
		activity.getSupportFragmentManager().popBackStack();
	}
}