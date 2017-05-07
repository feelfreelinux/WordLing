package io.github.feelfreelinux.wordling.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.github.feelfreelinux.wordling.R;

public class WordpackListAdapter extends ArrayAdapter {
    public WordpackListAdapter(Context context, WordpackList repository) {
        super(context, 0, repository.getWordpackEntries());
    }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        WordpackEntry entry = (WordpackEntry) getItem(pos);

        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wordpack_list_item, parent, false);
        TextView title = (TextView) convertView.findViewById(R.id.entryTitle);
        TextView name = (TextView) convertView.findViewById(R.id.entryName);

        title.setText(entry.title);
        name.setText(entry.description);

        return convertView;
    }
}
