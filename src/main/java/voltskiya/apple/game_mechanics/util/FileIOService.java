package voltskiya.apple.game_mechanics.util;

import apple.utilities.request.AppleRequest;
import apple.utilities.request.AppleRequestService;
import apple.utilities.request.RequestLogger;
import apple.utilities.request.settings.RequestSettingsBuilder;
import apple.utilities.util.ExceptionUnpackaging;
import java.util.function.Consumer;
import java.util.logging.Level;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

public class FileIOService extends AppleRequestService {

    private final static FileIOService instance = new FileIOService();

    public static FileIOService get() {
        return instance;
    }

    @Override
    public <T> AppleRequestService.RequestHandler<T> queue(AppleRequest<T> request,
        Consumer<T> runAfter, RequestSettingsBuilder<T> builder) {
        builder.addRequestLogger(new RequestLogger<T>() {
            @Override
            public void exceptionUncaught(Exception e) {
                PluginTMW.get().log(Level.WARNING,
                    "Exception doing file IO " + "\n" + ExceptionUnpackaging.getStackTrace(e));
            }
        });
        return super.queue(request, runAfter, builder);
    }

    @Override
    public int getRequestsPerTimeUnit() {
        return 100;
    }

    @Override
    public int getTimeUnitMillis() {
        return 0;
    }

    @Override
    public int getSafeGuardBuffer() {
        return 0;
    }
}
