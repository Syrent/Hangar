package io.papermc.hangar.controller.internal;

import io.papermc.hangar.model.common.NamedPermission;
import io.papermc.hangar.model.common.Platform;
import io.papermc.hangar.model.db.versions.JarScanResultTable;
import io.papermc.hangar.security.annotations.permission.PermissionRequired;
import io.papermc.hangar.security.annotations.ratelimit.RateLimit;
import io.papermc.hangar.service.internal.versions.JarScanningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RateLimit(path = "jarscanning")
@RequestMapping(value = "/api/internal/jarscanning", produces = MediaType.APPLICATION_JSON_VALUE)
public class JarScanningController {

    private final JarScanningService jarScanningService;

    public JarScanningController(final JarScanningService jarScanningService) {
        this.jarScanningService = jarScanningService;
    }

    @ResponseBody
    @GetMapping("/result/{platform}/{versionId}")
    @PermissionRequired(NamedPermission.REVIEWER)
    public JarScanResultTable getResult(@PathVariable final Platform platform, @PathVariable final long versionId)  {
        final JarScanResultTable lastResult = this.jarScanningService.getLastResult(versionId, platform);
        if (lastResult != null) {
            return lastResult;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping("/scan/{platform}/{versionId}")
    @PermissionRequired(NamedPermission.REVIEWER)
    public void scan(@PathVariable final Platform platform, @PathVariable final long versionId)  {
        this.jarScanningService.scan(versionId, platform);
    }
}
