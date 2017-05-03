package io.github.feelfreelinux.wordling.objects;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.screens.WordInputActivity;
import io.github.feelfreelinux.wordling.utils.SortedSessionManager;
import io.github.feelfreelinux.wordling.utils.StorageWordpackManager;

public class WordpackListAdapter extends ArrayAdapter {
    private WordpackEntry entry;

    public WordpackListAdapter(Context context, WordpackList repository) {
        super(context, 0, repository.getWordpackEntries());
    }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        entry = (WordpackEntry) getItem(pos);

        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wordpack_list_item, parent, false);
        TextView title = (TextView) convertView.findViewById(R.id.entryTitle);
        TextView name = (TextView) convertView.findViewById(R.id.entryName);

        title.setText(entry.title);
        name.setText(entry.description);

        return convertView;
    }

    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            entry = (WordpackEntry) getItem(position);
            // Create load wordpack data from memory, create session manager
            SortedSessionManager sm = new SortedSessionManager(
                    new StorageWordpackManager(getContext())
                            .getWordpackFromStorage(entry.key), entry.key);
            // Start new session
            Intent inputScreen = new Intent(getContext(), WordInputActivity.class);
            inputScreen.putExtra("SortedSessionManager", sm);
            getContext().startActivity(inputScreen);
        }
    };

}
