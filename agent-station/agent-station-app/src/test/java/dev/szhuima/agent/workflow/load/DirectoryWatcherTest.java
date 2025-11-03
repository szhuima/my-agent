package dev.szhuima.agent.workflow.load;

import dev.szhuima.agent.domain.workflow.service.load.DirectoryWatcher;
import jakarta.annotation.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DirectoryWatcherTest {

    @Resource
    private DirectoryWatcher directoryWatcher;

    @Before
    public void registerDirectories() {
        directoryWatcher.registerDirectories(List.of("/Users/jack/Temp/workflow"));
    }

    @Test
    public void startWatching() {
        directoryWatcher.startWatching("txt");

        // 等待一段时间，确保监听线程有机会运行
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}