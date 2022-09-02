package org.owntracks.android.data.repos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.greenrobot.eventbus.EventBus;
import org.owntracks.android.data.WaypointModel;
import org.owntracks.android.model.messages.MessageWaypoint;
import org.owntracks.android.services.LocationProcessor;
import org.owntracks.android.support.MessageWaypointCollection;

import java.util.List;

import io.objectbox.query.Query;

public abstract class WaypointsRepo {
    private final EventBus eventBus;

    protected WaypointsRepo(EventBus eventBus) {
        this.eventBus = eventBus;
    }
    public abstract WaypointModel get(long tst);
    protected abstract List<WaypointModel> getAll();
    public abstract List<WaypointModel> getAllWithGeofences();
    public abstract LiveData<List<WaypointModel>> getAllLive();
    private MutableLiveData<WaypointModel> mutableLastUpdatedWaypoint =new   MutableLiveData<WaypointModel>();
    public LiveData<WaypointModel> lastUpdatedWaypoint = mutableLastUpdatedWaypoint;
    public abstract Query<WaypointModel> getAllQuery();

    public void insert(WaypointModel w) {
        insert_impl(w);
        mutableLastUpdatedWaypoint.postValue(w);
    }

    public void update(WaypointModel w, boolean notify) {
        update_impl(w);
        if(notify) {
            mutableLastUpdatedWaypoint.postValue(w);
        }
    }

    public void delete(WaypointModel w) {
        delete_impl(w);
    }

    public void importFromMessage(@Nullable MessageWaypointCollection waypoints) {
        if(waypoints == null)
            return;

        for (MessageWaypoint m: waypoints) {
            // Delete existing waypoint if one with the same tst already exists
            WaypointModel exisiting = get(m.getTimestamp());
            if(exisiting != null) {
                delete(exisiting);
            }
            insert(new WaypointModel(m));
        }
    }

    @NonNull
    public MessageWaypointCollection exportToMessage() {
        MessageWaypointCollection messages = new MessageWaypointCollection();
        for(WaypointModel waypoint : getAll()) {
            messages.add(waypoint.toMessageWaypoint());
        }
        return messages;
    }

    protected abstract void insert_impl(WaypointModel w);
    protected abstract void update_impl(WaypointModel w);
    protected abstract void delete_impl(WaypointModel w);

}
