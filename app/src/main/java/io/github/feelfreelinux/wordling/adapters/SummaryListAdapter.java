package io.github.feelfreelinux.wordling.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.objects.Word;

public class SummaryListAdapter extends ArrayAdapter {
    public SummaryListAdapter(Context context, List<Word> wordList) {
        super(context, 0, wordList);
    }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {


        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.summary_list_item, parent, false);

        // Get all 4 textViews
        TextView origin = (TextView) convertView.findViewById(R.id.origin);
        TextView translation = (TextView) convertView.findViewById(R.id.translation);
        TextView passed = (TextView) convertView.findViewById(R.id.passedCount);
        TextView failed = (TextView) convertView.findViewById(R.id.failedCount);

        // Header is at pos 0
        if (pos>0) {
            Word word = (Word) getItem(pos);
            // Only show first word.
            origin.setText(word.getOriginLangQuestions().get(0));
            // We want to add count of additional words, so let's check it now
            if (word.getOriginLangQuestions().size() > 1) origin.setText(origin.getText() + " +" + Integer.toString(word.getOriginLangQuestions().size() -1) );
            translation.setText(word.getTranslationLangQuestion());
            passed.setText(Integer.toString(word.getPassedAttempts()));
            failed.setText(Integer.toString(word.getFailedAttempts()));
        } else {
            Resources res = getContext().getResources();
            origin.setText(res.getString(R.string.word));
            translation.setText(res.getString(R.string.translation));
            passed.setText(res.getString(R.string.passedAttempts));
            failed.setText(res.getString(R.string.failedAttempts));
        }
        return convertView;
    }
}
