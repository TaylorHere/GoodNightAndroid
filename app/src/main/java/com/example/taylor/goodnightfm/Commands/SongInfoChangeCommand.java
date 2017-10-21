package com.example.taylor.goodnightfm.Commands;

import com.example.taylor.goodnightfm.DoActions.Command;
import com.example.taylor.goodnightfm.Models.SongInfo;

import java.util.List;

/**
 * Created by taylor on 2017/10/9.
 */

public class SongInfoChangeCommand extends Command {
    private final List<SongInfo> songInfos;

    public SongInfoChangeCommand(List<SongInfo> songInfos) {
        this.songInfos = songInfos;
    }

    public List<SongInfo> getSongInfos() {
        return songInfos;
    }
}
