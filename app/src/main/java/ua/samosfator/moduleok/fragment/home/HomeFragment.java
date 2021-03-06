package ua.samosfator.moduleok.fragment.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.splunk.mint.Mint;
import com.splunk.mint.MintLogLevel;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import ua.samosfator.moduleok.R;
import ua.samosfator.moduleok.event.RefreshEndEvent;
import ua.samosfator.moduleok.event.SemesterChangedEvent;
import ua.samosfator.moduleok.student_bean.Module;
import ua.samosfator.moduleok.student_bean.Semester;
import ua.samosfator.moduleok.student_bean.Subject;
import ua.samosfator.moduleok.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private CardView mCardView;
    private List<Subject> mSubjects = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        App.registerClassForEventBus(this);
        Mint.logEvent("view HomeFragment", MintLogLevel.Info);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Semester semester = StudentKeeper.getStudent().getSemester(StudentKeeper.getCurrentSemesterIndex());
        mSubjects = semester.getSubjects();
        mCardView = (CardView) rootView.findViewById(R.id.home_cardview);

        FrameLayout firstNextModule = (FrameLayout) mCardView.findViewById(R.id.first_next_module);
        FrameLayout secondNextModule = (FrameLayout) mCardView.findViewById(R.id.second_next_module);

        TextView averageScoreTextView = (TextView) rootView.findViewById(R.id.average_score_textview);
//        TextView predictedScoreSecondModuleTextView = (TextView) secondNextModule.findViewById(R.id.second_subject_score);

        TextView firstModuleNameTextView = (TextView) firstNextModule.findViewById(R.id.subject_name);
        TextView firstModuleDateTextView = (TextView) firstNextModule.findViewById(R.id.subject_date);
        TextView firstModuleWeightTextView = (TextView) firstNextModule.findViewById(R.id.subject_weight);
        TextView secondModuleNameTextView = (TextView) secondNextModule.findViewById(R.id.second_subject_name);
        TextView secondModuleDateTextView = (TextView) secondNextModule.findViewById(R.id.second_subject_date);
        TextView secondModuleWeightTextView = (TextView) secondNextModule.findViewById(R.id.second_subject_weight);


        /////////////////////
        averageScoreTextView.setText(String.format("%.2f", semester.getAverageScore()));

        List<Module> nearestModules = getNearestModules();
        List<Subject> nearestModulesSubjects = getNearestModulesSubjects(nearestModules);

        if (nearestModules.size() == 0) {
            mCardView.removeAllViews();
            return rootView;
        }

        Module firstNearestModule = nearestModules.get(0);
        Subject firstNearestModulesSubject = nearestModulesSubjects.get(0);

        firstModuleNameTextView.setText(firstNearestModulesSubject.getName());

//        PredictedScore scorePredictionFirst = firstNearestModulesSubject.getScorePrediction();
//        int predictedFirstScore = scorePredictionFirst.getSufficientScore();
//        int desiredTotalScoreFirst = scorePredictionFirst.getDesiredTotalScore();
//        predictedScoreFirstModuleTextView.setText(String.valueOf(predictedFirstScore));
//
//        if (desiredTotalScoreFirst >= 90) {
//            predictedScoreFirstModuleTextView.setBackgroundResource(R.drawable.circle_5_prediction);
//        } else if (desiredTotalScoreFirst >= 75) {
//            predictedScoreFirstModuleTextView.setBackgroundResource(R.drawable.circle_4_prediction);
//        } else if (desiredTotalScoreFirst >= 60) {
//            predictedScoreFirstModuleTextView.setBackgroundResource(R.drawable.circle_3_prediction);
//        }

        firstModuleDateTextView.setText(firstNearestModule.getDate());
        firstModuleWeightTextView.setText(firstNearestModule.getWeight() + "%");

        if (nearestModules.size() == 1) {
            return rootView;
        }

        Module secondNearestModule = nearestModules.get(1);
        Subject secondNearestModulesSubject = nearestModulesSubjects.get(1);

        secondModuleNameTextView.setText(secondNearestModulesSubject.getName());

        secondModuleDateTextView.setText(secondNearestModule.getDate());
        secondModuleWeightTextView.setText(secondNearestModule.getWeight() + "%");

        return rootView;
    }

    private List<Module> getNearestModules() {
        List<Module> nearestModules = new ArrayList<>();
        for (Subject subject : mSubjects) {
            Module nearestModule = subject.getNearestModule();
            if (!nearestModule.getDate().equals("")) {
                nearestModules.add(nearestModule);
            }
        }
        Collections.sort(nearestModules, new Subject.ModulesByDateComparator());
        return nearestModules;
    }

    private List<Subject> getNearestModulesSubjects(List<Module> nearestModules) {
        List<Subject> nearestModulesSubjects = new ArrayList<>();
        for (Module nearestModule : nearestModules) {
            Subject subjectByModule = Semester.getSubjectByModule(mSubjects, nearestModule);
            nearestModulesSubjects.add(subjectByModule);
        }
        return nearestModulesSubjects;
    }

    @Subscribe
    public void onEvent(RefreshEndEvent event) {
        Log.d("EVENTS-LastTotal", "RefreshEndEvent");
        if (FragmentsKeeper.getHome().isVisible()) {
            FragmentsKeeper.setHome(new HomeFragment());
            FragmentUtils.showFragment(getFragmentManager().beginTransaction(), FragmentsKeeper.getHome());
        } else {
            FragmentsKeeper.setHome(new HomeFragment());
        }
    }

    @Subscribe
    public void onEvent(SemesterChangedEvent event) {
        Log.d("SEMESTER_CHANGED_EVENT", "SemesterIndex:" + StudentKeeper.getCurrentSemesterIndex());
        if (FragmentsKeeper.getHome().isVisible()) {
            FragmentsKeeper.setHome(new HomeFragment());
            FragmentUtils.showFragment(getFragmentManager().beginTransaction(), FragmentsKeeper.getHome());
        } else {
            FragmentsKeeper.setHome(new HomeFragment());
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
