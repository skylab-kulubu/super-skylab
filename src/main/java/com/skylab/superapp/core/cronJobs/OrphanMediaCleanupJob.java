package com.skylab.superapp.core.cronJobs;

import com.skylab.superapp.core.utilities.storage.R2StorageService;
import com.skylab.superapp.dataAccess.MediaDao;
import com.skylab.superapp.entities.Media;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrphanMediaCleanupJob {

    private final MediaDao mediaDao;
    private final R2StorageService r2StorageService;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanOrphanMedia() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);

        List<Media> orphans = mediaDao
                .findByAttachedFalseAndCreatedAtBefore(threshold);

        if (orphans.isEmpty()) {
            log.info("No orphan media found.");
            return;
        }

        log.info("Found {} orphan media, cleaning up...", orphans.size());

        orphans.forEach(media -> {
            try {
                r2StorageService.deleteFile(media.getFileUrl());
                mediaDao.delete(media);
                log.info("Deleted orphan media: {}", media.getId());
            } catch (Exception e) {
                log.error("Failed to delete orphan media: {}, error: {}",
                        media.getId(), e.getMessage());
            }
        });
    }
}
