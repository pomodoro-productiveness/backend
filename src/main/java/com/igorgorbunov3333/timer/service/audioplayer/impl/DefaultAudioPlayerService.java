package com.igorgorbunov3333.timer.service.audioplayer.impl;

import com.igorgorbunov3333.timer.service.audioplayer.AudioPlayerService;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;

@Service
public class DefaultAudioPlayerService implements AudioPlayerService {

    private static final String PATH = "D:\\timer.snd";

    private final Clip clip = AudioSystem.getClip();

    public DefaultAudioPlayerService() throws LineUnavailableException {
    }

    @Override
    public void play() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(PATH).getAbsoluteFile());
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void stop() {
        clip.stop();
        clip.close();
    }

}
