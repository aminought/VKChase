package serverdaemon.controller;

import com.google.inject.Inject;
import serverdaemon.controller.logic.IAppLogic;
import shared.controller.api_service.IApiService;
import shared.controller.db_service.IDBService;
import shared.model.event.EventType;
import shared.model.event.Follower_Events;
import shared.model.event.Following_EventTypes;
import shared.model.snapshots.Snapshot;
import shared.model.user.Follower;
import shared.model.user.Following;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

public class RefreshTask extends TimerTask {
    private IApiService apiService;
    private IAppLogic appLogic;
    private IDBService dbService;

    @Inject
    private RefreshTask(IApiService apiService,
                        IAppLogic appLogic,
                        IDBService dbService) {
        this.apiService = apiService;
        this.appLogic = appLogic;
        this.dbService = dbService;
    }

    @Override
    public void run() {
//        Set<Group> groups = dbService.getAllGroups();
//        GroupRefresher groupRefresher = new GroupRefresher(apiService, dbService);
//        groupRefresher.refresh(groups);
//
//        GroupSnapshotBuilder groupSnapshotBuilder = new GroupSnapshotBuilder();
//        for (Group group : groups) {
//            GroupSnapshot groupSnapshot = groupSnapshotBuilder.build(group);
//            dbService.saveGroupSnapshot(groupSnapshot);
//        }
//
//        Set<Following> following = dbService.getAllFollowings();
//        FollowingRefresher followingRefresher = new FollowingRefresher(appLogic, dbService);
//        followingRefresher.refresh(following);
        GroupRefresher groupRefresher = new GroupRefresher(apiService, dbService);

        Map<Long, Follower> followers = dbService.getAllFollowers();
        followers.forEach((id, follower) -> {
            Set<Following> followings = follower.getFollowing();
            followings.forEach(following -> {

                Following_EventTypes followingEventTypes = follower
                        .getFollowing_EventTypesList()
                        .stream()
                        .filter(following_eventTypes -> following_eventTypes.getFollowing() == following)
                        .findFirst()
                        .get();
                List<EventType> eventTypes = followingEventTypes.getEventTypes();

                Follower_Events followerEvents = following
                        .getFollower_EventsList()
                        .stream()
                        .filter(follower_events -> follower_events.getFollower() == follower)
                        .findFirst()
                        .get();
                List<Snapshot> snapshots = followerEvents.getSnapshots();

                eventTypes.forEach(eventType -> {
                    switch (eventType) {
                        case GROUP_LIKE:
                            Snapshot snapshot = groupRefresher.refresh(following, follower);
                            if(snapshots.size() == 0) {
                                snapshots.add(snapshot);
                            } else {
                                
                            }
                            break;
                    }
                });
            });
        });

        System.out.println("Ok");
    }
}
