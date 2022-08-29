package de.rehagoal.rehagoalwebapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.rehagoal.rehagoalwebapp.R;

import de.rehagoal.rehagoalwebapp.model.API.ListEntry;
import de.rehagoal.rehagoalwebapp.model.API.Start;
import de.rehagoal.rehagoalwebapp.services.RehaGoalResponderService;
import de.rehagoal.rehagoalwebapp.services.TTSService;

import java.util.List;

import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_START;
import static de.rehagoal.rehagoalwebapp.Constants.LIST_TYPE_WORKFLOW;
import static de.rehagoal.rehagoalwebapp.Constants.TTS_ACTION_SPEAK;

/**
 * Adapter Class for RecyclerView
 */

public class OverviewListAdapter extends RecyclerView.Adapter<OverviewListAdapter.ViewHolder> {
    private List<ListEntry> mOverviewList;
    private Context mContext;

    /**
     * Default constructor to instantiate this ListAdapter
     *
     * @param context provides the application context
     * @param entries provides the data elements needed to be bind to the view
     */
    OverviewListAdapter(Context context, List<ListEntry> entries) {
        mContext = context;
        mOverviewList = entries;
    }

    /**
     * Getter function to provide the application context
     *
     * @return application/activity context
     */
    private Context getContext() {
        return mContext;
    }

    /**
     * Implemented function from @RecyclerView.Adapter
     * It creates a ViewHolder from a static layout and prepares it to be filled with data elements
     *
     * @param parent   ViewGroup of this Adapter
     * @param viewType not needed in this adapter, see @RecyclerView.Adapter for details
     * @return returns a ViewHolder ready to be bound with the data elements
     */
    @Override
    public OverviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View entriesView = inflater.inflate(R.layout.simple_list_entry, parent, false);
        return new OverviewListAdapter.ViewHolder(entriesView);
    }

    /**
     * Implemented function from @RecyclerView.Adapter
     * It uses a provided ViewHolder to bind data elements to it
     *
     * @param viewHolder layout placeholder to be filled by the data element
     * @param position   position of the placeholder inside the layout (list)
     */
    @Override
    public void onBindViewHolder(OverviewListAdapter.ViewHolder viewHolder, int position) {
        final ListEntry entry = mOverviewList.get(position);
        Button button = viewHolder.button;
        button.setText(entry.getName());
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent speakTextIntent = new Intent(getContext(), TTSService.class);
                speakTextIntent.putExtra(TTS_ACTION_SPEAK, entry.getName());
                getContext().startService(speakTextIntent);
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStart(entry.getId(), LIST_TYPE_WORKFLOW);
            }
        });
    }

    /**
     * Implemented function from @RecyclerView.Adapter
     * Gets the size of the Adapter
     *
     * @return number of entries inside this adapter
     */
    @Override
    public int getItemCount() {
        return mOverviewList.size();
    }

    /**
     * Helper function to communicate that an entry was selected.
     * This calls the @RehaGoalResponderService to communicate a start message
     *
     * @param id   element id to be started
     * @param type type of the element
     */
    private void sendStart(int id, String type) {
        Intent intent = new Intent(getContext(), RehaGoalResponderService.class);
        intent.setAction(REHAGOAL_ACTION_START);
        intent.putExtra(REHAGOAL_ACTION_START, new Start(id, type));
        getContext().startService(intent);
    }

    /**
     * Implemented by RecyclerView.ViewHolder
     * Sets and allocates the available layout elements for the view
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            button = (Button) itemView.findViewById(R.id.button);
        }
    }


}