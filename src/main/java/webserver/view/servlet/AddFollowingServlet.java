package webserver.view.servlet;

import com.google.inject.Inject;
import shared.controller.account_service.IAccountService;
import shared.controller.api_service.IApiService;
import shared.controller.db_service.IDBService;
import shared.model.event.Follower_EventTypes;
import shared.model.event.EventType;
import shared.model.user.Follower;
import shared.model.user.Following;
import shared.model.user.UserInfo;
import webserver.controller.cookies_service.ICookiesService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddFollowingServlet extends HttpServlet {
    private IApiService apiService;
    private IAccountService accountService;
    private ICookiesService cookiesService;
    private IDBService dbService;

    @Inject
    public AddFollowingServlet(IApiService apiService,
                               IAccountService accountService,
                               ICookiesService cookiesService,
                               IDBService dbService) {
        this.apiService = apiService;
        this.accountService = accountService;
        this.cookiesService = cookiesService;
        this.dbService = dbService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userLink = req.getParameter("link");
        String[] events = req.getParameterValues("event");

        Long id = Long.valueOf(cookiesService.getCookie(req, "id"));
        Follower follower = accountService.getFollower(id);

        Long followingId = apiService.resolveScreenName(userLink);
        UserInfo userInfo = apiService.getUserInfo(followingId, follower.getAccessToken());
        Following following = new Following(userInfo);

        List<EventType> eventTypes = new ArrayList<>();
        for (String event : events) {
            eventTypes.add(
                    Arrays.stream(EventType.values())
                            .filter(type -> type.name().equals(event))
                            .findFirst()
                            .get());
        }
        Follower_EventTypes follower_eventTypes = new Follower_EventTypes(follower, eventTypes);

        follower.addFollowing(following);
        following.addFollower(follower);
        following.addFollower_EventTypes(follower_eventTypes);
        dbService.saveFollowing(following);
        dbService.updateFollower(follower);

        resp.sendRedirect("groups?following=" + userInfo.getVkId());
    }
}
