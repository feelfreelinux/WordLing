package io.github.feelfreelinux.wordling.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.feelfreelinux.wordling.R;

public class WordEditAdapter extends ArrayAdapter {
    public WordEditAdapter(Context context, List<String> wordList) {
        super(context, 0, wordList);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        String word = (String) getItem(pos);
        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_edit_word_list_item, parent, false);
        TextView wordText = (TextView) convertView.findViewById(R.id.word);

        wordText.setText(word);

        return convertView;
    }
}
