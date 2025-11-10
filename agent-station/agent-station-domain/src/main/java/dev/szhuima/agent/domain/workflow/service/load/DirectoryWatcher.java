//package dev.szhuima.agent.domain.workflow.service.load;
//
//import jakarta.annotation.PostConstruct;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Async;
//
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//public abstract class DirectoryWatcher {
//
//    private WatchService watchService;
//
//    /**
//     * 初始化 WatchService
//     */
//    @PostConstruct
//    public void init() throws IOException {
//        this.watchService = FileSystems.getDefault().newWatchService();
//    }
//
//    public abstract void onFileCreate(Path file);
//
//    public abstract void onFileModify(Path file);
//
//    public abstract void onFileDelete(Path file);
//
//
//    /**
//     * 注册多个目录
//     */
//    @SneakyThrows
//    public void registerDirectories(List<String> directories) {
//        for (String directory : directories) {
//            Path path = Paths.get(directory);
//            path.register(
//                    watchService,
//                    StandardWatchEventKinds.ENTRY_CREATE,
//                    StandardWatchEventKinds.ENTRY_MODIFY,
//                    StandardWatchEventKinds.ENTRY_DELETE
//            );
//            log.info("已注册目录监听: {}", directory);
//        }
//    }
//
//    /**
//     * 异步启动监听，不阻塞主流程
//     */
//    @Async
//    public void startWatching(String... suffix) {
//        log.info("开始监听文件变化, 后缀: {}", (Object) suffix);
//
//        if (suffix == null || suffix.length == 0) {
//            log.warn("监听文件后缀不能为空");
//            return;
//        }
//
//        while (true) {
//            WatchKey key;
//            try {
//                key = watchService.take(); // 阻塞等待事件
//            } catch (InterruptedException e) {
//                log.warn("文件监听线程被中断", e);
//                Thread.currentThread().interrupt();
//                break;
//            }
//            Path watchDir = (Path) key.watchable();
//            for (WatchEvent<?> event : key.pollEvents()) {
//                WatchEvent.Kind<?> kind = event.kind();
//                Path fileName = (Path) event.context();
//                try {
//                    if (Arrays.stream(suffix).noneMatch((suffixStr) -> fileName.toString().endsWith(suffixStr))) {
//                        continue;
//                    }
//                    Path fullPath = watchDir.resolve(fileName);
//
//                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
//                        log.info("创建: {}", fullPath);
//                        onFileCreate(fullPath);
//                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
//                        log.info("修改: {}", fullPath);
//                        onFileModify(fullPath);
//                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
//                        log.info("删除: {}", fullPath);
//                        onFileDelete(fullPath);
//                    }
//                } catch (Exception e) {
//                    log.error("处理文件事件时出错: {}", fileName, e);
//                }
//
//            }
//
//            if (!key.reset()) {
//                log.warn("目录不可用: {}", watchDir);
//                break;
//            }
//        }
//    }
//}
