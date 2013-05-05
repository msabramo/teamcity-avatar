package ru.mail.teamcity.avatar.web;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.*;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ru.mail.teamcity.avatar.AppConfiguration;
import ru.mail.teamcity.avatar.service.AvatarService;
import ru.mail.teamcity.avatar.supplier.AvatarSupplier;
import ru.mail.teamcity.avatar.supplier.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * User: Grigory Chernyshev
 * Date: 29.04.13 21:39
 */
public class AvatarProfileConfiguration extends SimpleCustomTab {

  private final AvatarService myAvatarService;

  public AvatarProfileConfiguration(@NotNull PagePlaces pagePlaces,
                                    @NotNull WebControllerManager controllerManager,
                                    @NotNull PluginDescriptor descriptor,
                                    @NotNull AvatarService avatarService) {

    super(
            pagePlaces,
            PlaceId.MY_TOOLS_TABS,
            AppConfiguration.PLUGIN_NAME,
            descriptor.getPluginResourcesPath("settings/avatarConfiguration.jsp"),
            "Avatar"
    );
    this.myAvatarService = avatarService;

    register();

    controllerManager.registerController("/profileAvatarConfig.html", new BaseController() {
      @Nullable
      @Override
      protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        SUser user = SessionUser.getUser(request);
        AvatarSupplier avatarSupplier = WebHelper.getAvatarSupplier(request);
        if (null == avatarSupplier) {
          // TODO: add error handling
          return null;
        }

        myAvatarService.store(user, avatarSupplier, request.getParameterMap());
        return new ModelAndView(new RedirectView(String.format("profile.html?tab=%s", AppConfiguration.PLUGIN_NAME), true));
      }
    });
  }

  @Override
  public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
    super.fillModel(model, request);
    SUser user = SessionUser.getUser(request);
    AvatarSupplier avatarSupplier = myAvatarService.getAvatarSupplier(user);

    model.put("avatarService", myAvatarService);
    if (null != avatarSupplier) {
      model.put("selectedAvatarSupplier", avatarSupplier.getClass().getName());
    }
    model.put("suppliers", new ArrayList<Supplier>(Arrays.asList(Supplier.values())));
  }
}