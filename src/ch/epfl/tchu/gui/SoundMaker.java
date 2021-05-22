package ch.epfl.tchu.gui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public final class SoundMaker {
    private SoundMaker(){}

    /**
     * Methode utilisée pour faire les sons
     * @param string (String) : le son à utilser
     */
    public static Clip makeSound(String string){
        try{
            File file = new File(string);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ua) {
            ua.printStackTrace();
        }
        return null;
    }

    /**
     * Méthode utilsée pour répéter indéfiniement un son
     * @param clip (Clip) : le clip audio en question
     */
    public static void makeInfLoop(Clip clip){
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}