package io.github.feelfreelinux.wordling.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.objects.Word;

public class WordpackEditorAdapter extends ArrayAdapter {
    public WordpackEditorAdapter(Context context, List<Word> wordList) {
        super(context, 0, wordList);
    }


    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        Word word = (Word) getItem(pos);
        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_wordpack_editor_item, parent, false);
        TextView origin = (TextView) convertView.findViewById(R.id.origin);
        TextView translation = (TextView) convertView.findViewById(R.id.translation);

        origin.setText(word.getOriginLangQuestions().get(0));
        translation.setText(word.getTranslationLangQuestion());

        return convertView;
    }
}
